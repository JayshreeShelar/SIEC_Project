package com.siec_acc.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "purchase_orders",
        indexes = {
                @Index(name = "idx_po_status", columnList = "status"),
                @Index(name = "idx_po_vendor_name", columnList = "vendor_name"),
                @Index(name = "idx_po_created_at", columnList = "created_at")
        }
)
public class PurchaseOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_prime_id")
    private Long poPrimeId;

    // Generated right after the first save (needs the DB id), same pattern as PurchaseEntity.
    @Column(name = "po_str_id", unique = true, length = 30)
    private String poStrId;

    @Column(name = "po_number", unique = true, length = 30)
    private String poNumber;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "po_date")
    private LocalDate poDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "tax_pct")
    private Double taxPct;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Set when this PO was created by converting an approved Purchase Requirement (see PurchaseEntity.convertedPoNumber).
    @Column(name = "source_purchase_str_id", length = 30)
    private String sourcePurchaseStrId;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderItemEntity> items = new ArrayList<>();

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

    public PurchaseOrderEntity() {}

    public Long getPoPrimeId() { return poPrimeId; }
    public void setPoPrimeId(Long poPrimeId) { this.poPrimeId = poPrimeId; }

    public String getPoStrId() { return poStrId; }
    public void setPoStrId(String poStrId) { this.poStrId = poStrId; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public LocalDate getPoDate() { return poDate; }
    public void setPoDate(LocalDate poDate) { this.poDate = poDate; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public Double getTaxPct() { return taxPct; }
    public void setTaxPct(Double taxPct) { this.taxPct = taxPct; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSourcePurchaseStrId() { return sourcePurchaseStrId; }
    public void setSourcePurchaseStrId(String sourcePurchaseStrId) { this.sourcePurchaseStrId = sourcePurchaseStrId; }

    public List<PurchaseOrderItemEntity> getItems() { return items; }

    /** Replaces the whole line-item list while keeping the bidirectional link intact (orphanRemoval deletes the old rows). */
    public void setItems(List<PurchaseOrderItemEntity> newItems) {
        this.items.clear();
        if (newItems != null) {
            for (PurchaseOrderItemEntity it : newItems) {
                it.setPurchaseOrder(this);
                this.items.add(it);
            }
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

