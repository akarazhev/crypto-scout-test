# crypto-scout-test

Production-ready test support library for the `crypto-scout` ecosystem. It provides:

- Mock data fixtures (Bybit, CoinMarketCap, crypto-scout internal) accessible via a typed Java API.
- A Podman Compose manager to spin up and tear down TimescaleDB (PostgreSQL) and RabbitMQ (Streams + AMQP) services for
  integration tests.

Use this library as a `test`-scope dependency to enable deterministic market data samples and a reproducible database
and messaging environment in your tests.

This project was authored with AI-driven tools and follows established engineering practices: explicit configuration,
fail-fast errors, clear defaults, and repeatable test setup.

## Features

- **Mock data API** (`com.github.akarazhev.cryptoscout.test.MockData`)
    - Typed access to bundled JSON fixtures.
    - Sources (`MockData.Source`): `CRYPTO_SCOUT`, `BYBIT_SPOT`, `BYBIT_LINEAR`.
    - Supported types (`MockData.Type`):
      `BTC_PRICE_RISK`, `BTC_RISK_PRICE`, `FGI`, `LPL`, `KLINE_1`, `KLINE_5`, `KLINE_15`, `KLINE_60`, `KLINE_240`,
      `KLINE_D`, `KLINE_W`, `TICKERS`, `PUBLIC_TRADE`, `ORDER_BOOK_1`, `ORDER_BOOK_50`, `ORDER_BOOK_200`,
      `ORDER_BOOK_1000`, `ALL_LIQUIDATION`.
- **Podman Compose manager** (`com.github.akarazhev.cryptoscout.test.PodmanCompose`)
    - `up()` starts the services defined in `src/main/resources/podman/podman-compose.yml` and waits until they are
      ready: TimescaleDB (PostgreSQL) and RabbitMQ (with Streams and AMQP enabled).
    - `down()` stops and removes the containers and waits until they are gone.
    - Automatic cleanup of temporary directories when running from JAR via JVM shutdown hooks.
- **RabbitMQ Streams test utilities** (`com.github.akarazhev.cryptoscout.test.StreamTestPublisher`,
  `com.github.akarazhev.cryptoscout.test.StreamTestConsumer`)
    - Lightweight helpers to publish/consume JSON payloads via RabbitMQ Streams protocol during integration tests.
    - Built on `com.rabbitmq:stream-client` and intended to work with the Streams service started by `PodmanCompose`.
    - Thread-safe implementation using `AtomicReference` for state management.
- **RabbitMQ AMQP test utilities** (`com.github.akarazhev.cryptoscout.test.AmqpTestPublisher`,
  `com.github.akarazhev.cryptoscout.test.AmqpTestConsumer`)
    - Lightweight helpers to publish/consume JSON messages via standard AMQP protocol.
    - Built on `com.rabbitmq:amqp-client` for traditional queue-based messaging tests.
    - Configurable timeout for publish confirmations (default: 5 seconds).
- **Database utilities** (`com.github.akarazhev.cryptoscout.test.DBUtils`)
    - Helper methods for database operations in tests (e.g., `deleteFromTables`, `canConnect`).
    - `deleteFromTables()` returns `boolean` to verify cleanup success (`true` if successful, `false` on failure).
- **Test assertions** (`com.github.akarazhev.cryptoscout.test.Assertions`)
    - Database state verification helpers (e.g., `assertTableCount`) for validating test data.
- **Robust defaults and configurability** via system properties (see Configuration).

## Requirements

- Java 25 (as configured in `pom.xml`).
- Maven 3.9+ (for building/installing locally).
- Podman and the `podman-compose` CLI available on `PATH` to use `PodmanCompose`.
    - If your environment does not provide a `podman-compose` binary, set the property to an absolute path of a
      compatible wrapper.
    - Note: Using a spaced command like `podman compose` is not supported directly; prefer a single binary name/path.

## Installation

```bash
mvn -q -DskipTests install
```

Add as a test dependency in your project:

