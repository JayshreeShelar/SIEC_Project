// =============================================================
// PURCHASE MANAGEMENT MODULE
// Same code patterns / visual language as the Vendors module:
// toast system, modal system, badge()/money() helpers, panel +
// table builders, icon-only SVG buttons.
// =============================================================

// ---------- SVG Icons ----------
function iconPlus() { return `<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 5v14M5 12h14"/></svg>`; }
function iconEdit() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 013 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>`; }
function iconTrash() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18"/><path d="M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2"/><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6"/></svg>`; }
function iconEye() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>`; }
function iconX() { return `<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M18 6L6 18M6 6l12 12"/></svg>`; }
function iconSave() { return `<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z"/><path d="M17 21v-8H7v8"/><path d="M7 3v5h8"/></svg>`; }
function iconCheckCircle() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 11-5.93-9.14"/><path d="M22 4L12 14.01l-3-3"/></svg>`; }
function iconXCircle() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M15 9l-6 6M9 9l6 6"/></svg>`; }
function iconTruck() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><path d="M16 8h4l3 3v5h-7V8z"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>`; }
function iconChevronLeft() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 18l-6-6 6-6"/></svg>`; }
function iconChevronRight() { return `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 18l6-6-6-6"/></svg>`; }

// ---------- Toast System (identical pattern to Vendors) ----------
function showToast(message, kind) {
  kind = kind || 'success';
  var wrap = document.getElementById('toastWrap');
  var colors = { success: 'bg-green-600', info: 'bg-primary', warning: 'bg-amber-600', error: 'bg-red-600' };
  var el = document.createElement('div');
  el.className = 'toast ' + colors[kind] + ' text-white text-sm font-semibold px-4 py-3 rounded-lg shadow-lg flex items-center gap-2 max-w-xs';
  el.innerHTML = '<span>' + message + '</span>';
  wrap.appendChild(el);
  setTimeout(function () {
    el.style.transition = 'opacity .3s';
    el.style.opacity = '0';
    setTimeout(function () { el.remove(); }, 300);
  }, 2600);
}

// ---------- Modal System ----------
function openModal(innerHtml, opts) {
  opts = opts || {};
  var root = document.getElementById('modalRoot');
  root.innerHTML = `
    <div class="fixed inset-0 z-[100] flex items-center justify-center p-4" id="modalOverlay">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-[2px]" id="modalBackdrop"></div>
      <div class="relative bg-white rounded-2xl shadow-2xl w-full ${opts.width || 'max-w-2xl'} max-h-[92vh] overflow-hidden animate-modal" ${opts.id ? 'id="' + opts.id + '"' : ''}>
        ${innerHtml}
      </div>
    </div>`;
  document.getElementById('modalBackdrop').addEventListener('click', closeModal);
  document.body.style.overflow = 'hidden';
}
function closeModal() {
  document.getElementById('modalRoot').innerHTML = '';
  document.body.style.overflow = '';
}
function modalHeader(title, subtitle) {
  return `
    <div class="flex items-start justify-between px-6 py-5 border-b border-gray-100 sticky top-0 bg-white z-10">
      <div>
        <h3 class="text-lg font-bold text-gray-900">${title}</h3>
        ${subtitle ? '<p class="text-xs text-gray-500 mt-0.5">' + subtitle + '</p>' : ''}
      </div>
      <button onclick="closeModal()" class="w-8 h-8 rounded-lg hover:bg-gray-100 flex items-center justify-center text-gray-400 hover:text-gray-700 transition">${iconX()}</button>
    </div>`;
}
function modalFooter(saveLabel, onSaveAttr, extraBtns) {
  return `
    <div class="flex items-center justify-between gap-2 px-6 py-4 border-t border-gray-100 bg-gray-50 rounded-b-2xl sticky bottom-0">
      <div>${extraBtns || ''}</div>
      <div class="flex gap-2">
        <button onclick="closeModal()" class="px-4 py-2 rounded-lg text-sm font-semibold text-gray-600 border border-gray-200 hover:bg-white transition">Cancel</button>
        <button ${onSaveAttr} class="px-4 py-2 rounded-lg text-sm font-semibold text-white bg-primary hover:bg-primary-dark transition shadow-sm flex items-center gap-1.5">${iconSave()} ${saveLabel}</button>
      </div>
    </div>`;
}
function fld(label, inputHtml, hint) {
  return `<div class="mb-3"><label class="form-label">${label}</label>${inputHtml}${hint ? '<div class="form-hint">' + hint + '</div>' : ''}</div>`;
}

// ---------- Shared helpers ----------
function money(n) { return '₹' + Number(n || 0).toLocaleString('en-IN'); }
function badge(text, color) {
  var map = { green: 'badge-green', amber: 'badge-amber', red: 'badge-red', gray: 'badge-gray', blue: 'badge-blue' };
  var dot = { green: 'badge-dot-green', amber: 'badge-dot-amber', red: 'badge-dot-red', gray: 'badge-dot-gray', blue: 'badge-dot-blue' };
  return '<span class="badge ' + map[color] + '"><span class="badge-dot ' + dot[color] + '"></span>' + text + '</span>';
}
function rowActionsHtml(editFn, delFn, viewFn) {
  var html = '<div class="flex gap-1">';
  if (viewFn) html += '<button onclick="' + viewFn + '" class="btn-icon-action" title="View">' + iconEye() + '</button>';
  html += '<button onclick="' + editFn + '" class="btn-icon-action" title="Edit">' + iconEdit() + '</button>';
  html += '<button onclick="' + delFn + '" class="btn-icon-action danger" title="Delete">' + iconTrash() + '</button>';
  html += '</div>';
  return html;
}
function panelWrap(titleHtml, bodyHtml, extraHead) {
  return `
    <div class="panel-card">
      <div class="panel-card-head">
        <div class="panel-card-title">${titleHtml}</div>
        ${extraHead || ''}
      </div>
      <div>${bodyHtml}</div>
    </div>`;
}
function tableHtml(cols, rowsHtml) {
  var colHeaders = cols.map(function (c) {
    return '<th class="text-left text-[11px] font-bold uppercase tracking-wide text-gray-500 px-4 py-3 whitespace-nowrap">' + c + '</th>';
  }).join('');
  return `
    <div class="overflow-x-auto">
      <table class="w-full text-[13px]">
        <thead><tr class="bg-gray-50 border-b border-gray-200">${colHeaders}</tr></thead>
        <tbody>${rowsHtml}</tbody>
      </table>
    </div>`;
}
function emptyRow(colspan, label, addFnLabel, addFn) {
  return `
    <tr><td colspan="${colspan}" class="text-center py-10 text-gray-400 text-sm">
      <div class="flex justify-center mb-2 empty-icon-svg">${iconTruck()}</div>
      ${label} Try adjusting your filters or
      <button onclick="${addFn}" class="text-primary font-semibold hover:underline">${addFnLabel}</button>.
    </td></tr>`;
}
function fmtDate(iso) {
  if (!iso) return '—';
  var d = new Date(iso + 'T00:00:00');
  if (isNaN(d)) return iso;
  return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}
