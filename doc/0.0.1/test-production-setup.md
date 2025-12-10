# crypto-scout-test – Production Setup Report

## Summary

- **Goal**: Provide professional, production-ready documentation for the `crypto-scout-test` library to be used as a
  `test`-scope dependency in `crypto-scout` and similar projects.
- **Scope**:
    - Describe capabilities: Mock data fixtures (Bybit, CoinMarketCap, crypto-scout internal) and Podman Compose-based
      DB + MQ lifecycle for tests.
    - Document configuration, requirements, usage patterns, and packaged resources.
    - Clearly state that the project was written with AI-driven tools and follows best practices.

## Recommended GitHub repository description

- **About (one-liner)**: Test support library for crypto-scout: mock data fixtures + Podman Compose-managed TimescaleDB
  and RabbitMQ (Streams + AMQP) for integration tests.

## Files reviewed

- **Build**: `pom.xml` (Java 25, Maven plugins; dependencies: `com.github.akarazhev.jcryptolib:jcryptolib`,
  `com.rabbitmq:stream-client`, `com.rabbitmq:amqp-client`, `org.postgresql:postgresql`,
  `org.junit.jupiter:junit-jupiter`)
- **Library classes**:
    - `src/main/java/com/github/akarazhev/cryptoscout/test/MockData.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/PodmanCompose.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/Constants.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/DBUtils.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/StreamTestPublisher.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/StreamTestConsumer.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/AmqpTestPublisher.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/AmqpTestConsumer.java`
- **Tests**:
    - `src/test/java/com/github/akarazhev/cryptoscout/test/PodmanComposeTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/StreamConsumerPublisherTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/AmqpConsumerPublisherTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/MockBybitSpotDataTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/MockBybitLinearDataTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/MockCryptoScoutDataTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/AssertBybitSpotTablesTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/AssertBybitLinearTablesTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/AssertCryptoScoutTablesTest.java`
- **Resources**:
    - `src/main/resources/crypto-scout/` – JSON fixtures: `btcPriceRisk.json`, `btcRiskPrice.json`, `fgi.json`,
      `kline.D.json`, `kline.W.json`, `lpl.json`
    - `src/main/resources/bybit-spot/` – JSON fixtures for klines (1/5/15/60/240/D), tickers, publicTrade, orderbooks
      (1/50/200/1000)
    - `src/main/resources/bybit-linear/` – JSON fixtures for klines (1/5/15/60/240/D), tickers, publicTrade, orderbooks
      (1/50/200/1000), allLiquidation
    - `src/main/resources/podman/podman-compose.yml` – TimescaleDB + RabbitMQ services with healthchecks and tuning
    - `src/main/resources/podman/script/` – SQL: `init.sql`, `bybit_spot_tables.sql`, `bybit_linear_tables.sql`,
      `crypto_scout_tables.sql`
    - `src/main/resources/podman/rabbitmq/` – RabbitMQ assets: `enabled_plugins`, `rabbitmq.conf`, `definitions.json`

## README changes implemented

- **Overview and Features**: Introduced the purpose and pillars of the library (mock data, Podman Compose manager,
  messaging utilities, DB utilities).
- **MockData API**: Documented sources (`CRYPTO_SCOUT`, `BYBIT_SPOT`, `BYBIT_LINEAR`) and types (`BTC_PRICE_RISK`,
  `BTC_RISK_PRICE`, `FGI`, `LPL`, `KLINE_1`–`KLINE_W`, `TICKERS`, `PUBLIC_TRADE`, `ORDER_BOOK_*`, `ALL_LIQUIDATION`).
- **Requirements**: Java 25, Maven 3.9+, Podman and `podman-compose` for DB/MQ lifecycle.
- **Installation**: Added Maven dependency snippet and optional PostgreSQL driver note for tests.
- **Usage**: Included Java examples for `MockData.get(...)` and JUnit lifecycle with `PodmanCompose.up()/down()`.
- **Streams utilities**: Documented `StreamTestPublisher` and `StreamTestConsumer` for RabbitMQ Streams-based tests.
- **AMQP utilities**: Documented `AmqpTestPublisher` and `AmqpTestConsumer` for standard AMQP messaging tests.
- **Database utilities**: Documented `DBUtils` for test database operations.
- **Configuration**: Documented all system properties and defaults from `Constants.PodmanCompose` and `Constants.DB`:
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
- **Resource loading**: Clarified that `MockData` reads fixtures from the classpath and `PodmanCompose` automatically
  extracts the `podman/` assets (compose file, RabbitMQ configs, SQL scripts) to a temporary directory when packaged in
  a JAR; if resources are on disk during test runs (e.g., Maven), it uses them in place. No manual copying is required.
- **Packaged Resources**: Listed all fixtures (`crypto-scout/`, `bybit-spot/`, `bybit-linear/`) and described the
  TimescaleDB compose service and bootstrap SQL.
- **Error Handling**: Described failure modes (`IllegalStateException` on resource missing or command/timeout issues).
- **AI-driven note and License**: Clearly stated AI-driven authorship and MIT license.
- **Gradle snippet**: Added `testImplementation("com.github.akarazhev.cryptoscout:crypto-scout-test:0.0.1")`.
- **Quickstart**: Added build/install/test commands and guidance for calling `PodmanCompose.up()/down()`.
- **RabbitMQ**: Documented Streams protocol (5552), AMQP protocol (5672), and Management UI.
- **CI/CD and Troubleshooting**: Added runner prerequisites, timeout tuning, and common fixes.

## Podman Compose service summary