```xml

<dependency>
    <groupId>com.github.akarazhev.cryptoscout</groupId>
    <artifactId>crypto-scout-test</artifactId>
    <version>0.0.1</version>
    <scope>test</scope>
</dependency>
```

Gradle (Kotlin):

```kotlin
testImplementation("com.github.akarazhev.cryptoscout:crypto-scout-test:0.0.1")
```

Note: The PostgreSQL JDBC driver is declared as a dependency of this library (see `pom.xml`). If you exclude it or need
to pin a different version, add `org.postgresql:postgresql` to your test scope explicitly.

## Quickstart

- **Build and install**: `mvn -q -DskipTests install`
- **Enable services in tests**: call `PodmanCompose.up()` in a JUnit `@BeforeAll`, and `PodmanCompose.down()` in
  `@AfterAll`.
- **Run tests**: `mvn -q -Dpodman.compose.up.timeout.min=5 test`
- **Override ports/credentials if needed** via system properties (see Configuration).

## Usage

### Mock data

```java
import com.github.akarazhev.cryptoscout.test.MockData;

// Load SPOT 1m klines
var spotKlines = MockData.get(MockData.Source.BYBIT_SPOT, MockData.Type.KLINE_1);
// Load LINEAR tickers
var linearTickers = MockData.get(MockData.Source.BYBIT_LINEAR, MockData.Type.TICKERS);
// Load crypto-scout FGI (Fear & Greed Index)
var fgi = MockData.get(MockData.Source.CRYPTO_SCOUT, MockData.Type.FGI);
// Load crypto-scout BTC price risk data
var btcPriceRisk = MockData.get(MockData.Source.CRYPTO_SCOUT, MockData.Type.BTC_PRICE_RISK);
```

### Podman Compose lifecycle in tests

```java
import com.github.akarazhev.cryptoscout.test.PodmanCompose;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

class IntegrationTest {
    @BeforeAll
    static void startDb() {
        PodmanCompose.up();
    }

    @AfterAll
    static void stopDb() {
        PodmanCompose.down();
    }
}
```

Note: `PodmanCompose` waits for both the TimescaleDB and RabbitMQ services to become ready before returning from `up()`.

### RabbitMQ Streams Publisher/Consumer

```java
import com.github.akarazhev.cryptoscout.test.StreamTestPublisher;
import com.github.akarazhev.cryptoscout.test.StreamTestConsumer;

// Create publisher
var publisher = StreamTestPublisher.create(reactor, executor, environment, "my-stream");
publisher.start().await();

// Publish message
var payload = new Payload<Map<String, Object>>(data);
publisher.publish(payload).await();

// Create consumer
var consumer = StreamTestConsumer.create(reactor, executor, environment, "my-stream");
consumer.start().await();

// Get result (waits for first message)
var result = consumer.getResult().await();
```

### RabbitMQ AMQP Publisher/Consumer

```java
import com.github.akarazhev.cryptoscout.test.AmqpTestPublisher;
import com.github.akarazhev.cryptoscout.test.AmqpTestConsumer;

// Create publisher
var publisher = AmqpTestPublisher.create(reactor, executor, connectionFactory, "my-queue");
publisher.start().await();

// Publish message with 5-second confirmation timeout
var message = new Message<>(data);
publisher.publish("exchange", "routingKey", message).await();

// Create consumer
var consumer = AmqpTestConsumer.create(reactor, executor, connectionFactory, "my-queue");
consumer.start().await();

// Get message
var received = consumer.getMessage().await();
```

## Configuration

All settings are provided via system properties (see `Constants.PodmanCompose`):

- `podman.compose.cmd` (default: `podman-compose`)
- `podman.cmd` (default: `podman`)
- `test.db.jdbc.url` (default: `jdbc:postgresql://localhost:5432/crypto_scout`)
- `test.db.user` (default: `crypto_scout_db`)
- `test.db.password` (default: `crypto_scout_db`)
- `test.mq.host` (default: `localhost`)
- `test.mq.port` (default: `5552`)
- `test.mq.user` (default: `crypto_scout_mq`)
- `test.mq.password` (default: `crypto_scout_mq`)
- `test.mq.stream` (default: `bybit-stream`)
- `podman.compose.up.timeout.min` (default: `3`)
- `podman.compose.down.timeout.min` (default: `1`)
- `podman.compose.ready.interval.sec` (default: `2`)

