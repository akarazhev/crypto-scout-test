# crypto-scout-test – Production Setup Report

## Summary

- **Goal**: Provide professional, production-ready documentation for the `crypto-scout-test` library to be used as a
  `test`-scope dependency in `crypto-scout` and similar projects.
- **Scope**:
    - Describe capabilities: Bybit mock data fixtures and Podman Compose-based DB lifecycle for tests.
    - Document configuration, requirements, usage patterns, and packaged resources.
    - Clearly state that the project was written with AI-driven tools and follows best practices.

## Recommended GitHub repository description

- **About (one-liner)**: Test support library for crypto-scout: Bybit mock data and Podman Compose-managed TimescaleDB
  for integration tests.

## Files reviewed

- **Build**: `pom.xml` (Java 25, Maven plugins, dependencies: DSL-JSON, optional PostgreSQL runtime, JUnit Jupiter for
  tests)
- **Library classes**:
    - `src/main/java/com/github/akarazhev/cryptoscout/test/BybitMockData.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/PodmanCompose.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/JsonUtils.java`
    - `src/main/java/com/github/akarazhev/cryptoscout/test/Constants.java`
- **Tests**:
    - `src/test/java/com/github/akarazhev/cryptoscout/test/BybitMockDataTest.java`
- **Resources**:
    - `src/main/resources/bybit-spot/` – JSON fixtures for spot klines (1/5/15/60/240/1d), tickers, public trades, and
      order books (1/50/200/1000)
    - `src/main/resources/podman/podman-compose.yml` – TimescaleDB PG17 service with healthcheck and tuning
    - `src/main/resources/podman/script/init.sql` – Schema bootstrap for `crypto_scout` (Spot and Linear tables,
      policies, compression, indexes)

## README changes implemented

- **Overview and Features**: Introduced the purpose and two pillars of the library (mock data and Podman Compose
  manager).
- **Requirements**: Java 25, Maven 3.9+, Podman and `podman-compose` for DB lifecycle.
- **Installation**: Added Maven dependency snippet and optional PostgreSQL driver note for tests.
- **Usage**: Included Java examples for `BybitMockData.get(...)` and JUnit lifecycle with `PodmanCompose.up()/down()`.
- **Configuration**: Documented all system properties and defaults from `Constants.PodmanComposeConfig`:
    - `podman.compose.cmd` (default: `podman-compose`)
    - `podman.cmd` (default: `podman`)
    - `test.db.jdbc.url` (default: `jdbc:postgresql://localhost:5432/crypto_scout`)
    - `test.db.user` (default: `crypto_scout_db`)
    - `test.db.password` (default: `crypto_scout_db`)
    - `podman.compose.up.timeout.min` (default: `3`)
    - `podman.compose.down.timeout.min` (default: `1`)
    - `podman.compose.ready.interval.sec` (default: `2`)
- **Packaged Resources**: Listed all Bybit Spot fixtures and described the TimescaleDB compose service and bootstrap
  SQL.
- **Error Handling**: Described failure modes (`IllegalStateException` on resource missing or command/timeout issues).
- **AI-driven note and License**: Clearly stated AI-driven authorship and MIT license.

## Podman Compose service summary

- **Service**: `crypto-scout-collector-db`
- **Image**: `timescale/timescaledb:latest-pg17`
- **Ports**: `5432:5432`
- **Environment**: `POSTGRES_DB=crypto_scout`, `POSTGRES_USER=crypto_scout_db`, `POSTGRES_PASSWORD=crypto_scout_db`,
  telemetry off, tuned worker/memory settings.
- **Init**: Mounting `script/init.sql` as read-only to bootstrap schema and tables.
- **Healthcheck**: `pg_isready -U $POSTGRES_USER -d $POSTGRES_DB`, with interval, timeout, retries, and start period.
- **Command tuning**: Enables `timescaledb` and `pg_stat_statements`, sets common PostgreSQL performance parameters.
- **Restart policy**: `unless-stopped`; `stop_grace_period: 1m`.

## Considerations

- **Resource loading**: Fixtures and compose files are loaded via the classloader and then resolved to disk paths.
  Ensure the test runtime provides these resources on the filesystem (typical with Maven during test runs). If
  packaging/consuming as a JAR in another project, verify resource availability on disk or adjust build/runtime
  accordingly.
- **PostgreSQL driver**: Required at test runtime when using `PodmanCompose` for JDBC readiness checks. Declared as
  optional in this library to avoid forcing it on consumers; add it to your test scope.

## Next steps (optional)

- Provide Bybit `LINEAR` mock JSON fixtures to pair with existing schema support.
- Add Gradle examples alongside Maven snippets.

## Verification checklist

- **Constants mapped to README**: Verified all property keys and defaults from `Constants.PodmanComposeConfig` are
  documented.
- **Resource inventory**: Verified filenames and locations for Bybit Spot fixtures and compose assets.
- **Examples compile conceptually**: Code snippets reflect actual class names and signatures in `BybitMockData` and
  `PodmanCompose`.

## Change log

- Updated `README.md` with production-ready documentation covering features, installation, configuration, usage,
  resources, error handling, and acknowledgements.