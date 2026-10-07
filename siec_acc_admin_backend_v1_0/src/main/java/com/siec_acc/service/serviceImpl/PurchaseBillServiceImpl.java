package com.siec_acc.service.serviceImpl;

import com.siec_acc.dto.request.PurchaseBillItemRequestDto;
import com.siec_acc.dto.request.PurchaseBillPatchDto;
import com.siec_acc.dto.request.PurchaseBillPaymentRequestDto;
import com.siec_acc.dto.request.PurchaseBillRequestDto;
import com.siec_acc.dto.response.PurchaseBillItemResponseDto;
import com.siec_acc.dto.response.PurchaseBillPaymentResponseDto;
import com.siec_acc.dto.response.PurchaseBillResponseDto;
import com.siec_acc.entity.GoodsReceiptEntity;
import com.siec_acc.entity.PurchaseBillEntity;
import com.siec_acc.entity.PurchaseBillItemEntity;
import com.siec_acc.entity.PurchaseBillPaymentEntity;
import com.siec_acc.entity.PurchaseOrderEntity;
import com.siec_acc.entity.PurchaseOrderItemEntity;
import com.siec_acc.exceptions.InvalidOperationException;
import com.siec_acc.exceptions.ResourceNotFoundException;
import com.siec_acc.repository.GoodsReceiptRepository;
import com.siec_acc.repository.PurchaseBillRepository;
import com.siec_acc.repository.PurchaseOrderRepository;
import com.siec_acc.service.PurchaseBillService;
import com.siec_acc.service.PurchaseOrderClosureService;
import com.siec_acc.utils.StrIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PurchaseBillServiceImpl implements PurchaseBillService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseBillServiceImpl.class);

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");

    private static final Set<String> ALLOWED_STATUSES = Set.of("received", "partial", "paid", "overdue");
    private static final double EPSILON = 0.005; // guards against floating-point rounding at the paid/balance boundary
    private static final String DEFAULT_UNIT = "Pcs";

    private final PurchaseBillRepository purchaseBillRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final PurchaseOrderClosureService closureService;

    public PurchaseBillServiceImpl(PurchaseBillRepository purchaseBillRepository,
                                   PurchaseOrderRepository purchaseOrderRepository,
                                   GoodsReceiptRepository goodsReceiptRepository,
                                   PurchaseOrderClosureService closureService) {
        this.purchaseBillRepository = purchaseBillRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.goodsReceiptRepository = goodsReceiptRepository;
        this.closureService = closureService;
    }

    private record Line(String name, int qty, String unit, double rate, double gstPct, double amount, double gstAmount) {}

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public PurchaseBillResponseDto createPurchaseBill(PurchaseBillRequestDto dto) {
        logger.info("Creating new purchase bill for vendor: {}", dto.getVendorName());

        String vendor = dto.getVendorName().trim();
        String poNumber = trimToNull(dto.getPoNumber());
        LocalDate billDate = dto.getBillDate() != null ? dto.getBillDate() : LocalDate.now(BUSINESS_ZONE);
        validateDates(billDate, dto.getDueDate());

        List<Line> lines = buildLines(dto.getItems());
        validateAgainstPo(poNumber, vendor, lines, null, false);

        PurchaseBillEntity bill = new PurchaseBillEntity();
        bill.setVendorName(vendor);
        bill.setPoNumber(poNumber);
        bill.setBillDate(billDate);
        bill.setDueDate(dto.getDueDate());
        bill.replaceItems(toEntities(lines));
        bill.setAmount(total(lines)); // amount column is NOT NULL, so it must be set before the first save

        PurchaseBillEntity saved = purchaseBillRepository.save(bill);
        saved.setPbStrId(StrIdGenerator.generate("PB", saved.getPbPrimeId()));
        saved.setPbNumber("PB-" + (4000 + saved.getPbPrimeId()));
        saved = purchaseBillRepository.save(saved);

        closureService.reevaluate(poNumber);
        logger.info("PurchaseBillEntity created successfully: {}", saved.getPbStrId());
        return mapToResponse(saved);
    }

    // ------------------------------------------------------------------
    // UPDATE (PUT) - pbNumber is never changed; the total is recomputed from the items and cannot drop below
    // what is already paid; vendor / PO cannot be switched once a payment exists.
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public PurchaseBillResponseDto updatePurchaseBill(String pbStrId, PurchaseBillRequestDto dto) {
        logger.info("Updating purchase bill: {}", pbStrId);
        PurchaseBillEntity bill = getEntityOrThrow(pbStrId);

        String vendor = dto.getVendorName().trim();
        String newPo = trimToNull(dto.getPoNumber());
        String oldPo = bill.getPoNumber();
        LocalDate billDate = dto.getBillDate() != null ? dto.getBillDate() : bill.getBillDate();
        validateDates(billDate, dto.getDueDate());

        double paidSoFar = paidAmount(bill);
        if (paidSoFar > 0 && (!vendor.equalsIgnoreCase(bill.getVendorName()) || !sameText(newPo, oldPo))) {
            throw new InvalidOperationException(
                    "Vendor and purchase order cannot be changed after a payment has been recorded. Delete the payments first.");
        }

        List<Line> lines = buildLines(dto.getItems());
        validateAgainstPo(newPo, vendor, lines, bill.getPbPrimeId(), sameText(newPo, oldPo));

        double newTotal = total(lines);
        if (newTotal + EPSILON < paidSoFar) {
            throw new InvalidOperationException(
                    "Bill total (" + newTotal + ") cannot be less than the " + paidSoFar + " already paid.");
        }

        bill.setVendorName(vendor);
        bill.setPoNumber(newPo);
        bill.setBillDate(billDate);
        bill.setDueDate(dto.getDueDate());
        bill.replaceItems(toEntities(lines));
        bill.setAmount(newTotal);

        PurchaseBillEntity updated = purchaseBillRepository.save(bill);
        closureService.reevaluate(oldPo);
        if (!sameText(oldPo, newPo)) closureService.reevaluate(newPo);
        logger.info("PurchaseBillEntity updated successfully: {}", pbStrId);
        return mapToResponse(updated);
    }

    // ------------------------------------------------------------------
    // PATCH - header fields only (items / amount change through PUT so totals are always recomputed)
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public PurchaseBillResponseDto patchPurchaseBill(String pbStrId, PurchaseBillPatchDto dto) {
        logger.info("Patching purchase bill: {}", pbStrId);
        PurchaseBillEntity bill = getEntityOrThrow(pbStrId);

        LocalDate newBillDate = dto.getBillDate() != null ? dto.getBillDate() : bill.getBillDate();
        LocalDate newDueDate = dto.getDueDate() != null ? dto.getDueDate() : bill.getDueDate();
        validateDates(newBillDate, newDueDate);

        String oldPo = bill.getPoNumber();
        String newVendor = dto.getVendorName() != null ? dto.getVendorName().trim() : bill.getVendorName();
        String newPoNumber = dto.getPoNumber() != null ? trimToNull(dto.getPoNumber()) : oldPo;

        boolean linkChanged = !newVendor.equalsIgnoreCase(bill.getVendorName()) || !sameText(newPoNumber, oldPo);
        if (linkChanged) {
            if (paidAmount(bill) > 0) {
                throw new InvalidOperationException(
                        "Vendor and purchase order cannot be changed after a payment has been recorded. Delete the payments first.");
            }
            List<Line> existing = bill.getItems().stream()
                    .map(i -> line(i.getName(), i.getQty(), i.getUnit(), i.getRate(), i.getGstPct()))
                    .collect(Collectors.toList());
            validateAgainstPo(newPoNumber, newVendor, existing, bill.getPbPrimeId(), sameText(newPoNumber, oldPo));
        }

        bill.setVendorName(newVendor);
        bill.setPoNumber(newPoNumber);
        bill.setBillDate(newBillDate);
        bill.setDueDate(newDueDate);

        PurchaseBillEntity patched = purchaseBillRepository.save(bill);
        if (!sameText(oldPo, newPoNumber)) {
            closureService.reevaluate(oldPo);
            closureService.reevaluate(newPoNumber);
        }
        logger.info("PurchaseBillEntity patched successfully: {}", pbStrId);
        return mapToResponse(patched);
    }

    // ------------------------------------------------------------------
    // DELETE - refused while payments exist (they would silently disappear from the books)
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public void deletePurchaseBill(String pbStrId) {
        logger.info("Deleting purchase bill: {}", pbStrId);
        PurchaseBillEntity bill = getEntityOrThrow(pbStrId);
        if (!bill.getPayments().isEmpty()) {
            throw new InvalidOperationException("Cannot delete " + bill.getPbNumber()
                    + ": payments have been recorded against it. Delete the payments first.");
        }
        String poNumber = bill.getPoNumber();
        purchaseBillRepository.delete(bill);
        purchaseBillRepository.flush();
        closureService.reevaluate(poNumber); // a closed PO re-opens if it no longer has a settled bill
        logger.info("PurchaseBillEntity deleted successfully: {}", pbStrId);
    }

    // ------------------------------------------------------------------
    // READS
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public PurchaseBillResponseDto getPurchaseBillByStrId(String pbStrId) {
        logger.info("Fetching purchase bill: {}", pbStrId);
        return mapToResponse(getEntityOrThrow(pbStrId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDto> getAllPurchaseBills() {
        logger.info("Fetching all purchase bills");
        return purchaseBillRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Status is derived, not a DB column, so filtering happens in memory after loading (fine at this data scale).
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDto> getPurchaseBillsByStatus(String status) {
        String normalized = normalizeStatus(status);
        logger.info("Fetching purchase bills with status: {}", normalized);
        return purchaseBillRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .filter(b -> normalized.equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // PAYMENTS
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public PurchaseBillResponseDto recordPayment(String pbStrId, PurchaseBillPaymentRequestDto dto) {
        logger.info("Recording payment for purchase bill: {}", pbStrId);
        PurchaseBillEntity bill = getEntityOrThrow(pbStrId);

        double balance = balance(bill);
        if (balance <= EPSILON) {
            throw new InvalidOperationException("This bill is already fully paid.");
        }
        if (dto.getAmount() > balance + EPSILON) {
            throw new InvalidOperationException("Payment exceeds the balance of " + round2(balance) + ".");
        }
        if (bill.getBillDate() != null && dto.getDate().isBefore(bill.getBillDate())) {
            throw new InvalidOperationException("Payment date cannot be before the bill date.");
        }

        PurchaseBillPaymentEntity payment = new PurchaseBillPaymentEntity();
        payment.setDate(dto.getDate());
        payment.setAmount(dto.getAmount());
        payment.setMode(trimToNull(dto.getMode()));
        payment.setRef(trimToNull(dto.getRef()));
        bill.addPayment(payment);

        PurchaseBillEntity saved = purchaseBillRepository.save(bill);
        closureService.reevaluate(saved.getPoNumber());
        logger.info("Payment of {} recorded against {}", round2(dto.getAmount()), bill.getPbNumber());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDto deletePayment(String pbStrId, Long paymentId) {
        logger.info("Deleting payment {} of purchase bill {}", paymentId, pbStrId);
        PurchaseBillEntity bill = getEntityOrThrow(pbStrId);

        boolean removed = bill.getPayments().removeIf(p -> p.getPaymentId() != null && p.getPaymentId().equals(paymentId));
        if (!removed) {
            throw new ResourceNotFoundException("No payment with ID '" + paymentId + "' on " + bill.getPbNumber() + ".");
        }

        PurchaseBillEntity saved = purchaseBillRepository.save(bill);
        purchaseBillRepository.flush();
        closureService.reevaluate(saved.getPoNumber()); // a closed PO re-opens because the bill is unpaid again
        return mapToResponse(saved);
    }

    // ------------------------------------------------------------------
    // BILLABLE ITEMS (pre-fill for a new bill)
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillItemResponseDto> getBillableItems(String poNumber, String excludePbStrId) {
        PurchaseOrderEntity po = findPoOrThrow(poNumber);

        Long excludeId = null;
        if (!isBlank(excludePbStrId)) {
            excludeId = getEntityOrThrow(excludePbStrId.trim()).getPbPrimeId();
        }
        Map<String, Integer> accepted = acceptedByItem(po);
        Map<String, Integer> billed = billedByItem(po.getPoNumber(), excludeId);

        List<PurchaseBillItemResponseDto> out = new ArrayList<>();
        double gst = po.getTaxPct() == null ? 0.0 : po.getTaxPct();
        for (PurchaseOrderItemEntity poItem : po.getItems()) {
            String key = lc(poItem.getName());
            int remaining = accepted.getOrDefault(key, 0) - billed.getOrDefault(key, 0);
            if (remaining <= 0) continue;
            Line l = line(poItem.getName(), remaining, poItem.getUnit(), poItem.getRate(), gst);
            out.add(toItemResponse(l));
        }
        return out;
    }

    // ==================================================================
    // validation
    // ==================================================================
    private void validateDates(LocalDate billDate, LocalDate dueDate) {
        if (billDate != null && dueDate != null && dueDate.isBefore(billDate)) {
            throw new InvalidOperationException("Due date cannot be before the bill date.");
        }
    }

    /** Validates each item and works out amount / GST per line (rounded per line, like a GST invoice). */
    private List<Line> buildLines(List<PurchaseBillItemRequestDto> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOperationException("Add at least one item to the bill.");
        }
        List<Line> lines = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < items.size(); i++) {
            PurchaseBillItemRequestDto it = items.get(i);
            String prefix = "Item " + (i + 1) + ": ";
            if (it == null || isBlank(it.getName())) throw new InvalidOperationException(prefix + "enter an item name.");
            if (it.getQty() == null || it.getQty() <= 0) throw new InvalidOperationException(prefix + "quantity must be greater than 0.");
            if (it.getRate() == null || it.getRate().isNaN() || it.getRate().isInfinite() || it.getRate() <= 0) {
                throw new InvalidOperationException(prefix + "enter a rate greater than 0.");
            }
            double gst = it.getGstPct() == null ? 0.0 : it.getGstPct();
            if (gst < 0 || gst > 100) throw new InvalidOperationException(prefix + "GST must be between 0 and 100.");
            if (!seen.add(lc(it.getName()))) {
                throw new InvalidOperationException(prefix + "'" + it.getName().trim() + "' is listed more than once - merge the lines.");
            }
            lines.add(line(it.getName().trim(), it.getQty(), it.getUnit(), it.getRate(), gst));
        }
        return lines;
    }

    private Line line(String name, int qty, String unit, double rate, Double gstPct) {
        double gst = gstPct == null ? 0.0 : gstPct;
        double amount = round2(qty * rate);
        double gstAmount = round2(amount * gst / 100.0);
        return new Line(name, qty, isBlank(unit) ? DEFAULT_UNIT : unit.trim(), rate, gst, amount, gstAmount);
    }

    /**
     * If a PO number is given:
     *  - the PO must exist, be for the same vendor, not be closed (unless this bill already belongs to it),
     *    and have goods received;
     *  - every billed item must be on the PO, and qty billed (this bill + other bills) cannot exceed the
     *    accepted quantity (received - rejected) - so you can't be billed for goods you never accepted.
     * Direct bills (no PO) skip all of this. Bills with no lines (legacy) skip only the quantity check.
     */
    private void validateAgainstPo(String poNumber, String vendorName, List<Line> lines, Long excludeBillId,
                                   boolean allowClosedPo) {
        if (isBlank(poNumber)) return;

        PurchaseOrderEntity po = findPoOrThrow(poNumber);
        if (!po.getVendorName().trim().equalsIgnoreCase(vendorName.trim())) {
            throw new InvalidOperationException(
                    "Vendor does not match " + po.getPoNumber() + " (" + po.getVendorName() + ").");
        }
        if (!allowClosedPo && "closed".equalsIgnoreCase(po.getStatus())) {
            throw new InvalidOperationException(po.getPoNumber() + " is already closed (fully received, billed and paid).");
        }
        if (!goodsReceiptRepository.existsByPoStrId(po.getPoStrId())) {
            throw new InvalidOperationException(
                    "No goods have been received against " + po.getPoNumber() + " yet. Record a goods receipt before billing it.");
        }
        if (lines.isEmpty()) return;

        Map<String, Integer> accepted = acceptedByItem(po);
        Map<String, Integer> billed = billedByItem(po.getPoNumber(), excludeBillId);
        for (Line l : lines) {
            String key = lc(l.name());
            if (!accepted.containsKey(key)) {
                throw new InvalidOperationException("'" + l.name() + "' has not been received on " + po.getPoNumber() + ".");
            }
            int already = billed.getOrDefault(key, 0);
            int remaining = accepted.get(key) - already;
            if (l.qty() > remaining) {
                throw new InvalidOperationException("Only " + Math.max(0, remaining) + " of '" + l.name()
                        + "' left to bill on " + po.getPoNumber() + " (accepted " + accepted.get(key)
                        + ", already billed " + already + ").");
            }
        }
    }

    private PurchaseOrderEntity findPoOrThrow(String poNumber) {
        if (isBlank(poNumber)) throw new InvalidOperationException("PO number is required.");
        return purchaseOrderRepository.findByPoNumber(poNumber.trim())
                .orElseThrow(() -> new InvalidOperationException("Purchase order '" + poNumber.trim() + "' was not found."));
    }

    /** Accepted quantity per item name = received - rejected, summed over all goods receipts of the PO. */
    private Map<String, Integer> acceptedByItem(PurchaseOrderEntity po) {
        Map<String, Integer> accepted = new LinkedHashMap<>();
        for (GoodsReceiptEntity g : goodsReceiptRepository.findByPoStrIdOrderByGrnPrimeIdAsc(po.getPoStrId())) {
            int rejected = g.getRejectedQty() == null ? 0 : g.getRejectedQty();
            accepted.merge(lc(g.getItemName()), g.getReceivedQty() - rejected, Integer::sum);
        }
        return accepted;
    }

    /** Quantity already billed per item name on a PO, optionally leaving out one bill (the one being edited). */
    private Map<String, Integer> billedByItem(String poNumber, Long excludeBillId) {
        Map<String, Integer> billed = new LinkedHashMap<>();
        for (PurchaseBillEntity b : purchaseBillRepository.findByPoNumberIgnoreCaseOrderByCreatedAtDesc(poNumber)) {
            if (excludeBillId != null && excludeBillId.equals(b.getPbPrimeId())) continue;
            for (PurchaseBillItemEntity it : b.getItems()) billed.merge(lc(it.getName()), it.getQty(), Integer::sum);
        }
        return billed;
    }

    // ==================================================================
    // helpers
    // ==================================================================
    private PurchaseBillEntity getEntityOrThrow(String pbStrId) {
        return purchaseBillRepository.findByPbStrId(pbStrId)
                .orElseThrow(() -> {
                    logger.warn("PurchaseBillEntity not found: {}", pbStrId);
                    return new ResourceNotFoundException("No purchase bill found with ID '" + pbStrId + "'.");
                });
    }

    private List<PurchaseBillItemEntity> toEntities(List<Line> lines) {
        List<PurchaseBillItemEntity> out = new ArrayList<>();
        for (Line l : lines) out.add(new PurchaseBillItemEntity(l.name(), l.qty(), l.unit(), l.rate(), l.gstPct()));
        return out;
    }

    private double subtotal(List<Line> lines) { return round2(lines.stream().mapToDouble(Line::amount).sum()); }

    private double gstTotal(List<Line> lines) { return round2(lines.stream().mapToDouble(Line::gstAmount).sum()); }

    private double total(List<Line> lines) { return round2(subtotal(lines) + gstTotal(lines)); }

    private double paidAmount(PurchaseBillEntity bill) {
        return round2(bill.getPayments().stream().mapToDouble(PurchaseBillPaymentEntity::getAmount).sum());
    }

    private double balance(PurchaseBillEntity bill) {
        double amt = bill.getAmount() == null ? 0.0 : bill.getAmount();
        return Math.max(0.0, round2(amt - paidAmount(bill)));
    }

    /** Mirrors the frontend's pbStatus(): paid > overdue > partial > received (unpaid). */
    private String deriveStatus(PurchaseBillEntity bill) {
        double amt = bill.getAmount() == null ? 0.0 : bill.getAmount();
        double bal = balance(bill);
        if (amt > 0 && bal <= EPSILON) return "paid";
        if (bill.getDueDate() != null && bill.getDueDate().isBefore(LocalDate.now(BUSINESS_ZONE))) return "overdue";
        if (paidAmount(bill) > 0) return "partial";
        return "received";
    }

    private String normalizeStatus(String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_STATUSES.contains(s)) {
            throw new InvalidOperationException(
                    "Invalid status '" + raw + "'. Allowed: received, partial, paid, overdue.");
        }
        return s;
    }

    private boolean sameText(String a, String b) {
        return (a == null ? "" : a.trim()).equalsIgnoreCase(b == null ? "" : b.trim());
    }

    private String lc(String s) { return s == null ? "" : s.trim().toLowerCase(Locale.ROOT); }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }

    private String trimToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

    private double round2(double n) { return Math.round(n * 100.0) / 100.0; }

    // ==================================================================
    // response mapping
    // ==================================================================
    private PurchaseBillItemResponseDto toItemResponse(Line l) {
        return new PurchaseBillItemResponseDto(l.name(), l.qty(), l.unit(), l.rate(), l.gstPct(),
                l.amount(), l.gstAmount(), round2(l.amount() + l.gstAmount()));
    }

    private PurchaseBillResponseDto mapToResponse(PurchaseBillEntity bill) {
        List<PurchaseBillPaymentResponseDto> paymentDtos = bill.getPayments().stream()
                .map(p -> new PurchaseBillPaymentResponseDto(p.getPaymentId(), p.getDate(), p.getAmount(), p.getMode(), p.getRef()))
                .collect(Collectors.toList());

        List<Line> lines = bill.getItems().stream()
                .map(i -> line(i.getName(), i.getQty(), i.getUnit(), i.getRate(), i.getGstPct()))
                .collect(Collectors.toList());

        double amount = bill.getAmount() == null ? 0.0 : bill.getAmount();
        // Legacy bills (no items): there is no breakdown, so show the stored amount as the subtotal with no GST.
        double subtotal = lines.isEmpty() ? amount : subtotal(lines);
        double gst = lines.isEmpty() ? 0.0 : gstTotal(lines);

        PurchaseBillResponseDto dto = new PurchaseBillResponseDto();
        dto.setPbPrimeId(bill.getPbPrimeId());
        dto.setPbStrId(bill.getPbStrId());
        dto.setPbNumber(bill.getPbNumber());
        dto.setVendorName(bill.getVendorName());
        dto.setPoNumber(bill.getPoNumber());
        dto.setBillDate(bill.getBillDate());
        dto.setDueDate(bill.getDueDate());
        dto.setItems(lines.stream().map(this::toItemResponse).collect(Collectors.toList()));
        dto.setSubtotal(subtotal);
        dto.setGstAmount(gst);
        dto.setAmount(amount);
        dto.setPaidAmount(paidAmount(bill));
        dto.setBalance(balance(bill));
        dto.setStatus(deriveStatus(bill));
        dto.setPayments(paymentDtos);
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setUpdatedAt(bill.getUpdatedAt());
        return dto;
    }
}