Example:

```bash
mvn -q -Dpodman.compose.up.timeout.min=5 -Dtest.db.jdbc.url=jdbc:postgresql://localhost:5432/crypto_scout test
```

## RabbitMQ

- **Credentials**: use `test.mq.user` / `test.mq.password` (defaults: `crypto_scout_mq` / `crypto_scout_mq`).
- **Streams protocol**: `localhost:5552` (configurable via `test.mq.port`).
- **AMQP protocol**: `localhost:5672` (standard AMQP port).

## Packaged resources

- `src/main/resources/crypto-scout/`
    - `btcPriceRisk.json`, `btcRiskPrice.json`, `fgi.json`, `kline.D.json`, `kline.W.json`, `lpl.json`
- `src/main/resources/bybit-spot/`
    - `kline.1.json`, `kline.5.json`, `kline.15.json`, `kline.60.json`, `kline.240.json`, `kline.D.json`
    - `tickers.json`, `publicTrade.json`
    - `orderbook.1.json`, `orderbook.50.json`, `orderbook.200.json`, `orderbook.1000.json`
- `src/main/resources/bybit-linear/`
    - `kline.1.json`, `kline.5.json`, `kline.15.json`, `kline.60.json`, `kline.240.json`, `kline.D.json`
    - `tickers.json`, `publicTrade.json`, `allLiquidation.json`
    - `orderbook.1.json`, `orderbook.50.json`, `orderbook.200.json`, `orderbook.1000.json`
- `src/main/resources/podman/podman-compose.yml`
    - Services: TimescaleDB PG17 (`crypto-scout-collector-db`, port `5432`) and RabbitMQ 4.1.4 (`crypto-scout-mq`, ports
      `5672`, `5552`).
- `src/main/resources/podman/script/`
    - `init.sql`, `bybit_spot_tables.sql`, `bybit_linear_tables.sql`, `crypto_scout_tables.sql`
- `src/main/resources/podman/rabbitmq/`
    - `enabled_plugins`, `rabbitmq.conf`, `definitions.json`

## Thread Safety

All publisher and consumer implementations are thread-safe:

- **StreamTestPublisher**: Uses `AtomicReference` for producer state management. Prevents race conditions between
  `start()` and `stop()` operations.
- **StreamTestConsumer**: Uses `AtomicReference` for result promise management. Guarantees only the first message is
  captured; subsequent messages are ignored. `getResult()` returns an error if called before `start()`.
- **AmqpTestPublisher**: Uses `AtomicReference` for connection and channel state. Eliminates TOCTOU (Time-of-Check-
  Time-of-Use) race conditions. Includes 5-second timeout for publish confirmations.
- **AmqpTestConsumer**: Thread-safe message handling with automatic acknowledgment and consumer cancellation.

## Error handling and behavior

- `MockData.get(...)` throws `IllegalStateException` if a fixture is not found and may propagate parsing
  exceptions.
- `PodmanCompose.up()/down()` throw `IllegalStateException` on command failure or timeout.
- `PodmanCompose.copyScript()` fails fast with `IllegalStateException` if required scripts are missing.
- `StreamTestPublisher.publish()` throws `IllegalStateException` if called before `start()` or if the publisher is
  not properly initialized.
- `StreamTestConsumer.getResult()` returns a failed promise with `IllegalStateException` if called before `start()`.
- `AmqpTestPublisher.publish()` throws `IllegalStateException` if called before `start()`, if the channel is closed,
  or if the confirmation timeout (5 seconds) is exceeded.
- All AMQP/Stream publishers and consumers consistently throw `IllegalStateException` with proper exception chaining
  and descriptive error messages (including timeout values, table names, and other context).