function todayIso() { return new Date().toISOString().split('T')[0]; }
function uid(prefix, list) {
  var n = list.length ? Math.max.apply(null, list.map(function (x) { return parseInt((x.number || '0').split('-')[1], 10) || 0; })) + 1 : 1001;
  return prefix + '-' + n;
}

// ---------- Inline per-table filters ----------
// Each table now carries its own filter select(s) directly in its toolbar
// (no shared "Filter" dropdown button). These just read the relevant
// <select> values — nothing to open/close.

// =============================================================
// PAGINATION (shared engine — every table gets its own state)
// =============================================================
var pmPageState = {
  pr: { page: 1, pageSize: 10 },
  po: { page: 1, pageSize: 10 },
  grn: { page: 1, pageSize: 10 },
  pb: { page: 1, pageSize: 10 }
};

function paginateList(list, state) {
  var totalItems = list.length;
  var totalPages = Math.max(1, Math.ceil(totalItems / state.pageSize));
  if (state.page > totalPages) state.page = totalPages;
  if (state.page < 1) state.page = 1;
  var start = (state.page - 1) * state.pageSize;
  return { pageItems: list.slice(start, start + state.pageSize), totalItems: totalItems, totalPages: totalPages };
}

function paginationBarHtml(module, totalItems, state) {
  var totalPages = Math.max(1, Math.ceil(totalItems / state.pageSize));
  var start = totalItems === 0 ? 0 : (state.page - 1) * state.pageSize + 1;
  var end = Math.min(totalItems, state.page * state.pageSize);
  return `
    <div class="pagination-bar">
      <div class="pagination-info">Showing ${start}–${end} of ${totalItems}</div>
      <div class="pagination-controls">
        <select class="pagination-size-select" onchange="changePMPageSize('${module}', this.value)">
          <option value="5" ${state.pageSize === 5 ? 'selected' : ''}>5</option>
          <option value="10" ${state.pageSize === 10 ? 'selected' : ''}>10</option>
          <option value="25" ${state.pageSize === 25 ? 'selected' : ''}>25</option>
          <option value="50" ${state.pageSize === 50 ? 'selected' : ''}>50</option>
        </select>
        <button type="button" class="pagination-btn" onclick="goToPMPage('${module}', ${state.page - 1})" ${state.page <= 1 ? 'disabled' : ''} title="Previous page">${iconChevronLeft()}</button>
        <span class="pagination-page-label">Page ${state.page} of ${totalPages}</span>
        <button type="button" class="pagination-btn" onclick="goToPMPage('${module}', ${state.page + 1})" ${state.page >= totalPages ? 'disabled' : ''} title="Next page">${iconChevronRight()}</button>
      </div>
    </div>`;
}

function renderPMModule(module, keepPage) {
  if (module === 'pr') renderPR(keepPage);
  if (module === 'po') renderPO(keepPage);
  if (module === 'grn') renderGRN(keepPage);
  if (module === 'pb') renderPB(keepPage);
}
function goToPMPage(module, page) {
  pmPageState[module].page = page;
  renderPMModule(module, true);
}
function changePMPageSize(module, size) {
  pmPageState[module].pageSize = Number(size);
  pmPageState[module].page = 1;
  renderPMModule(module, true);
}

// =============================================================
// MODULE TAB SWITCHING
// =============================================================
function switchModuleTab(mod) {
  document.querySelectorAll('.module-tab-btn').forEach(function (b) {
    b.classList.toggle('active', b.dataset.module === mod);
  });
  document.querySelectorAll('.module-tab-panel').forEach(function (p) {
    p.classList.toggle('hidden', p.dataset.module !== mod);
  });
  renderPMModule(mod, true);
}

// =============================================================
// SEED DATA
// =============================================================
var vendorDirectory = [
  { id: 'VEN-001', name: 'Global Imports Inc.' },
  { id: 'VEN-002', name: 'Local Supplier Co.' },
  { id: 'VEN-003', name: 'ABC Distributors' },
  { id: 'VEN-004', name: 'Tech Components Ltd.' },
  { id: 'VEN-006', name: 'Premium Raw Materials' }
];
function vendorOptionsHtml(selected) {
  return vendorDirectory.map(function (v) {
    return '<option value="' + v.name + '" ' + (v.name === selected ? 'selected' : '') + '>' + v.name + '</option>';
  }).join('');
}

var purchaseRequirements = [
  { id: 'pr1', number: 'PR-1001', itemName: 'A4 Copier Paper (Ream)', quantity: 50, unit: 'Box', requestedBy: 'Rohit Verma', department: 'Admin', requiredDate: '2026-09-25', priority: 'medium', status: 'pending', remarks: 'For Q3 office restock', createdAt: '2026-09-10' },
  { id: 'pr2', number: 'PR-1002', itemName: 'Steel Rods 12mm', quantity: 200, unit: 'Pcs', requestedBy: 'Ankita Rao', department: 'Production', requiredDate: '2026-09-20', priority: 'high', status: 'approved', remarks: 'Urgent — line stoppage risk', createdAt: '2026-09-08' },
  { id: 'pr3', number: 'PR-1003', itemName: 'Laptop — 16GB RAM', quantity: 5, unit: 'Pcs', requestedBy: 'Suresh Nair', department: 'IT', requiredDate: '2026-10-01', priority: 'low', status: 'rejected', remarks: 'Budget deferred to next quarter', createdAt: '2026-09-05' }
];

var purchaseOrders = [
  { id: 'po1', number: 'PO-2001', vendorName: 'Global Imports Inc.', poDate: '2026-09-05', deliveryDate: '2026-09-18', items: [{ name: 'A4 Copier Paper', qty: 50, unit: 'Box', rate: 950 }], status: 'sent' },
  { id: 'po2', number: 'PO-2002', vendorName: 'Premium Raw Materials', poDate: '2026-09-02', deliveryDate: '2026-09-12', items: [{ name: 'Steel Rods 12mm', qty: 200, unit: 'Pcs', rate: 385 }], status: 'partial' },
  { id: 'po3', number: 'PO-2003', vendorName: 'Tech Components Ltd.', poDate: '2026-08-20', deliveryDate: '2026-08-30', items: [{ name: 'Laptop 16GB', qty: 5, unit: 'Pcs', rate: 68000 }], status: 'closed' }
];

var goodsReceipts = [
  { id: 'grn1', number: 'GRN-3001', poNumber: 'PO-2002', vendorName: 'Premium Raw Materials', receiptDate: '2026-09-10', items: [{ name: 'Steel Rods 12mm', ordered: 200, received: 140 }], status: 'draft' },
  { id: 'grn2', number: 'GRN-3002', poNumber: 'PO-2003', vendorName: 'Tech Components Ltd.', receiptDate: '2026-08-29', items: [{ name: 'Laptop 16GB', ordered: 5, received: 5 }], status: 'completed' }
];

