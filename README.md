# crypto-scout-test

Test support library for the crypto-scout ecosystem. Provides mock data fixtures and containerized test infrastructure (TimescaleDB + RabbitMQ).

## Features

- **MockData** - Typed access to JSON fixtures (Bybit Spot/Linear, CMC, internal)
- **PodmanCompose** - Lifecycle management for TimescaleDB and RabbitMQ containers
- **StreamTestPublisher/Consumer** - RabbitMQ Streams test utilities (thread-safe)
- **AmqpTestPublisher/Consumer** - AMQP test utilities with confirmation timeouts
- **DBUtils** - Database cleanup and connection helpers
- **Assertions** - Database state verification (table counts, etc.)

## Quick Start

### Build
```bash
mvn -q -DskipTests install
```

### Add Dependency
```xml
<dependency>
    <groupId>com.github.akarazhev.cryptoscout</groupId>
    <artifactId>crypto-scout-test</artifactId>
    <version>0.0.1</version>
    <scope>test</scope>
</dependency>
```

### Usage

**Mock Data:**
```java
var spotKlines = MockData.get(Source.BYBIT_SPOT, Type.KLINE_1);
var fgi = MockData.get(Source.CRYPTO_SCOUT, Type.FGI);
```

**Container Lifecycle:**
```java
@BeforeAll static void setUp() { PodmanCompose.up(); }
@AfterAll static void tearDown() { PodmanCompose.down(); }
```

**RabbitMQ Streams:**
```java
var publisher = StreamTestPublisher.create(reactor, executor, env, "stream");
publisher.start().await();
publisher.publish(payload).await();

var consumer = StreamTestConsumer.create(reactor, executor, env, "stream");
consumer.start().await();
var result = consumer.getResult().await();
```

**RabbitMQ AMQP:**
```java
var publisher = AmqpTestPublisher.create(reactor, executor, factory, "queue");
publisher.publish("exchange", "key", message).await();

var consumer = AmqpTestConsumer.create(reactor, executor, factory, "queue");
var received = consumer.getMessage().await();
```

**Database Operations:**
```java
DBUtils.deleteFromTables(dataSource, "table1", "table2");
Assertions.assertTableCount("table1", 5);
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `podman.compose.cmd` | `podman-compose` | Podman Compose executable |
| `test.db.jdbc.url` | `jdbc:postgresql://localhost:5432/crypto_scout` | Database URL |
| `test.db.user` | `crypto_scout_db` | Database username |
| `test.db.password` | `crypto_scout_db` | Database password |
| `test.mq.host` | `localhost` | RabbitMQ host |
| `test.mq.port` | `5552` | RabbitMQ Streams port |
| `test.mq.user` | `crypto_scout_mq` | RabbitMQ username |
| `test.mq.password` | `crypto_scout_mq` | RabbitMQ password |
| `podman.compose.up.timeout.min` | `3` | Startup timeout (minutes) |

### Example
```bash
mvn -q -Dpodman.compose.up.timeout.min=5 test
```

## Requirements

- Java 25
- Maven 3.9+
- Podman + podman-compose

## Services

**TimescaleDB** (PostgreSQL 17)
- Port: 5432
- Default DB: `crypto_scout`
- Credentials: `crypto_scout_db` / `crypto_scout_db`

**RabbitMQ** 4.1.4
- AMQP: 5672
- Streams: 5552
- Credentials: `crypto_scout_mq` / `crypto_scout_mq`

## Thread Safety

All publishers and consumers are thread-safe using `AtomicReference`:
- `StreamTestPublisher` - Atomic producer state management
- `StreamTestConsumer` - Atomic result capture (first message only)
- `AmqpTestPublisher` - Atomic connection/channel state, 5s confirmation timeout
- `AmqpTestConsumer` - Thread-safe message handling

## Error Handling

All operations throw `IllegalStateException` on failure:
- `MockData.get()` - Resource not found
- `PodmanCompose.up()/down()` - Command failure or timeout
- `*Publisher.publish()` - Not started, connection closed, or timeout
- `*Consumer.getResult()/getMessage()` - Not started

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Command not found | Set `-Dpodman.compose.cmd=/path/to/podman-compose` |
| Streams unreachable | Check port 5552 is free |
| DB unreachable | Verify JDBC URL and credentials |
| Cleanup failures | Check `DBUtils.deleteFromTables()` return value |
| Temp directories | Auto-cleaned via shutdown hooks; manual cleanup if JVM killed with -9 |

## License

MIT License. See `LICENSE`.
