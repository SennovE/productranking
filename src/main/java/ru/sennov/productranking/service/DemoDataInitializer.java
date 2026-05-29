package ru.sennov.productranking.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sennov.productranking.domain.Category;
import ru.sennov.productranking.domain.CustomerOrder;
import ru.sennov.productranking.domain.Inventory;
import ru.sennov.productranking.domain.OrderItem;
import ru.sennov.productranking.domain.Pricing;
import ru.sennov.productranking.domain.Product;
import ru.sennov.productranking.domain.ProductClick;
import ru.sennov.productranking.repository.CategoryRepository;
import ru.sennov.productranking.repository.CustomerOrderRepository;
import ru.sennov.productranking.repository.InventoryRepository;
import ru.sennov.productranking.repository.PricingRepository;
import ru.sennov.productranking.repository.ProductClickRepository;
import ru.sennov.productranking.repository.ProductRepository;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final PricingRepository pricingRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ProductClickRepository clickRepository;
    private final CustomerOrderRepository orderRepository;
    private final ScoreCalculationService scoreCalculationService;

    public DemoDataInitializer(CategoryRepository categoryRepository, PricingRepository pricingRepository,
            InventoryRepository inventoryRepository, ProductRepository productRepository,
            ProductClickRepository clickRepository, CustomerOrderRepository orderRepository,
            ScoreCalculationService scoreCalculationService) {
        this.categoryRepository = categoryRepository;
        this.pricingRepository = pricingRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.clickRepository = clickRepository;
        this.orderRepository = orderRepository;
        this.scoreCalculationService = scoreCalculationService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category electronics = categoryRepository.save(new Category("Электроника"));
        Category home = categoryRepository.save(new Category("Дом"));
        Category beauty = categoryRepository.save(new Category("Красота"));

        Product phone = createProduct("Смартфон", electronics, "79990.00", 38);
        Product kettle = createProduct("Чайник", home, "4990.00", 86);
        Product lamp = createProduct("Настольная лампа", home, "3490.00", 122);
        Product cream = createProduct("Крем для лица", beauty, "1290.00", 240);

        createOrder(UUID.fromString("22222222-2222-2222-2222-222222222222"),
                Instant.now().minus(2, ChronoUnit.DAYS), cream, lamp);
        createOrder(UUID.fromString("33333333-3333-3333-3333-333333333333"),
                Instant.now().minus(5, ChronoUnit.DAYS), kettle, cream);
        createOrder(UUID.fromString("44444444-4444-4444-4444-444444444444"),
                Instant.now().minus(12, ChronoUnit.DAYS), phone, cream);

        createClick(phone, 9);
        createClick(kettle, 4);
        createClick(lamp, 7);
        createClick(cream, 11);

        scoreCalculationService.recalculateAll(7);
        scoreCalculationService.recalculateAll(30);
    }

    private Product createProduct(String name, Category category, String priceValue, int quantity) {
        Pricing pricing = pricingRepository.save(new Pricing(new BigDecimal(priceValue)));
        Inventory inventory = inventoryRepository.save(new Inventory(quantity));
        return productRepository.save(new Product(name, category, pricing, inventory));
    }

    private void createOrder(UUID userId, Instant purchasedAt, Product first, Product second) {
        BigDecimal firstLine = first.getPricing().getPrice();
        BigDecimal secondLine = second.getPricing().getPrice().multiply(new BigDecimal("2"));
        CustomerOrder order = new CustomerOrder(firstLine.add(secondLine), userId, purchasedAt);
        order.addItem(new OrderItem(first, first.getPricing().getPrice(), 1));
        order.addItem(new OrderItem(second, second.getPricing().getPrice(), 2));
        orderRepository.save(order);
    }

    private void createClick(Product product, int count) {
        for (int index = 0; index < count; index++) {
            clickRepository.save(new ProductClick(product, UUID.randomUUID(),
                    Instant.now().minus(index + 1L, ChronoUnit.HOURS)));
        }
    }
}