- Resource loading: `MockData` reads JSON fixtures from the classpath. `PodmanCompose` resolves `podman/` assets to
  a real directory automatically: if they are on disk it uses them; if packaged in a JAR it extracts them to a temporary
  folder. No manual resource copying is required.
- Temporary directories extracted from JAR are automatically cleaned up via JVM shutdown hooks.

## CI/CD

- Ensure Podman and `podman-compose` are available on the runner. If the binary name differs, set
  `-Dpodman.compose.cmd=/path/to/podman-compose`.
- The library waits for DB and MQ readiness; adjust timeouts with `-Dpodman.compose.up.timeout.min` when runners are
  slow.
- To skip environment startup in certain jobs, avoid calling `PodmanCompose.up()` or guard it behind a Maven/Gradle
  profile.

## Quality and Reliability

The library is production-ready with comprehensive quality assurance:

- **Last Verified**: February 1, 2026 - Complete project review confirmed documentation accuracy and alignment with
  current codebase. All thread-safety and resource management issues resolved.
- **Test Coverage**: 100% test pass rate (65/65 tests) with comprehensive coverage of all public APIs.
- **Thread Safety**: All publishers and consumers use `AtomicReference` for state management, eliminating race
  conditions and ensuring consistent behavior under concurrent access.
- **Exception Handling**: Proper exception propagation with `IllegalStateException` throughout. All publishers/consumers
  throw exceptions on failure instead of swallowing them silently. Descriptive error messages include context such as
  timeout values and resource names.
- **Resource Management**: Robust cleanup with try-finally blocks, shutdown hooks for temp directory cleanup, and
  proper resource disposal even when tests fail.
- **Fail-Fast Behavior**: Missing resources or invalid states are detected immediately with clear error messages.
  Calling methods in the wrong order (e.g., `publish()` before `start()`) produces descriptive errors.
- **API Documentation**: Comprehensive Javadoc on all public APIs for excellent IDE support and developer experience.
- **Code Standards**: Adherence to project conventions defined in `AGENTS.md` for consistent style and patterns.

## Troubleshooting

- **Command not found**: Set `-Dpodman.compose.cmd` or `-Dpodman.cmd` to absolute paths.
- **Streams not reachable**: Verify port `5552` is free and not blocked by firewall; confirm `rabbitmq.conf` advertises
  `localhost`.
- **DB not reachable**: Confirm `jdbc:postgresql://localhost:5432/crypto_scout` and credentials (`crypto_scout_db` /
  `crypto_scout_db`).
- **Database cleanup failures**: `DBUtils.deleteFromTables()` returns `false` on failure. Check logs for SQL errors
  and verify tables exist. You can verify cleanup success:
  ```java
  if (!DBUtils.deleteFromTables(dataSource, "table1", "table2")) {
      LOGGER.warn("Table cleanup failed, tests may be polluted");
  }
  ```
- **Publish failures**: `StreamTestPublisher.publish()` and `AmqpTestPublisher.publish()` throw
  `IllegalStateException` on failure instead of being silent. Common causes:
    - Calling `publish()` before `start()` - ensure proper lifecycle
    - Channel/connection closed - check RabbitMQ connectivity
    - Confirmation timeout (AMQP) - increase timeout or check broker performance
  
  Catch and log exceptions for debugging:
  ```java
  try {
      publisher.publish(message);
  } catch (final IllegalStateException e) {
      LOGGER.error("Failed to publish message", e);
      // Handle failure appropriately
  }
  ```
- **Temp directory accumulation**: When running from JAR, temp directories are extracted and cleaned up via shutdown
  hooks. If the JVM terminates abnormally (kill -9), manual cleanup of `/tmp/crypto-scout-podman-*` may be needed.

## License

MIT. See `LICENSE`.

## Acknowledgements

This project has been written with AI-driven tools to assist with structure and documentation while ensuring correctness
through code and tests.

## Documentation

- **API Documentation**: All public APIs include comprehensive Javadoc. Use your IDE's built-in documentation support.
- **Development Guidelines**: See `AGENTS.md` for code style conventions, build commands, and testing patterns.
