# weyland-command-service

Spring Boot 3 сервис обработки команд с приоритетной очередью, AOP-аудитом и метриками Prometheus.

## Содержание проекта

- **Java 17, Spring Boot 3.5, Gradle Kotlin DSL**
- **HTTP API** на Spring Web с валидацией DTO (`jakarta.validation`)
- **Очередь команд**: `BlockingQueue<CommandRequest>` (capacity 100) + пул из 2 воркеров на `ExecutorService`
- **Приоритеты**: `CRITICAL` команды выполняются синхронно мимо очереди, `COMMON` — кладутся в очередь и обрабатываются воркерами
- **Backpressure**: при переполнении очереди — `QueueOverflowException`, отдаётся через `GlobalExceptionHandler`
- **AOP-аудит** через кастомную аннотацию `@WeylandWatchingYou` — метод-перехватчик пишет в консоль или в Kafka-топик `android-audit` (режим переключается через `audit.mode`)
- **Метрики Micrometer + Prometheus**:
  - `android.tasks.queue.size` (gauge) — текущий размер очереди
  - `android.tasks.completed.by.author{author=...}` (counter) — количество выполненных команд по авторам
- **Тесты**: `CommandControllerTest`, `GlobalExceptionHandlerTest` на JUnit 5 + Mockito

## Архитектура

```
HTTP POST /commands
        │
        ▼
┌──────────────────┐  @WeylandWatchingYou  ┌──────────────┐
│ CommandController├──────────────────────►│ AuditAspect  │──► console / kafka
└────────┬─────────┘                       └──────────────┘
         ▼
┌──────────────────┐
│  CommandService  │
└────────┬─────────┘
         │ priority == CRITICAL ? execute now : queue.offer()
         ▼
┌──────────────────┐  N=2 worker threads  ┌──────────────────────┐
│  BlockingQueue   ├─────────────────────►│  CommandQueueWorker  │
└──────────────────┘                      └──────────────────────┘
```

## Запуск

```bash
# 1. Поднять Kafka + Zookeeper + Kafka UI
docker compose up -d

# 2. Запустить приложение
./gradlew bootRun
```

Сервис слушает `:8080`, Kafka UI — `:8081`, Prometheus метрики — `:8080/actuator/prometheus`.

## Пример запроса

```bash
curl -X POST http://localhost:8080/commands \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Restart reactor cooling subsystem",
    "priority": "CRITICAL",
    "author": "ripley",
    "time": "2026-05-02T19:30:00Z"
  }'
# → 200 OK {"status":"ACCEPTED"}
```

При невалидном теле — 400 с описанием поля; при переполнении очереди — 503.

## Конфигурация

`application.yml`:

```yaml
audit:
  mode: console        # или: kafka
  kafka-topic: android-audit
spring:
  kafka:
    bootstrap-servers: localhost:29092
```

## Тесты

```bash
./gradlew test
```

## Ключевые технические решения

- Конкурентная обработка через `BlockingQueue` + `ExecutorService`
- Spring AOP с собственной аннотацией
- Production-ready наблюдаемость (Actuator + Prometheus + кастомные Micrometer-метрики)
- Чистое разделение слоёв: controller → service → worker
- Глобальная обработка исключений и валидация на границе
