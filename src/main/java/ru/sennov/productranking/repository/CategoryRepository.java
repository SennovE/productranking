package ru.sennov.productranking.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sennov.productranking.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
