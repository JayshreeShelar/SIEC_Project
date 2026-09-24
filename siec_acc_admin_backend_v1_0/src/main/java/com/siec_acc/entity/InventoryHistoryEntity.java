package com.siec_acc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_history")
@Builder
public class InventoryHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_prime_id")
    private Long historyPrimeId;

    @Column(name = "history_str_id", unique = true, length = 30)
    private String historyStrId;

    @Column(name = "product_prime_id")
    private Long productPrimeId;

    @Column(name = "product_str_id")
    private String productStrId;

    @Column(name = "variant_prime_id")
    private Long variantPrimeId;

    @Column(name = "variant_str_id")
    private String variantStrId;

    @Column(name = "history_change_type", length = 30)
    private String historyChangeType; // CREATE, STOCK_ADD, STOCK_REDUCE, MANUAL_UPDATE, VENDOR_PURCHASE_ADD

    @Column(name = "history_previous_stock", precision = 15, scale = 2)
    private BigDecimal historyPreviousStock;

    @Column(name = "history_new_stock", precision = 15, scale = 2)
    private BigDecimal historyNewStock;

    @Column(name = "history_change_qty", precision = 15, scale = 2)
    private BigDecimal historyChangeQty;

    @Column(name = "history_remarks")
    private String historyRemarks;

    @Column(name = "history_created_at")
    private LocalDateTime historyCreatedAt;

    @PrePersist
    protected void onCreate() {
        this.historyCreatedAt = LocalDateTime.now();
    }

    public InventoryHistoryEntity(Long historyPrimeId, String historyStrId, Long productPrimeId, String productStrId, Long variantPrimeId, String variantStrId, String historyChangeType, BigDecimal historyPreviousStock, BigDecimal historyNewStock, BigDecimal historyChangeQty, String historyRemarks, LocalDateTime historyCreatedAt) {
        this.historyPrimeId = historyPrimeId;
        this.historyStrId = historyStrId;
        this.productPrimeId = productPrimeId;
        this.productStrId = productStrId;
        this.variantPrimeId = variantPrimeId;
        this.variantStrId = variantStrId;
        this.historyChangeType = historyChangeType;
        this.historyPreviousStock = historyPreviousStock;
        this.historyNewStock = historyNewStock;
        this.historyChangeQty = historyChangeQty;
        this.historyRemarks = historyRemarks;
        this.historyCreatedAt = historyCreatedAt;
    }
    public InventoryHistoryEntity() {}

    public Long getHistoryPrimeId() {
        return historyPrimeId;
    }

    public void setHistoryPrimeId(Long historyPrimeId) {
        this.historyPrimeId = historyPrimeId;
    }

    public String getHistoryStrId() {
        return historyStrId;
    }

    public void setHistoryStrId(String historyStrId) {
        this.historyStrId = historyStrId;
    }

    public Long getProductPrimeId() {
        return productPrimeId;
    }

    public void setProductPrimeId(Long productPrimeId) {
        this.productPrimeId = productPrimeId;
    }

    public String getProductStrId() {
        return productStrId;
    }

    public void setProductStrId(String productStrId) {
        this.productStrId = productStrId;
    }

    public Long getVariantPrimeId() {
        return variantPrimeId;
    }

    public void setVariantPrimeId(Long variantPrimeId) {
        this.variantPrimeId = variantPrimeId;
    }

    public String getVariantStrId() {
        return variantStrId;
    }

    public void setVariantStrId(String variantStrId) {
        this.variantStrId = variantStrId;
    }

    public String getHistoryChangeType() {
        return historyChangeType;
    }

    public void setHistoryChangeType(String historyChangeType) {
        this.historyChangeType = historyChangeType;
    }

    public BigDecimal getHistoryPreviousStock() {
        return historyPreviousStock;
    }

    public void setHistoryPreviousStock(BigDecimal historyPreviousStock) {
        this.historyPreviousStock = historyPreviousStock;
    }

    public BigDecimal getHistoryNewStock() {
        return historyNewStock;
    }

    public void setHistoryNewStock(BigDecimal historyNewStock) {
        this.historyNewStock = historyNewStock;
    }

    public BigDecimal getHistoryChangeQty() {
        return historyChangeQty;
    }

    public void setHistoryChangeQty(BigDecimal historyChangeQty) {
        this.historyChangeQty = historyChangeQty;
    }

    public String getHistoryRemarks() {
        return historyRemarks;
    }

    public void setHistoryRemarks(String historyRemarks) {
        this.historyRemarks = historyRemarks;
    }

    public LocalDateTime getHistoryCreatedAt() {
        return historyCreatedAt;
    }

    public void setHistoryCreatedAt(LocalDateTime historyCreatedAt) {
        this.historyCreatedAt = historyCreatedAt;
    }
}