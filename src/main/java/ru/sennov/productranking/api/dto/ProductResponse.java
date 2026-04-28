package ru.sennov.productranking.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ProductResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String name;
    private final UUID categoryId;
    private final String categoryName;
    private final UUID pricingId;
    private final BigDecimal price;
    private final UUID inventoryId;
    private final Integer quantity;

    public ProductResponse(UUID id, Instant createdAt, Instant updatedAt, String name,
            UUID categoryId, String categoryName, UUID pricingId, BigDecimal price,
            UUID inventoryId, Integer quantity) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.pricingId = pricingId;
        this.price = price;
        this.inventoryId = inventoryId;
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

    public String getName() {
        return name;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public UUID getPricingId() {
        return pricingId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public UUID getInventoryId() {
        return inventoryId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
