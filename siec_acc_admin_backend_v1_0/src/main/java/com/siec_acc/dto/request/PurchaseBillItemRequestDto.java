package com.siec_acc.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PurchaseBillItemRequestDto {

    @NotBlank(message = "Item name is required")
    @Size(max = 255, message = "Item name must be at most 255 characters")
    private String name;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer qty;

    @Size(max = 30, message = "Unit must be at most 30 characters")
    private String unit;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0")
    private Double rate;

    @DecimalMin(value = "0.0", message = "GST % cannot be negative")
    @DecimalMax(value = "100.0", message = "GST % cannot be more than 100")
    private Double gstPct;

    public PurchaseBillItemRequestDto() {}

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
}
