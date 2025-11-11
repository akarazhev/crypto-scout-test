# crypto-scout-test

Production-ready test support library for the `crypto-scout` ecosystem. It provides:

- Bybit mock data fixtures accessible via a tiny Java API.
- A Podman Compose manager to spin up and tear down TimescaleDB (PostgreSQL) and RabbitMQ (Streams) services for
  integration tests.

Use this library as a `test`-scope dependency to enable deterministic market data samples and a reproducible database
and messaging environment in your tests.

This project was authored with AI-driven tools and follows established engineering practices: explicit configuration,
fail-fast errors, clear defaults, and repeatable test setup.

## Features

- **Bybit mock data API** (`com.github.akarazhev.cryptoscout.test.MockData`)
    - Typed access to bundled JSON fixtures.
    - Sources (`MockData.Source`): `CMC_PARSER`, `BYBIT_PARSER`, `BYBIT_SPOT`, `BYBIT_TA_SPOT`, `BYBIT_LINEAR`,
      `BYBIT_TA_LINEAR`.
    - Supported types (`MockData.Type`):
      `FGI`, `LPL`, `KLINE_1`, `KLINE_5`, `KLINE_15`, `KLINE_60`, `KLINE_240`, `KLINE_D`,
      `TICKERS`, `PUBLIC_TRADE`, `ORDER_BOOK_1`, `ORDER_BOOK_50`, `ORDER_BOOK_200`, `ORDER_BOOK_1000`, `ALL_LIQUDATION`.
- **Podman Compose manager** (`com.github.akarazhev.cryptoscout.test.PodmanCompose`)
    - `up()` starts the services defined in `src/main/resources/podman/podman-compose.yml` and waits until they are
      ready: TimescaleDB (PostgreSQL) and RabbitMQ (with Streams enabled).
    - `down()` stops and removes the containers and waits until they are gone.
- **Robust defaults and configurability** via system properties (see Configuration).

## Requirements

- Java 25 (as configured in `pom.xml`).
- Maven 3.9+ (for building/installing locally).
- Podman and the `podman-compose` CLI available on `PATH` to use `PodmanCompose`.
    - If your environment does not provide a `podman-compose` binary, set the property to an absolute path of a
      compatible wrapper.
    - Note: Using a spaced command like `podman compose` is not supported directly; prefer a single binary name/path.

## Installation

Install the library to your local Maven repository:

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

Note: The PostgreSQL JDBC driver is declared as a dependency of this library (see `pom.xml`). If you exclude it or need
to pin a different version, add `org.postgresql:postgresql` to your test scope explicitly.

## Usage

### Mock data

```java
import com.github.akarazhev.cryptoscout.test.MockData;

// Load SPOT 1m klines
var data = MockData.get(
        MockData.Source.BYBIT_SPOT,
        MockData.Type.KLINE_1
);
// "data" is a Map<String, Object> parsed from the bundled JSON fixture
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
- `test.mq.stream` (default: `bybit-crypto-stream`)
- `podman.compose.up.timeout.min` (default: `3`)
- `podman.compose.down.timeout.min` (default: `1`)
- `podman.compose.ready.interval.sec` (default: `2`)

Example:

```bash
mvn -q -Dpodman.compose.up.timeout.min=5 -Dtest.db.jdbc.url=jdbc:postgresql://localhost:5432/crypto_scout test
```

## Packaged resources

- `src/main/resources/bybit-spot/`
    - `kline.1.json`, `kline.5.json`, `kline.15.json`, `kline.60.json`, `kline.240.json`, `kline.D.json`
    - `tickers.json`
- `src/main/resources/bybit-linear/`
    - `kline.1.json`, `kline.5.json`, `kline.15.json`, `kline.60.json`, `kline.240.json`, `kline.D.json`
    - `tickers.json`
- `src/main/resources/bybit-ta-spot/`
    - `orderbook.1.json`, `orderbook.50.json`, `orderbook.200.json`, `orderbook.1000.json`, `publicTrade.json`
- `src/main/resources/bybit-ta-linear/`
    - `orderbook.1.json`, `orderbook.50.json`, `orderbook.200.json`, `orderbook.1000.json`, `publicTrade.json`,
      `allLiquidation.json`
- `src/main/resources/cmc-parser/`
    - `cmc_fgi.json`
- `src/main/resources/bybit-parser/`
    - `bybit_lpl.json`
- `src/main/resources/podman/podman-compose.yml`
    - Services: TimescaleDB PG17 (`crypto-scout-collector-db`, port `5432`) and RabbitMQ 4.x (`crypto-scout-mq`, ports
      `5672`, `5552`, `15672`, `15692`).
- `src/main/resources/podman/script/`
    - `init.sql`, `bybit_spot_tables.sql`, `bybit_ta_spot_tables.sql`, `bybit_linear_tables.sql`,
      `bybit_ta_linear_tables.sql`, `cmc_parser_tables.sql`, `bybit_parser_tables.sql`
- `src/main/resources/podman/rabbitmq/`
    - `enabled_plugins`, `rabbitmq.conf`, `definitions.json`

## Error handling and behavior

- `MockData.get(...)` throws `IllegalStateException` if a fixture is not found and may propagate parsing
  exceptions.
- `PodmanCompose.up()/down()` throw `IllegalStateException` on command failure or timeout.
- Resource loading: `MockData` reads JSON fixtures from the classpath. `PodmanCompose` resolves `podman/` assets to
  a real directory automatically: if they are on disk it uses them; if packaged in a JAR it extracts them to a temporary
  folder. No manual resource copying is required.

## License

MIT. See `LICENSE`.

## Acknowledgements

This project has been written with AI-driven tools to assist with structure and documentation while ensuring correctness
through code and tests.