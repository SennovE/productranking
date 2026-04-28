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
import ru.sennov.productranking.domain.Score;
import ru.sennov.productranking.repository.CategoryRepository;
import ru.sennov.productranking.repository.CustomerOrderRepository;
import ru.sennov.productranking.repository.InventoryRepository;
import ru.sennov.productranking.repository.PricingRepository;
import ru.sennov.productranking.repository.ProductRepository;
import ru.sennov.productranking.repository.ScoreRepository;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final PricingRepository pricingRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ScoreRepository scoreRepository;
    private final CustomerOrderRepository orderRepository;

    public DemoDataInitializer(CategoryRepository categoryRepository, PricingRepository pricingRepository,
            InventoryRepository inventoryRepository, ProductRepository productRepository,
            ScoreRepository scoreRepository, CustomerOrderRepository orderRepository) {
        this.categoryRepository = categoryRepository;
        this.pricingRepository = pricingRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.scoreRepository = scoreRepository;
        this.orderRepository = orderRepository;
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

        Product phone = createProduct("Phone", electronics, "79990.00", 38);
        Product kettle = createProduct("Kettle", home, "4990.00", 86);
        Product lamp = createProduct("Lamp", home, "3490.00", 122);
        Product cream = createProduct("Cream", beauty, "1290.00", 240);

        scoreRepository.save(new Score(phone, 7, 918));
        scoreRepository.save(new Score(kettle, 7, 676));
        scoreRepository.save(new Score(lamp, 7, 731));
        scoreRepository.save(new Score(cream, 7, 845));

        scoreRepository.save(new Score(phone, 30, 3240));
        scoreRepository.save(new Score(kettle, 30, 2190));
        scoreRepository.save(new Score(lamp, 30, 2510));
        scoreRepository.save(new Score(cream, 30, 3025));

        createOrder(UUID.fromString("22222222-2222-2222-2222-222222222222"),
                Instant.now().minus(2, ChronoUnit.DAYS), cream, lamp);
        createOrder(UUID.fromString("33333333-3333-3333-3333-333333333333"),
                Instant.now().minus(5, ChronoUnit.DAYS), kettle, cream);
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
}