var purchaseBills = [
  { id: 'pb1', number: 'PB-4001', vendorName: 'Tech Components Ltd.', poNumber: 'PO-2003', grnNumber: 'GRN-3002', billDate: '2026-08-30', dueDate: '2026-09-14', amount: 340000, paidAmount: 340000, status: 'paid' },
  { id: 'pb2', number: 'PB-4002', vendorName: 'Premium Raw Materials', poNumber: 'PO-2002', grnNumber: 'GRN-3001', billDate: '2026-09-11', dueDate: '2026-09-25', amount: 53900, paidAmount: 20000, status: 'partial' },
  { id: 'pb3', number: 'PB-4003', vendorName: 'Global Imports Inc.', poNumber: 'PO-2001', grnNumber: '', billDate: '2026-08-01', dueDate: '2026-08-15', amount: 47500, paidAmount: 0, status: 'overdue' }
];

// =============================================================
// PURCHASE REQUIREMENT (PR)
// =============================================================
function prStatusBadge(s) {
  if (s === 'approved') return badge('Approved', 'green');
  if (s === 'rejected') return badge('Rejected', 'red');
  return badge('Pending', 'amber');
}
function priorityBadge(p) {
  if (p === 'high') return badge('High', 'red');
  if (p === 'low') return badge('Low', 'gray');
  return badge('Medium', 'amber');
}
function getFilteredPR() {
  var q = (document.getElementById('search-pr') || {}).value || '';
  q = q.toLowerCase();
  var status = (document.getElementById('filterStatus-pr') || {}).value || 'all';
  var priority = (document.getElementById('filterPriority-pr') || {}).value || 'all';
  return purchaseRequirements.filter(function (r) {
    var matchSearch = r.number.toLowerCase().indexOf(q) !== -1 ||
      r.requestedBy.toLowerCase().indexOf(q) !== -1 ||
      r.itemName.toLowerCase().indexOf(q) !== -1;
    var matchStatus = status === 'all' || r.status === status;
    var matchPriority = priority === 'all' || r.priority === priority;
    return matchSearch && matchStatus && matchPriority;
  });
}
function renderPR(keepPage) {
  var filtered = getFilteredPR();
  if (!keepPage) pmPageState.pr.page = 1;
  var page = paginateList(filtered, pmPageState.pr);
  var rows = page.pageItems.length ? page.pageItems.map(function (r) {
    return `
      <tr class="border-b border-gray-100 table-row-hover">
        <td class="px-4 py-3 font-mono text-[12.5px] font-semibold text-gray-700">${r.number}</td>
        <td class="px-4 py-3">
          <div class="font-semibold text-gray-800">${r.itemName}</div>
          <div class="text-[11px] text-gray-400">${r.quantity} ${r.unit}</div>
        </td>
        <td class="px-4 py-3 text-gray-600">${r.requestedBy}<div class="text-[11px] text-gray-400">${r.department}</div></td>
        <td class="px-4 py-3 text-gray-600">${fmtDate(r.requiredDate)}</td>
        <td class="px-4 py-3">${priorityBadge(r.priority)}</td>
        <td class="px-4 py-3">${prStatusBadge(r.status)}</td>
        <td class="px-4 py-3">${rowActionsHtml("editPR('" + r.id + "')", "delPR('" + r.id + "')")}</td>
      </tr>`;
  }).join('') : emptyRow(7, 'No purchase requirements found.', 'raise a new requirement', 'openAddPR()');

  document.getElementById('prPanel').innerHTML = panelWrap(
    'Requirements <span class="panel-count-chip">' + filtered.length + '</span>',
    tableHtml(['PR ', 'Item', 'Requested By', 'Required By', 'Priority', 'Status', 'Actions'], rows) + paginationBarHtml('pr', filtered.length, pmPageState.pr)
  );

  document.getElementById('totalPR').textContent = purchaseRequirements.length;
  document.getElementById('pendingPR').textContent = purchaseRequirements.filter(function (r) { return r.status === 'pending'; }).length;
  document.getElementById('approvedPR').textContent = purchaseRequirements.filter(function (r) { return r.status === 'approved'; }).length;
  document.getElementById('rejectedPR').textContent = purchaseRequirements.filter(function (r) { return r.status === 'rejected'; }).length;
}
function filterPR() { renderPR(); }
function clearFiltersPR() {
  document.getElementById('filterStatus-pr').value = 'all';
  document.getElementById('filterPriority-pr').value = 'all';
  document.getElementById('search-pr').value = '';
  renderPR();
}

