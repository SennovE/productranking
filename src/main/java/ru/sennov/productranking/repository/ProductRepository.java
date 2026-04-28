package ru.sennov.productranking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sennov.productranking.domain.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Override
    @EntityGraph(attributePaths = {"category", "pricing", "inventory"})
    List<Product> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = {"category", "pricing", "inventory"})
    Optional<Product> findById(UUID id);
}
