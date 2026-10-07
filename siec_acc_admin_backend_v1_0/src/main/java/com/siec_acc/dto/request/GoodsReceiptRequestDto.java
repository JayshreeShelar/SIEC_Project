package com.siec_acc.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class GoodsReceiptRequestDto {

    @NotBlank(message = "Purchase order is required")
    @Size(max = 30, message = "PO id must be at most 30 characters")
    private String poStrId;

    // Optional when the PO has exactly one item; required to pick the line when it has several.
    @Size(max = 255, message = "Item name must be at most 255 characters")
    private String itemName;

    @NotNull(message = "Received quantity is required")
    @Min(value = 1, message = "Received quantity must be at least 1")
    private Integer receivedQty;

    @Min(value = 0, message = "Rejected quantity cannot be negative")
    private Integer rejectedQty;

    @NotNull(message = "Received date is required")
    private LocalDate receivedDate;

    @Size(max = 100, message = "Challan number must be at most 100 characters")
    private String challanNumber;

    @Size(max = 255, message = "Received by must be at most 255 characters")
    private String receivedBy;

    @Size(max = 2000, message = "Remarks must be at most 2000 characters")
    private String remarks;

    public GoodsReceiptRequestDto() {}

    public String getPoStrId() { return poStrId; }
    public void setPoStrId(String v) { this.poStrId = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
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
    public String getRemarks() { return remarks; }
    public void setRemarks(String v) { this.remarks = v; }
}