function openAddPR() { openPRModal(null); }
function editPR(id) { openPRModal(purchaseRequirements.find(function (r) { return r.id === id; })); }
function openPRModal(existing) {
  var e = existing || { itemName: '', quantity: 1, unit: 'Pcs', requestedBy: '', department: '', requiredDate: todayIso(), priority: 'medium', status: 'pending', remarks: '' };
  var isEdit = !!existing;
  var content = `
    ${modalHeader(isEdit ? 'Edit Purchase Requirement' : 'New Purchase Requirement', isEdit ? e.number : 'Raise a material request for approval')}
    <div class="px-6 py-4 modal-scroll">
      <div class="grid grid-cols-2 gap-3">
        ${fld('Item Name <span class="required">*</span>', '<input id="pr_item" value="' + e.itemName + '" placeholder="e.g. A4 Copier Paper" class="form-input">')}
        <div class="grid grid-cols-2 gap-3">
          ${fld('Quantity', '<input id="pr_qty" type="number" min="1" value="' + e.quantity + '" class="form-input">')}
          ${fld('Unit', '<input id="pr_unit" value="' + e.unit + '" placeholder="Pcs / Box / Kg" class="form-input">')}
        </div>
      </div>
      <div class="grid grid-cols-2 gap-3">
        ${fld('Requested By <span class="required">*</span>', '<input id="pr_by" value="' + e.requestedBy + '" placeholder="Employee name" class="form-input">')}
        ${fld('Department', '<input id="pr_dept" value="' + e.department + '" placeholder="e.g. Production" class="form-input">')}
      </div>
      <div class="grid grid-cols-2 gap-3">
        ${fld('Required By', '<input id="pr_reqdate" type="date" value="' + e.requiredDate + '" class="form-input">')}
        ${fld('Priority', '<select id="pr_priority" class="form-input"><option value="low" ' + (e.priority === 'low' ? 'selected' : '') + '>Low</option><option value="medium" ' + (e.priority === 'medium' ? 'selected' : '') + '>Medium</option><option value="high" ' + (e.priority === 'high' ? 'selected' : '') + '>High</option></select>')}
      </div>
      ${fld('Approval Status', '<select id="pr_status" class="form-input"><option value="pending" ' + (e.status === 'pending' ? 'selected' : '') + '>Pending</option><option value="approved" ' + (e.status === 'approved' ? 'selected' : '') + '>Approved</option><option value="rejected" ' + (e.status === 'rejected' ? 'selected' : '') + '>Rejected</option></select>')}
      ${fld('Remarks', '<textarea id="pr_remarks" rows="2" class="form-input" placeholder="Optional note">' + (e.remarks || '') + '</textarea>')}
    </div>
    ${modalFooter(isEdit ? 'Update Requirement' : 'Save Requirement', 'onclick="savePR(\'' + (isEdit ? e.id : '') + '\')"')}
  `;
  openModal(content, { width: 'max-w-xl' });
}
function savePR(existingId) {
  var itemName = document.getElementById('pr_item').value.trim();
  var requestedBy = document.getElementById('pr_by').value.trim();
  if (!itemName || !requestedBy) { showToast('Item name and requested-by are required', 'error'); return; }
  var payload = {
    itemName: itemName,
    quantity: Number(document.getElementById('pr_qty').value) || 1,
    unit: document.getElementById('pr_unit').value.trim() || 'Pcs',
    requestedBy: requestedBy,
    department: document.getElementById('pr_dept').value.trim(),
    requiredDate: document.getElementById('pr_reqdate').value,
    priority: document.getElementById('pr_priority').value,
    status: document.getElementById('pr_status').value,
    remarks: document.getElementById('pr_remarks').value.trim()
  };
  if (existingId) {
    var r = purchaseRequirements.find(function (x) { return x.id === existingId; });
    Object.assign(r, payload);
    showToast('Purchase requirement updated', 'success');
  } else {
    payload.id = 'pr' + Date.now();
    payload.number = uid('PR', purchaseRequirements);
    payload.createdAt = todayIso();
    purchaseRequirements.unshift(payload);
    showToast('Purchase requirement raised', 'success');
  }
  closeModal();
  renderPR();
}
function delPR(id) {
  var r = purchaseRequirements.find(function (x) { return x.id === id; });
  if (!r) return;
  openModal(`
    ${modalHeader('Delete Requirement', r.number)}
    <div class="px-6 py-4 text-sm text-gray-600">Delete <b class="text-gray-800">${r.itemName}</b> requested by ${r.requestedBy}? This cannot be undone.</div>
    ${modalFooter('Delete', 'onclick="confirmDelPR(\'' + id + '\')"')}
  `, { width: 'max-w-md' });
}
function confirmDelPR(id) {
  purchaseRequirements = purchaseRequirements.filter(function (x) { return x.id !== id; });
  closeModal(); renderPR();
  showToast('Purchase requirement deleted', 'warning');
}

// =============================================================
// PURCHASE ORDERS (PO)
// =============================================================
function poStatusBadge(s) {
  var map = { draft: ['Draft', 'gray'], approved: ['Approved', 'blue'], sent: ['Sent', 'amber'], partial: ['Partial', 'amber'], fully_received: ['Fully Received', 'green'], closed: ['Closed', 'green'] };
  var m = map[s] || ['Draft', 'gray'];
  return badge(m[0], m[1]);
}
function poTotal(po) { return po.items.reduce(function (s, it) { return s + it.qty * it.rate; }, 0); }
function getFilteredPO() {
  var q = ((document.getElementById('search-po') || {}).value || '').toLowerCase();
  var status = (document.getElementById('filterStatus-po') || {}).value || 'all';
  return purchaseOrders.filter(function (p) {
    var matchSearch = p.number.toLowerCase().indexOf(q) !== -1 || p.vendorName.toLowerCase().indexOf(q) !== -1;
    var matchStatus = status === 'all' || p.status === status;
    return matchSearch && matchStatus;
  });
}
function renderPO(keepPage) {
  var filtered = getFilteredPO();
  if (!keepPage) pmPageState.po.page = 1;
  var page = paginateList(filtered, pmPageState.po);
  var rows = page.pageItems.length ? page.pageItems.map(function (p) {
    return `
      <tr class="border-b border-gray-100 table-row-hover">
        <td class="px-4 py-3 font-mono text-[12.5px] font-semibold text-gray-700">${p.number}</td>
        <td class="px-4 py-3 font-semibold text-gray-800">${p.vendorName}</td>
        <td class="px-4 py-3 text-gray-600">${fmtDate(p.poDate)}</td>
        <td class="px-4 py-3 text-gray-600">${fmtDate(p.deliveryDate)}</td>
        <td class="px-4 py-3 font-mono font-semibold text-gray-700">${money(poTotal(p))}</td>
        <td class="px-4 py-3">${poStatusBadge(p.status)}</td>
        <td class="px-4 py-3">${rowActionsHtml("editPO('" + p.id + "')", "delPO('" + p.id + "')", "viewPO('" + p.id + "')")}</td>
      </tr>`;
  }).join('') : emptyRow(7, 'No purchase orders found.', 'create a purchase order', 'openAddPO()');

  document.getElementById('poPanel').innerHTML = panelWrap(
    'Purchase Orders <span class="panel-count-chip">' + filtered.length + '</span>',
    tableHtml(['PO', 'Vendor', 'PO Date', 'Delivery', 'Value', 'Status', 'Actions'], rows) + paginationBarHtml('po', filtered.length, pmPageState.po)
  );

  document.getElementById('totalPO').textContent = purchaseOrders.length;
  document.getElementById('draftPO').textContent = purchaseOrders.filter(function (p) { return p.status === 'draft'; }).length;
  document.getElementById('openPO').textContent = purchaseOrders.filter(function (p) { return p.status === 'sent' || p.status === 'partial'; }).length;
  document.getElementById('receivedPO').textContent = purchaseOrders.filter(function (p) { return p.status === 'fully_received' || p.status === 'closed'; }).length;
  document.getElementById('totalValuePO').textContent = money(purchaseOrders.reduce(function (s, p) { return s + poTotal(p); }, 0));
}
function filterPO() { renderPO(); }
function clearFiltersPO() {
  document.getElementById('filterStatus-po').value = 'all';
  document.getElementById('search-po').value = '';
  renderPO();
}

var poLineItemCounter = 0;
function poLineItemRow(idx, item) {
  item = item || {};
  return `
    <div class="line-item-row" data-line-idx="${idx}">
      <input class="li_name form-input" placeholder="Item name" value="${item.name || ''}">
      <input class="li_qty form-input" type="number" min="1" placeholder="Qty" value="${item.qty || 1}">
      <input class="li_unit form-input" placeholder="Unit" value="${item.unit || 'Pcs'}">
      <input class="li_rate form-input" type="number" min="0" placeholder="Rate ₹" value="${item.rate || 0}">
      <button type="button" onclick="removeLineItem(${idx})" class="btn-icon-action danger" title="Remove">${iconX()}</button>
    </div>`;
}
function addLineItemRow(item) {
  var wrap = document.getElementById('poLineItemsWrap');
  if (!wrap) return;
  var idx = poLineItemCounter++;
  var div = document.createElement('div');
  div.innerHTML = poLineItemRow(idx, item);
  wrap.appendChild(div.firstElementChild);
}
function removeLineItem(idx) {
  var row = document.querySelector('.line-item-row[data-line-idx="' + idx + '"]');
  if (row) row.remove();
}
function collectLineItems() {
  var rows = document.querySelectorAll('#poLineItemsWrap .line-item-row');
  var items = [];
  rows.forEach(function (row) {
    var name = row.querySelector('.li_name').value.trim();
    var qty = Number(row.querySelector('.li_qty').value) || 0;
    var unit = row.querySelector('.li_unit').value.trim() || 'Pcs';
    var rate = Number(row.querySelector('.li_rate').value) || 0;
    if (name && qty > 0) items.push({ name: name, qty: qty, unit: unit, rate: rate });
  });
  return items;
}

