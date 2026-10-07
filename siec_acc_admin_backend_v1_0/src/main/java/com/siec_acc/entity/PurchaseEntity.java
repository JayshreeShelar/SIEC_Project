package com.siec_acc.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "purchases",
        indexes = {
                @Index(name = "idx_purchases_status", columnList = "status"),
                @Index(name = "idx_purchases_department", columnList = "department"),
                @Index(name = "idx_purchases_created_at", columnList = "created_at")
        }
)
public class PurchaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_prime_id")
    private Long purchasePrimeId;

    // Generated right after the first save (needs the DB id), so these two stay nullable at insert time.
    @Column(name = "purchase_str_id", unique = true, length = 30)
    private String purchaseStrId;

    @Column(name = "purchase_number", unique = true, length = 30)
    private String purchaseNumber;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit", length = 30)
    private String unit;

    @Column(name = "requested_by", nullable = false)
    private String requestedBy;

    @Column(name = "department")
    private String department;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Separate from remarks so rejecting never overwrites the requester's original note.
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Set by the Purchase Order module when this requirement is converted into a PO.
    @Column(name = "converted_po_number", length = 30)
    private String convertedPoNumber;

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

    public PurchaseEntity() {}

    public Long getPurchasePrimeId() { return purchasePrimeId; }
    public void setPurchasePrimeId(Long purchasePrimeId) { this.purchasePrimeId = purchasePrimeId; }

    public String getPurchaseStrId() { return purchaseStrId; }
    public void setPurchaseStrId(String purchaseStrId) { this.purchaseStrId = purchaseStrId; }

    public String getPurchaseNumber() { return purchaseNumber; }
    public void setPurchaseNumber(String purchaseNumber) { this.purchaseNumber = purchaseNumber; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getRequiredDate() { return requiredDate; }
    public void setRequiredDate(LocalDate requiredDate) { this.requiredDate = requiredDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getConvertedPoNumber() { return convertedPoNumber; }
    public void setConvertedPoNumber(String convertedPoNumber) { this.convertedPoNumber = convertedPoNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

