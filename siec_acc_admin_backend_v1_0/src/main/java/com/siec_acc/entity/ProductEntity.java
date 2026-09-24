package com.siec_acc.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_prime_id")
    private Long productPrimeId;

    @Column(name = "product_str_id", unique = true, length = 30)
    private String productStrId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_sku")
    private String productSku;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_sub_category")
    private String productSubCategory;

    @Column(name = "product_hsn_code")
    private String productHsnCode;

    @Column(name = "product_unit")
    private String productUnit;

    @Column(name = "product_height")
    private String productHeight;

    @Column(name = "product_width")
    private String productWidth;

    @Column(name = "product_length")
    private String productLength;

    @Column(name = "product_material_type")
    private String productMaterialType;

    @Column(name = "product_size")
    private String productSize;

    @Column(name = "product_number")
    private String productNumber;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "product_selling_price", precision = 15, scale = 2)
    private BigDecimal productSellingPrice;

    @Column(name = "product_mrp_price", precision = 15, scale = 2)
    private BigDecimal productMrpPrice;

    @Column(name = "product_gst_rate")
    private String productGstRate;

    @Column(name = "product_vendor_name")
    private String productVendorName;

    @Column(name = "product_vendor_company")
    private String productVendorCompany;

    @Column(name = "product_status", length = 20)
    private String productStatus;

    @Column(name = "product_created_at")
    private LocalDateTime productCreatedAt;

    @Column(name = "product_updated_at")
    private LocalDateTime productUpdatedAt;

    @PrePersist
    protected void onCreate() {
        this.productCreatedAt = LocalDateTime.now();
        this.productUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.productUpdatedAt = LocalDateTime.now();
    }

    public ProductEntity(){}

    public ProductEntity(Long productPrimeId, String productStrId, String productName, String productSku, String productCategory, String productSubCategory, String productHsnCode, String productUnit, String productHeight, String productWidth, String productLength, String productMaterialType, String productSize, String productNumber, String productDescription, BigDecimal productSellingPrice, BigDecimal productMrpPrice, String productGstRate, String productVendorName, String productVendorCompany, String productStatus, LocalDateTime productCreatedAt, LocalDateTime productUpdatedAt) {
        this.productPrimeId = productPrimeId;
        this.productStrId = productStrId;
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
        this.productStatus = productStatus;
        this.productCreatedAt = productCreatedAt;
        this.productUpdatedAt = productUpdatedAt;
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

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public LocalDateTime getProductCreatedAt() {
        return productCreatedAt;
    }

    public void setProductCreatedAt(LocalDateTime productCreatedAt) {
        this.productCreatedAt = productCreatedAt;
    }

    public LocalDateTime getProductUpdatedAt() {
        return productUpdatedAt;
    }

    public void setProductUpdatedAt(LocalDateTime productUpdatedAt) {
        this.productUpdatedAt = productUpdatedAt;
    }
}