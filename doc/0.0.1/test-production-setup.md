# crypto-scout-test – Production Setup Report

## Summary

- **Goal**: Provide professional, production-ready documentation for the `crypto-scout-test` library to be used as a
  `test`-scope dependency in `crypto-scout` and similar projects.
- **Scope**:
    - Describe capabilities: Bybit mock data fixtures and Podman Compose-based DB + MQ lifecycle for tests.
    - Document configuration, requirements, usage patterns, and packaged resources.
    - Clearly state that the project was written with AI-driven tools and follows best practices.

## Recommended GitHub repository description

- **About (one-liner)**: Test support library for crypto-scout: Bybit mock data + Podman Compose-managed TimescaleDB and
  RabbitMQ (Streams) for integration tests.

## Files reviewed

- **Build**: `pom.xml` (Java 25, Maven plugins; dependencies: `com.github.akarazhev.jcryptolib:jcryptolib`,
  `com.rabbitmq:stream-client`, `com.rabbitmq:amqp-client`, `org.postgresql:postgresql`,
  `org.junit.jupiter:junit-jupiter`)
- **Library classes**:
    - `src/main/java/com/github/akarazhev/cryptoscout/test/MockData.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/PodmanCompose.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/Constants.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/StreamTestPublisher.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/StreamTestConsumer.java`
- **Tests**:
    - `src/test/java/com/github/akarazhev/cryptoscout/test/PodmanComposeTest.java`
    - `src/test/java/com/github/akarazhev/cryptoscout/test/StreamConsumerPublisherTest.java`
- **Resources**:
    - `src/main/resources/bybit-spot/` – JSON fixtures for klines (1/5/15/60/240/1d), tickers
    - `src/main/resources/bybit-linear/` – JSON fixtures for klines (1/5/15/60/240/1d), tickers
    - `src/main/resources/bybit-ta-spot/` – JSON fixtures for order books (1/50/200/1000), public trades
    - `src/main/resources/bybit-ta-linear/` – JSON fixtures for order books (1/50/200/1000), public trades, all
      liquidations
    - `src/main/resources/cmc-parser/` – CMC Fear & Greed Index (`fgi.json`)
    - `src/main/resources/bybit-parser/` – Bybit LPL (`lpl.json`)
    - `src/main/resources/podman/podman-compose.yml` – TimescaleDB + RabbitMQ services with healthchecks and tuning
    - `src/main/resources/podman/script/` – SQL: `init.sql`, `bybit_spot_tables.sql`, `bybit_ta_spot_tables.sql`,
      `bybit_linear_tables.sql`, `bybit_ta_linear_tables.sql`, `cmc_parser_tables.sql`, `bybit_parser_tables.sql`
    - `src/main/resources/podman/rabbitmq/` – RabbitMQ assets: `enabled_plugins`, `rabbitmq.conf`, `definitions.json`

## README changes implemented

- **Overview and Features**: Introduced the purpose and two pillars of the library (mock data and Podman Compose
  manager).
- **Requirements**: Java 25, Maven 3.9+, Podman and `podman-compose` for DB/MQ lifecycle.
- **Installation**: Added Maven dependency snippet and optional PostgreSQL driver note for tests.
- **Usage**: Included Java examples for `MockData.get(...)` and JUnit lifecycle with `PodmanCompose.up()/down()`.
- **Streams utilities**: Documented `StreamTestPublisher` and `StreamTestConsumer` for RabbitMQ Streams-based tests.
- **Configuration**: Documented all system properties and defaults from `Constants.PodmanCompose`:
    - `podman.compose.cmd` (default: `podman-compose`)
    - `podman.cmd` (default: `podman`)
    - `test.db.jdbc.url` (default: `jdbc:postgresql://localhost:5432/crypto_scout`)
    - `test.db.user` (default: `crypto_scout_db`)
    - `test.db.password` (default: `crypto_scout_db`)
    - MQ: `test.mq.host`, `test.mq.port`, `test.mq.user`, `test.mq.password`, `test.mq.stream`
    - `podman.compose.up.timeout.min` (default: `3`)
    - `podman.compose.down.timeout.min` (default: `1`)
    - `podman.compose.ready.interval.sec` (default: `2`)
