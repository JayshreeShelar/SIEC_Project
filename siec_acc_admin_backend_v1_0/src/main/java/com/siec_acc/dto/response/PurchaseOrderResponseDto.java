package com.siec_acc.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrderResponseDto {

    private Long poPrimeId;
    private String poStrId;
    private String poNumber;
    private String vendorName;
    private LocalDate poDate;
    private LocalDate deliveryDate;
    private Double taxPct;
    private String status;
    private String sourcePurchaseStrId;
    private List<PurchaseOrderItemResponseDto> items;

    // Computed, never stored: kept consistent with the frontend's poSubtotal()/poTax()/poTotal().
    private Double subtotal;
    private Double taxAmount;
    private Double total;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PurchaseOrderResponseDto() {}

    public PurchaseOrderResponseDto(Long poPrimeId, String poStrId, String poNumber, String vendorName,
                                    LocalDate poDate, LocalDate deliveryDate, Double taxPct, String status,
                                    String sourcePurchaseStrId, List<PurchaseOrderItemResponseDto> items,
                                    Double subtotal, Double taxAmount, Double total,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.poPrimeId = poPrimeId;
        this.poStrId = poStrId;
        this.poNumber = poNumber;
        this.vendorName = vendorName;
        this.poDate = poDate;
        this.deliveryDate = deliveryDate;
        this.taxPct = taxPct;
        this.status = status;
        this.sourcePurchaseStrId = sourcePurchaseStrId;
        this.items = items;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.total = total;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public List<PurchaseOrderItemResponseDto> getItems() { return items; }
    public void setItems(List<PurchaseOrderItemResponseDto> items) { this.items = items; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

