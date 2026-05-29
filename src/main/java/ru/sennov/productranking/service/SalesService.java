package ru.sennov.productranking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sennov.productranking.api.dto.SaleAppendRequest;
import ru.sennov.productranking.api.dto.SaleAppendResponse;
import ru.sennov.productranking.api.dto.SaleItemRequest;
import ru.sennov.productranking.domain.CustomerOrder;
import ru.sennov.productranking.domain.OrderItem;
import ru.sennov.productranking.domain.Product;
import ru.sennov.productranking.repository.CustomerOrderRepository;
import ru.sennov.productranking.repository.ProductRepository;

@Service
public class SalesService {

    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ScoreCalculationService scoreCalculationService;

    public SalesService(CustomerOrderRepository orderRepository, ProductRepository productRepository,
            ScoreCalculationService scoreCalculationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.scoreCalculationService = scoreCalculationService;
    }

    @Transactional
    public SaleAppendResponse appendSale(SaleAppendRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
        UUID orderId = request.getOrderId() == null ? UUID.randomUUID() : request.getOrderId();
        CustomerOrder existingOrder = orderRepository.findById(orderId).orElse(null);
        if (existingOrder != null) {
            return toResponse(existingOrder);
        }

        UUID userId = request.getUserId() == null ? UUID.randomUUID() : request.getUserId();
        Instant purchasedAt = request.getPurchasedAt() == null ? Instant.now() : request.getPurchasedAt();
        CustomerOrder order = new CustomerOrder(BigDecimal.ZERO, userId, purchasedAt);
        order.setId(orderId);

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SaleItemRequest item : request.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("productId must not be null");
            }
            if (item.getQuantity() == null || item.getQuantity() < 1) {
                throw new IllegalArgumentException("quantity must be positive");
            }
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            int quantity = item.getQuantity();
            int currentQuantity = product.getInventory().getQuantity();
            if (currentQuantity < quantity) {
                throw new IllegalArgumentException("Insufficient inventory for product: " + product.getId());
            }

            BigDecimal unitPrice = resolveUnitPrice(item, product);
            product.getInventory().setQuantity(currentQuantity - quantity);
            order.addItem(new OrderItem(product, unitPrice, quantity));
            totalPrice = totalPrice.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        CustomerOrder savedOrder = orderRepository.save(order);
        scoreCalculationService.recalculateProducts(affectedProductIds(savedOrder));
        return toResponse(savedOrder);
    }

    private BigDecimal resolveUnitPrice(SaleItemRequest item, Product product) {
        if (item.getPrice() != null) {
            return item.getPrice().setScale(2, RoundingMode.HALF_UP);
        }
        if (item.getTotalPrice() != null) {
            return item.getTotalPrice()
                    .divide(BigDecimal.valueOf(item.getQuantity()), 2, RoundingMode.HALF_UP);
        }
        return product.getPricing().getPrice().setScale(2, RoundingMode.HALF_UP);
    }

    private SaleAppendResponse toResponse(CustomerOrder order) {
        List<UUID> affectedProductIds = affectedProductIds(order);
        return new SaleAppendResponse(
                order.getId(),
                order.getUserId(),
                order.getPurchasedAt(),
                order.getTotalPrice(),
                order.getItems().size(),
                affectedProductIds);
    }

    private List<UUID> affectedProductIds(CustomerOrder order) {
        return order.getItems().stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .collect(Collectors.toList());
    }
}
