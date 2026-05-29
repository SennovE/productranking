package ru.sennov.productranking.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sennov.productranking.domain.ProductClick;

public interface ProductClickRepository extends JpaRepository<ProductClick, UUID> {

    @EntityGraph(attributePaths = {"product"})
    List<ProductClick> findByClickedAtGreaterThanEqual(Instant clickedAt);
}
