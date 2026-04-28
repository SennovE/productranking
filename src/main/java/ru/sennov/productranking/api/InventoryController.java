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
import ru.sennov.productranking.api.dto.InventoryRequest;
import ru.sennov.productranking.api.dto.InventoryResponse;
import ru.sennov.productranking.domain.Inventory;
import ru.sennov.productranking.repository.InventoryRepository;
import ru.sennov.productranking.service.ResourceNotFoundException;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<InventoryResponse> list() {
        return inventoryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public InventoryResponse get(@PathVariable UUID id) {
        return toResponse(findInventory(id));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody InventoryRequest request) {
        Inventory inventory = inventoryRepository.save(new Inventory(request.getQuantity()));
        return ResponseEntity.created(URI.create("/api/inventory/" + inventory.getId()))
                .body(toResponse(inventory));
    }

    @PutMapping("/{id}")
    public InventoryResponse update(@PathVariable UUID id, @Valid @RequestBody InventoryRequest request) {
        Inventory inventory = findInventory(id);
        inventory.setQuantity(request.getQuantity());
        return toResponse(inventoryRepository.save(inventory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found: " + id);
        }
        inventoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Inventory findInventory(UUID id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(inventory.getId(), inventory.getCreatedAt(), inventory.getUpdatedAt(), inventory.getQuantity());
    }
}