function openAddPO() { openPOModal(null); }
function editPO(id) { openPOModal(purchaseOrders.find(function (p) { return p.id === id; })); }
function openPOModal(existing) {
  var e = existing || { vendorName: vendorDirectory[0].name, poDate: todayIso(), deliveryDate: todayIso(), items: [{ name: '', qty: 1, unit: 'Pcs', rate: 0 }], status: 'draft' };
  var isEdit = !!existing;
  poLineItemCounter = 0;
  var content = `
    ${modalHeader(isEdit ? 'Edit Purchase Order' : 'New Purchase Order', isEdit ? e.number : 'Issue an order to a vendor')}
    <div class="px-6 py-4 modal-scroll">
      <div class="grid grid-cols-2 gap-3">
        ${fld('Vendor <span class="required">*</span>', '<select id="po_vendor" class="form-input">' + vendorOptionsHtml(e.vendorName) + '</select>')}
        ${fld('Status', '<select id="po_status" class="form-input"><option value="draft" ' + (e.status === 'draft' ? 'selected' : '') + '>Draft</option><option value="approved" ' + (e.status === 'approved' ? 'selected' : '') + '>Approved</option><option value="sent" ' + (e.status === 'sent' ? 'selected' : '') + '>Sent</option><option value="partial" ' + (e.status === 'partial' ? 'selected' : '') + '>Partial</option><option value="fully_received" ' + (e.status === 'fully_received' ? 'selected' : '') + '>Fully Received</option><option value="closed" ' + (e.status === 'closed' ? 'selected' : '') + '>Closed</option></select>')}
      </div>
      <div class="grid grid-cols-2 gap-3">
        ${fld('PO Date', '<input id="po_date" type="date" value="' + e.poDate + '" class="form-input">')}
        ${fld('Expected Delivery', '<input id="po_delivery" type="date" value="' + e.deliveryDate + '" class="form-input">')}
      </div>
      <div class="mt-2 pt-3 border-t border-gray-100">
        <div class="flex items-center justify-between mb-2">
          <h4 class="text-[12.5px] font-semibold text-gray-800">Items</h4>
          <button type="button" onclick="addLineItemRow()" class="text-[12px] font-semibold text-primary hover:underline flex items-center gap-1">${iconPlus()} Add Item</button>
        </div>
        <div class="line-item-header"><span>Item</span><span>Qty</span><span>Unit</span><span>Rate ₹</span><span></span></div>
        <div id="poLineItemsWrap"></div>
      </div>
    </div>
    ${modalFooter(isEdit ? 'Update Order' : 'Save Order', 'onclick="savePO(\'' + (isEdit ? e.id : '') + '\')"')}
  `;
  openModal(content, { width: 'max-w-2xl' });
  (e.items && e.items.length ? e.items : [{}]).forEach(function (it) { addLineItemRow(it); });
}
function savePO(existingId) {
  var vendorName = document.getElementById('po_vendor').value;
  var items = collectLineItems();
  if (!items.length) { showToast('Add at least one valid item (name + quantity)', 'error'); return; }
  var payload = {
    vendorName: vendorName,
    poDate: document.getElementById('po_date').value,
    deliveryDate: document.getElementById('po_delivery').value,
    status: document.getElementById('po_status').value,
    items: items
  };
  if (existingId) {
    var p = purchaseOrders.find(function (x) { return x.id === existingId; });
    Object.assign(p, payload);
    showToast('Purchase order updated', 'success');
  } else {
    payload.id = 'po' + Date.now();
    payload.number = uid('PO', purchaseOrders);
    purchaseOrders.unshift(payload);
    showToast('Purchase order created', 'success');
  }
  closeModal();
  renderPO();
}
function delPO(id) {
  var p = purchaseOrders.find(function (x) { return x.id === id; });
  if (!p) return;
  openModal(`
    ${modalHeader('Delete Purchase Order', p.number)}
    <div class="px-6 py-4 text-sm text-gray-600">Delete order to <b class="text-gray-800">${p.vendorName}</b> worth ${money(poTotal(p))}? This cannot be undone.</div>
    ${modalFooter('Delete', 'onclick="confirmDelPO(\'' + id + '\')"')}
  `, { width: 'max-w-md' });
}
function confirmDelPO(id) {
  purchaseOrders = purchaseOrders.filter(function (x) { return x.id !== id; });
  closeModal(); renderPO();
  showToast('Purchase order deleted', 'warning');
}
function viewPO(id) {
  var p = purchaseOrders.find(function (x) { return x.id === id; });
  if (!p) return;
  var rows = p.items.map(function (it) {
    return `<tr class="border-b border-gray-100"><td class="px-3 py-2">${it.name}</td><td class="px-3 py-2">${it.qty} ${it.unit || ''}</td><td class="px-3 py-2 font-mono">${money(it.rate)}</td><td class="px-3 py-2 font-mono font-semibold">${money(it.qty * it.rate)}</td></tr>`;
  }).join('');
  openModal(`
    ${modalHeader('Purchase Order — ' + p.number, p.vendorName)}
    <div class="px-6 py-4">
      <div class="grid grid-cols-3 gap-3 mb-4 text-sm">
        <div><span class="text-gray-500">PO Date</span><br>${fmtDate(p.poDate)}</div>
        <div><span class="text-gray-500">Delivery</span><br>${fmtDate(p.deliveryDate)}</div>
        <div><span class="text-gray-500">Status</span><br>${poStatusBadge(p.status)}</div>
      </div>
      <table class="w-full text-[13px]">
        <thead><tr class="bg-gray-50 border-b border-gray-200"><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Item</th><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Qty</th><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Rate</th><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Amount</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
      <div class="text-right font-bold text-gray-800 mt-2">Total: ${money(poTotal(p))}</div>
    </div>
    ${modalFooter('Close', 'onclick="closeModal()"')}
  `, { width: 'max-w-xl' });
}

