package ru.sennov.productranking.api.dto;

import java.time.Instant;
import java.util.UUID;

public class ProductClickResponse {

    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final UUID userId;
    private final Instant clickedAt;
    private final Instant createdAt;

    public ProductClickResponse(UUID id, UUID productId, String productName, UUID userId,
            Instant clickedAt, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.userId = userId;
        this.clickedAt = clickedAt;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
