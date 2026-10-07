package com.siec_acc.dto.response;

public class PurchaseBillItemResponseDto {

    private String name;
    private Integer qty;
    private String unit;
    private Double rate;
    private Double gstPct;
    private Double amount;     // qty x rate, before GST
    private Double gstAmount;  // GST on that amount
    private Double total;      // amount + gstAmount

    public PurchaseBillItemResponseDto() {}

    public PurchaseBillItemResponseDto(String name, Integer qty, String unit, Double rate, Double gstPct,
                                       Double amount, Double gstAmount, Double total) {
        this.name = name;
        this.qty = qty;
        this.unit = unit;
        this.rate = rate;
        this.gstPct = gstPct;
        this.amount = amount;
        this.gstAmount = gstAmount;
        this.total = total;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public Double getGstPct() { return gstPct; }
    public void setGstPct(Double gstPct) { this.gstPct = gstPct; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getGstAmount() { return gstAmount; }
    public void setGstAmount(Double gstAmount) { this.gstAmount = gstAmount; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}

