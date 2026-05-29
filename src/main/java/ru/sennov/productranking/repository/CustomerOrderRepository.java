package ru.sennov.productranking.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sennov.productranking.domain.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {

    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<CustomerOrder> findAll(Sort sort);

    @Query("""
            select distinct o
            from CustomerOrder o
            left join fetch o.items i
            left join fetch i.product p
            left join fetch p.category
            left join fetch p.pricing
            left join fetch p.inventory
            where o.purchasedAt >= :from
            """)
    List<CustomerOrder> findAllPurchasedAtAfterWithItems(@Param("from") Instant from);
}