// =============================================================
// GOODS RECEIPT (GRN)
// =============================================================
function grnStatusBadge(s) { return s === 'completed' ? badge('Completed', 'green') : badge('Draft', 'gray'); }
function grnReceivedPct(g) {
  var ordered = g.items.reduce(function (s, i) { return s + i.ordered; }, 0);
  var received = g.items.reduce(function (s, i) { return s + i.received; }, 0);
  return ordered ? Math.round((received / ordered) * 100) : 0;
}
function getFilteredGRN() {
  var q = ((document.getElementById('search-grn') || {}).value || '').toLowerCase();
  var status = (document.getElementById('filterStatus-grn') || {}).value || 'all';
  return goodsReceipts.filter(function (g) {
    var matchSearch = g.number.toLowerCase().indexOf(q) !== -1 || g.poNumber.toLowerCase().indexOf(q) !== -1 || g.vendorName.toLowerCase().indexOf(q) !== -1;
    var matchStatus = status === 'all' || g.status === status;
    return matchSearch && matchStatus;
  });
}
function renderGRN(keepPage) {
  var filtered = getFilteredGRN();
  if (!keepPage) pmPageState.grn.page = 1;
  var page = paginateList(filtered, pmPageState.grn);
  var rows = page.pageItems.length ? page.pageItems.map(function (g) {
    return `
      <tr class="border-b border-gray-100 table-row-hover">
        <td class="px-4 py-3 font-mono text-[12.5px] font-semibold text-gray-700">${g.number}</td>
        <td class="px-4 py-3 font-mono text-[12.5px] text-gray-600">${g.poNumber}</td>
        <td class="px-4 py-3 font-semibold text-gray-800">${g.vendorName}</td>
        <td class="px-4 py-3 text-gray-600">${fmtDate(g.receiptDate)}</td>
        <td class="px-4 py-3 text-gray-600">${grnReceivedPct(g)}%</td>
        <td class="px-4 py-3">${grnStatusBadge(g.status)}</td>
        <td class="px-4 py-3">${rowActionsHtml("editGRN('" + g.id + "')", "delGRN('" + g.id + "')", "viewGRN('" + g.id + "')")}</td>
      </tr>`;
  }).join('') : emptyRow(7, 'No goods receipts found.', 'record a goods receipt', 'openAddGRN()');

  document.getElementById('grnPanel').innerHTML = panelWrap(
    'Goods Receipts <span class="panel-count-chip">' + filtered.length + '</span>',
    tableHtml(['GRN ', 'PO ', 'Vendor', 'Receipt Date', 'Received', 'Status', 'Actions'], rows) + paginationBarHtml('grn', filtered.length, pmPageState.grn)
  );

  document.getElementById('totalGRN').textContent = goodsReceipts.length;
  document.getElementById('draftGRN').textContent = goodsReceipts.filter(function (g) { return g.status === 'draft'; }).length;
  document.getElementById('completedGRN').textContent = goodsReceipts.filter(function (g) { return g.status === 'completed'; }).length;
  var billedPOs = purchaseBills.map(function (b) { return b.grnNumber; });
  document.getElementById('awaitingBillGRN').textContent = goodsReceipts.filter(function (g) { return g.status === 'completed' && billedPOs.indexOf(g.number) === -1; }).length;
}
function filterGRN() { renderGRN(); }
function clearFiltersGRN() {
  document.getElementById('filterStatus-grn').value = 'all';
  document.getElementById('search-grn').value = '';
  renderGRN();
}

var grnLineItemCounter = 0;
function grnLineItemRow(idx, item) {
  item = item || {};
  return `
    <div class="grid grid-cols-[1fr_90px_90px_32px] gap-2 items-center mb-2" data-grn-line-idx="${idx}">
      <input class="gli_name form-input" placeholder="Item name" value="${item.name || ''}">
      <input class="gli_ordered form-input" type="number" min="0" placeholder="Ordered" value="${item.ordered || 0}">
      <input class="gli_received form-input" type="number" min="0" placeholder="Received" value="${item.received || 0}">
      <button type="button" onclick="removeGRNLineItem(${idx})" class="btn-icon-action danger" title="Remove">${iconX()}</button>
    </div>`;
}
function addGRNLineItemRow(item) {
  var wrap = document.getElementById('grnLineItemsWrap');
  if (!wrap) return;
  var idx = grnLineItemCounter++;
  var div = document.createElement('div');
  div.innerHTML = grnLineItemRow(idx, item);
  wrap.appendChild(div.firstElementChild);
}
function removeGRNLineItem(idx) {
  var row = document.querySelector('[data-grn-line-idx="' + idx + '"]');
  if (row) row.remove();
}
function collectGRNLineItems() {
  var rows = document.querySelectorAll('#grnLineItemsWrap [data-grn-line-idx]');
  var items = [];
  rows.forEach(function (row) {
    var name = row.querySelector('.gli_name').value.trim();
    var ordered = Number(row.querySelector('.gli_ordered').value) || 0;
    var received = Number(row.querySelector('.gli_received').value) || 0;
    if (name) items.push({ name: name, ordered: ordered, received: received });
  });
  return items;
}

