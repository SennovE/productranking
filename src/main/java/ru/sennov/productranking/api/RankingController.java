package ru.sennov.productranking.api;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sennov.productranking.api.dto.RankingProductResponse;
import ru.sennov.productranking.api.dto.TopOrderResponse;
import ru.sennov.productranking.service.RankingService;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/products/{productId}")
    public RankingProductResponse productRanking(@PathVariable UUID productId,
            @RequestParam(defaultValue = "7") int windowDays) {
        return rankingService.productRanking(productId, windowDays);
    }

    @GetMapping("/categories/{categoryId}/top")
    public List<RankingProductResponse> categoryTop(@PathVariable UUID categoryId,
            @RequestParam(defaultValue = "7") int windowDays,
            @RequestParam(defaultValue = "50") int limit) {
        return rankingService.categoryTop(categoryId, windowDays, limit);
    }

    @GetMapping("/top")
    public List<RankingProductResponse> globalTop(@RequestParam(defaultValue = "7") int windowDays,
            @RequestParam(defaultValue = "50") int limit) {
        return rankingService.globalTop(windowDays, limit);
    }

    @GetMapping("/orders/top")
    public List<TopOrderResponse> topOrders(@RequestParam(defaultValue = "10") int limit) {
        return rankingService.topOrders(limit);
    }
}
