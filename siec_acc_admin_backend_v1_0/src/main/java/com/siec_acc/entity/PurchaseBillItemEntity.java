package com.siec_acc.entity;

import jakarta.persistence.*;

/** One line of a purchase bill: what was billed, how many, at what rate and GST. */
@Entity
@Table(name = "purchase_bill_items")
public class PurchaseBillItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_item_id")
    private Long billItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pb_prime_id", nullable = false)
    private PurchaseBillEntity purchaseBill;

    // Matched to the PO / GRN line by item name (same convention as GoodsReceiptEntity.itemName).
    @Column(name = "item_name", nullable = false)
    private String name;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "unit", length = 30)
    private String unit;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "gst_pct")
    private Double gstPct;

    public PurchaseBillItemEntity() {}

    public PurchaseBillItemEntity(String name, Integer qty, String unit, Double rate, Double gstPct) {
        this.name = name;
        this.qty = qty;
        this.unit = unit;
        this.rate = rate;
        this.gstPct = gstPct;
    }

    public Long getBillItemId() { return billItemId; }
    public void setBillItemId(Long billItemId) { this.billItemId = billItemId; }

    public PurchaseBillEntity getPurchaseBill() { return purchaseBill; }
    public void setPurchaseBill(PurchaseBillEntity purchaseBill) { this.purchaseBill = purchaseBill; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public Double getGstPct() { return gstPct; }
    public void setGstPct(Double gstPct) { this.gstPct = gstPct; }
}


