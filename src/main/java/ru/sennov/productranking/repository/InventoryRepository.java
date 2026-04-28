package ru.sennov.productranking.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sennov.productranking.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
}
