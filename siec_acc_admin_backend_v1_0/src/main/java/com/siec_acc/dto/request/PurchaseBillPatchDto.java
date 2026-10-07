package com.siec_acc.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PurchaseBillPatchDto {

    @Pattern(regexp = ".*\\S.*", message = "Vendor name cannot be blank")
    @Size(max = 255, message = "Vendor name must be at most 255 characters")
    private String vendorName;

    @Size(max = 30, message = "PO number must be at most 30 characters")
    private String poNumber;

    private LocalDate billDate;
    private LocalDate dueDate;

    public PurchaseBillPatchDto() {}

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}

