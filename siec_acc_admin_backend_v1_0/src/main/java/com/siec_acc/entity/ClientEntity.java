package com.siec_acc.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_prime_id")
    private Long clientPrimeId;

    @Column(name = "client_str_id")
    private String clientStrId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_company_name")
    private String clientCompanyName;

    @Column(name = "client_customer_type", length = 30)
    private String clientCustomerType;

    @Column(name = "client_gstin", length = 20)
    private String clientGstin;

    @Column(name = "client_pan", length = 15)
    private String clientPan;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_phone", length = 30)
    private String clientPhone;

    @Column(name = "client_billing_address", columnDefinition = "TEXT")
    private String clientBillingAddress;

    @Column(name = "client_shipping_address", columnDefinition = "TEXT")
    private String clientShippingAddress;

    @Column(name = "client_city")
    private String clientCity;

    @Column(name = "client_state")
    private String clientState;

    @Column(name = "client_state_code", length = 10)
    private String clientStateCode;

    @Column(name = "client_pincode", length = 10)
    private String clientPincode;

    @Column(name = "client_payment_terms")
    private String clientPaymentTerms;

    @Column(name = "client_credit_limit", precision = 15, scale = 2)
    private BigDecimal clientCreditLimit;

    @Column(name = "client_gst_registered")
    private Boolean clientGstRegistered;

    @Column(name = "client_status", length = 20)
    private String clientStatus;

    @Column(name = "client_receivable", precision = 15, scale = 2)
    private BigDecimal clientReceivable;

    @Column(name = "client_created_at")
    private LocalDate clientCreatedAt;

    @Column(name = "client_color", length = 20)
    private String clientColor;

    @PrePersist
    protected void onCreate() {
        if (this.clientCreatedAt == null) {
            this.clientCreatedAt = LocalDate.now();
        }
    }

    public ClientEntity() {
    }

    public ClientEntity(Long clientPrimeId, String clientStrId, String clientName, String clientCompanyName,
                        String clientCustomerType, String clientGstin, String clientPan, String clientEmail,
                        String clientPhone, String clientBillingAddress, String clientShippingAddress,
                        String clientCity, String clientState, String clientStateCode, String clientPincode,
                        String clientPaymentTerms, BigDecimal clientCreditLimit, Boolean clientGstRegistered,
                        String clientStatus, BigDecimal clientReceivable, LocalDate clientCreatedAt,
                        String clientColor) {
        this.clientPrimeId = clientPrimeId;
        this.clientStrId = clientStrId;
        this.clientName = clientName;
        this.clientCompanyName = clientCompanyName;
        this.clientCustomerType = clientCustomerType;
        this.clientGstin = clientGstin;
        this.clientPan = clientPan;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.clientBillingAddress = clientBillingAddress;
        this.clientShippingAddress = clientShippingAddress;
        this.clientCity = clientCity;
        this.clientState = clientState;
        this.clientStateCode = clientStateCode;
        this.clientPincode = clientPincode;
        this.clientPaymentTerms = clientPaymentTerms;
        this.clientCreditLimit = clientCreditLimit;
        this.clientGstRegistered = clientGstRegistered;
        this.clientStatus = clientStatus;
        this.clientReceivable = clientReceivable;
        this.clientCreatedAt = clientCreatedAt;
        this.clientColor = clientColor;
    }

    public Long getClientPrimeId() {
        return clientPrimeId;
    }

    public void setClientPrimeId(Long clientPrimeId) {
        this.clientPrimeId = clientPrimeId;
    }

    public String getClientStrId() {
        return clientStrId;
    }

    public void setClientStrId(String clientStrId) {
        this.clientStrId = clientStrId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }

    public String getClientCustomerType() {
        return clientCustomerType;
    }

    public void setClientCustomerType(String clientCustomerType) {
        this.clientCustomerType = clientCustomerType;
    }

    public String getClientGstin() {
        return clientGstin;
    }

    public void setClientGstin(String clientGstin) {
        this.clientGstin = clientGstin;
    }

    public String getClientPan() {
        return clientPan;
    }

    public void setClientPan(String clientPan) {
        this.clientPan = clientPan;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientBillingAddress() {
        return clientBillingAddress;
    }

    public void setClientBillingAddress(String clientBillingAddress) {
        this.clientBillingAddress = clientBillingAddress;
    }

    public String getClientShippingAddress() {
        return clientShippingAddress;
    }

    public void setClientShippingAddress(String clientShippingAddress) {
        this.clientShippingAddress = clientShippingAddress;
    }

    public String getClientCity() {
        return clientCity;
    }

    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }

    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    public String getClientStateCode() {
        return clientStateCode;
    }

    public void setClientStateCode(String clientStateCode) {
        this.clientStateCode = clientStateCode;
    }

    public String getClientPincode() {
        return clientPincode;
    }

    public void setClientPincode(String clientPincode) {
        this.clientPincode = clientPincode;
    }

    public String getClientPaymentTerms() {
        return clientPaymentTerms;
    }

    public void setClientPaymentTerms(String clientPaymentTerms) {
        this.clientPaymentTerms = clientPaymentTerms;
    }

    public BigDecimal getClientCreditLimit() {
        return clientCreditLimit;
    }

    public void setClientCreditLimit(BigDecimal clientCreditLimit) {
        this.clientCreditLimit = clientCreditLimit;
    }

    public Boolean getClientGstRegistered() {
        return clientGstRegistered;
    }

    public void setClientGstRegistered(Boolean clientGstRegistered) {
        this.clientGstRegistered = clientGstRegistered;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    public BigDecimal getClientReceivable() {
        return clientReceivable;
    }

    public void setClientReceivable(BigDecimal clientReceivable) {
        this.clientReceivable = clientReceivable;
    }

    public LocalDate getClientCreatedAt() {
        return clientCreatedAt;
    }

    public void setClientCreatedAt(LocalDate clientCreatedAt) {
        this.clientCreatedAt = clientCreatedAt;
    }

    public String getClientColor() {
        return clientColor;
    }

    public void setClientColor(String clientColor) {
        this.clientColor = clientColor;
    }
}