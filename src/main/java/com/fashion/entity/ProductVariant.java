package com.fashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(length = 20)
    private String size;

    @Column(length = 100)
    private String color;

    @Column(name = "color_hex", length = 10)
    private String colorHex;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(name = "stock_qty", nullable = false)
    @Builder.Default
    private Integer stockQty = 0;

    @Column(name = "price_adjustment", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal priceAdjustment = BigDecimal.ZERO;

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        if (color != null) sb.append(color);
        if (size != null) {
            if (!sb.isEmpty()) sb.append(" / ");
            sb.append(size);
        }
        return sb.toString();
    }
}
