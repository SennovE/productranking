package ru.sennov.productranking.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PricingResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final BigDecimal price;

    public PricingResponse(UUID id, Instant createdAt, Instant updatedAt, BigDecimal price) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
