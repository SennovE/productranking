package ru.sennov.productranking.api;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiInfoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("service", "product-ranking-service");
        payload.put("status", "up");
        payload.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(payload);
    }
}
