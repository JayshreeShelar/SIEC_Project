package com.siec_acc.service;

import com.siec_acc.entity.GoodsReceiptEntity;
import com.siec_acc.entity.PurchaseBillEntity;
import com.siec_acc.entity.PurchaseBillItemEntity;
import com.siec_acc.entity.PurchaseBillPaymentEntity;
import com.siec_acc.entity.PurchaseOrderEntity;
import com.siec_acc.repository.GoodsReceiptRepository;
import com.siec_acc.repository.PurchaseBillRepository;
import com.siec_acc.repository.PurchaseOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class PurchaseOrderClosureService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderClosureService.class);
    private static final String FULLY_RECEIVED = "fully_received";
    private static final String CLOSED = "closed";
    private static final double EPSILON = 0.005;

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseBillRepository purchaseBillRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;

    public PurchaseOrderClosureService(PurchaseOrderRepository purchaseOrderRepository,
                                       PurchaseBillRepository purchaseBillRepository,
                                       GoodsReceiptRepository goodsReceiptRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseBillRepository = purchaseBillRepository;
        this.goodsReceiptRepository = goodsReceiptRepository;
    }

    public void reevaluate(String poNumber) {
        if (poNumber == null || poNumber.isBlank()) return;
        purchaseOrderRepository.findByPoNumber(poNumber.trim()).ifPresent(po -> reevaluate(po));
    }

    public void reevaluate(PurchaseOrderEntity po) {
        String status = lc(po.getStatus());
        if (!FULLY_RECEIVED.equals(status) && !CLOSED.equals(status)) return;

        boolean settled = isSettled(po);
        if (FULLY_RECEIVED.equals(status) && settled) {
            po.setStatus(CLOSED);
            purchaseOrderRepository.save(po);
            logger.info("{} closed: fully received, fully billed and fully paid", po.getPoNumber());
        } else if (CLOSED.equals(status) && !settled) {
            po.setStatus(FULLY_RECEIVED);
            purchaseOrderRepository.save(po);
            logger.info("{} re-opened: it is no longer fully billed and paid", po.getPoNumber());
        }
    }

    private boolean isSettled(PurchaseOrderEntity po) {
        List<PurchaseBillEntity> bills = purchaseBillRepository.findByPoNumberIgnoreCaseOrderByCreatedAtDesc(po.getPoNumber());
        if (bills.isEmpty()) return false;
        if (!bills.stream().allMatch(b -> balance(b) <= EPSILON)) return false;

        // Old bills (created before bill line items existed) have no item breakdown - trust their amount.
        if (bills.stream().anyMatch(b -> b.getItems().isEmpty())) return true;

        Map<String, Integer> accepted = new HashMap<>();
        for (GoodsReceiptEntity g : goodsReceiptRepository.findByPoStrIdOrderByGrnPrimeIdAsc(po.getPoStrId())) {
            int rejected = g.getRejectedQty() == null ? 0 : g.getRejectedQty();
            accepted.merge(lc(g.getItemName()), g.getReceivedQty() - rejected, Integer::sum);
        }
        Map<String, Integer> billed = new HashMap<>();
        for (PurchaseBillEntity b : bills) {
            for (PurchaseBillItemEntity it : b.getItems()) billed.merge(lc(it.getName()), it.getQty(), Integer::sum);
        }
        return accepted.entrySet().stream().allMatch(e -> billed.getOrDefault(e.getKey(), 0) >= e.getValue());
    }

    private double balance(PurchaseBillEntity bill) {
        double paid = bill.getPayments().stream().mapToDouble(PurchaseBillPaymentEntity::getAmount).sum();
        double amt = bill.getAmount() == null ? 0.0 : bill.getAmount();
        return Math.max(0.0, Math.round((amt - paid) * 100.0) / 100.0);
    }

    private String lc(String s) { return s == null ? "" : s.trim().toLowerCase(Locale.ROOT); }
}

