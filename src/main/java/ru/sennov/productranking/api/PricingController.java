package ru.sennov.productranking.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sennov.productranking.api.dto.PricingRequest;
import ru.sennov.productranking.api.dto.PricingResponse;
import ru.sennov.productranking.domain.Pricing;
import ru.sennov.productranking.repository.PricingRepository;
import ru.sennov.productranking.service.ResourceNotFoundException;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingRepository pricingRepository;

    public PricingController(PricingRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
    }

    @GetMapping
    public List<PricingResponse> list() {
        return pricingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PricingResponse get(@PathVariable UUID id) {
        return toResponse(findPricing(id));
    }

    @PostMapping
    public ResponseEntity<PricingResponse> create(@Valid @RequestBody PricingRequest request) {
        Pricing pricing = pricingRepository.save(new Pricing(request.getPrice()));
        return ResponseEntity.created(URI.create("/api/pricing/" + pricing.getId()))
                .body(toResponse(pricing));
    }

    @PutMapping("/{id}")
    public PricingResponse update(@PathVariable UUID id, @Valid @RequestBody PricingRequest request) {
        Pricing pricing = findPricing(id);
        pricing.setPrice(request.getPrice());
        return toResponse(pricingRepository.save(pricing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!pricingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pricing not found: " + id);
        }
        pricingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Pricing findPricing(UUID id) {
        return pricingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing not found: " + id));
    }

    private PricingResponse toResponse(Pricing pricing) {
        return new PricingResponse(pricing.getId(), pricing.getCreatedAt(), pricing.getUpdatedAt(), pricing.getPrice());
    }
}
