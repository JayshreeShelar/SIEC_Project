package com.siec_acc.dto.request;


public class SalesOrderItemRequestDto {

    private String itemId;
    private Integer qty;
    private Double rate;

    public SalesOrderItemRequestDto() {}

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }
}
