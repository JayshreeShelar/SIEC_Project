package com.siec_acc.entity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** One delivery of ONE PO line item. A PO with several items gets one GRN per item per delivery. */
@Entity
@Table(
        name = "goods_receipts",
        indexes = {
                @Index(name = "idx_grn_po_str_id", columnList = "po_str_id"),
                @Index(name = "idx_grn_status", columnList = "status"),
                @Index(name = "idx_grn_created_at", columnList = "created_at")
        }
)
public class GoodsReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grn_prime_id")
    private Long grnPrimeId;

    @Column(name = "grn_str_id", unique = true, length = 30)
    private String grnStrId;

    @Column(name = "grn_number", unique = true, length = 30)
    private String grnNumber;

    // Soft link by id/number (same convention as PurchaseBillEntity.poNumber), no hard FK.
    @Column(name = "po_str_id", nullable = false, length = 30)
    private String poStrId;

    @Column(name = "po_number", length = 30)
    private String poNumber;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    // Snapshot of the PO line item being received (matched to the PO by name, because PO items are replaced on edit).
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "unit", length = 30)
    private String unit;

    // Refreshed by GoodsReceiptServiceImpl.resyncPo() whenever the PO changes.
    @Column(name = "ordered_qty")
    private Integer orderedQty;

    @Column(name = "received_qty", nullable = false)
    private Integer receivedQty;

    @Column(name = "rejected_qty", nullable = false)
    private Integer rejectedQty = 0;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "challan_number", length = 100)
    private String challanNumber;

    @Column(name = "received_by")
    private String receivedBy;

    // partial | completed — derived (running total of this item on the PO vs. ordered qty), never client-supplied.
    @Column(name = "status", nullable = false, length = 20)
    private String status = "partial";

    @Column(name = "remarks", length = 2000)
    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public GoodsReceiptEntity() {}

    public Long getGrnPrimeId() { return grnPrimeId; }
    public void setGrnPrimeId(Long v) { this.grnPrimeId = v; }
    public String getGrnStrId() { return grnStrId; }
    public void setGrnStrId(String v) { this.grnStrId = v; }
    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String v) { this.grnNumber = v; }
    public String getPoStrId() { return poStrId; }
    public void setPoStrId(String v) { this.poStrId = v; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String v) { this.poNumber = v; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String v) { this.vendorName = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
    public String getUnit() { return unit; }
    public void setUnit(String v) { this.unit = v; }
    public Integer getOrderedQty() { return orderedQty; }
    public void setOrderedQty(Integer v) { this.orderedQty = v; }
    public Integer getReceivedQty() { return receivedQty; }
    public void setReceivedQty(Integer v) { this.receivedQty = v; }
    public Integer getRejectedQty() { return rejectedQty; }
    public void setRejectedQty(Integer v) { this.rejectedQty = v; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate v) { this.receivedDate = v; }
    public String getChallanNumber() { return challanNumber; }
    public void setChallanNumber(String v) { this.challanNumber = v; }
    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String v) { this.receivedBy = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String v) { this.remarks = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}

