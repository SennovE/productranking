package ru.sennov.productranking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sennov.productranking.domain.CustomerOrder;
import ru.sennov.productranking.domain.OrderItem;
import ru.sennov.productranking.domain.Product;
import ru.sennov.productranking.domain.ProductClick;
import ru.sennov.productranking.domain.Score;
import ru.sennov.productranking.repository.CustomerOrderRepository;
import ru.sennov.productranking.repository.ProductClickRepository;
import ru.sennov.productranking.repository.ProductRepository;
import ru.sennov.productranking.repository.ScoreRepository;

@Service
public class ScoreCalculationService {

    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;
    private final ProductClickRepository clickRepository;
    private final ScoreRepository scoreRepository;
    private final List<Integer> defaultWindows;

    public ScoreCalculationService(ProductRepository productRepository, CustomerOrderRepository orderRepository,
            ProductClickRepository clickRepository, ScoreRepository scoreRepository,
            @Value("${app.ranking.default-windows:7,30}") String defaultWindows) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.clickRepository = clickRepository;
        this.scoreRepository = scoreRepository;
        this.defaultWindows = Arrays.stream(defaultWindows.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    @Transactional
    public Score recalculateProduct(UUID productId, int windowDays) {
        validateWindowDays(windowDays);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Metrics metrics = collectSalesMetrics(windowDays).getOrDefault(productId, new Metrics());
        long clicks = collectClickMetrics(windowDays).getOrDefault(productId, 0L);
        return saveScore(product, windowDays, calculateScore(product, metrics, clicks));
    }

    @Transactional
    public List<Score> recalculateAll(int windowDays) {
        validateWindowDays(windowDays);
        Map<UUID, Metrics> salesMetrics = collectSalesMetrics(windowDays);
        Map<UUID, Long> clickMetrics = collectClickMetrics(windowDays);
        return productRepository.findAll(Sort.by("name")).stream()
                .map(product -> {
                    Metrics metrics = salesMetrics.getOrDefault(product.getId(), new Metrics());
                    long clicks = clickMetrics.getOrDefault(product.getId(), 0L);
                    return saveScore(product, windowDays, calculateScore(product, metrics, clicks));
                })
                .collect(Collectors.toList());
    }

    public void recalculateProducts(Collection<UUID> productIds) {
        for (Integer windowDays : defaultWindows) {
            for (UUID productId : productIds) {
                recalculateProduct(productId, windowDays);
            }
        }
    }

    public void validateWindowDays(int windowDays) {
        if (windowDays < 1 || windowDays > 3650) {
            throw new IllegalArgumentException("windowDays must be between 1 and 3650");
        }
    }

    private Map<UUID, Metrics> collectSalesMetrics(int windowDays) {
        Instant from = Instant.now().minus(windowDays, ChronoUnit.DAYS);
        Map<UUID, Metrics> metrics = new HashMap<UUID, Metrics>();
        for (CustomerOrder order : orderRepository.findAllPurchasedAtAfterWithItems(from)) {
            Set<UUID> countedProductsInOrder = new HashSet<UUID>();
            for (OrderItem item : order.getItems()) {
                UUID productId = item.getProduct().getId();
                Metrics productMetrics = metrics.computeIfAbsent(productId, key -> new Metrics());
                productMetrics.quantity += item.getQuantity();
                productMetrics.revenue = productMetrics.revenue.add(
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                if (countedProductsInOrder.add(productId)) {
                    productMetrics.orders += 1;
                }
            }
        }
        return metrics;
    }

    private Map<UUID, Long> collectClickMetrics(int windowDays) {
        Instant from = Instant.now().minus(windowDays, ChronoUnit.DAYS);
        Map<UUID, Long> metrics = new HashMap<UUID, Long>();
        for (ProductClick click : clickRepository.findByClickedAtGreaterThanEqual(from)) {
            UUID productId = click.getProduct().getId();
            metrics.put(productId, metrics.getOrDefault(productId, 0L) + 1L);
        }
        return metrics;
    }

    private Score saveScore(Product product, int windowDays, int scoreValue) {
        Score score = scoreRepository.findByProductIdAndWindowDays(product.getId(), windowDays)
                .orElseGet(() -> new Score(product, windowDays, 0));
        score.setProduct(product);
        score.setWindowDays(windowDays);
        score.setScore(scoreValue);
        return scoreRepository.save(score);
    }

    private int calculateScore(Product product, Metrics metrics, long clicks) {
        int soldSignal = metrics.quantity * 100;
        int orderSignal = metrics.orders * 35;
        int revenueSignal = metrics.revenue.divide(new BigDecimal("100"), RoundingMode.DOWN).intValue();
        int clickSignal = Math.toIntExact(Math.min(clicks * 8L, 100_000L));
        int stockSignal = Math.min(Math.max(product.getInventory().getQuantity(), 0), 120);
        int availabilityPenalty = product.getInventory().getQuantity() <= 0 ? -200 : 0;
        return Math.max(0, soldSignal + orderSignal + revenueSignal + clickSignal + stockSignal + availabilityPenalty);
    }

    private static class Metrics {
        private int quantity;
        private int orders;
        private BigDecimal revenue = BigDecimal.ZERO;
    }
}
