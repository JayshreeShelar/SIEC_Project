package com.siec_acc.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoodsReceiptResponseDto {

    private Long grnPrimeId;
    private String grnStrId;
    private String grnNumber;
    private String poStrId;
    private String poNumber;
    private String vendorName;
    private String itemName;
    private String unit;
    private Integer orderedQty;
    private Integer receivedQty;
    private Integer acceptedQty;
    private Integer rejectedQty;
    private LocalDate receivedDate;
    private String challanNumber;
    private String receivedBy;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GoodsReceiptResponseDto() {}

    public GoodsReceiptResponseDto(Long grnPrimeId, String grnStrId, String grnNumber, String poStrId, String poNumber,
                                   String vendorName, String itemName, String unit, Integer orderedQty,
                                   Integer receivedQty, Integer acceptedQty, Integer rejectedQty, LocalDate receivedDate,
                                   String challanNumber, String receivedBy, String status, String remarks,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.grnPrimeId = grnPrimeId; this.grnStrId = grnStrId; this.grnNumber = grnNumber;
        this.poStrId = poStrId; this.poNumber = poNumber; this.vendorName = vendorName;
        this.itemName = itemName; this.unit = unit; this.orderedQty = orderedQty;
        this.receivedQty = receivedQty; this.acceptedQty = acceptedQty; this.rejectedQty = rejectedQty;
        this.receivedDate = receivedDate; this.challanNumber = challanNumber; this.receivedBy = receivedBy;
        this.status = status; this.remarks = remarks; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Long getGrnPrimeId() { return grnPrimeId; }
    public String getGrnStrId() { return grnStrId; }
    public String getGrnNumber() { return grnNumber; }
    public String getPoStrId() { return poStrId; }
    public String getPoNumber() { return poNumber; }
    public String getVendorName() { return vendorName; }
    public String getItemName() { return itemName; }
    public String getUnit() { return unit; }
    public Integer getOrderedQty() { return orderedQty; }
    public Integer getReceivedQty() { return receivedQty; }
    public Integer getAcceptedQty() { return acceptedQty; }
    public Integer getRejectedQty() { return rejectedQty; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public String getChallanNumber() { return challanNumber; }
    public String getReceivedBy() { return receivedBy; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}


