package com.siec_acc.dto.request;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PurchaseBillPaymentRequestDto {

    @NotNull(message = "Payment date is required")
    private LocalDate date;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than 0")
    private Double amount;

    @Size(max = 40, message = "Mode must be at most 40 characters")
    private String mode;

    @Size(max = 100, message = "Reference must be at most 100 characters")
    private String ref;

    public PurchaseBillPaymentRequestDto() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
}

