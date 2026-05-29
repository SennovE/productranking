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
import ru.sennov.productranking.api.dto.RankingProductResponse;
import ru.sennov.productranking.api.dto.TopOrderResponse;
import ru.sennov.productranking.domain.CustomerOrder;
import ru.sennov.productranking.domain.Product;
import ru.sennov.productranking.domain.Score;
import ru.sennov.productranking.repository.CategoryRepository;
import ru.sennov.productranking.repository.CustomerOrderRepository;
import ru.sennov.productranking.repository.ScoreRepository;

@Service
public class RankingService {

    private final ScoreRepository scoreRepository;
    private final CustomerOrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final ScoreCalculationService scoreCalculationService;

    public RankingService(ScoreRepository scoreRepository, CustomerOrderRepository orderRepository,
            CategoryRepository categoryRepository, ScoreCalculationService scoreCalculationService) {
        this.scoreRepository = scoreRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.scoreCalculationService = scoreCalculationService;
    }

    public RankingProductResponse productRanking(UUID productId, int windowDays) {
        Score score = scoreCalculationService.recalculateProduct(productId, windowDays);
        return toRankingResponse(score);
    }

    public List<RankingProductResponse> globalTop(int windowDays, int limit) {
        scoreCalculationService.recalculateAll(windowDays);
        return scoreRepository.findByWindowDaysOrderByScoreDesc(windowDays).stream()
                .map(this::toRankingResponse)
                .limit(normalizeLimit(limit))
                .collect(Collectors.toList());
    }

    public List<RankingProductResponse> categoryTop(UUID categoryId, int windowDays, int limit) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
        scoreCalculationService.recalculateAll(windowDays);
        return scoreRepository.findByWindowDaysAndProductCategoryIdOrderByScoreDesc(windowDays, categoryId).stream()
                .map(this::toRankingResponse)
                .limit(normalizeLimit(limit))
                .collect(Collectors.toList());
    }

    public List<TopOrderResponse> topOrders(int limit) {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "totalPrice")).stream()
                .map(this::toTopOrder)
                .sorted(Comparator.comparing(TopOrderResponse::getScore).reversed())
                .limit(normalizeLimit(limit))
                .collect(Collectors.toList());
    }

    private RankingProductResponse toRankingResponse(Score score) {
        Product product = score.getProduct();
        return new RankingProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getPricing().getPrice(),
                product.getInventory().getQuantity(),
                score.getWindowDays(),
                score.getScore(),
                score.getUpdatedAt());
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

    private long normalizeLimit(int limit) {
        if (limit < 1) {
            return 10;
        }
        return Math.min(limit, 100);
    }
}
