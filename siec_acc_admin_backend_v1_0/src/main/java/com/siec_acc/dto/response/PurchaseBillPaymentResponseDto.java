package com.siec_acc.dto.response;

import java.time.LocalDate;

public class PurchaseBillPaymentResponseDto {

    private Long paymentId;
    private LocalDate date;
    private Double amount;
    private String mode;
    private String ref;

    public PurchaseBillPaymentResponseDto() {}

    public PurchaseBillPaymentResponseDto(Long paymentId, LocalDate date, Double amount, String mode, String ref) {
        this.paymentId = paymentId;
        this.date = date;
        this.amount = amount;
        this.mode = mode;
        this.ref = ref;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
}

