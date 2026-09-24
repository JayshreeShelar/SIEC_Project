package com.siec_acc.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class VariantRequestDTO {
    private String productStrId; // required only on create, to link the parent product
    private String variantName;
    private String variantSku;
    private String variantHeight;
    private String variantWidth;
    private String variantLength;
    private String variantUnit;
    private String variantMaterialType;
    private String variantSize;
    private String variantProductNumber;
    private String variantCategory;
    private String variantSubCategory;
    private BigDecimal variantStock;


    public VariantRequestDTO(String productStrId, String variantName, String variantSku, String variantHeight, String variantWidth, String variantLength, String variantUnit, String variantMaterialType, String variantSize, String variantProductNumber, String variantCategory, String variantSubCategory, BigDecimal variantStock) {
        this.productStrId = productStrId;
        this.variantName = variantName;
        this.variantSku = variantSku;
        this.variantHeight = variantHeight;
        this.variantWidth = variantWidth;
        this.variantLength = variantLength;
        this.variantUnit = variantUnit;
        this.variantMaterialType = variantMaterialType;
        this.variantSize = variantSize;
        this.variantProductNumber = variantProductNumber;
        this.variantCategory = variantCategory;
        this.variantSubCategory = variantSubCategory;
        this.variantStock = variantStock;
    }

    public VariantRequestDTO(){}

    public String getProductStrId() {
        return productStrId;
    }

    public void setProductStrId(String productStrId) {
        this.productStrId = productStrId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getVariantSku() {
        return variantSku;
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }

    public String getVariantHeight() {
        return variantHeight;
    }

    public void setVariantHeight(String variantHeight) {
        this.variantHeight = variantHeight;
    }

    public String getVariantWidth() {
        return variantWidth;
    }

    public void setVariantWidth(String variantWidth) {
        this.variantWidth = variantWidth;
    }

    public String getVariantLength() {
        return variantLength;
    }

    public void setVariantLength(String variantLength) {
        this.variantLength = variantLength;
    }

    public String getVariantUnit() {
        return variantUnit;
    }

    public void setVariantUnit(String variantUnit) {
        this.variantUnit = variantUnit;
    }

    public String getVariantMaterialType() {
        return variantMaterialType;
    }

    public void setVariantMaterialType(String variantMaterialType) {
        this.variantMaterialType = variantMaterialType;
    }

    public String getVariantSize() {
        return variantSize;
    }

    public void setVariantSize(String variantSize) {
        this.variantSize = variantSize;
    }

    public String getVariantProductNumber() {
        return variantProductNumber;
    }

    public void setVariantProductNumber(String variantProductNumber) {
        this.variantProductNumber = variantProductNumber;
    }

    public String getVariantCategory() {
        return variantCategory;
    }

    public void setVariantCategory(String variantCategory) {
        this.variantCategory = variantCategory;
    }

    public String getVariantSubCategory() {
        return variantSubCategory;
    }

    public void setVariantSubCategory(String variantSubCategory) {
        this.variantSubCategory = variantSubCategory;
    }

    public BigDecimal getVariantStock() {
        return variantStock;
    }

    public void setVariantStock(BigDecimal variantStock) {
        this.variantStock = variantStock;
    }
}