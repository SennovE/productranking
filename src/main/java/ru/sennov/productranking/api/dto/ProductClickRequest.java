package ru.sennov.productranking.api.dto;

import java.time.Instant;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public class ProductClickRequest {

    @NotNull
    private UUID productId;

    private UUID userId;

    private Instant clickedAt;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }
}
