package ru.sennov.productranking.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TopOrderResponse {

    private final UUID orderId;
    private final UUID userId;
    private final Instant purchasedAt;
    private final BigDecimal totalPrice;
    private final Integer itemsCount;
    private final Integer score;

    public TopOrderResponse(UUID orderId, UUID userId, Instant purchasedAt, BigDecimal totalPrice,
            Integer itemsCount, Integer score) {
        this.orderId = orderId;
        this.userId = userId;
        this.purchasedAt = purchasedAt;
        this.totalPrice = totalPrice;
        this.itemsCount = itemsCount;
        this.score = score;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getPurchasedAt() {
        return purchasedAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public Integer getScore() {
        return score;
    }
}