function openAddGRN() { openGRNModal(null); }
function editGRN(id) { openGRNModal(goodsReceipts.find(function (g) { return g.id === id; })); }
function openGRNModal(existing) {
  var e = existing || { poNumber: purchaseOrders[0] ? purchaseOrders[0].number : '', vendorName: purchaseOrders[0] ? purchaseOrders[0].vendorName : '', receiptDate: todayIso(), items: [{ name: '', ordered: 0, received: 0 }], status: 'draft' };
  var isEdit = !!existing;
  grnLineItemCounter = 0;
  var poOptions = purchaseOrders.map(function (p) { return '<option value="' + p.number + '" data-vendor="' + p.vendorName + '" ' + (p.number === e.poNumber ? 'selected' : '') + '>' + p.number + ' — ' + p.vendorName + '</option>'; }).join('');
  var content = `
    ${modalHeader(isEdit ? 'Edit Goods Receipt' : 'New Goods Receipt', isEdit ? e.number : 'Record what arrived against a PO')}
    <div class="px-6 py-4 modal-scroll">
      <div class="grid grid-cols-2 gap-3">
        ${fld('Against PO <span class="required">*</span>', '<select id="grn_po" class="form-input" onchange="syncGRNVendor()">' + poOptions + '</select>')}
        ${fld('Vendor', '<input id="grn_vendor" value="' + e.vendorName + '" class="form-input" disabled>')}
      </div>
      <div class="grid grid-cols-2 gap-3">
        ${fld('Receipt Date', '<input id="grn_date" type="date" value="' + e.receiptDate + '" class="form-input">')}
        ${fld('Status', '<select id="grn_status" class="form-input"><option value="draft" ' + (e.status === 'draft' ? 'selected' : '') + '>Draft</option><option value="completed" ' + (e.status === 'completed' ? 'selected' : '') + '>Completed</option></select>')}
      </div>
      <div class="mt-2 pt-3 border-t border-gray-100">
        <div class="flex items-center justify-between mb-2">
          <h4 class="text-[12.5px] font-semibold text-gray-800">Items Received</h4>
          <button type="button" onclick="addGRNLineItemRow()" class="text-[12px] font-semibold text-primary hover:underline flex items-center gap-1">${iconPlus()} Add Item</button>
        </div>
        <div class="grid grid-cols-[1fr_90px_90px_32px] gap-2 text-[10px] font-bold uppercase tracking-wide text-gray-400 mb-1"><span>Item</span><span>Ordered</span><span>Received</span><span></span></div>
        <div id="grnLineItemsWrap"></div>
      </div>
    </div>
    ${modalFooter(isEdit ? 'Update Receipt' : 'Save Receipt', 'onclick="saveGRN(\'' + (isEdit ? e.id : '') + '\')"')}
  `;
  openModal(content, { width: 'max-w-2xl' });
  (e.items && e.items.length ? e.items : [{}]).forEach(function (it) { addGRNLineItemRow(it); });
}
function syncGRNVendor() {
  var sel = document.getElementById('grn_po');
  var opt = sel.options[sel.selectedIndex];
  document.getElementById('grn_vendor').value = opt ? opt.dataset.vendor : '';
}
function saveGRN(existingId) {
  var poNumber = document.getElementById('grn_po').value;
  var vendorName = document.getElementById('grn_vendor').value;
  var items = collectGRNLineItems();
  if (!items.length) { showToast('Add at least one item', 'error'); return; }
  var payload = {
    poNumber: poNumber,
    vendorName: vendorName,
    receiptDate: document.getElementById('grn_date').value,
    status: document.getElementById('grn_status').value,
    items: items
  };
  if (existingId) {
    var g = goodsReceipts.find(function (x) { return x.id === existingId; });
    Object.assign(g, payload);
    showToast('Goods receipt updated', 'success');
  } else {
    payload.id = 'grn' + Date.now();
    payload.number = uid('GRN', goodsReceipts);
    goodsReceipts.unshift(payload);
    showToast('Goods receipt recorded', 'success');
  }
  closeModal();
  renderGRN();
}
function delGRN(id) {
  var g = goodsReceipts.find(function (x) { return x.id === id; });
  if (!g) return;
  openModal(`
    ${modalHeader('Delete Goods Receipt', g.number)}
    <div class="px-6 py-4 text-sm text-gray-600">Delete receipt against <b class="text-gray-800">${g.poNumber}</b>? This cannot be undone.</div>
    ${modalFooter('Delete', 'onclick="confirmDelGRN(\'' + id + '\')"')}
  `, { width: 'max-w-md' });
}
function confirmDelGRN(id) {
  goodsReceipts = goodsReceipts.filter(function (x) { return x.id !== id; });
  closeModal(); renderGRN();
  showToast('Goods receipt deleted', 'warning');
}
function viewGRN(id) {
  var g = goodsReceipts.find(function (x) { return x.id === id; });
  if (!g) return;
  var rows = g.items.map(function (it) {
    var full = it.received >= it.ordered;
    return `<tr class="border-b border-gray-100"><td class="px-3 py-2">${it.name}</td><td class="px-3 py-2">${it.ordered}</td><td class="px-3 py-2">${it.received}</td><td class="px-3 py-2">${full ? badge('Full', 'green') : badge('Short', 'amber')}</td></tr>`;
  }).join('');
  openModal(`
    ${modalHeader('Goods Receipt — ' + g.number, g.poNumber + ' · ' + g.vendorName)}
    <div class="px-6 py-4">
      <table class="w-full text-[13px]">
        <thead><tr class="bg-gray-50 border-b border-gray-200"><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Item</th><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Ordered</th><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Received</th><th class="text-left px-3 py-2 text-[10.5px] font-bold uppercase text-gray-500">Status</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
    ${modalFooter('Close', 'onclick="closeModal()"')}
  `, { width: 'max-w-xl' });
}

// =============================================================
// PURCHASE BILLS (PB)
// =============================================================
function pbStatusBadge(s) {
  var map = { draft: ['Draft', 'gray'], received: ['Received', 'blue'], partial: ['Partially Paid', 'amber'], paid: ['Paid', 'green'], overdue: ['Overdue', 'red'] };
  var m = map[s] || ['Draft', 'gray'];
  return badge(m[0], m[1]);
}
function pbBalance(b) { return Math.max(0, (b.amount || 0) - (b.paidAmount || 0)); }
function getFilteredPB() {
  var q = ((document.getElementById('search-pb') || {}).value || '').toLowerCase();
  var status = (document.getElementById('filterStatus-pb') || {}).value || 'all';
  return purchaseBills.filter(function (b) {
    var matchSearch = b.number.toLowerCase().indexOf(q) !== -1 || b.vendorName.toLowerCase().indexOf(q) !== -1;
    var matchStatus = status === 'all' || b.status === status;
    return matchSearch && matchStatus;
  });
}
function renderPB(keepPage) {
  var filtered = getFilteredPB();
  if (!keepPage) pmPageState.pb.page = 1;
  var page = paginateList(filtered, pmPageState.pb);
  var rows = page.pageItems.length ? page.pageItems.map(function (b) {
    return `
      <tr class="border-b border-gray-100 table-row-hover">
        <td class="px-4 py-3 font-mono text-[12.5px] font-semibold text-gray-700">${b.number}</td>
        <td class="px-4 py-3 font-semibold text-gray-800">${b.vendorName}</td>
        <td class="px-4 py-3 font-mono text-[12px] text-gray-500">${b.poNumber || '—'}</td>
        <td class="px-4 py-3 text-gray-600">${fmtDate(b.dueDate)}</td>
        <td class="px-4 py-3 font-mono font-semibold text-gray-700">${money(b.amount)}</td>
        <td class="px-4 py-3 font-mono font-semibold text-amber-600">${money(pbBalance(b))}</td>
        <td class="px-4 py-3">${pbStatusBadge(b.status)}</td>
        <td class="px-4 py-3">${rowActionsHtml("editPB('" + b.id + "')", "delPB('" + b.id + "')", "viewPB('" + b.id + "')")}</td>
      </tr>`;
  }).join('') : emptyRow(8, 'No purchase bills found.', 'record a purchase bill', 'openAddPB()');

  document.getElementById('pbPanel').innerHTML = panelWrap(
    'Purchase Bills <span class="panel-count-chip">' + filtered.length + '</span>',
    tableHtml(['Bill', 'Vendor', 'PO ', 'Due Date', 'Amount', 'Balance', 'Status', 'Actions'], rows) + paginationBarHtml('pb', filtered.length, pmPageState.pb)
  );

  document.getElementById('totalPB').textContent = purchaseBills.length;
  document.getElementById('unpaidPB').textContent = purchaseBills.filter(function (b) { return b.status === 'received' || b.status === 'partial'; }).length;
  document.getElementById('overduePB').textContent = purchaseBills.filter(function (b) { return b.status === 'overdue'; }).length;
  document.getElementById('paidPB').textContent = purchaseBills.filter(function (b) { return b.status === 'paid'; }).length;
  document.getElementById('totalPayable').textContent = money(purchaseBills.reduce(function (s, b) { return s + pbBalance(b); }, 0));
}
function filterPB() { renderPB(); }
function clearFiltersPB() {
  document.getElementById('filterStatus-pb').value = 'all';
  document.getElementById('search-pb').value = '';
  renderPB();
}

