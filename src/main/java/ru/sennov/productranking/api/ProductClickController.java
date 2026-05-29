package ru.sennov.productranking.api;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sennov.productranking.api.dto.ProductClickRequest;
import ru.sennov.productranking.api.dto.ProductClickResponse;
import ru.sennov.productranking.service.ProductClickService;

@RestController
@RequestMapping("/api/clicks")
public class ProductClickController {

    private final ProductClickService clickService;

    public ProductClickController(ProductClickService clickService) {
        this.clickService = clickService;
    }

    @PostMapping("/append")
    public ResponseEntity<ProductClickResponse> append(@Valid @RequestBody ProductClickRequest request) {
        ProductClickResponse response = clickService.appendClick(request);
        return ResponseEntity.created(URI.create("/api/clicks/" + response.getId()))
                .body(response);
    }
}
