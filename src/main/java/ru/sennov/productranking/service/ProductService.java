package ru.sennov.productranking.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sennov.productranking.api.dto.ProductRequest;
import ru.sennov.productranking.api.dto.ProductResponse;
import ru.sennov.productranking.domain.Category;
import ru.sennov.productranking.domain.Inventory;
import ru.sennov.productranking.domain.Pricing;
import ru.sennov.productranking.domain.Product;
import ru.sennov.productranking.repository.CategoryRepository;
import ru.sennov.productranking.repository.InventoryRepository;
import ru.sennov.productranking.repository.PricingRepository;
import ru.sennov.productranking.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PricingRepository pricingRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            PricingRepository pricingRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.pricingRepository = pricingRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<ProductResponse> list() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse get(UUID id) {
        return toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        fill(product, request);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findProduct(id);
        fill(product, request);
        return toResponse(productRepository.save(product));
    }

    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private void fill(Product product, ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));
        Pricing pricing = pricingRepository.findById(request.getPricingId())
                .orElseThrow(() -> new ResourceNotFoundException("Pricing not found: " + request.getPricingId()));
        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + request.getInventoryId()));

        product.setName(request.getName());
        product.setCategory(category);
        product.setPricing(pricing);
        product.setInventory(inventory);
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getPricing().getId(),
                product.getPricing().getPrice(),
                product.getInventory().getId(),
                product.getInventory().getQuantity());
    }
}
