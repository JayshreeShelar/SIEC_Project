package com.siec_acc.dto.response;

public class SalesOrderItemResponseDto {

    private String itemId;
    private String name;
    private Integer qty;
    private Double rate;

    public SalesOrderItemResponseDto() {}

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }
}

