package com.siec_acc.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Builder
public class ProductRequestDTO {
    private String productName;
    private String productSku;
    private String productCategory;
    private String productSubCategory;
    private String productHsnCode;
    private String productUnit;
    private String productHeight;
    private String productWidth;
    private String productLength;
    private String productMaterialType;
    private String productSize;
    private String productNumber;
    private String productDescription;
    private BigDecimal productSellingPrice;
    private BigDecimal productMrpPrice;
    private String productGstRate;
    private String productVendorName;
    private String productVendorCompany;
    private BigDecimal productStock; // opening/updated stock -> written to Inventory


    public ProductRequestDTO(String productName, String productSku, String productCategory, String productSubCategory, String productHsnCode, String productUnit, String productHeight, String productWidth, String productLength, String productMaterialType, String productSize, String productNumber, String productDescription, BigDecimal productSellingPrice, BigDecimal productMrpPrice, String productGstRate, String productVendorName, String productVendorCompany, BigDecimal productStock) {
        this.productName = productName;
        this.productSku = productSku;
        this.productCategory = productCategory;
        this.productSubCategory = productSubCategory;
        this.productHsnCode = productHsnCode;
        this.productUnit = productUnit;
        this.productHeight = productHeight;
        this.productWidth = productWidth;
        this.productLength = productLength;
        this.productMaterialType = productMaterialType;
        this.productSize = productSize;
        this.productNumber = productNumber;
        this.productDescription = productDescription;
        this.productSellingPrice = productSellingPrice;
        this.productMrpPrice = productMrpPrice;
        this.productGstRate = productGstRate;
        this.productVendorName = productVendorName;
        this.productVendorCompany = productVendorCompany;
        this.productStock = productStock;
    }

    public ProductRequestDTO(){}

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductSubCategory() {
        return productSubCategory;
    }

    public void setProductSubCategory(String productSubCategory) {
        this.productSubCategory = productSubCategory;
    }

    public String getProductHsnCode() {
        return productHsnCode;
    }

    public void setProductHsnCode(String productHsnCode) {
        this.productHsnCode = productHsnCode;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductHeight() {
        return productHeight;
    }

    public void setProductHeight(String productHeight) {
        this.productHeight = productHeight;
    }

    public String getProductWidth() {
        return productWidth;
    }

    public void setProductWidth(String productWidth) {
        this.productWidth = productWidth;
    }

    public String getProductLength() {
        return productLength;
    }

    public void setProductLength(String productLength) {
        this.productLength = productLength;
    }

    public String getProductMaterialType() {
        return productMaterialType;
    }

    public void setProductMaterialType(String productMaterialType) {
        this.productMaterialType = productMaterialType;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getProductSellingPrice() {
        return productSellingPrice;
    }

    public void setProductSellingPrice(BigDecimal productSellingPrice) {
        this.productSellingPrice = productSellingPrice;
    }

    public BigDecimal getProductMrpPrice() {
        return productMrpPrice;
    }

    public void setProductMrpPrice(BigDecimal productMrpPrice) {
        this.productMrpPrice = productMrpPrice;
    }

    public String getProductGstRate() {
        return productGstRate;
    }

    public void setProductGstRate(String productGstRate) {
        this.productGstRate = productGstRate;
    }

    public String getProductVendorName() {
        return productVendorName;
    }

    public void setProductVendorName(String productVendorName) {
        this.productVendorName = productVendorName;
    }

    public String getProductVendorCompany() {
        return productVendorCompany;
    }

    public void setProductVendorCompany(String productVendorCompany) {
        this.productVendorCompany = productVendorCompany;
    }

    public BigDecimal getProductStock() {
        return productStock;
    }

    public void setProductStock(BigDecimal productStock) {
        this.productStock = productStock;
    }
}