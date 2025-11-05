# crypto-scout-test

Production-ready test support library for the `crypto-scout` ecosystem. It provides:

- Bybit mock data fixtures accessible via a tiny Java API.
- A Podman Compose manager to spin up and tear down a TimescaleDB (PostgreSQL) service for integration tests.

Use this library as a `test`-scope dependency to enable deterministic market data samples and a reproducible database
environment in your tests.

This project was authored with AI-driven tools and follows established engineering practices: explicit configuration,
fail-fast errors, clear defaults, and repeatable test setup.

## Features

- **Bybit mock data API** (`com.github.akarazhev.cryptoscout.test.BybitMockData`)
    - Typed access to bundled JSON fixtures.
    - Supported types (`BybitMockData.Type`): `KLINE_1`, `KLINE_5`, `KLINE_15`, `KLINE_60`, `KLINE_240`, `KLINE_D`,
      `TICKERS`, `PUBLIC_TRADE`, `ORDER_BOOK_1`, `ORDER_BOOK_50`, `ORDER_BOOK_200`, `ORDER_BOOK_1000`.
    - Sources (`BybitMockData.Source`): `SPOT` is provided in this version. `LINEAR` is reserved for future use.
- **Podman Compose manager** (`com.github.akarazhev.cryptoscout.test.PodmanCompose`)
    - `up()` starts a TimescaleDB service defined in `src/main/resources/podman/podman-compose.yml` and waits until the
      DB is ready.
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

If you use `PodmanCompose`, ensure the PostgreSQL driver is on your test runtime classpath (the driver is optional here
and not transitive):

```xml

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.8</version>
    <scope>test</scope>
</dependency>
```

## Usage

### Mock data

```java
import com.github.akarazhev.cryptoscout.test.BybitMockData;

// Load SPOT 1m klines
var data = BybitMockData.get(
        BybitMockData.Source.SPOT,
        BybitMockData.Type.KLINE_1
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

## Configuration

All settings are provided via system properties (see `Constants.PodmanComposeConfig`):

- `podman.compose.cmd` (default: `podman-compose`)
- `podman.cmd` (default: `podman`)
- `test.db.jdbc.url` (default: `jdbc:postgresql://localhost:5432/crypto_scout`)
- `test.db.user` (default: `crypto_scout_db`)
- `test.db.password` (default: `crypto_scout_db`)
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
    - `tickers.json`, `publicTrade.json`
    - `orderbook.1.json`, `orderbook.50.json`, `orderbook.200.json`, `orderbook.1000.json`
- `src/main/resources/podman/podman-compose.yml`
    - Runs `timescale/timescaledb:latest-pg17` as `crypto-scout-collector-db` on `5432`.
    - Loads init SQL from `src/main/resources/podman/script/init.sql` to bootstrap the `crypto_scout` schema and tables
      for Bybit Spot and Linear.

## Error handling and behavior

- `BybitMockData.get(...)` throws `IllegalStateException` if a fixture is not found and may propagate parsing
  exceptions.
- `PodmanCompose.up()/down()` throw `IllegalStateException` on command failure or timeout.
- Resource resolution is performed via `ClassLoader` and then resolved to a disk path; ensure your test runtime provides
  resources on the filesystem (e.g., Maven places them under `target/test-classes`).

## License

MIT. See `LICENSE`.

## Acknowledgements

This project has been written with AI-driven tools to assist with structure and documentation while ensuring correctness
through code and tests.