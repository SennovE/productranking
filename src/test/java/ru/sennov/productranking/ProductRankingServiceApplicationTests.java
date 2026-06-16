package ru.sennov.productranking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.grpc.enabled=false")
class ProductRankingServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
