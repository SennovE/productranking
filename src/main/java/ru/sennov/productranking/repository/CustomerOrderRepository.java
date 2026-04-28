package ru.sennov.productranking.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sennov.productranking.domain.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {

    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<CustomerOrder> findAll(Sort sort);
}
