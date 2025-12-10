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
- **RabbitMQ Streams test utilities** (`com.github.akarazhev.cryptoscout.test.StreamTestPublisher`,
  `com.github.akarazhev.cryptoscout.test.StreamTestConsumer`)
    - Lightweight helpers to publish/consume JSON payloads via RabbitMQ Streams protocol during integration tests.
    - Built on `com.rabbitmq:stream-client` and intended to work with the Streams service started by `PodmanCompose`.
- **RabbitMQ AMQP test utilities** (`com.github.akarazhev.cryptoscout.test.AmqpTestPublisher`,
  `com.github.akarazhev.cryptoscout.test.AmqpTestConsumer`)
    - Lightweight helpers to publish/consume JSON messages via standard AMQP protocol.
    - Built on `com.rabbitmq:amqp-client` for traditional queue-based messaging tests.
- **Database utilities** (`com.github.akarazhev.cryptoscout.test.DBUtils`)
    - Helper methods for database operations in tests (e.g., `deleteFromTables`).
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

## Error handling and behavior

- `MockData.get(...)` throws `IllegalStateException` if a fixture is not found and may propagate parsing
  exceptions.
- `PodmanCompose.up()/down()` throw `IllegalStateException` on command failure or timeout.
- Resource loading: `MockData` reads JSON fixtures from the classpath. `PodmanCompose` resolves `podman/` assets to
  a real directory automatically: if they are on disk it uses them; if packaged in a JAR it extracts them to a temporary
  folder. No manual resource copying is required.

## CI/CD

- Ensure Podman and `podman-compose` are available on the runner. If the binary name differs, set
  `-Dpodman.compose.cmd=/path/to/podman-compose`.
- The library waits for DB and MQ readiness; adjust timeouts with `-Dpodman.compose.up.timeout.min` when runners are
  slow.
- To skip environment startup in certain jobs, avoid calling `PodmanCompose.up()` or guard it behind a Maven/Gradle
  profile.

## Troubleshooting

- **Command not found**: Set `-Dpodman.compose.cmd` or `-Dpodman.cmd` to absolute paths.
- **Streams not reachable**: Verify port `5552` is free and not blocked by firewall; confirm `rabbitmq.conf` advertises
  `localhost`.
- **DB not reachable**: Confirm `jdbc:postgresql://localhost:5432/crypto_scout` and credentials (`crypto_scout_db` /
  `crypto_scout_db`).

## License

MIT. See `LICENSE`.

## Acknowledgements

This project has been written with AI-driven tools to assist with structure and documentation while ensuring correctness
through code and tests.