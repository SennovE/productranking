package ru.sennov.productranking.api.dto;

import java.time.Instant;
import java.util.UUID;

public class CategoryResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String name;

    public CategoryResponse(UUID id, Instant createdAt, Instant updatedAt, String name) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
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
}
