package com.siec_acc.dto.request;


import lombok.*;
import java.math.BigDecimal;

@Builder
public class InventoryStockUpdateDTO {
    private BigDecimal changeQty; // must be > 0; direction decided by endpoint (add/reduce)
    private String remarks;

    public InventoryStockUpdateDTO(BigDecimal changeQty, String remarks) {
        this.changeQty = changeQty;
        this.remarks = remarks;
    }

    public InventoryStockUpdateDTO(){}

    public BigDecimal getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(BigDecimal changeQty) {
        this.changeQty = changeQty;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}