- DB:
    - **Service**: `crypto-scout-collector-db`
    - **Image**: `timescale/timescaledb:latest-pg17`
    - **Ports**: `127.0.0.1:5432:5432`
    - **Environment**: `POSTGRES_DB=crypto_scout`, `POSTGRES_USER=crypto_scout_db`, `POSTGRES_PASSWORD=crypto_scout_db`,
      telemetry off, tuned worker/memory settings.
    - **Init**: Mounts SQL from `podman/script/*.sql` to bootstrap schema and tables (`init.sql`,
      `bybit_spot_tables.sql`,
      `bybit_linear_tables.sql`, `crypto_scout_tables.sql`).
    - **Healthcheck**: `pg_isready -U $POSTGRES_USER -d $POSTGRES_DB`.
    - **Command tuning**: Enables `timescaledb` and `pg_stat_statements`, sets common PostgreSQL performance parameters.
    - **Resource limits**: 2 CPUs, 8GB memory limit, 4GB reservation, 1GB shared memory.
    - **Restart policy**: `unless-stopped`; `stop_grace_period: 1m`.
- MQ:
    - **Service**: `crypto-scout-mq`
    - **Image**: `rabbitmq:4.1.4-management`
    - **Ports**: `127.0.0.1:5672:5672` (AMQP), `127.0.0.1:5552:5552` (Streams)
    - **Volumes**: mounts `podman/rabbitmq/enabled_plugins`, `rabbitmq.conf`, `definitions.json` as read-only
    - **Healthcheck**: `rabbitmq-diagnostics -q ping`
    - **Security/limits**: `no-new-privileges`, tmpfs for `/tmp`, ulimits tuned, 2 CPUs, 1GB memory limit
    - **Restart policy**: `unless-stopped`; `stop_grace_period: 1m`.

## Considerations

- **Resource loading**: `MockData` reads JSON fixtures directly from the classpath. `PodmanCompose` resolves compose
  assets via the classpath and will auto-extract the `podman/` directory (including RabbitMQ configs) to a temporary
  location if the resources are packaged in a JAR; when resources are available on disk (typical during Maven test
  runs), it uses them directly.
- **PostgreSQL driver**: Required at test runtime when using `PodmanCompose` for JDBC readiness checks. It is declared
  as a dependency of this library (see `pom.xml`). If a consumer excludes it or needs a different version, they should
  add `org.postgresql:postgresql` to their test scope explicitly.
- **RabbitMQ Streams**: Uses port `5552`. Credentials/user/stream name are configurable via `test.mq.*` properties.

## Next steps (optional)

- Add a README section with a minimal Streams round-trip example (publisher/consumer) and lifecycle notes.
- Provide a sample CI workflow (GitHub Actions) that installs Podman and runs tests with `PodmanCompose` enabled.

## Verification checklist

- **Constants mapped to README**: Verified all property keys and defaults from `Constants.PodmanCompose` and
  `Constants.DB` are documented (DB, MQ, timeouts, intervals, commands).
- **MockData API**: Verified `MockData.Source` enum (`CRYPTO_SCOUT`, `BYBIT_SPOT`, `BYBIT_LINEAR`) and `MockData.Type`
  enum (18 types including `BTC_PRICE_RISK`, `BTC_RISK_PRICE`, `KLINE_W`) match documentation.
- **Resource inventory**: Verified filenames and locations for all fixtures (`crypto-scout/`, `bybit-spot/`,
  `bybit-linear/`), compose SQL scripts, and RabbitMQ assets.
- **Examples compile conceptually**: Code snippets reflect actual class names and signatures in `MockData`,
  `PodmanCompose`, and messaging utilities.
- **Streams utilities**: Verified documentation references `StreamTestPublisher` and `StreamTestConsumer` accurately.
- **AMQP utilities**: Verified documentation references `AmqpTestPublisher` and `AmqpTestConsumer` accurately.
- **Database utilities**: Verified documentation references `DBUtils` accurately.
- **Test coverage**: Verified all 9 test classes are documented.

## Change log

- Updated `README.md` with production-ready documentation covering features, installation, configuration (DB + MQ),
  usage, resources, error handling, Streams utilities, AMQP utilities, DB utilities, Gradle snippet, Quickstart,
  RabbitMQ, CI/CD, and Troubleshooting.
- Corrected `MockData.Source` values from outdated (`CMC_PARSER`, `BYBIT_PARSER`, `BYBIT_TA_SPOT`, `BYBIT_TA_LINEAR`)
  to current (`CRYPTO_SCOUT`, `BYBIT_SPOT`, `BYBIT_LINEAR`).
- Added missing `MockData.Type` values: `BTC_PRICE_RISK`, `BTC_RISK_PRICE`, `KLINE_W`.
- Updated resource directories from outdated (`cmc-parser/`, `bybit-parser/`, `bybit-ta-spot/`, `bybit-ta-linear/`)
  to current (`crypto-scout/`, `bybit-spot/`, `bybit-linear/`).
- Updated SQL scripts list from outdated to current (`init.sql`, `bybit_spot_tables.sql`, `bybit_linear_tables.sql`,
  `crypto_scout_tables.sql`).
- Fixed `test.mq.stream` default value from `bybit-crypto-stream` to `bybit-stream`.
- Added documentation for `AmqpTestPublisher`, `AmqpTestConsumer`, and `DBUtils`.
- Updated Podman Compose service summary with accurate port bindings and resource limits.
- Clarified resource-loading behavior in README and this report; corrected reference to `Constants.PodmanCompose`.
- Updated files reviewed to reflect all 8 library classes and 9 test classes.
- Fixed PostgreSQL driver note to reflect that the driver is included by default in this library.