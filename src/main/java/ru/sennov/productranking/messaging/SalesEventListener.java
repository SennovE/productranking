package ru.sennov.productranking.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.sennov.productranking.api.dto.SaleAppendRequest;
import ru.sennov.productranking.service.SalesService;
import tools.jackson.databind.ObjectMapper;

@Component
public class SalesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesEventListener.class);

    private final ObjectMapper objectMapper;
    private final SalesService salesService;

    public SalesEventListener(ObjectMapper objectMapper, SalesService salesService) {
        this.objectMapper = objectMapper;
        this.salesService = salesService;
    }

    @KafkaListener(
            topics = "${app.kafka.sales-topic:sales.events}",
            groupId = "${spring.kafka.consumer.group-id:product-ranking-service}",
            autoStartup = "${app.kafka.listener-enabled:false}")
    public void onMessage(String payload) {
        try {
            SaleAppendRequest request = objectMapper.readValue(payload, SaleAppendRequest.class);
            salesService.appendSale(request);
        } catch (Exception exception) {
            LOGGER.warn("Invalid sales event payload: {}", payload, exception);
            throw new IllegalArgumentException("Invalid sales event payload", exception);
        }
    }
}
