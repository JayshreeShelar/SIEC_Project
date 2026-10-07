package com.siec_acc.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class PurchaseBillRequestDto {

    @NotBlank(message = "Vendor name is required")
    @Size(max = 255, message = "Vendor name must be at most 255 characters")
    private String vendorName;

    // Optional - set this to link the bill to an existing Purchase Order.
    @Size(max = 30, message = "PO number must be at most 30 characters")
    private String poNumber;

    private LocalDate billDate;
    private LocalDate dueDate;

    // The bill amount is NOT accepted from the client any more: the server works it out from these lines.
    @NotEmpty(message = "Add at least one item to the bill")
    @Valid
    private List<PurchaseBillItemRequestDto> items;

    public PurchaseBillRequestDto() {}

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public List<PurchaseBillItemRequestDto> getItems() { return items; }
    public void setItems(List<PurchaseBillItemRequestDto> items) { this.items = items; }
}


