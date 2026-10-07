package com.siec_acc.dto.request;
import java.util.List;

public class SalesOrderRequestDto {

    private String customerId;
    private String date;
    private String expectedDelivery;
    private String deliveryLocation;
    private String paymentTerms;
    private String status;
    private Double discount;
    private Double tax;
    private String notes;
    private String quotationReference;
    private String piReference;
    private List<SalesOrderItemRequestDto> items;

    public SalesOrderRequestDto() {}

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

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

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getQuotationReference() { return quotationReference; }
    public void setQuotationReference(String quotationReference) { this.quotationReference = quotationReference; }

    public String getPiReference() { return piReference; }
    public void setPiReference(String piReference) { this.piReference = piReference; }

    public List<SalesOrderItemRequestDto> getItems() { return items; }
    public void setItems(List<SalesOrderItemRequestDto> items) { this.items = items; }
}

