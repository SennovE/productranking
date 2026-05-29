package ru.sennov.productranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class ProductRankingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductRankingServiceApplication.class, args);
    }
}
