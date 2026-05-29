# Product Ranking Service

Сервис расчета витринных рейтингов и популярности товаров.

## Что реализовано

- REST CRUD для `Product`, `Category`, `Pricing`, `Inventory`.
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`.
- Ручной ввод продаж: `POST /api/sales/append`.
- Ручной ввод кликов: `POST /api/clicks/append`.
- Kafka consumers:
  - `sales.events` принимает JSON продажи;
  - `products.click` принимает JSON клика.
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

Compose поднимает `app`, `postgres`, `kafka` и `kafka-ui`. По умолчанию приложение включает Kafka-listeners только в Docker через `APP_KAFKA_LISTENER_ENABLED=true`; локальный запуск без Kafka работает на H2.

Kafka доступна:

- внутри Docker-сети: `kafka:9092`;
- с хоста: `localhost:9092`;
- Kafka UI: `http://localhost:8081`.

## Пример продажи

```json
{
  "userId": "22222222-2222-2222-2222-222222222222",
  "purchasedAt": "2026-05-17T10:00:00Z",
  "items": [
    {
      "productId": "UUID_ТОВАРА",
      "quantity": 2,
      "price": 1290.00
    }
  ]
}
```

`price` можно не передавать: тогда сервис возьмет текущую цену товара. Вместо `price` можно передать `totalPrice` позиции.

## Проверка

```bash
mvn test
```

Если Maven не установлен локально:

```bash
docker run --rm -v ${PWD}:/workspace -w /workspace maven:3.9.6-eclipse-temurin-17 mvn test
```
