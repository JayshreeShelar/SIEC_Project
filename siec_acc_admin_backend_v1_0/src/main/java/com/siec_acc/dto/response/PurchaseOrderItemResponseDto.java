package com.siec_acc.dto.response;

public class PurchaseOrderItemResponseDto {

    private Long itemId;
    private String name;
    private Integer qty;
    private String unit;
    private Double rate;
    private Double amount; // qty * rate, computed server-side

    public PurchaseOrderItemResponseDto() {}

    public PurchaseOrderItemResponseDto(Long itemId, String name, Integer qty, String unit, Double rate, Double amount) {
        this.itemId = itemId;
        this.name = name;
        this.qty = qty;
        this.unit = unit;
        this.rate = rate;
        this.amount = amount;
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}

