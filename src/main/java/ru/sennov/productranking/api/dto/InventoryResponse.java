package ru.sennov.productranking.api.dto;

import java.time.Instant;
import java.util.UUID;

public class InventoryResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer quantity;

    public InventoryResponse(UUID id, Instant createdAt, Instant updatedAt, Integer quantity) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }
}
