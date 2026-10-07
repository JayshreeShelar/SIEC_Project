/* =========================================================================
   Purchase Management — PR → PO → GRN → PB   (Spring Boot, port 9093)
   Flow: Requirement (approved) → Convert to PO → PO approved/sent → Goods Receipt
         (PO becomes partial / fully_received) → Bill → Payment → PO closed
   Every field name below matches the backend DTOs (PurchaseOrderRequestDto,
   PurchaseBillRequestDto, PurchaseBillPaymentRequestDto, GoodsReceiptRequestDto).
   ========================================================================= */
const API_HOST = window.API_HOST || "http://localhost:9093"; // set window.API_HOST once (e.g. in shared-data.js) to change it everywhere
const PR_API_BASE = `${API_HOST}/api/purchases/v1`;
const PO_API_BASE = `${API_HOST}/api/purchase-orders/v1`;
const PB_API_BASE = `${API_HOST}/api/purchase-bills/v1`;
const GRN_API_BASE = `${API_HOST}/api/goods-receipts/v1`;
const RECEIVABLE_PO = ["approved", "sent", "partial"];       // PO states that can receive goods
const BILLABLE_PO = ["partial", "fully_received"];           // PO states that have goods received

/* ---------- small utils ---------- */
function escapeHtml(s) {
  if (s === null || s === undefined) return "";
  return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#39;");
}
const E = escapeHtml;
const lc = (s) => (s || "").toString().trim().toLowerCase();
const today = () => new Date().toISOString().slice(0, 10);
const count = (a, ...st) => a.filter((x) => st.includes(lc(x.status))).length;
const sum = (a, f) => a.reduce((s, x) => s + (Number(f(x)) || 0), 0);
const round2 = (n) => Math.round(n * 100) / 100;
const setText = (id, v) => { const el = document.getElementById(id); if (el) el.textContent = v; };
const emptyModulePanel = (m) => `<div class="bg-white border border-[#6da84c] rounded-xl p-10 text-center text-gray-400 text-xs">${E(m)}</div>`;
const cap = (s) => s.charAt(0).toUpperCase() + s.slice(1);

/* Oldest first. Orders by created time, then by the number in the document number (PR-1001 → 1001). */
const numPart = (s) => parseInt(String(s || "").replace(/\D/g, ""), 10) || 0;
const ascBy = (numField) => (a, b) => {
  const ta = new Date(a.createdAt).getTime() || 0, tb = new Date(b.createdAt).getTime() || 0;
  return ta !== tb ? ta - tb : numPart(a[numField]) - numPart(b[numField]);
};

