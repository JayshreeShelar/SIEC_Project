package com.siec_acc.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public class PurchaseOrderPatchDto {

    @Pattern(regexp = ".*\\S.*", message = "Vendor name cannot be blank")
    @Size(max = 255, message = "Vendor name must be at most 255 characters")
    private String vendorName;

    private LocalDate poDate;

    private LocalDate deliveryDate;

    @DecimalMin(value = "0.0", message = "GST % cannot be negative")
    @DecimalMax(value = "100.0", message = "GST % cannot exceed 100")
    private Double taxPct;

    @Pattern(regexp = "(?i)draft|approved|sent|partial|fully_received|closed",
            message = "Status must be one of draft, approved, sent, partial, fully_received, closed")
    private String status;

    @Valid
    private List<PurchaseOrderItemRequestDto> items;

    public PurchaseOrderPatchDto() {}

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

    public List<PurchaseOrderItemRequestDto> getItems() { return items; }
    public void setItems(List<PurchaseOrderItemRequestDto> items) { this.items = items; }
}

