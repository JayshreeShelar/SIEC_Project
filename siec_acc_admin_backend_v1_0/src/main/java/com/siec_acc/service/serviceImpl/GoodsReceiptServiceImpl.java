package com.siec_acc.service.serviceImpl;

import com.siec_acc.dto.request.GoodsReceiptRequestDto;
import com.siec_acc.dto.response.GoodsReceiptResponseDto;
import com.siec_acc.entity.GoodsReceiptEntity;
import com.siec_acc.entity.PurchaseOrderEntity;
import com.siec_acc.entity.PurchaseOrderItemEntity;
import com.siec_acc.exceptions.InvalidOperationException;
import com.siec_acc.exceptions.ResourceNotFoundException;
import com.siec_acc.repository.GoodsReceiptRepository;
import com.siec_acc.repository.PurchaseOrderRepository;
import com.siec_acc.service.GoodsReceiptService;
import com.siec_acc.service.PurchaseOrderClosureService;
import com.siec_acc.utils.StrIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GoodsReceiptServiceImpl implements GoodsReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsReceiptServiceImpl.class);

    private static final Set<String> RECEIVABLE = Set.of("approved", "sent", "partial");
    private static final Set<String> EDITABLE = Set.of("approved", "sent", "partial", "fully_received");

    private final GoodsReceiptRepository goodsReceiptRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderClosureService closureService; // closes the PO if goods arrive after it was already billed and paid

    public GoodsReceiptServiceImpl(GoodsReceiptRepository goodsReceiptRepository,
                                   PurchaseOrderRepository purchaseOrderRepository,
                                   PurchaseOrderClosureService closureService) {
        this.goodsReceiptRepository = goodsReceiptRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.closureService = closureService;
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public GoodsReceiptResponseDto createGoodsReceipt(GoodsReceiptRequestDto dto) {
        logger.info("Creating goods receipt for PO: {}", dto.getPoStrId());
        PurchaseOrderEntity po = getPoOrThrow(dto.getPoStrId().trim());

        if (!RECEIVABLE.contains(lc(po.getStatus()))) {
            throw new InvalidOperationException("Goods can only be received against an approved, sent or partially received PO. "
                    + po.getPoNumber() + " is currently " + po.getStatus() + ".");
        }

        PurchaseOrderItemEntity item = resolveItem(po, dto.getItemName());
        int rejected = dto.getRejectedQty() == null ? 0 : dto.getRejectedQty();
        validateQuantities(po, item, dto.getReceivedQty(), rejected, null);

        GoodsReceiptEntity grn = new GoodsReceiptEntity();
        grn.setPoStrId(po.getPoStrId());
        grn.setPoNumber(po.getPoNumber());
        grn.setVendorName(po.getVendorName());
        grn.setItemName(item.getName());
        grn.setUnit(item.getUnit());
        grn.setOrderedQty(item.getQty());
        applyEditableFields(grn, dto, rejected);

        GoodsReceiptEntity saved = goodsReceiptRepository.save(grn);
        saved.setGrnStrId(StrIdGenerator.generate("GRN", saved.getGrnPrimeId()));
        saved.setGrnNumber("GRN-" + (5000 + saved.getGrnPrimeId()));
        saved = goodsReceiptRepository.save(saved);

        resyncPo(po);
        logger.info("GoodsReceiptEntity created successfully: {}", saved.getGrnStrId());
        return mapToResponse(saved);
    }

    // ------------------------------------------------------------------
    // UPDATE (PUT) — grnNumber, PO and item are never changed here.
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public GoodsReceiptResponseDto updateGoodsReceipt(String grnStrId, GoodsReceiptRequestDto dto) {
        logger.info("Updating goods receipt: {}", grnStrId);
        GoodsReceiptEntity grn = getEntityOrThrow(grnStrId);
        PurchaseOrderEntity po = getPoOrThrow(grn.getPoStrId());

        if (!EDITABLE.contains(lc(po.getStatus()))) {
            throw new InvalidOperationException("Cannot edit a receipt of " + po.getPoNumber()
                    + " because the PO is " + po.getStatus() + ".");
        }
        if (dto.getPoStrId() != null && !dto.getPoStrId().trim().equals(grn.getPoStrId())) {
            throw new InvalidOperationException("The purchase order of a goods receipt cannot be changed.");
        }

        PurchaseOrderItemEntity item = resolveItem(po, grn.getItemName());
        int rejected = dto.getRejectedQty() == null ? 0 : dto.getRejectedQty();
        validateQuantities(po, item, dto.getReceivedQty(), rejected, grn.getGrnPrimeId());

        applyEditableFields(grn, dto, rejected);
        GoodsReceiptEntity updated = goodsReceiptRepository.save(grn);

        resyncPo(po);
        logger.info("GoodsReceiptEntity updated successfully: {}", grnStrId);
        return mapToResponse(updated);
    }

    // ------------------------------------------------------------------
    // DELETE — quantities and PO status are recalculated afterwards.
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public void deleteGoodsReceipt(String grnStrId) {
        logger.info("Deleting goods receipt: {}", grnStrId);
        GoodsReceiptEntity grn = getEntityOrThrow(grnStrId);
        PurchaseOrderEntity po = getPoOrThrow(grn.getPoStrId());

        if ("closed".equals(lc(po.getStatus()))) {
            throw new InvalidOperationException("Cannot delete a receipt of " + po.getPoNumber() + ": the PO is closed.");
        }
        goodsReceiptRepository.delete(grn);
        goodsReceiptRepository.flush();
        resyncPo(po);
        logger.info("GoodsReceiptEntity deleted successfully: {}", grnStrId);
    }

    // ------------------------------------------------------------------
    // READS
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public GoodsReceiptResponseDto getGoodsReceiptByStrId(String grnStrId) {
        return mapToResponse(getEntityOrThrow(grnStrId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceiptResponseDto> getAllGoodsReceipts() {
        logger.info("Fetching all goods receipts");
        return goodsReceiptRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceiptResponseDto> getGoodsReceiptsByPo(String poStrId) {
        getPoOrThrow(poStrId);
        return goodsReceiptRepository.findByPoStrIdOrderByGrnPrimeIdAsc(poStrId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // PO sync — the single place that decides GRN status and PO partial / fully_received.
    // Also called by PurchaseOrderServiceImpl after a PO's items change.
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public void resyncPo(PurchaseOrderEntity po) {
        List<GoodsReceiptEntity> grns = goodsReceiptRepository.findByPoStrIdOrderByGrnPrimeIdAsc(po.getPoStrId());

        Map<String, Integer> running = new HashMap<>();
        for (GoodsReceiptEntity g : grns) {
            String key = lc(g.getItemName());
            int total = running.merge(key, g.getReceivedQty(), Integer::sum);
            PurchaseOrderItemEntity item = findItem(po, g.getItemName());
            int ordered = item == null ? (g.getOrderedQty() == null ? 0 : g.getOrderedQty()) : item.getQty();
            g.setOrderedQty(ordered);
            g.setPoNumber(po.getPoNumber());
            g.setStatus(total >= ordered ? "completed" : "partial");
        }
        goodsReceiptRepository.saveAll(grns);

        String current = lc(po.getStatus());
        if (!EDITABLE.contains(current)) return; // draft / closed are left alone

        boolean anyReceived = running.values().stream().anyMatch(q -> q > 0);
        boolean allFull = !po.getItems().isEmpty() && po.getItems().stream()
                .allMatch(it -> running.getOrDefault(lc(it.getName()), 0) >= it.getQty());

        String next = allFull ? "fully_received" : anyReceived ? "partial" : ("partial".equals(current) || "fully_received".equals(current) ? "sent" : current);
        if (!next.equals(current)) {
            po.setStatus(next);
            purchaseOrderRepository.save(po);
            logger.info("{} moved {} -> {}", po.getPoNumber(), current, next);
        }

        // Bills may already be fully paid (goods arrived after billing) - let the single closing rule decide.
        closureService.reevaluate(po);
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------
    private void applyEditableFields(GoodsReceiptEntity grn, GoodsReceiptRequestDto dto, int rejected) {
        grn.setReceivedQty(dto.getReceivedQty());
        grn.setRejectedQty(rejected);
        grn.setReceivedDate(dto.getReceivedDate());
        grn.setChallanNumber(trimToNull(dto.getChallanNumber()));
        grn.setReceivedBy(trimToNull(dto.getReceivedBy()));
        grn.setRemarks(trimToNull(dto.getRemarks()));
    }

    /** received <= remaining on the PO line (excluding this GRN when editing) and rejected <= received. */
    private void validateQuantities(PurchaseOrderEntity po, PurchaseOrderItemEntity item, int received, int rejected, Long excludeGrnId) {
        if (rejected > received) {
            throw new InvalidOperationException("Rejected quantity cannot exceed the received quantity.");
        }
        int alreadyReceived = goodsReceiptRepository.findByPoStrIdOrderByGrnPrimeIdAsc(po.getPoStrId()).stream()
                .filter(g -> lc(g.getItemName()).equals(lc(item.getName())))
                .filter(g -> !g.getGrnPrimeId().equals(excludeGrnId))
                .mapToInt(GoodsReceiptEntity::getReceivedQty).sum();
        int remaining = Math.max(0, item.getQty() - alreadyReceived);
        if (received > remaining) {
            throw new InvalidOperationException("Only " + remaining + " " + (item.getUnit() == null ? "" : item.getUnit())
                    + " of '" + item.getName() + "' is left to receive on " + po.getPoNumber() + ".");
        }
    }

    private PurchaseOrderItemEntity resolveItem(PurchaseOrderEntity po, String itemName) {
        if (po.getItems().isEmpty()) {
            throw new InvalidOperationException(po.getPoNumber() + " has no items to receive.");
        }
        if (itemName == null || itemName.isBlank()) {
            if (po.getItems().size() == 1) return po.getItems().get(0);
            throw new InvalidOperationException(po.getPoNumber() + " has several items; choose which item is being received.");
        }
        PurchaseOrderItemEntity item = findItem(po, itemName);
        if (item == null) {
            throw new InvalidOperationException("'" + itemName.trim() + "' is not an item of " + po.getPoNumber() + ".");
        }
        return item;
    }

    private PurchaseOrderItemEntity findItem(PurchaseOrderEntity po, String name) {
        return po.getItems().stream().filter(i -> lc(i.getName()).equals(lc(name))).findFirst().orElse(null);
    }

    private GoodsReceiptEntity getEntityOrThrow(String grnStrId) {
        return goodsReceiptRepository.findByGrnStrId(grnStrId)
                .orElseThrow(() -> new ResourceNotFoundException("No goods receipt found with ID '" + grnStrId + "'."));
    }

    private PurchaseOrderEntity getPoOrThrow(String poStrId) {
        return purchaseOrderRepository.findByPoStrId(poStrId)
                .orElseThrow(() -> new ResourceNotFoundException("No purchase order found with ID '" + poStrId + "'."));
    }

    private String lc(String s) { return s == null ? "" : s.trim().toLowerCase(Locale.ROOT); }

    private String trimToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

    private GoodsReceiptResponseDto mapToResponse(GoodsReceiptEntity g) {
        int accepted = g.getReceivedQty() - (g.getRejectedQty() == null ? 0 : g.getRejectedQty());
        return new GoodsReceiptResponseDto(g.getGrnPrimeId(), g.getGrnStrId(), g.getGrnNumber(), g.getPoStrId(), g.getPoNumber(),
                g.getVendorName(), g.getItemName(), g.getUnit(), g.getOrderedQty(), g.getReceivedQty(), accepted,
                g.getRejectedQty(), g.getReceivedDate(), g.getChallanNumber(), g.getReceivedBy(), g.getStatus(),
                g.getRemarks(), g.getCreatedAt(), g.getUpdatedAt());
    }
}

