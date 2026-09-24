package com.siec_acc.dto.response;


import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public class InventoryResponseDTO {
    private Long inventoryPrimeId;
    private String inventoryStrId;
    private String productStrId;
    private BigDecimal productStock;
    private LocalDateTime inventoryUpdatedAt;

    public InventoryResponseDTO(Long inventoryPrimeId, String inventoryStrId, String productStrId, BigDecimal productStock, LocalDateTime inventoryUpdatedAt) {
        this.inventoryPrimeId = inventoryPrimeId;
        this.inventoryStrId = inventoryStrId;
        this.productStrId = productStrId;
        this.productStock = productStock;
        this.inventoryUpdatedAt = inventoryUpdatedAt;
    }

    public InventoryResponseDTO(){}

    public Long getInventoryPrimeId() {
        return inventoryPrimeId;
    }

    public void setInventoryPrimeId(Long inventoryPrimeId) {
        this.inventoryPrimeId = inventoryPrimeId;
    }

    public String getInventoryStrId() {
        return inventoryStrId;
    }

    public void setInventoryStrId(String inventoryStrId) {
        this.inventoryStrId = inventoryStrId;
    }

    public String getProductStrId() {
        return productStrId;
    }

    public void setProductStrId(String productStrId) {
        this.productStrId = productStrId;
    }

    public BigDecimal getProductStock() {
        return productStock;
    }

    public void setProductStock(BigDecimal productStock) {
        this.productStock = productStock;
    }

    public LocalDateTime getInventoryUpdatedAt() {
        return inventoryUpdatedAt;
    }

    public void setInventoryUpdatedAt(LocalDateTime inventoryUpdatedAt) {
        this.inventoryUpdatedAt = inventoryUpdatedAt;
    }
}