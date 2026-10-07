package com.siec_acc.dto.response;

import java.util.List;

public class SalesOrderResponseDto {

    private String id;
    private String number;
    private String customerId;
    private String customerName;
    private String date;
    private String expectedDelivery;
    private String deliveryLocation;
    private String paymentTerms;
    private String status;
    private String fulfillmentStatus;
    private List<SalesOrderItemResponseDto> items;
    private Double discount;
    private Double tax;
    private Double subtotal;
    private Double total;
    private String notes;
    private String quotationReference;
    private String piReference;

    public SalesOrderResponseDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(String expectedDelivery) { this.expectedDelivery = expectedDelivery; }

    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFulfillmentStatus() { return fulfillmentStatus; }
    public void setFulfillmentStatus(String fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }

    public List<SalesOrderItemResponseDto> getItems() { return items; }
    public void setItems(List<SalesOrderItemResponseDto> items) { this.items = items; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getQuotationReference() { return quotationReference; }
    public void setQuotationReference(String quotationReference) { this.quotationReference = quotationReference; }

    public String getPiReference() { return piReference; }
    public void setPiReference(String piReference) { this.piReference = piReference; }
}

