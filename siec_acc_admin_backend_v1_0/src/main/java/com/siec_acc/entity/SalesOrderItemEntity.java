package com.siec_acc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_order_items")
public class SalesOrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemId;
    private Integer qty;
    private Double rate;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrderEntity salesOrder;

    public SalesOrderItemEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public SalesOrderEntity getSalesOrder() { return salesOrder; }
    public void setSalesOrder(SalesOrderEntity salesOrder) { this.salesOrder = salesOrder; }
}