function openAddPB() { openPBModal(null); }
function editPB(id) { openPBModal(purchaseBills.find(function (b) { return b.id === id; })); }
function openPBModal(existing) {
  var e = existing || { vendorName: vendorDirectory[0].name, poNumber: '', grnNumber: '', billDate: todayIso(), dueDate: todayIso(), amount: 0, paidAmount: 0, status: 'received' };
  var isEdit = !!existing;
  var poOptions = '<option value="">— none —</option>' + purchaseOrders.map(function (p) { return '<option value="' + p.number + '" ' + (p.number === e.poNumber ? 'selected' : '') + '>' + p.number + '</option>'; }).join('');
  var grnOptions = '<option value="">— none —</option>' + goodsReceipts.map(function (g) { return '<option value="' + g.number + '" ' + (g.number === e.grnNumber ? 'selected' : '') + '>' + g.number + '</option>'; }).join('');
  var content = `
    ${modalHeader(isEdit ? 'Edit Purchase Bill' : 'New Purchase Bill', isEdit ? e.number : 'Record a vendor bill for payment')}
    <div class="px-6 py-4 modal-scroll">
      <div class="grid grid-cols-2 gap-3">
        ${fld('Vendor <span class="required">*</span>', '<select id="pb_vendor" class="form-input">' + vendorOptionsHtml(e.vendorName) + '</select>')}
        ${fld('Status', '<select id="pb_status" class="form-input"><option value="draft" ' + (e.status === 'draft' ? 'selected' : '') + '>Draft</option><option value="received" ' + (e.status === 'received' ? 'selected' : '') + '>Received</option><option value="partial" ' + (e.status === 'partial' ? 'selected' : '') + '>Partially Paid</option><option value="paid" ' + (e.status === 'paid' ? 'selected' : '') + '>Paid</option><option value="overdue" ' + (e.status === 'overdue' ? 'selected' : '') + '>Overdue</option></select>')}
      </div>
      <div class="grid grid-cols-2 gap-3">
        ${fld('Against PO', '<select id="pb_po" class="form-input">' + poOptions + '</select>')}
        ${fld('Against GRN', '<select id="pb_grn" class="form-input">' + grnOptions + '</select>')}
      </div>
      <div class="grid grid-cols-2 gap-3">
        ${fld('Bill Date', '<input id="pb_billdate" type="date" value="' + e.billDate + '" class="form-input">')}
        ${fld('Due Date', '<input id="pb_duedate" type="date" value="' + e.dueDate + '" class="form-input">')}
      </div>
      <div class="grid grid-cols-2 gap-3">
        ${fld('Bill Amount <span class="required">*</span>', '<input id="pb_amount" type="number" min="0" value="' + e.amount + '" class="form-input">')}
        ${fld('Paid Amount', '<input id="pb_paid" type="number" min="0" value="' + e.paidAmount + '" class="form-input">')}
      </div>
    </div>
    ${modalFooter(isEdit ? 'Update Bill' : 'Save Bill', 'onclick="savePB(\'' + (isEdit ? e.id : '') + '\')"')}
  `;
  openModal(content, { width: 'max-w-xl' });
}
function savePB(existingId) {
  var vendorName = document.getElementById('pb_vendor').value;
  var amount = Number(document.getElementById('pb_amount').value) || 0;
  if (!vendorName || amount <= 0) { showToast('Vendor and a valid bill amount are required', 'error'); return; }
  var payload = {
    vendorName: vendorName,
    poNumber: document.getElementById('pb_po').value,
    grnNumber: document.getElementById('pb_grn').value,
    billDate: document.getElementById('pb_billdate').value,
    dueDate: document.getElementById('pb_duedate').value,
    amount: amount,
    paidAmount: Number(document.getElementById('pb_paid').value) || 0,
    status: document.getElementById('pb_status').value
  };
  if (existingId) {
    var b = purchaseBills.find(function (x) { return x.id === existingId; });
    Object.assign(b, payload);
    showToast('Purchase bill updated', 'success');
  } else {
    payload.id = 'pb' + Date.now();
    payload.number = uid('PB', purchaseBills);
    purchaseBills.unshift(payload);
    showToast('Purchase bill recorded', 'success');
  }
  closeModal();
  renderPB();
}
function delPB(id) {
  var b = purchaseBills.find(function (x) { return x.id === id; });
  if (!b) return;
  openModal(`
    ${modalHeader('Delete Purchase Bill', b.number)}
    <div class="px-6 py-4 text-sm text-gray-600">Delete bill from <b class="text-gray-800">${b.vendorName}</b> worth ${money(b.amount)}? This cannot be undone.</div>
    ${modalFooter('Delete', 'onclick="confirmDelPB(\'' + id + '\')"')}
  `, { width: 'max-w-md' });
}
function confirmDelPB(id) {
  purchaseBills = purchaseBills.filter(function (x) { return x.id !== id; });
  closeModal(); renderPB();
  showToast('Purchase bill deleted', 'warning');
}
function viewPB(id) {
  var b = purchaseBills.find(function (x) { return x.id === id; });
  if (!b) return;
  openModal(`
    ${modalHeader('Purchase Bill — ' + b.number, b.vendorName)}
    <div class="px-6 py-4">
      <div class="grid grid-cols-2 gap-3 text-sm">
        <div><span class="text-gray-500">PO #</span><br><span class="font-mono">${b.poNumber || '—'}</span></div>
        <div><span class="text-gray-500">GRN #</span><br><span class="font-mono">${b.grnNumber || '—'}</span></div>
        <div><span class="text-gray-500">Bill Date</span><br>${fmtDate(b.billDate)}</div>
        <div><span class="text-gray-500">Due Date</span><br>${fmtDate(b.dueDate)}</div>
        <div><span class="text-gray-500">Bill Amount</span><br><span class="font-semibold">${money(b.amount)}</span></div>
        <div><span class="text-gray-500">Paid</span><br><span class="font-semibold text-green-600">${money(b.paidAmount)}</span></div>
        <div><span class="text-gray-500">Balance</span><br><span class="font-semibold text-amber-600">${money(pbBalance(b))}</span></div>
        <div><span class="text-gray-500">Status</span><br>${pbStatusBadge(b.status)}</div>
      </div>
    </div>
    ${modalFooter('Close', 'onclick="closeModal()"')}
  `, { width: 'max-w-lg' });
}

// =============================================================
// INIT
// =============================================================
renderPR();
renderPO();
renderGRN();
renderPB();

console.log('📦 Purchase Management Module Loaded');