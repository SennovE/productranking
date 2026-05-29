package ru.sennov.productranking.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SaleAppendResponse {

    private final UUID orderId;
    private final UUID userId;
    private final Instant purchasedAt;
    private final BigDecimal totalPrice;
    private final Integer itemsCount;
    private final List<UUID> affectedProductIds;

    public SaleAppendResponse(UUID orderId, UUID userId, Instant purchasedAt, BigDecimal totalPrice,
            Integer itemsCount, List<UUID> affectedProductIds) {
        this.orderId = orderId;
        this.userId = userId;
        this.purchasedAt = purchasedAt;
        this.totalPrice = totalPrice;
        this.itemsCount = itemsCount;
        this.affectedProductIds = affectedProductIds;
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

    public List<UUID> getAffectedProductIds() {
        return affectedProductIds;
    }
}
