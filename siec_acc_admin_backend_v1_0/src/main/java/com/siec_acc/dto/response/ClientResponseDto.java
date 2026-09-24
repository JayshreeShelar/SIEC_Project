package com.siec_acc.dto.response;

import java.math.BigDecimal;

/**
 * RESPONSE PAYLOAD  ->  one customer object, exactly like an item of the "customers" array.
 */
public class ClientResponseDto {

    private String id;
    private String name;
    private String companyName;
    private String customerType;
    private String gstin;
    private String pan;
    private String email;
    private String phone;
    private String billingAddress;
    private String shippingAddress;
    private String city;
    private String state;
    private String stateCode;
    private String pincode;
    private String paymentTerms;
    private BigDecimal creditLimit;
    private Boolean gstRegistered;
    private String status;
    private BigDecimal receivable;
    private String createdAt;   // yyyy-MM-dd
    private String color;

    public ClientResponseDto() {
    }

    public ClientResponseDto(String id, String name, String companyName, String customerType, String gstin,
                             String pan, String email, String phone, String billingAddress, String shippingAddress,
                             String city, String state, String stateCode, String pincode, String paymentTerms,
                             BigDecimal creditLimit, Boolean gstRegistered, String status, BigDecimal receivable,
                             String createdAt, String color) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
        this.customerType = customerType;
        this.gstin = gstin;
        this.pan = pan;
        this.email = email;
        this.phone = phone;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.city = city;
        this.state = state;
        this.stateCode = stateCode;
        this.pincode = pincode;
        this.paymentTerms = paymentTerms;
        this.creditLimit = creditLimit;
        this.gstRegistered = gstRegistered;
        this.status = status;
        this.receivable = receivable;
        this.createdAt = createdAt;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Boolean getGstRegistered() {
        return gstRegistered;
    }

    public void setGstRegistered(Boolean gstRegistered) {
        this.gstRegistered = gstRegistered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getReceivable() {
        return receivable;
    }

    public void setReceivable(BigDecimal receivable) {
        this.receivable = receivable;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}