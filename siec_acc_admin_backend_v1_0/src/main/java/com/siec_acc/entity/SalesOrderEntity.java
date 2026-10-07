package com.siec_acc.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
public class SalesOrderEntity {

    @Id
    private String id;

    private String number;
    private String customerId;
    private LocalDate date;
    private LocalDate expectedDelivery;
    private String deliveryLocation;
    private String paymentTerms;
    private String status;
    private String fulfillmentStatus;
    private Double discount;
    private Double tax;
    private String notes;
    private String quotationReference;
    private String piReference;
    private Double subtotal;
    private Double total;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItemEntity> items = new ArrayList<>();

    public SalesOrderEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDate getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(LocalDate expectedDelivery) { this.expectedDelivery = expectedDelivery; }

    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFulfillmentStatus() { return fulfillmentStatus; }
    public void setFulfillmentStatus(String fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }

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

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<SalesOrderItemEntity> getItems() { return items; }
    public void setItems(List<SalesOrderItemEntity> items) { this.items = items; }
}


