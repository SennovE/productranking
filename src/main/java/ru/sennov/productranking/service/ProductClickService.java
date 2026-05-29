package ru.sennov.productranking.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sennov.productranking.api.dto.ProductClickRequest;
import ru.sennov.productranking.api.dto.ProductClickResponse;
import ru.sennov.productranking.domain.Product;
import ru.sennov.productranking.domain.ProductClick;
import ru.sennov.productranking.repository.ProductClickRepository;
import ru.sennov.productranking.repository.ProductRepository;

@Service
public class ProductClickService {

    private final ProductRepository productRepository;
    private final ProductClickRepository clickRepository;
    private final ScoreCalculationService scoreCalculationService;

    public ProductClickService(ProductRepository productRepository, ProductClickRepository clickRepository,
            ScoreCalculationService scoreCalculationService) {
        this.productRepository = productRepository;
        this.clickRepository = clickRepository;
        this.scoreCalculationService = scoreCalculationService;
    }

    @Transactional
    public ProductClickResponse appendClick(ProductClickRequest request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("productId must not be null");
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));
        Instant clickedAt = request.getClickedAt() == null ? Instant.now() : request.getClickedAt();
        ProductClick click = clickRepository.save(new ProductClick(product, request.getUserId(), clickedAt));
        scoreCalculationService.recalculateProducts(List.of(product.getId()));
        return toResponse(click);
    }

    private ProductClickResponse toResponse(ProductClick click) {
        return new ProductClickResponse(
                click.getId(),
                click.getProduct().getId(),
                click.getProduct().getName(),
                click.getUserId(),
                click.getClickedAt(),
                click.getCreatedAt());
    }
}
