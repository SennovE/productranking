package ru.sennov.productranking.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sennov.productranking.domain.Pricing;

public interface PricingRepository extends JpaRepository<Pricing, UUID> {
}
