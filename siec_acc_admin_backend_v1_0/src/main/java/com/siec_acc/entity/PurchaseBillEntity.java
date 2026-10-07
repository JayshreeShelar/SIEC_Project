package com.siec_acc.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "purchase_bills",
        indexes = {
                @Index(name = "idx_pb_vendor_name", columnList = "vendor_name"),
                @Index(name = "idx_pb_po_number", columnList = "po_number"),
                @Index(name = "idx_pb_due_date", columnList = "due_date"),
                @Index(name = "idx_pb_created_at", columnList = "created_at")
        }
)
public class PurchaseBillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pb_prime_id")
    private Long pbPrimeId;

    @Column(name = "pb_str_id", unique = true, length = 30)
    private String pbStrId;

    @Column(name = "pb_number", unique = true, length = 30)
    private String pbNumber;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    // Optional link to a Purchase Order; not a hard FK so a PO can still be referenced by number even if deleted.
    @Column(name = "po_number", length = 30)
    private String poNumber;

    @Column(name = "bill_date")
    private LocalDate billDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount", nullable = false)
    private Double amount;

    // Status (unpaid / partially paid / paid / overdue) is intentionally NOT stored —
    // it is always derived from `payments` + `dueDate` at read time, same as the frontend.
    @OneToMany(mappedBy = "purchaseBill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseBillPaymentEntity> payments = new ArrayList<>();

    // Line items. Bills created before this field existed simply have no rows here (their stored amount is kept).
    @OneToMany(mappedBy = "purchaseBill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseBillItemEntity> items = new ArrayList<>();

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

    public PurchaseBillEntity() {}

    public Long getPbPrimeId() { return pbPrimeId; }
    public void setPbPrimeId(Long pbPrimeId) { this.pbPrimeId = pbPrimeId; }

    public String getPbStrId() { return pbStrId; }
    public void setPbStrId(String pbStrId) { this.pbStrId = pbStrId; }

    public String getPbNumber() { return pbNumber; }
    public void setPbNumber(String pbNumber) { this.pbNumber = pbNumber; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public List<PurchaseBillPaymentEntity> getPayments() { return payments; }

    public List<PurchaseBillItemEntity> getItems() { return items; }

    /** Replaces the whole line-item list while keeping the bidirectional link intact (orphanRemoval deletes old rows). */
    public void replaceItems(List<PurchaseBillItemEntity> newItems) {
        this.items.clear();
        if (newItems != null) {
            for (PurchaseBillItemEntity it : newItems) {
                it.setPurchaseBill(this);
                this.items.add(it);
            }
        }
    }

    public void addPayment(PurchaseBillPaymentEntity payment) {
        payment.setPurchaseBill(this);
        this.payments.add(payment);
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


