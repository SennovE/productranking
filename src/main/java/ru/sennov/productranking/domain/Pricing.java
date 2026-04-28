package ru.sennov.productranking.domain;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pricing")
public class Pricing extends BaseEntity {

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public Pricing() {
    }

    public Pricing(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
