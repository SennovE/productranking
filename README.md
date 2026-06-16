# Product Ranking Service

Сервис расчета витринных рейтингов и популярности товаров.

## Что реализовано

- REST CRUD для `Product`, `Category`, `Pricing`, `Inventory`.
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`.
- Ручной ввод продаж: `POST /api/sales/append`.
- Ручной ввод кликов: `POST /api/clicks/append`.
- gRPC прием событий:
  - `RecordProductClick`;
  - `RecordSaleEvent`.
- Kafka consumers:
  - `sales.events` принимает JSON продажи;
  - `products.click` принимает JSON клика.
- Nginx proxy с Basic Auth для REST/UI и gRPC.
- gRPC UI: `http://localhost:8082`.
- Kafka UI: `http://localhost:8081`.
- Рейтинг товара, топ категории и глобальный топ:
  - `GET /api/ranking/products/{productId}?windowDays=7`
  - `GET /api/ranking/categories/{categoryId}/top?windowDays=7&limit=50`
  - `GET /api/ranking/top?windowDays=7&limit=50`
- Web UI: `http://localhost:8080/`.

## Запуск

```bash
docker compose up -d --build
```

Compose поднимает `proxy`, `app`, `grpc-ui`, `postgres`, `kafka` и `kafka-ui`. По умолчанию приложение включает Kafka-listeners только в Docker через `APP_KAFKA_LISTENER_ENABLED=true`; локальный запуск без Kafka работает на H2.

В Docker прямые порты `app` не публикуются наружу. REST/UI доступны через proxy на `http://localhost:8080`, gRPC UI - на `http://localhost:8082`, gRPC - на `localhost:9090`. Dev-доступ:

- логин: `product-ranking`;
- пароль: `product-ranking`;
- файл Basic Auth: `deploy/nginx/.htpasswd`.

```bash
curl -u product-ranking:product-ranking http://localhost:8080/actuator/health
```

Kafka доступна:

- внутри Docker-сети: `kafka:9092`;
- с хоста: `localhost:9092`;
- Kafka UI: `http://localhost:8081`.

## Пример продажи

```json
{
  "orderId": "90000000-0000-0000-0000-000000000003",
  "userId": "90000000-0000-0000-0000-000000000002",
  "items": [
    {
      "productId": "4950e5ab-61c2-4897-bcd4-3fbdf5e1972c",
      "quantity": 1
    }
  ]
}
```

`price` можно не передавать: тогда сервис возьмет текущую цену товара. Вместо `price` можно передать `totalPrice` позиции.

## gRPC

Proto-контракт лежит в `src/main/proto/product_events.proto`.

Web-интерфейс gRPC UI доступен на `http://localhost:8082` после Basic Auth. Он подключается к внутреннему `app:9090` через gRPC reflection.

```bash
AUTH=$(printf 'product-ranking:product-ranking' | base64)

grpcurl -plaintext \
  -H "authorization: Basic ${AUTH}" \
  localhost:9090 list
```

Пример клика:

```bash
grpcurl -plaintext \
  -H "authorization: Basic ${AUTH}" \
  -d '{"productId":"4950e5ab-61c2-4897-bcd4-3fbdf5e1972c","userId":"90000000-0000-0000-0000-000000000002","clickedAt":"2026-05-17T10:00:00Z"}' \
  localhost:9090 ru.sennov.productranking.grpc.v1.ProductEventsService/RecordProductClick
```

Пример покупки:

```bash
grpcurl -plaintext \
  -H "authorization: Basic ${AUTH}" \
  -d '{"orderId":"90000000-0000-0000-0000-000000000003","userId":"90000000-0000-0000-0000-000000000002","items":[{"productId":"4950e5ab-61c2-4897-bcd4-3fbdf5e1972c","quantity":1}]}' \
  localhost:9090 ru.sennov.productranking.grpc.v1.ProductEventsService/RecordSaleEvent
```

## Проверка

```bash
mvn test
```

Если Maven не установлен локально:

```bash
docker run --rm -v ${PWD}:/workspace -w /workspace maven:3.9.6-eclipse-temurin-17 mvn test
```
