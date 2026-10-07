package com.siec_acc.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "purchase_bill_payments")
public class PurchaseBillPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pb_prime_id", nullable = false)
    private PurchaseBillEntity purchaseBill;

    @Column(name = "payment_date", nullable = false)
    private LocalDate date;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "mode", length = 40)
    private String mode;

    @Column(name = "reference", length = 100)
    private String ref;

    public PurchaseBillPaymentEntity() {}

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public PurchaseBillEntity getPurchaseBill() { return purchaseBill; }
    public void setPurchaseBill(PurchaseBillEntity purchaseBill) { this.purchaseBill = purchaseBill; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
}


