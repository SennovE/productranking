package ru.sennov.productranking.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.sennov.productranking.api.dto.ProductClickRequest;
import ru.sennov.productranking.service.ProductClickService;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProductClickEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductClickEventListener.class);

    private final ObjectMapper objectMapper;
    private final ProductClickService clickService;

    public ProductClickEventListener(ObjectMapper objectMapper, ProductClickService clickService) {
        this.objectMapper = objectMapper;
        this.clickService = clickService;
    }

    @KafkaListener(
            topics = "${app.kafka.product-click-topic:products.click}",
            groupId = "${spring.kafka.consumer.group-id:product-ranking-service}",
            autoStartup = "${app.kafka.listener-enabled:false}")
    public void onMessage(String payload) {
        try {
            ProductClickRequest request = objectMapper.readValue(payload, ProductClickRequest.class);
            clickService.appendClick(request);
        } catch (Exception exception) {
            LOGGER.warn("Invalid product click payload: {}", payload, exception);
            throw new IllegalArgumentException("Invalid product click payload", exception);
        }
    }
}
