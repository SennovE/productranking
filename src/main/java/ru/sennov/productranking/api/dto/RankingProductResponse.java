package ru.sennov.productranking.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class RankingProductResponse {

    private final UUID productId;
    private final String productName;
    private final UUID categoryId;
    private final String categoryName;
    private final BigDecimal price;
    private final Integer quantity;
    private final Integer windowDays;
    private final Integer score;
    private final Instant updatedAt;

    public RankingProductResponse(UUID productId, String productName, UUID categoryId,
            String categoryName, BigDecimal price, Integer quantity, Integer windowDays,
            Integer score, Instant updatedAt) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.quantity = quantity;
        this.windowDays = windowDays;
        this.score = score;
        this.updatedAt = updatedAt;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getWindowDays() {
        return windowDays;
    }

    public Integer getScore() {
        return score;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
