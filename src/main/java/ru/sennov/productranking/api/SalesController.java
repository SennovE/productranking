package ru.sennov.productranking.api;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sennov.productranking.api.dto.SaleAppendRequest;
import ru.sennov.productranking.api.dto.SaleAppendResponse;
import ru.sennov.productranking.service.SalesService;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping("/append")
    public ResponseEntity<SaleAppendResponse> append(@Valid @RequestBody SaleAppendRequest request) {
        SaleAppendResponse response = salesService.appendSale(request);
        return ResponseEntity.created(URI.create("/api/ranking/orders/top"))
                .body(response);
    }
}
