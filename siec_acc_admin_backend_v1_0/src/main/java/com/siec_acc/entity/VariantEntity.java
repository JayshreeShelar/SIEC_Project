package com.siec_acc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "variants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_prime_id")
    private Long variantPrimeId;

    @Column(name = "variant_str_id", unique = true, length = 30)
    private String variantStrId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_prime_id", referencedColumnName = "product_prime_id")
    private ProductEntity product;

    @Column(name = "variant_name")
    private String variantName;

    @Column(name = "variant_sku")
    private String variantSku;

    @Column(name = "variant_height")
    private String variantHeight;

    @Column(name = "variant_width")
    private String variantWidth;

    @Column(name = "variant_length")
    private String variantLength;

    @Column(name = "variant_unit")
    private String variantUnit;

    @Column(name = "variant_material_type")
    private String variantMaterialType;

    @Column(name = "variant_size")
    private String variantSize;

    @Column(name = "variant_product_number")
    private String variantProductNumber;

    @Column(name = "variant_category")
    private String variantCategory;

    @Column(name = "variant_sub_category")
    private String variantSubCategory;

    @Column(name = "variant_stock", precision = 15, scale = 2)
    private BigDecimal variantStock;

    @Column(name = "variant_created_at")
    private LocalDateTime variantCreatedAt;

    @Column(name = "variant_updated_at")
    private LocalDateTime variantUpdatedAt;

    @PrePersist
    protected void onCreate() {
        this.variantCreatedAt = LocalDateTime.now();
        this.variantUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.variantUpdatedAt = LocalDateTime.now();
    }


}