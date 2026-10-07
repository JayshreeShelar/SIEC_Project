package com.siec_acc.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseBillResponseDto {

    private Long pbPrimeId;
    private String pbStrId;
    private String pbNumber;
    private String vendorName;
    private String poNumber;
    private LocalDate billDate;
    private LocalDate dueDate;

    private List<PurchaseBillItemResponseDto> items;
    private Double subtotal;   // sum of line amounts before GST
    private Double gstAmount;  // sum of line GST
    private Double amount;     // grand total (subtotal + GST)

    // Computed, never stored: kept consistent with the frontend's pbPaid()/pbBalance()/pbStatus().
    private Double paidAmount;
    private Double balance;
    private String status; // received (unpaid) | partial | paid | overdue
    private List<PurchaseBillPaymentResponseDto> payments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PurchaseBillResponseDto() {}

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

    public List<PurchaseBillItemResponseDto> getItems() { return items; }
    public void setItems(List<PurchaseBillItemResponseDto> items) { this.items = items; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getGstAmount() { return gstAmount; }
    public void setGstAmount(Double gstAmount) { this.gstAmount = gstAmount; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<PurchaseBillPaymentResponseDto> getPayments() { return payments; }
    public void setPayments(List<PurchaseBillPaymentResponseDto> payments) { this.payments = payments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

