package com.siec_acc.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseResponseDto {

    private Long purchasePrimeId;
    private String purchaseStrId;
    private String purchaseNumber;
    private String itemName;
    private Integer quantity;
    private String unit;
    private String requestedBy;
    private String department;
    private LocalDate requiredDate;
    private String priority;
    private String status;
    private String remarks;
    private String rejectionReason;
    private String convertedPoNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PurchaseResponseDto() {}

    public PurchaseResponseDto(Long purchasePrimeId, String purchaseStrId, String purchaseNumber, String itemName,
                               Integer quantity, String unit, String requestedBy, String department,
                               LocalDate requiredDate, String priority, String status, String remarks,
                               String rejectionReason, String convertedPoNumber,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.purchasePrimeId = purchasePrimeId;
        this.purchaseStrId = purchaseStrId;
        this.purchaseNumber = purchaseNumber;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.requestedBy = requestedBy;
        this.department = department;
        this.requiredDate = requiredDate;
        this.priority = priority;
        this.status = status;
        this.remarks = remarks;
        this.rejectionReason = rejectionReason;
        this.convertedPoNumber = convertedPoNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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