- **Resource loading**: Clarified that `MockData` reads fixtures from the classpath and `PodmanCompose` automatically
  extracts the `podman/` assets (compose file, RabbitMQ configs, SQL scripts) to a temporary directory when packaged in
  a JAR; if
  resources are on disk during test runs (e.g., Maven), it uses them in place. No manual copying is required.
- **Packaged Resources**: Listed all Bybit Spot fixtures and described the TimescaleDB compose service and bootstrap
  SQL.
- **Error Handling**: Described failure modes (`IllegalStateException` on resource missing or command/timeout issues).
- **AI-driven note and License**: Clearly stated AI-driven authorship and MIT license.
- **Gradle snippet**: Added `testImplementation("com.github.akarazhev.cryptoscout:crypto-scout-test:0.0.1")`.
- **Quickstart**: Added build/install/test commands and guidance for calling `PodmanCompose.up()/down()`.
- **RabbitMQ Management UI**: Documented URL and credentials mapping to `test.mq.*` properties.
- **CI/CD and Troubleshooting**: Added runner prerequisites, timeout tuning, and common fixes.

## Podman Compose service summary

- DB:
    - **Service**: `crypto-scout-collector-db`
    - **Image**: `timescale/timescaledb:latest-pg17`
    - **Ports**: `5432:5432`
    - **Environment**: `POSTGRES_DB=crypto_scout`, `POSTGRES_USER=crypto_scout_db`, `POSTGRES_PASSWORD=crypto_scout_db`,
      telemetry off, tuned worker/memory settings.
    - **Init**: Mounts SQL from `podman/script/*.sql` to bootstrap schema and tables.
    - **Healthcheck**: `pg_isready -U $POSTGRES_USER -d $POSTGRES_DB`.
    - **Command tuning**: Enables `timescaledb` and `pg_stat_statements`, sets common PostgreSQL performance parameters.
    - **Restart policy**: `unless-stopped`; `stop_grace_period: 1m`.
- MQ:
    - **Service**: `crypto-scout-mq`
    - **Image**: `rabbitmq:4.1.4-management`
    - **Ports**: `5672:5672` (AMQP), `5552:5552` (Streams), `127.0.0.1:15672:15672` (Mgmt UI), `127.0.0.1:15692:15692` (
      metrics)
    - **Volumes**: mounts `podman/rabbitmq/enabled_plugins`, `rabbitmq.conf`, `definitions.json` as read-only
    - **Healthcheck**: `rabbitmq-diagnostics -q ping`
    - **Security/limits**: `no-new-privileges`, tmpfs for `/tmp`, ulimits tuned
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

- **Constants mapped to README**: Verified all property keys and defaults from `Constants.PodmanCompose` are
  documented (DB, MQ, timeouts, intervals, commands).
- **Resource inventory**: Verified filenames and locations for Bybit fixtures (all sources/types), compose SQL scripts,
  and RabbitMQ assets.
- **Examples compile conceptually**: Code snippets reflect actual class names and signatures in `MockData` and
  `PodmanCompose`.
- **Streams utilities**: Verified documentation references `StreamTestPublisher` and `StreamTestConsumer` accurately.

## Change log

- Updated `README.md` with production-ready documentation covering features, installation, configuration (DB + MQ),
  usage, resources, error handling, Streams utilities, Gradle snippet, Quickstart, RabbitMQ Management UI, CI/CD,
  and Troubleshooting.
- Clarified resource-loading behavior in README and this report; corrected reference to `Constants.PodmanCompose`.
- Updated files reviewed to reflect actual classes and tests; added RabbitMQ service details and MQ configuration.
- Fixed PostgreSQL driver note to reflect that the driver is included by default in this library.