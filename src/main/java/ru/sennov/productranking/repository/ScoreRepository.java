package ru.sennov.productranking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sennov.productranking.domain.Score;

public interface ScoreRepository extends JpaRepository<Score, UUID> {

    @EntityGraph(attributePaths = {"product", "product.category", "product.pricing", "product.inventory"})
    Optional<Score> findByProductIdAndWindowDays(UUID productId, Integer windowDays);

    @EntityGraph(attributePaths = {"product", "product.category", "product.pricing", "product.inventory"})
    List<Score> findByWindowDaysOrderByScoreDesc(Integer windowDays);

    @EntityGraph(attributePaths = {"product", "product.category", "product.pricing", "product.inventory"})
    List<Score> findByWindowDaysAndProductCategoryIdOrderByScoreDesc(Integer windowDays, UUID categoryId);
}