function formatDate(iso) {
  if (!iso) return "-";
  const d = new Date(iso);
  return isNaN(d.getTime()) ? iso : d.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
}
function formatCurrency(a) {
  if (a === null || a === undefined || a === "") return "-";
  const n = Number(a);
  return isNaN(n) ? "-" : n.toLocaleString("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 2 });
}
const fmtTs = (t) => (t ? new Date(t).toLocaleString() : "-");

const GRAY = "bg-gray-50 text-gray-500 border-gray-200", AMBER = "bg-amber-50 text-amber-600 border-amber-200",
  GREEN = "bg-green-50 text-green-600 border-green-200", RED = "bg-red-50 text-red-600 border-red-200",
  BLUE = "bg-blue-50 text-blue-600 border-blue-200";
function badge(value, map, fallback) {
  const s = lc(value);
  const label = s === "received" ? "unpaid" : s.replace(/_/g, " "); // bill status "received" = unpaid
  return `<span class="inline-block px-2 py-0.5 rounded-full border text-[10px] font-semibold capitalize ${map[s] || fallback}">${E(label || "-")}</span>`;
}
const priorityBadge = (p) => badge(p, { high: RED, medium: AMBER, low: GRAY }, AMBER);
const statusBadge = (s) => badge(s, {
  pending: AMBER, approved: GREEN, rejected: RED,                                // PR
  draft: GRAY, sent: BLUE, partial: AMBER, fully_received: GREEN, closed: GRAY,  // PO / GRN
  completed: GREEN,                                                              // GRN
  received: BLUE, paid: GREEN, overdue: RED,                                     // PB (received = unpaid)
}, GRAY);

function showToast(message, type = "success") {
  const wrap = document.getElementById("toastWrap");
  if (!wrap) return;
  const t = document.createElement("div");
  t.className = `toast px-4 py-2.5 rounded-lg shadow-lg text-xs font-medium text-white transition-opacity duration-300 ${type === "error" ? "bg-red-600" : "bg-primary"}`;
  t.textContent = message;
  wrap.appendChild(t);
  setTimeout(() => { t.classList.add("opacity-0"); setTimeout(() => t.remove(), 300); }, 3500);
}

function downloadCsv(lines, prefix) {
  const url = URL.createObjectURL(new Blob([lines.join("\n")], { type: "text/csv;charset=utf-8;" }));
  const a = document.createElement("a");
  a.href = url; a.download = `${prefix}-${today()}.csv`;
  document.body.appendChild(a); a.click(); document.body.removeChild(a);
  URL.revokeObjectURL(url);
}
const csvEscape = (v) => { const s = v == null ? "" : String(v); return /[",\n]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s; };

/* ---------- API (backend wraps everything as { status, message, data }) ---------- */
async function apiRequest(method, base, path, body) {
  const opts = { method, headers: { "Content-Type": "application/json" } };
  if (body !== undefined) opts.body = JSON.stringify(body);
  let res, json = null;
  try { res = await fetch(`${base}${path}`, opts); }
  catch { throw new Error("Could not reach the server. Is it running on port 9093?"); }
  try { json = await res.json(); } catch { /* empty body */ }
  if (!res.ok) throw new Error((json && (json.message || json.error)) || `Request failed with status ${res.status}`);
  return json && typeof json === "object" && "data" in json ? json.data : json;
}

/* ---------- modal + form helpers ---------- */
function showModal(title, body, width = "max-w-lg") {
  const root = document.getElementById("modalRoot");
  if (!root) return;
  root.innerHTML = `
    <div class="fixed inset-0 bg-black/40 z-[150] flex items-center justify-center p-4">
      <div class="bg-white rounded-xl shadow-xl w-full ${width} max-h-[90vh] overflow-y-auto animate-modal">
        <div class="flex items-center justify-between px-5 py-3 border-b border-gray-100">
          <h3 class="text-sm font-semibold text-gray-900">${E(title)}</h3>
          <button onclick="closeModal()" class="text-gray-400 hover:text-gray-600"><i class="fa-solid fa-xmark"></i></button>
        </div>${body}
      </div>
    </div>`;
}
function closeModal() {
  const root = document.getElementById("modalRoot");
  if (root) root.innerHTML = "";
  Object.values(ST).forEach((s) => (s.editingId = null));
}
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape" && document.getElementById("modalRoot")?.firstElementChild) closeModal();
});

/* In-page confirmation overlay (replaces the browser's confirm() alert box).
   onConfirm runs when the primary button is clicked; it is responsible for closing the overlay. */
function confirmModal({ title, message, label = "Confirm", danger = false, onConfirm }) {
  showModal(title, `
    <div class="p-5 text-xs text-gray-600">${E(message)}</div>
    <div class="flex justify-end gap-2 px-5 pb-5">
      <button type="button" onclick="closeModal()" class="px-3 py-1.5 rounded-lg border border-gray-200 text-gray-600 font-medium hover:bg-gray-50">Cancel</button>
      <button type="button" id="confirmOk" class="px-3 py-1.5 rounded-lg font-semibold text-white ${danger ? "bg-red-600 hover:bg-red-700" : "bg-primary hover:bg-primary-dark"}">${E(label)}</button>
    </div>`, "max-w-sm");
  const ok = document.getElementById("confirmOk");
  if (!ok) return;
  ok.onclick = async () => {
    ok.disabled = true;
    ok.textContent = "Please wait…";
    await onConfirm();
  };
}

const footer = (label, cls = "bg-primary text-white hover:bg-primary-dark") => `
  <div class="col-span-2 flex justify-end gap-2 pt-2">
    <button type="button" onclick="closeModal()" class="px-3 py-1.5 rounded-lg border border-gray-200 text-gray-600 font-medium hover:bg-gray-50">Cancel</button>
    <button type="submit" class="px-3 py-1.5 rounded-lg font-semibold ${cls}">${label}</button>
  </div>`;

/* field spec: n name, l label, t type, req, max, min, step, d default, o options ([[value,label]] or fn(src)),
   a raw attrs, full (span 2 cols), editOnly, dis (disabled when editing), blank (placeholder option), ph, ro (never sent) */
function fieldHtml(f, src, editing) {
  if (f.t === "info") return `<div id="${f.n}" class="col-span-2 form-hint"></div>`;
  const v = src ? src[f.n] : undefined;
  const req = f.req ? "required" : "";
  let inp;
  if (f.t === "select") {
    const opts = typeof f.o === "function" ? f.o(src) : f.o;
    inp = `<select name="${f.n}" class="form-input" ${req} ${f.dis && editing ? "disabled" : ""} ${f.a || ""}>
      ${f.blank ? `<option value="">${E(f.blank)}</option>` : ""}
      ${opts.map(([val, lab]) => `<option value="${E(val)}" ${(v ?? f.d) === val ? "selected" : ""}>${E(lab)}</option>`).join("")}</select>`;
  } else if (f.t === "textarea") {
    inp = `<textarea name="${f.n}" rows="3" maxlength="2000" class="form-input" placeholder="${E(f.ph || "")}">${E(v || "")}</textarea>`;
  } else {
    inp = `<input name="${f.n}" type="${f.t || "text"}" class="form-input" ${req} ${f.a || ""}
      ${f.max ? `maxlength="${f.max}"` : ""} ${f.min != null ? `min="${f.min}"` : ""} ${f.step ? `step="${f.step}"` : ""}
      placeholder="${E(f.ph || "")}" value="${E(v ?? f.d ?? "")}" />`;
  }
  return `<div class="${f.full || f.t === "textarea" ? "col-span-2" : ""}"><label class="form-label">${E(f.l)}${f.req ? " *" : ""}</label>${inp}</div>`;
}
const formFields = (fields, src, editing) => fields.filter((f) => !f.editOnly || editing).map((f) => fieldHtml(f, src, editing)).join("");

function readFields(fields, fd) {
  const p = {};
  fields.forEach((f) => {
    if (f.ro || f.t === "info") return;
    if (f.editOnly && !fd.has(f.n)) return;
    const raw = (fd.get(f.n) ?? "").toString();
    if (f.t === "number") p[f.n] = raw === "" ? undefined : Number(raw);
    else p[f.n] = (f.t === "date" ? raw : raw.trim()) || undefined;
  });
  return p;
}

/* =========================================================================
   MODULE CONFIG — one entry per tab; CRUD / filter / stats / CSV are shared
   ========================================================================= */
const ST = {
  pr: { all: [], filtered: [], loading: false, editingId: null },
  po: { all: [], filtered: [], loading: false, editingId: null },
  pb: { all: [], filtered: [], loading: false, editingId: null },
  grn: { all: [], filtered: [], loading: false, editingId: null },
};
const txt = (f) => (r) => E(r[f] || "-");
const dt = (f) => (r) => formatDate(r[f]);
const cur = (f) => (r) => formatCurrency(r[f]);
const ab = (cls, fn, title, icon) => `<button onclick="${fn}" class="btn-icon-action ${cls}" title="${title}"><i class="${icon}"></i></button>`;
const itemsSummary = (r) => {
  const it = r.items || [];
  return it.length ? `${E(it[0].name)}${it.length > 1 ? ` <span class="text-gray-400">+${it.length - 1} more</span>` : ""}` : "-";
};
const itemsCsv = (r) => (r.items || []).map((i) => `${i.name} x${i.qty} @${i.rate}`).join(" | ");

const poOptions = (statuses, keep) => (ST.po.all || [])
  .filter((o) => statuses.includes(lc(o.status)) || (keep && keep(o)))
  .map((o) => [o.poStrId, `${o.poNumber} — ${o.vendorName}`]);

const PO_HEAD = [
  { n: "vendorName", l: "Vendor Name", req: 1, max: 255, full: 1 },
  { n: "poDate", l: "PO Date", t: "date", d: today() }, { n: "deliveryDate", l: "Delivery Date", t: "date" },
  { n: "taxPct", l: "GST %", t: "number", min: 0, step: "0.01", d: 18, a: 'oninput="recalcPo()"' },
  { n: "status", l: "Status", t: "select", editOnly: 1, o: (s) => {
    // draft -> approved -> sent are chosen by hand. partial / fully received / closed are set by the server
    // (goods receipts, bills, payments), so they are shown but cannot be picked.
    const cur = lc(s?.status);
    if (["partial", "fully_received", "closed"].includes(cur)) return [[cur, cap(cur.replace("_", " ")) + " (automatic)"]];
    return [["draft", "Draft"], ["approved", "Approved"], ["sent", "Sent"]];
  } },
];

const PB_HEAD = [
  { n: "poNumber", l: "Purchase Order (goods must be received)", t: "select", full: 1, blank: "No PO (direct bill)", a: 'onchange="onBillPoChange(this.value)"',
    o: (src) => ST.po.all
      .filter((o) => BILLABLE_PO.includes(lc(o.status)) || (src && o.poNumber === src.poNumber))
      .map((o) => [o.poNumber, `${o.poNumber} — ${o.vendorName}`]) },
  { n: "vendorName", l: "Vendor Name", req: 1, max: 255, full: 1 },
  { n: "billDate", l: "Bill Date", t: "date", d: today() }, { n: "dueDate", l: "Due Date", t: "date" },
];

const MODULES = {
  /* ---------------- Purchase Requirement ---------------- */
  pr: {
    noun: "purchase requirement", panel: "prPanel", base: PR_API_BASE, id: "purchaseStrId", file: "purchase-requirements",
    list: "/get-all-purchases", create: "/create-purchase", update: "/update-purchase/", del: "/delete-purchase/",
    createLabel: "Create Requirement", editTitle: "Edit Purchase Requirement", newTitle: "New Purchase Requirement",
    emptyMsg: 'No purchase requirements yet. Click "New Requirement" to create one.',
    sort: ascBy("purchaseNumber"),
    search: ["purchaseNumber", "itemName", "requestedBy", "department"],
    filters: { "filterPriority-pr": "priority", "filterStatus-pr": "status" },
    cols: [["PR Number", txt("purchaseNumber")], ["Item", txt("itemName")], ["Qty", (r) => `${r.quantity ?? "-"} ${E(r.unit || "")}`],
      ["Requested By", txt("requestedBy")], ["Department", txt("department")], ["Required Date", dt("requiredDate")],
      ["Priority", (r) => priorityBadge(r.priority)], ["Status", (r) => statusBadge(r.status)],
      ["PO", (r) => (r.convertedPoNumber ? E(r.convertedPoNumber) : '<span class="text-gray-300">-</span>')]],
    fields: [
      { n: "itemName", l: "Item Name", req: 1, max: 255, full: 1 },
      { n: "quantity", l: "Quantity", t: "number", min: 1, req: 1 }, { n: "unit", l: "Unit", max: 30, ph: "Pcs" },
      { n: "requestedBy", l: "Requested By", req: 1, max: 255 }, { n: "department", l: "Department", max: 255 },
      { n: "requiredDate", l: "Required Date", t: "date" },
      { n: "priority", l: "Priority", t: "select", d: "medium", o: [["low", "Low"], ["medium", "Medium"], ["high", "High"]] },
      { n: "status", l: "Status", t: "select", editOnly: 1, full: 1, o: [["pending", "Pending"], ["approved", "Approved"], ["rejected", "Rejected"]] },
      { n: "remarks", l: "Remarks", t: "textarea" },
    ],
    actions: (r) => {
      const s = lc(r.status);
      if (s === "pending") return ab("success", `approvePR('${r.purchaseStrId}')`, "Approve", "fa-solid fa-check") + ab("danger", `openRejectPR('${r.purchaseStrId}')`, "Reject", "fa-solid fa-xmark");
      if (s === "approved" && !r.convertedPoNumber) return ab("success", `convertPRtoPO('${r.purchaseStrId}')`, "Convert to Purchase Order", "fa-solid fa-arrow-right");
      return "";
    },
    view: (r) => [["PR Number", r.purchaseNumber], ["Item", r.itemName], ["Quantity", `${r.quantity ?? "-"} ${r.unit || ""}`], ["Requested By", r.requestedBy],
      ["Department", r.department], ["Required Date", formatDate(r.requiredDate)], ["Priority", r.priority], ["Status", r.status], ["Remarks", r.remarks],
      ["Rejection Reason", r.rejectionReason], ["Converted PO", r.convertedPoNumber], ["Created", fmtTs(r.createdAt)], ["Updated", fmtTs(r.updatedAt)]],
    stats: (a) => ({ totalPR: a.length, pendingPR: count(a, "pending"), approvedPR: count(a, "approved"), rejectedPR: count(a, "rejected") }),
    csv: [["PR Number", "purchaseNumber"], ["Item Name", "itemName"], ["Quantity", "quantity"], ["Unit", "unit"], ["Requested By", "requestedBy"], ["Department", "department"],
      ["Required Date", "requiredDate"], ["Priority", "priority"], ["Status", "status"], ["Remarks", "remarks"], ["Rejection Reason", "rejectionReason"],
      ["Converted PO", "convertedPoNumber"], ["Created At", "createdAt"], ["Updated At", "updatedAt"]],
  },

  /* ---------------- Purchase Order (multi-item, GST) ---------------- */
  po: {
    noun: "purchase order", panel: "poPanel", base: PO_API_BASE, id: "poStrId", file: "purchase-orders",
    list: "/get-all-purchase-orders", create: "/create-purchase-order", update: "/update-purchase-order/", del: "/delete-purchase-order/",
    createLabel: "Create Order", editTitle: "Edit Purchase Order", newTitle: "New Purchase Order", width: "max-w-2xl",
    emptyMsg: 'No purchase orders yet. Click "New Purchase Order" to create one.',
    sort: ascBy("poNumber"),
    refresh: ["pr", "grn"], // converting / deleting a PO changes the source PR; editing items re-syncs receipts
    search: ["poNumber", "vendorName", "status"],
    searchExtra: (r) => (r.items || []).map((i) => i.name).join(" "),
    filters: { "filterStatus-po": "status" },
    cols: [["PO Number", txt("poNumber")], ["Vendor", txt("vendorName")], ["Items", itemsSummary], ["PO Date", dt("poDate")],
      ["Delivery", dt("deliveryDate")], ["Total", cur("total")], ["Status", (r) => statusBadge(r.status)]],
    fields: PO_HEAD,
    customForm: (src, editing) => `
      ${formFields(PO_HEAD, src, editing)}
      <input type="hidden" name="sourcePurchaseStrId" value="${E(src?.sourcePurchaseStrId || "")}" />
      <div class="col-span-2">
        <label class="form-label">Items *</label>
        <div class="line-item-header"><span>Item</span><span>Qty</span><span>Unit</span><span>Rate (₹)</span><span></span></div>
        <div id="poItems">${((src?.items && src.items.length) ? src.items : [{}]).map(poItemRow).join("")}</div>
        <button type="button" onclick="addPoItem()" class="text-primary font-semibold text-[11px] mt-1"><i class="fa-solid fa-plus"></i> Add item</button>
        <div class="po-totals" id="poTotals"></div>
      </div>`,
    afterOpen: () => recalcPo(),
    formNote: "Partial, fully received and closed are set automatically from goods receipts, bills and payments.",
    collect: (fd, form, ex) => {
      const p = readFields(PO_HEAD, fd);
      if (p.taxPct === undefined) p.taxPct = 0;
      const src = fd.get("sourcePurchaseStrId");
      if (src && !ex) p.sourcePurchaseStrId = src;
      p.items = [...form.querySelectorAll(".line-item-row")].map((r) => ({
        name: r.querySelector(".li-name").value.trim(),
        qty: Number(r.querySelector(".li-qty").value),
        unit: r.querySelector(".li-unit").value.trim() || undefined,
        rate: Number(r.querySelector(".li-rate").value),
      }));
      p.items.forEach((it, i) => {
        if (!it.name) throw new Error(`Item ${i + 1}: enter an item name.`);
        if (!(it.qty > 0)) throw new Error(`Item ${i + 1}: quantity must be greater than 0.`);
        if (!(it.rate > 0)) throw new Error(`Item ${i + 1}: enter a rate greater than 0.`);
      });
      return p;
    },
    actions: (r) => {
      const s = lc(r.status), id = r.poStrId;
      let h = "";
      if (s === "draft") h += ab("success", `patchPOStatus('${id}','approved')`, "Approve PO", "fa-solid fa-check");
      if (s === "approved") h += ab("success", `patchPOStatus('${id}','sent')`, "Mark as Sent to vendor", "fa-regular fa-paper-plane");
      if (RECEIVABLE_PO.includes(s)) h += ab("success", `receiveGoodsForPO('${id}')`, "Receive goods (GRN)", "fa-solid fa-boxes-packing");
      if (BILLABLE_PO.includes(s)) h += ab("success", `billForPO('${id}')`, "Create purchase bill", "fa-solid fa-file-invoice");
      return h;
    },
    view: (r) => [["PO Number", r.poNumber], ["Vendor", r.vendorName], ["PO Date", formatDate(r.poDate)], ["Delivery Date", formatDate(r.deliveryDate)],
      ["Status", r.status], ["Source PR", r.sourcePurchaseStrId],
      ...(r.items || []).map((i, n) => [`Item ${n + 1}`, `${i.name} — ${i.qty} ${i.unit || ""} × ${formatCurrency(i.rate)} = ${formatCurrency(i.amount)}`]),
      ["Subtotal", formatCurrency(r.subtotal)], [`GST (${r.taxPct ?? 0}%)`, formatCurrency(r.taxAmount)], ["Total", formatCurrency(r.total)],
      ["Created", fmtTs(r.createdAt)], ["Updated", fmtTs(r.updatedAt)]],
    stats: (a) => ({ totalPO: a.length, draftPO: count(a, "draft"), openPO: count(a, "sent", "partial"), receivedPO: count(a, "fully_received"),
      totalValuePO: formatCurrency(sum(a, (o) => o.total)) }),
    csv: [["PO Number", "poNumber"], ["Vendor", "vendorName"], ["Items", itemsCsv], ["PO Date", "poDate"], ["Delivery Date", "deliveryDate"], ["GST %", "taxPct"],
      ["Subtotal", "subtotal"], ["GST Amount", "taxAmount"], ["Total", "total"], ["Status", "status"], ["Source PR", "sourcePurchaseStrId"], ["Created At", "createdAt"]],
  },

  /* ---------------- Purchase Bill (items + GST; total and status are calculated server-side) ---------------- */
  pb: {
    noun: "purchase bill", panel: "pbPanel", base: PB_API_BASE, id: "pbStrId", file: "purchase-bills",
    list: "/get-all-purchase-bills", create: "/create-purchase-bill", update: "/update-purchase-bill/", del: "/delete-purchase-bill/",
    createLabel: "Create Bill", editTitle: "Edit Purchase Bill", newTitle: "New Purchase Bill", width: "max-w-2xl",
    emptyMsg: 'No purchase bills yet. Click "New Purchase Bill" to create one.',
    sort: ascBy("pbNumber"),
    refresh: ["po"],
    search: ["pbNumber", "vendorName", "poNumber"],
    searchExtra: (r) => (r.items || []).map((i) => i.name).join(" "),
    filters: { "filterStatus-pb": "status" },
    cols: [["Bill Number", txt("pbNumber")], ["Vendor", txt("vendorName")], ["PO", txt("poNumber")], ["Items", itemsSummary], ["Bill Date", dt("billDate")], ["Due Date", dt("dueDate")],
      ["Total", cur("amount")], ["Paid", cur("paidAmount")], ["Balance", cur("balance")], ["Status", (r) => statusBadge(r.status)]],
    fields: PB_HEAD,
    customForm: (src, editing) => `
      ${formFields(PB_HEAD, src, editing)}
      <div class="col-span-2">
        <label class="form-label">Items *</label>
        <div class="line-item-header pb-row"><span>Item</span><span>Qty</span><span>Unit</span><span>Rate (₹)</span><span>GST %</span><span></span></div>
        <div id="pbItems">${((src?.items && src.items.length) ? src.items : [{}]).map(pbItemRow).join("")}</div>
        <button type="button" onclick="addPbItem()" class="text-primary font-semibold text-[11px] mt-1"><i class="fa-solid fa-plus"></i> Add item</button>
        <div id="pbInfo" class="form-hint mt-1"></div>
        <div class="po-totals" id="pbTotals"></div>
      </div>`,
    formNote: "Total and status are calculated automatically from the items, payments and due date.",
    beforeOpen: async () => { if (!ST.po.all.length) await loadModule("po"); },
    afterOpen: (src, editing) => {
      recalcPb();
      if (editing && !(src?.items && src.items.length)) {
        const info = document.getElementById("pbInfo");
        if (info) info.textContent = `This bill was created before item details were recorded (stored total ${formatCurrency(src.amount)}). Enter its items to save changes.`;
      }
    },
    collect: (fd, form, ex) => {
      const p = readFields(PB_HEAD, fd);
      p.items = [...form.querySelectorAll("#pbItems .line-item-row")].map((r) => ({
        name: r.querySelector(".li-name").value.trim(),
        qty: Number(r.querySelector(".li-qty").value),
        unit: r.querySelector(".li-unit").value.trim() || undefined,
        rate: Number(r.querySelector(".li-rate").value),
        gstPct: Number(r.querySelector(".li-gst").value) || 0,
      }));
      p.items.forEach((it, i) => {
        if (!it.name) throw new Error(`Item ${i + 1}: enter an item name.`);
        if (!(it.qty > 0) || !Number.isInteger(it.qty)) throw new Error(`Item ${i + 1}: quantity must be a whole number greater than 0.`);
        if (!(it.rate > 0)) throw new Error(`Item ${i + 1}: enter a rate greater than 0.`);
        if (it.gstPct < 0 || it.gstPct > 100) throw new Error(`Item ${i + 1}: GST must be between 0 and 100.`);
      });
      if (p.billDate && p.dueDate && p.dueDate < p.billDate) throw new Error("Due date cannot be before the bill date.");
      return p;
    },
    actions: (r) => {
      let h = "";
      if (Number(r.balance) > 0) h += ab("success", `openRecordPaymentPB('${r.pbStrId}')`, "Record Payment", "fa-solid fa-indian-rupee-sign");
      if ((r.payments || []).length) h += ab("", `openPaymentsPB('${r.pbStrId}')`, "View / delete payments", "fa-solid fa-receipt");
      return h;
    },
    view: (r) => [["Bill Number", r.pbNumber], ["Vendor", r.vendorName], ["PO Number", r.poNumber], ["Bill Date", formatDate(r.billDate)], ["Due Date", formatDate(r.dueDate)],
      ...(r.items || []).map((i, n) => [`Item ${n + 1}`, `${i.name} — ${i.qty} ${i.unit || ""} × ${formatCurrency(i.rate)} + GST ${i.gstPct ?? 0}% = ${formatCurrency(i.total)}`]),
      ["Subtotal", formatCurrency(r.subtotal)], ["GST", formatCurrency(r.gstAmount)], ["Total", formatCurrency(r.amount)],
      ["Paid", formatCurrency(r.paidAmount)], ["Balance", formatCurrency(r.balance)], ["Status", r.status],
      ...(r.payments || []).map((p, i) => [`Payment ${i + 1}`, `${formatDate(p.date)} · ${formatCurrency(p.amount)} · ${(p.mode || "-").replace("_", " ")}${p.ref ? " · " + p.ref : ""}`]),
      ["Created", fmtTs(r.createdAt)], ["Updated", fmtTs(r.updatedAt)]],
    stats: (a) => ({ totalPB: a.length, unpaidPB: count(a, "received", "partial"), overduePB: count(a, "overdue"), paidPB: count(a, "paid"),
      totalPayable: formatCurrency(sum(a, (b) => b.balance)) }),
    csv: [["Bill Number", "pbNumber"], ["Vendor", "vendorName"], ["PO Number", "poNumber"], ["Items", itemsCsv], ["Bill Date", "billDate"], ["Due Date", "dueDate"],
      ["Subtotal", "subtotal"], ["GST", "gstAmount"], ["Total", "amount"], ["Paid", "paidAmount"], ["Balance", "balance"], ["Status", "status"], ["Created At", "createdAt"]],
  },

  /* ---------------- Goods Receipt (one PO item per receipt) ----------------
     No client-side validation on this form for now: no required / min / max attributes,
     the browser's form check is switched off (noValidate) and nothing is checked before saving.
     The server's own response is still shown as a toast if it rejects the data. */
  grn: {
    noun: "goods receipt", panel: "grnPanel", base: GRN_API_BASE, id: "grnStrId", file: "goods-receipts",
    list: "/get-all-goods-receipts", create: "/create-goods-receipt", update: "/update-goods-receipt/", del: "/delete-goods-receipt/",
    createLabel: "Create Receipt", editTitle: "Edit Goods Receipt", newTitle: "New Goods Receipt",
    emptyMsg: 'No goods receipts yet. Click "New Goods Receipt" to record a delivery against a PO.',
    noValidate: true,
    refresh: ["po"], // PO status (partial / fully_received) is recalculated by the server
    search: ["grnNumber", "poNumber", "vendorName", "itemName", "challanNumber"],
    filters: { "filterStatus-grn": "status" },
    cols: [["GRN No.", txt("grnNumber")], ["PO", txt("poNumber")], ["Vendor", txt("vendorName")], ["Item", txt("itemName")],
      ["Received / Ordered", (r) => `${r.receivedQty ?? "-"} / ${r.orderedQty ?? "-"} ${E(r.unit || "")}`],
      ["Rejected", (r) => `<span class="text-red-600">${r.rejectedQty ?? 0}</span>`], ["Date", dt("receivedDate")], ["Status", (r) => statusBadge(r.status)]],
    fields: [
      { n: "poStrId", l: "Purchase Order", t: "select", full: 1, dis: 1, blank: "Select a PO…", a: 'onchange="onGRNPoChange(this.value)"',
        o: (src) => poOptions(RECEIVABLE_PO, (o) => src && o.poStrId === src.poStrId) },
      { n: "itemName", l: "Item", t: "select", full: 1, dis: 1, blank: "Select item…", a: 'id="grnItem" onchange="onGRNItemChange()"', o: [] },
      { n: "grnInfo", t: "info" },
      { n: "receivedQty", l: "Received Qty", t: "number", a: 'id="grnRecv"' },
      { n: "rejectedQty", l: "Rejected Qty", t: "number", d: 0 },
      { n: "receivedDate", l: "Received Date", t: "date", d: today() }, { n: "challanNumber", l: "Challan / Invoice No." },
      { n: "receivedBy", l: "Received By", full: 1 },
      { n: "remarks", l: "Remarks (damage, shortage…)", t: "textarea" },
    ],
    formNote: "PO and item can't be changed after saving. Status (partial / completed) is set automatically by the server.",
    beforeOpen: async () => {
      await loadModule("po"); // always fetch fresh, so a PO approved a moment ago shows up
      if (!ST.grn.all.length && !ST.grn.loading) await loadModule("grn");
    },
    afterOpen: (src, editing) => {
      if (src?.poStrId) { onGRNPoChange(src.poStrId, editing ? src.itemName : undefined); return; }
      // Explain an empty PO dropdown instead of leaving it silently blank.
      const sel = document.querySelector('#modalRoot [name="poStrId"]'), info = document.getElementById("grnInfo");
      if (sel && info && sel.options.length <= 1) {
        info.classList.add("text-red-600");
        info.textContent = ST.po.all.length
          ? `No PO is open for receiving. ${ST.po.all.length} PO(s) exist, but none is Approved, Sent or Partial. Approve a Draft PO in the Purchase Orders tab first.`
          : "No purchase orders found. Create a PO (Purchase Orders tab), approve it, then record the receipt. If you already have POs, check the server is running on port 9093.";
      }
    },
    prep: (p, ex) => {
      // Disabled selects aren't submitted when editing, so carry the original PO and item over. No checks.
      if (ex) { p.poStrId = ex.poStrId; p.itemName = ex.itemName; }
      p.rejectedQty = p.rejectedQty || 0;
    },
    actions: () => "",
    view: (r) => [["GRN Number", r.grnNumber], ["PO Number", r.poNumber], ["Vendor", r.vendorName], ["Item", r.itemName],
      ["Ordered", `${r.orderedQty ?? "-"} ${r.unit || ""}`], ["Received", r.receivedQty], ["Accepted", r.acceptedQty], ["Rejected", r.rejectedQty],
      ["Received Date", formatDate(r.receivedDate)], ["Challan No.", r.challanNumber], ["Received By", r.receivedBy], ["Status", r.status],
      ["Remarks", r.remarks], ["Created", fmtTs(r.createdAt)]],
    stats: (a) => ({ totalGRN: a.length, partialGRN: count(a, "partial"), completedGRN: count(a, "completed"), rejectedQtyGRN: sum(a, (g) => g.rejectedQty) }),
    csv: [["GRN Number", "grnNumber"], ["PO Number", "poNumber"], ["Vendor", "vendorName"], ["Item", "itemName"], ["Unit", "unit"], ["Ordered", "orderedQty"],
      ["Received", "receivedQty"], ["Accepted", "acceptedQty"], ["Rejected", "rejectedQty"], ["Received Date", "receivedDate"], ["Challan", "challanNumber"],
      ["Received By", "receivedBy"], ["Status", "status"], ["Remarks", "remarks"]],
  },
};

/* =========================================================================
   Generic engine
   ========================================================================= */
async function loadModule(key) {
  const M = MODULES[key], S = ST[key];
  S.loading = true;
  const panel = document.getElementById(M.panel);
  if (panel) panel.innerHTML = emptyModulePanel(`Loading ${M.noun}s…`);
  try {
    const data = await apiRequest("GET", M.base, M.list);
    S.all = Array.isArray(data) ? data : [];
    if (M.sort) S.all.sort(M.sort); // PR / PO / PB: oldest first
  } catch (err) {
    showToast(err.message || `Failed to load ${M.noun}s`, "error");
    S.all = [];
  } finally {
    S.loading = false;
  }
  applyFilters(key);
  Object.entries(M.stats(S.all)).forEach(([id, v]) => setText(id, v));
}
const reload = (key) => Promise.all([loadModule(key), ...(MODULES[key].refresh || []).map(loadModule)]);

function applyFilters(key) {
  const M = MODULES[key], S = ST[key];
  const q = (document.getElementById(`search-${key}`)?.value || "").trim().toLowerCase();
  const active = Object.entries(M.filters).map(([el, field]) => [field, document.getElementById(el)?.value || "all"]);
  S.filtered = S.all.filter((r) => {
    const hay = M.search.map((f) => lc(r[f])).join(" ") + " " + lc(M.searchExtra ? M.searchExtra(r) : "");
    return (!q || hay.includes(q)) && active.every(([f, v]) => v === "all" || lc(r[f]) === v);
  });
  renderTable(key);
}

function clearFilters(key) {
  const s = document.getElementById(`search-${key}`);
  if (s) s.value = "";
  Object.keys(MODULES[key].filters).forEach((el) => { const e = document.getElementById(el); if (e) e.value = "all"; });
  applyFilters(key);
}

/* Full grid borders: outer green frame (same as the stat cards) + a border on every header and cell. */
function renderTable(key) {
  const M = MODULES[key], S = ST[key];
  const panel = document.getElementById(M.panel);
  if (!panel) return;
  if (S.filtered.length === 0) {
    panel.innerHTML = emptyModulePanel(S.all.length === 0 ? M.emptyMsg : `No ${M.noun}s match your search / filters.`);
    return;
  }
  const rows = S.filtered.map((r) => {
    const id = r[M.id];
    return `<tr class="hover:bg-gray-50 transition">
      ${M.cols.map(([, fn], i) => `<td class="px-3 py-2 border border-gray-300 ${i === 0 ? "font-medium text-gray-900" : ""}">${fn(r)}</td>`).join("")}
      <td class="px-3 py-2 border border-gray-300"><div class="flex items-center gap-1.5 flex-wrap">
        ${ab("", `viewRecord('${key}','${id}')`, "View", "fa-regular fa-eye")}
        ${ab("", `openAdd('${key}','${id}')`, "Edit", "fa-regular fa-pen-to-square")}
        ${M.actions(r)}
        ${ab("danger", `deleteRecord('${key}','${id}')`, "Delete", "fa-regular fa-trash-can")}
      </div></td></tr>`;
  }).join("");
  panel.innerHTML = `
    <div class="bg-white border border-[#6da84c] rounded-xl shadow-sm overflow-hidden"><div class="overflow-x-auto">
      <table class="w-full text-xs border-collapse"><thead><tr class="bg-gray-50 text-gray-600 uppercase text-[10px] tracking-wide">
        ${M.cols.map(([h]) => `<th class="px-3 py-2 text-left border border-gray-300">${h}</th>`).join("")}<th class="px-3 py-2 text-left border border-gray-300">Actions</th>
      </tr></thead><tbody>${rows}</tbody></table></div></div>`;
}

/* id = record being edited; pre = prefilled values for a NEW record (e.g. PO from a PR) */
async function openAdd(key, id, pre) {
  const M = MODULES[key], S = ST[key];
  if (M.beforeOpen) await M.beforeOpen();
  S.editingId = id || null;
  const ex = id ? S.all.find((r) => r[M.id] === id) : null;
  const src = ex || pre || null, editing = !!ex;
  showModal(editing ? M.editTitle : M.newTitle, `
    <form class="p-5 grid grid-cols-2 gap-3 text-xs" ${M.noValidate ? "novalidate" : ""} onsubmit="submitForm(event,'${key}')">
      ${M.customForm ? M.customForm(src, editing) : formFields(M.fields, src, editing)}
      ${editing && M.formNote ? `<p class="col-span-2 form-hint">${E(M.formNote)}</p>` : ""}
      ${footer(editing ? "Save Changes" : M.createLabel)}
    </form>`, M.width);
  if (M.afterOpen) M.afterOpen(src, editing);
}

async function submitForm(evt, key) {
  evt.preventDefault();
  const M = MODULES[key], S = ST[key], form = evt.target;
  const ex = S.editingId ? S.all.find((r) => r[M.id] === S.editingId) : null;
  const fd = new FormData(form);
  let payload;
  try {
    payload = M.collect ? M.collect(fd, form, ex) : readFields(M.fields, fd);
    if (M.prep) M.prep(payload, ex);
  } catch (e) { return showToast(e.message, "error"); }

  const btn = form.querySelector('button[type="submit"]');
  const label = btn ? btn.textContent : "";
  if (btn) { btn.disabled = true; btn.textContent = "Saving…"; }
  try {
    if (S.editingId) await apiRequest("PUT", M.base, `${M.update}${encodeURIComponent(S.editingId)}`, payload);
    else await apiRequest("POST", M.base, M.create, payload);
    showToast(`${cap(M.noun)} ${S.editingId ? "updated" : "created"} successfully.`, "success");
    closeModal();
    await reload(key);
  } catch (err) {
    showToast(err.message || `Failed to save ${M.noun}`, "error");
    if (btn) { btn.disabled = false; btn.textContent = label; }
  }
}

function viewRecord(key, id) {
  const M = MODULES[key], r = ST[key].all.find((x) => x[M.id] === id);
  if (!r) return;
  showModal(`${cap(M.noun)} Details`, `<div class="p-5 text-xs space-y-2">${M.view(r).map(([l, v]) => `
    <div class="flex justify-between gap-3 py-1 border-b border-gray-50">
      <span class="text-gray-400">${E(l)}</span><span class="text-gray-800 font-medium text-right">${E(String(v === undefined || v === null || v === "" ? "-" : v))}</span>
    </div>`).join("")}</div>`, "max-w-md");
}

/* Delete (PR, PO, PB, GRN): confirmation overlay instead of the browser alert */
function deleteRecord(key, id) {
  const M = MODULES[key], r = ST[key].all.find((x) => x[M.id] === id);
  const ref = r ? (r.purchaseNumber || r.poNumber || r.pbNumber || r.grnNumber || "") : "";
  confirmModal({
    title: `Delete ${cap(M.noun)}`,
    message: `Delete ${M.noun}${ref ? " " + ref : ""}? This cannot be undone.`,
    label: "Delete",
    danger: true,
    onConfirm: async () => {
      try {
        await apiRequest("DELETE", M.base, `${M.del}${encodeURIComponent(id)}`);
        closeModal();
        showToast(`${cap(M.noun)} deleted.`, "success");
        await reload(key);
      } catch (err) {
        closeModal();
        showToast(err.message || `Failed to delete ${M.noun}`, "error");
      }
    },
  });
}

function exportModule(key) {
  const M = MODULES[key], S = ST[key];
  if (S.filtered.length === 0) return showToast("Nothing to export.", "error");
  const lines = [M.csv.map(([h]) => h).join(",")];
  S.filtered.forEach((r) => lines.push(M.csv.map(([, f]) => csvEscape(typeof f === "function" ? f(r) : r[f])).join(",")));
  downloadCsv(lines, M.file);
}

/* Names used by the HTML (oninput / onclick) */
Object.keys(MODULES).forEach((key) => {
  const K = key.toUpperCase();
  window[`render${K}`] = () => applyFilters(key);
  window[`filter${K}`] = () => applyFilters(key);
  window[`clearFilters${K}`] = () => clearFilters(key);
  window[`export${K}`] = () => exportModule(key);
  window[`openAdd${K}`] = (id) => openAdd(key, id);
});

/* =========================================================================
   Tabs + bootstrap
   ========================================================================= */
function switchModuleTab(module) {
  document.querySelectorAll(".module-tab-btn").forEach((b) => b.classList.toggle("active", b.dataset.module === module));
  document.querySelectorAll(".module-tab-panel").forEach((p) => p.classList.toggle("hidden", p.dataset.module !== module));
  if (ST[module] && ST[module].all.length === 0 && !ST[module].loading) loadModule(module);
}
document.addEventListener("DOMContentLoaded", () => loadModule("pr"));

/* =========================================================================
   PR: approve / reject / convert to PO
   ========================================================================= */
function approvePR(id) {
  const pr = ST.pr.all.find((p) => p.purchaseStrId === id);
  confirmModal({
    title: "Approve Requirement",
    message: `Approve purchase requirement${pr ? " " + pr.purchaseNumber : ""}?`,
    label: "Approve",
    onConfirm: async () => {
      try {
        await apiRequest("PATCH", PR_API_BASE, `/approve-purchase/${encodeURIComponent(id)}`);
        closeModal();
        showToast("Purchase requirement approved.", "success");
        await loadModule("pr");
      } catch (err) {
        closeModal();
        showToast(err.message || "Failed to approve requirement", "error");
      }
    },
  });
}

function openRejectPR(id) {
  showModal("Reject Requirement", `
    <form class="p-5 grid grid-cols-2 gap-3 text-xs" onsubmit="submitRejectPR(event,'${id}')">
      ${fieldHtml({ n: "reason", l: "Reason (optional)", t: "textarea", ph: "Why is this being rejected?" })}
      ${footer("Reject", "bg-red-600 text-white hover:bg-red-700")}
    </form>`, "max-w-sm");
}
async function submitRejectPR(evt, id) {
  evt.preventDefault();
  const reason = ((new FormData(evt.target).get("reason")) || "").toString().trim() || undefined;
  try {
    await apiRequest("PATCH", PR_API_BASE, `/reject-purchase/${encodeURIComponent(id)}`, { reason });
    showToast("Purchase requirement rejected.", "success");
    closeModal();
    await loadModule("pr");
  } catch (err) { showToast(err.message || "Failed to reject requirement", "error"); }
}

/* Opens the PO form pre-filled from an approved PR (backend links it via sourcePurchaseStrId) */
function convertPRtoPO(id) {
  const pr = ST.pr.all.find((p) => p.purchaseStrId === id);
  if (!pr) return;
  switchModuleTab("po");
  openAdd("po", null, {
    sourcePurchaseStrId: pr.purchaseStrId,
    deliveryDate: pr.requiredDate,
    items: [{ name: pr.itemName, qty: pr.quantity, unit: pr.unit, rate: "" }],
  });
}

/* =========================================================================
   PO: line-item builder, status patch, shortcuts to GRN / bill
   ========================================================================= */
function poItemRow(it = {}) {
  return `<div class="line-item-row">
    <input class="form-input li-name" maxlength="255" placeholder="Item name" value="${E(it.name ?? "")}" oninput="recalcPo()" />
    <input class="form-input li-qty" type="number" min="1" placeholder="Qty" value="${E(it.qty ?? "")}" oninput="recalcPo()" />
    <input class="form-input li-unit" maxlength="30" placeholder="Pcs" value="${E(it.unit ?? "")}" />
    <input class="form-input li-rate" type="number" min="0.01" step="0.01" placeholder="Rate" value="${E(it.rate ?? "")}" oninput="recalcPo()" />
    <button type="button" class="btn-icon-action danger" title="Remove" onclick="removePoItem(this)"><i class="fa-solid fa-xmark"></i></button>
  </div>`;
}
function addPoItem() { document.getElementById("poItems")?.insertAdjacentHTML("beforeend", poItemRow()); }
function removePoItem(btn) {
  const rows = document.querySelectorAll("#poItems .line-item-row");
  if (rows.length > 1) { btn.closest(".line-item-row").remove(); recalcPo(); }
}
function recalcPo() {
  const box = document.getElementById("poTotals");
  if (!box) return;
  let subtotal = 0;
  document.querySelectorAll("#poItems .line-item-row").forEach((r) => {
    subtotal += (Number(r.querySelector(".li-qty").value) || 0) * (Number(r.querySelector(".li-rate").value) || 0);
  });
  const pct = Number(document.querySelector('#modalRoot [name="taxPct"]')?.value) || 0;
  const tax = round2(subtotal * pct / 100);
  box.innerHTML = `<div class="flex justify-between"><span>Subtotal</span><span>${formatCurrency(round2(subtotal))}</span></div>
    <div class="flex justify-between"><span>GST (${pct}%)</span><span>${formatCurrency(tax)}</span></div>
    <div class="flex justify-between font-bold text-gray-900"><span>Total</span><span>${formatCurrency(round2(subtotal + tax))}</span></div>`;
}

async function patchPOStatus(id, status) {
  try {
    await apiRequest("PATCH", PO_API_BASE, `/patch-purchase-order/${encodeURIComponent(id)}`, { status });
    showToast(`Purchase order marked ${status}.`, "success");
    await loadModule("po");
  } catch (err) { showToast(err.message || "Failed to update purchase order", "error"); }
}

function receiveGoodsForPO(poStrId) {
  switchModuleTab("grn");
  openAdd("grn", null, { poStrId });
}
async function billForPO(poStrId) {
  const po = ST.po.all.find((o) => o.poStrId === poStrId);
  if (!po) return;
  let items;
  try { items = await apiRequest("GET", PB_API_BASE, `/billable-items/${encodeURIComponent(po.poNumber)}`); }
  catch (err) { return showToast(err.message || "Could not load billable items", "error"); }
  if (!items.length) return showToast("Everything received on this PO has already been billed.", "error");
  switchModuleTab("pb");
  openAdd("pb", null, { poNumber: po.poNumber, vendorName: po.vendorName, items });
}

/* =========================================================================
   PB: item rows, live totals, PO picker pre-fills billable items, payments
   ========================================================================= */
function pbItemRow(it = {}) {
  return `<div class="line-item-row pb-row">
    <input class="form-input li-name" maxlength="255" placeholder="Item name" value="${E(it.name ?? "")}" oninput="recalcPb()" />
    <input class="form-input li-qty" type="number" min="1" step="1" placeholder="Qty" value="${E(it.qty ?? "")}" oninput="recalcPb()" />
    <input class="form-input li-unit" maxlength="30" placeholder="Pcs" value="${E(it.unit ?? "")}" />
    <input class="form-input li-rate" type="number" min="0.01" step="0.01" placeholder="Rate" value="${E(it.rate ?? "")}" oninput="recalcPb()" />
    <input class="form-input li-gst" type="number" min="0" max="100" step="0.01" placeholder="GST %" value="${E(it.gstPct ?? 18)}" oninput="recalcPb()" />
    <button type="button" class="btn-icon-action danger" title="Remove" onclick="removePbItem(this)"><i class="fa-solid fa-xmark"></i></button>
  </div>`;
}
function addPbItem() { document.getElementById("pbItems")?.insertAdjacentHTML("beforeend", pbItemRow()); }
function removePbItem(btn) {
  if (document.querySelectorAll("#pbItems .line-item-row").length > 1) { btn.closest(".line-item-row").remove(); recalcPb(); }
}
function recalcPb() {
  const box = document.getElementById("pbTotals");
  if (!box) return;
  let subtotal = 0, gst = 0;
  document.querySelectorAll("#pbItems .line-item-row").forEach((r) => {
    const amount = round2((Number(r.querySelector(".li-qty").value) || 0) * (Number(r.querySelector(".li-rate").value) || 0));
    subtotal += amount;
    gst += round2(amount * (Number(r.querySelector(".li-gst").value) || 0) / 100);
  });
  box.innerHTML = `<div class="flex justify-between"><span>Subtotal</span><span>${formatCurrency(round2(subtotal))}</span></div>
    <div class="flex justify-between"><span>GST</span><span>${formatCurrency(round2(gst))}</span></div>
    <div class="flex justify-between font-bold text-gray-900"><span>Total</span><span>${formatCurrency(round2(subtotal + gst))}</span></div>`;
}

/* Choosing a PO fills the vendor and replaces the rows with what can still be billed (accepted - already billed). */
async function onBillPoChange(poNumber) {
  const info = document.getElementById("pbInfo");
  if (!poNumber) { if (info) info.textContent = ""; return; }
  const po = ST.po.all.find((o) => o.poNumber === poNumber);
  const v = document.querySelector('#modalRoot [name="vendorName"]');
  if (po && v) v.value = po.vendorName;
  try {
    const q = ST.pb.editingId ? `?excludePbStrId=${encodeURIComponent(ST.pb.editingId)}` : "";
    const items = await apiRequest("GET", PB_API_BASE, `/billable-items/${encodeURIComponent(poNumber)}${q}`);
    const box = document.getElementById("pbItems");
    if (box) box.innerHTML = (items.length ? items : [{}]).map(pbItemRow).join("");
    if (info) info.textContent = items.length
      ? "Billable now: " + items.map((i) => `${i.name} ${i.qty} ${i.unit || ""}`.trim()).join(", ") + ". You cannot bill more than the accepted quantity."
      : "Everything received on this PO has already been billed.";
    recalcPb();
  } catch (err) { showToast(err.message || "Could not load billable items", "error"); }
}

function openPaymentsPB(id) {
  const b = ST.pb.all.find((x) => x.pbStrId === id);
  if (!b) return;
  const rows = (b.payments || []).map((p) => `
    <div class="flex items-center justify-between gap-3 py-2 border-b border-gray-50">
      <div><div class="font-semibold text-gray-800">${formatCurrency(p.amount)}</div>
        <div class="text-gray-400">${formatDate(p.date)} · ${E((p.mode || "-").replace("_", " "))}${p.ref ? " · " + E(p.ref) : ""}</div></div>
      <button class="btn-icon-action danger" title="Delete payment" onclick="deletePaymentPB('${id}', ${Number(p.paymentId)})"><i class="fa-regular fa-trash-can"></i></button>
    </div>`).join("");
  showModal(`Payments — ${b.pbNumber || ""}`, `<div class="p-5 text-xs">${rows || '<p class="text-gray-400">No payments recorded.</p>'}</div>`, "max-w-sm");
}
function deletePaymentPB(id, paymentId) {
  confirmModal({
    title: "Delete Payment",
    message: "Delete this payment? The bill balance will go up again.",
    label: "Delete",
    danger: true,
    onConfirm: async () => {
      try {
        await apiRequest("DELETE", PB_API_BASE, `/delete-payment/${encodeURIComponent(id)}/${paymentId}`);
        closeModal();
        showToast("Payment deleted.", "success");
        await reload("pb"); // the PO may re-open
      } catch (err) {
        closeModal();
        showToast(err.message || "Failed to delete payment", "error");
      }
    },
  });
}

const payFields = (b) => [
  { n: "amount", l: "Amount", t: "number", min: 0.01, step: "0.01", req: 1, full: 1, d: b ? b.balance : undefined },
  { n: "date", l: "Payment Date", t: "date", req: 1, d: today() },
  { n: "mode", l: "Payment Mode", t: "select", o: [["bank_transfer", "Bank Transfer"], ["cheque", "Cheque"], ["cash", "Cash"], ["upi", "UPI"], ["card", "Card"]] },
  { n: "ref", l: "Reference / UTR", max: 100, full: 1 },
];
function openRecordPaymentPB(id) {
  const b = ST.pb.all.find((x) => x.pbStrId === id);
  if (!b) return;
  showModal(`Record Payment — ${b.pbNumber || ""}`, `
    <form class="p-5 grid grid-cols-2 gap-3 text-xs" onsubmit="submitRecordPaymentPB(event,'${id}')">
      <p class="col-span-2 form-hint">Outstanding balance: ${formatCurrency(b.balance)}</p>
      ${formFields(payFields(b))}${footer("Record Payment")}
    </form>`, "max-w-sm");
}
async function submitRecordPaymentPB(evt, id) {
  evt.preventDefault();
  try {
    await apiRequest("POST", PB_API_BASE, `/record-payment/${encodeURIComponent(id)}`, readFields(payFields(), new FormData(evt.target)));
    showToast("Payment recorded successfully.", "success");
    closeModal();
    await reload("pb"); // a fully paid bill can close its PO
  } catch (err) { showToast(err.message || "Failed to record payment", "error"); }
}

/* =========================================================================
   GRN helpers: item picker per PO, remaining qty per item (shown as a hint only)
   ========================================================================= */
function grnRemaining(po, itemName, excludeGrnId) {
  const item = (po.items || []).find((i) => lc(i.name) === lc(itemName));
  if (!item) return 0;
  const received = ST.grn.all
    .filter((g) => g.poStrId === po.poStrId && lc(g.itemName) === lc(itemName) && g.grnStrId !== excludeGrnId)
    .reduce((s, g) => s + (Number(g.receivedQty) || 0), 0);
  return Math.max(0, (Number(item.qty) || 0) - received);
}

function onGRNPoChange(poId, selectedItem) {
  const sel = document.getElementById("grnItem");
  const po = ST.po.all.find((o) => o.poStrId === poId);
  if (!sel) return;
  const items = po ? po.items || [] : [];
  sel.innerHTML = `<option value="">Select item…</option>` +
    items.map((i) => `<option value="${E(i.name)}">${E(i.name)} (${i.qty} ${E(i.unit || "")})</option>`).join("");
  if (selectedItem) sel.value = selectedItem;
  else if (items.length === 1) sel.value = items[0].name;
  onGRNItemChange();
}

function onGRNItemChange() {
  const info = document.getElementById("grnInfo");
  const poSel = document.querySelector('#modalRoot [name="poStrId"]');
  const itemSel = document.getElementById("grnItem");
  const poId = poSel?.value || ST.grn.all.find((g) => g.grnStrId === ST.grn.editingId)?.poStrId;
  const po = ST.po.all.find((o) => o.poStrId === poId);
  if (!info) return;
  if (!po || !itemSel?.value) { info.textContent = po ? `Vendor: ${po.vendorName}` : ""; return; }
  const item = po.items.find((i) => lc(i.name) === lc(itemSel.value));
  const remaining = grnRemaining(po, itemSel.value, ST.grn.editingId);
  info.textContent = `${po.vendorName} · Ordered: ${item.qty} ${item.unit || ""} · Remaining to receive: ${remaining}`;
}