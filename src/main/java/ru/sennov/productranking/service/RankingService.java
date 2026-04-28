package ru.sennov.productranking.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sennov.productranking.api.dto.RankingProductResponse;
import ru.sennov.productranking.api.dto.TopOrderResponse;
import ru.sennov.productranking.domain.CustomerOrder;
import ru.sennov.productranking.domain.Product;
import ru.sennov.productranking.domain.Score;
import ru.sennov.productranking.repository.CustomerOrderRepository;
import ru.sennov.productranking.repository.ProductRepository;
import ru.sennov.productranking.repository.ScoreRepository;

@Service
public class RankingService {

    private final ProductRepository productRepository;
    private final ScoreRepository scoreRepository;
    private final CustomerOrderRepository orderRepository;

    public RankingService(ProductRepository productRepository, ScoreRepository scoreRepository,
            CustomerOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.scoreRepository = scoreRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public RankingProductResponse productRanking(UUID productId, int windowDays) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Score storedScore = scoreRepository.findByProductIdAndWindowDays(productId, windowDays).orElse(null);
        int score = storedScore == null ? fakeProductScore(product, windowDays) : storedScore.getScore();
        Instant updatedAt = storedScore == null ? product.getUpdatedAt() : storedScore.getUpdatedAt();
        return toRankingResponse(product, windowDays, score, updatedAt);
    }

    @Transactional(readOnly = true)
    public List<RankingProductResponse> globalTop(int windowDays, int limit) {
        List<RankingProductResponse> responses = scoreRepository.findByWindowDaysOrderByScoreDesc(windowDays).stream()
                .map(score -> toRankingResponse(score.getProduct(), windowDays, score.getScore(), score.getUpdatedAt()))
                .collect(Collectors.toList());

        if (responses.isEmpty()) {
            responses = productRepository.findAll(Sort.by("name")).stream()
                    .map(product -> toRankingResponse(product, windowDays, fakeProductScore(product, windowDays), product.getUpdatedAt()))
                    .sorted(Comparator.comparing(RankingProductResponse::getScore).reversed())
                    .collect(Collectors.toList());
        }

        return responses.stream()
                .limit(normalizeLimit(limit))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RankingProductResponse> categoryTop(UUID categoryId, int windowDays, int limit) {
        return globalTop(windowDays, Integer.MAX_VALUE).stream()
                .filter(item -> categoryId.equals(item.getCategoryId()))
                .limit(normalizeLimit(limit))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopOrderResponse> topOrders(int limit) {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "totalPrice")).stream()
                .map(this::toTopOrder)
                .sorted(Comparator.comparing(TopOrderResponse::getScore).reversed())
                .limit(normalizeLimit(limit))
                .collect(Collectors.toList());
    }

    private RankingProductResponse toRankingResponse(Product product, int windowDays, int score, Instant updatedAt) {
        return new RankingProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getPricing().getPrice(),
                product.getInventory().getQuantity(),
                windowDays,
                score,
                updatedAt);
    }

    private TopOrderResponse toTopOrder(CustomerOrder order) {
        int daysAgo = (int) Math.max(0, ChronoUnit.DAYS.between(order.getPurchasedAt(), Instant.now()));
        int recencyBonus = Math.max(0, 300 - daysAgo * 12);
        int score = order.getTotalPrice().divide(new BigDecimal("10")).intValue()
                + order.getItems().size() * 85
                + recencyBonus;
        return new TopOrderResponse(
                order.getId(),
                order.getUserId(),
                order.getPurchasedAt(),
                order.getTotalPrice(),
                order.getItems().size(),
                score);
    }

    private int fakeProductScore(Product product, int windowDays) {
        int stockSignal = Math.min(product.getInventory().getQuantity(), 120) * 2;
        int priceSignal = product.getPricing().getPrice().remainder(new BigDecimal("250")).intValue();
        int windowSignal = Math.min(windowDays, 60) * 5;
        return stockSignal + priceSignal + windowSignal;
    }

    private long normalizeLimit(int limit) {
        if (limit < 1) {
            return 10;
        }
        return Math.min(limit, 100);
    }
}
