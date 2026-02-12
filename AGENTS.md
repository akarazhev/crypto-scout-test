# AGENTS.md

This document provides guidelines for agentic coding contributors to the crypto-scout-test module.

## Project Overview

**crypto-scout-test** is a Java 25 Maven library providing test support utilities for the crypto-scout ecosystem:

## MCP Server Configuration

This module uses the **Context7 MCP server** for enhanced code intelligence and documentation retrieval.

### Available MCP Tools

When working with this codebase, you can use the following MCP tools via the context7 server:

- **resolve-library-id**: Resolve a library name to its Context7 library ID
- **get-library-docs**: Retrieve up-to-date documentation for a library by its ID

### Configuration

The MCP server is configured in `.opencode/package.json`:

```json
{
  "mcp": {
    "context7": {
      "type": "remote",
      "url": "https://mcp.context7.com/mcp",
      "headers": {
        "CONTEXT7_API_KEY": "ctx7sk-4cec80b8-d947-4ff4-a29a-d00bea5a2fac"
      },
      "enabled": true
    }
  }
}
```

### Usage Guidelines

1. **JUnit 6/Jupiter**: Use `resolve-library-id` for "junit" to get the latest testing patterns, lifecycle management, and assertion best practices.

2. **TestContainers/Podman**: Retrieve container testing documentation for database and messaging infrastructure testing.

3. **RabbitMQ Testing**: Access stream and AMQP testing patterns for publisher/consumer test implementations.

4. **Mock Data Patterns**: Get guidance on JSON fixture management and test data organization best practices.

| Category | Files | Purpose |
|----------|-------|---------|
| Main Source | 9 | Test utilities, mock data, RabbitMQ helpers |
| Test Source | 9 | Self-tests for the test library |
| Mock Data | 27 | JSON fixtures (Bybit Spot/Linear, CryptoScout) |

**Total: 18 Java source files, 27 resource files**

## Directory Structure

```
crypto-scout-test/
├── pom.xml                                    # Maven configuration
├── README.md                                  # Module documentation
├── LICENSE                                    # MIT License
├── AGENTS.md                                  # This file
├── .gitignore                                 # Git ignore rules
├── .git/                                      # Git repository
├── .idea/                                     # IntelliJ IDEA configuration
│   ├── workspace.xml
│   ├── dbnavigator.xml
│   ├── .gitignore
│   ├── vcs.xml
│   ├── jarRepositories.xml
│   ├── encodings.xml
│   ├── compiler.xml
│   └── misc.xml
├── .opencode/                                 # OpenCode configuration
│   ├── .gitignore
│   ├── package.json
│   ├── bun.lock
│   ├── OPENCODE_GUIDE.md
│   ├── skills/
│   │   ├── podman-testing/SKILL.md
│   │   ├── java-test-library/SKILL.md
│   │   └── java-code-style/SKILL.md
│   └── agents/
│       ├── developer.md
│       ├── reviewer.md
│       └── writer.md
├── target/                                    # Build output
│   └── surefire-reports/                      # Test reports
│       ├── TEST-com.github.akarazhev.cryptoscout.test.*.xml
│       └── com.github.akarazhev.cryptoscout.test.*.txt
└── src/
    ├── main/
    │   ├── java/com/github/akarazhev/cryptoscout/test/
    │   │   ├── Assertions.java               # Custom test assertions for DB validation
    │   │   ├── AmqpTestConsumer.java         # AMQP consumer for testing
    │   │   ├── AmqpTestPublisher.java        # AMQP publisher with confirmation timeout
    │   │   ├── Constants.java                # All test constants (DB, MQ, Podman)
    │   │   ├── DBUtils.java                  # Database cleanup and connection helpers
    │   │   ├── MockData.java                 # Typed access to JSON mock fixtures
    │   │   ├── PodmanCompose.java            # Container lifecycle management
    │   │   ├── StreamTestConsumer.java       # RabbitMQ Streams consumer (thread-safe)
    │   │   └── StreamTestPublisher.java      # RabbitMQ Streams publisher (thread-safe)
    │   └── resources/
    │       ├── bybit-linear/                 # Linear market mock data (13 files)
    │       │   ├── allLiquidation.json       # Liquidation data
    │       │   ├── kline.1.json              # 1-minute kline data
    │       │   ├── kline.5.json              # 5-minute kline data
    │       │   ├── kline.15.json             # 15-minute kline data
    │       │   ├── kline.60.json             # 60-minute kline data
    │       │   ├── kline.240.json            # 4-hour kline data
    │       │   ├── kline.D.json              # Daily kline data
    │       │   ├── orderbook.1.json          # Order book depth 1
    │       │   ├── orderbook.50.json         # Order book depth 50
    │       │   ├── orderbook.200.json        # Order book depth 200
    │       │   ├── orderbook.1000.json       # Order book depth 1000
    │       │   ├── publicTrade.json          # Public trade data
    │       │   └── tickers.json              # Ticker data
    │       ├── bybit-spot/                   # Spot market mock data (12 files)
    │       │   ├── kline.1.json              # 1-minute kline data
    │       │   ├── kline.5.json              # 5-minute kline data
    │       │   ├── kline.15.json             # 15-minute kline data
    │       │   ├── kline.60.json             # 60-minute kline data
    │       │   ├── kline.240.json            # 4-hour kline data
    │       │   ├── kline.D.json              # Daily kline data
    │       │   ├── orderbook.1.json          # Order book depth 1
    │       │   ├── orderbook.50.json         # Order book depth 50
    │       │   ├── orderbook.200.json        # Order book depth 200
    │       │   ├── orderbook.1000.json       # Order book depth 1000
    │       │   ├── publicTrade.json          # Public trade data
    │       │   └── tickers.json              # Ticker data
    │       └── crypto-scout/                 # Crypto scout mock data (2 files)
    │           ├── kline.D.json              # Daily kline data
    │           └── kline.W.json              # Weekly kline data
    └── test/
        └── java/com/github/akarazhev/cryptoscout/test/
            ├── AmqpConsumerPublisherTest.java    # AMQP pub/sub tests
            ├── AssertBybitLinearTablesTest.java  # Linear table assertion tests
            ├── AssertBybitSpotTablesTest.java    # Spot table assertion tests
            ├── AssertCryptoScoutTablesTest.java  # Crypto scout table assertion tests
            ├── MockBybitLinearDataTest.java      # Linear mock data tests
            ├── MockBybitSpotDataTest.java        # Spot mock data tests
            ├── MockCryptoScoutDataTest.java      # Crypto scout mock data tests
            ├── PodmanComposeTest.java            # Container lifecycle tests
            └── StreamConsumerPublisherTest.java  # Streams pub/sub tests
```

## Key Classes

### MockData
Provides typed access to JSON mock fixtures.

```java
// Available sources
MockData.Source.CRYPTO_SCOUT
MockData.Source.BYBIT_SPOT
MockData.Source.BYBIT_LINEAR

// Available types
MockData.Type.KLINE_1             // 1 minute (Bybit only)
MockData.Type.KLINE_5             // 5 minutes
MockData.Type.KLINE_15            // 15 minutes
MockData.Type.KLINE_60            // 60 minutes
MockData.Type.KLINE_240           // 4 hours
MockData.Type.KLINE_D             // Daily (all sources)
MockData.Type.KLINE_W             // Weekly (crypto-scout only)
MockData.Type.TICKERS             // Bybit only
MockData.Type.PUBLIC_TRADE        // Bybit only
MockData.Type.ORDER_BOOK_1        // Bybit only
MockData.Type.ORDER_BOOK_50       // Bybit only
MockData.Type.ORDER_BOOK_200      // Bybit only
MockData.Type.ORDER_BOOK_1000     // Bybit only
MockData.Type.ALL_LIQUIDATION     // Bybit linear only

// Usage
final var data = MockData.get(MockData.Source.BYBIT_SPOT, MockData.Type.KLINE_1);
```

### PodmanCompose
Manages TimescaleDB and RabbitMQ containers for integration testing.

```java
@BeforeAll
static void setUp() {
    PodmanCompose.up();   // Starts containers with 3-minute timeout
}

@AfterAll
static void tearDown() {
    PodmanCompose.down(); // Stops and removes containers
}
```

### StreamTestPublisher / StreamTestConsumer
Thread-safe RabbitMQ Streams testing utilities.

```java
// Publisher
final var publisher = StreamTestPublisher.create(reactor, executor, env, "bybit-stream");
publisher.start().await();
publisher.publish(payload).await();

// Consumer
final var consumer = StreamTestConsumer.create(reactor, executor, env, "bybit-stream");
consumer.start().await();
final var result = consumer.getResult().await();
```

### AmqpTestPublisher / AmqpTestConsumer
AMQP protocol testing with confirmation timeout (5s).

```java
// Publisher
final var publisher = AmqpTestPublisher.create(reactor, executor, factory, "queue");
publisher.publish("exchange", "routing.key", message).await();

// Consumer
final var consumer = AmqpTestConsumer.create(reactor, executor, factory, "queue");
final var received = consumer.getMessage().await();
```

### DBUtils
Database operations for test setup and teardown.

```java
// Delete all rows from tables
DBUtils.deleteFromTables(dataSource,
    "crypto_scout.bybit_spot_tickers",
    "crypto_scout.bybit_linear_tickers");

// Wait for database readiness
DBUtils.waitForDatabaseReady(dataSource, Duration.ofMinutes(3));
```

### Assertions
Custom JUnit assertions for database validation.

```java
// Assert table row count
Assertions.assertTableCount("crypto_scout.bybit_spot_tickers", 5);
```

## Database Tables (Constants.DB)

### Crypto Scout Tables
- `CMC_FGI` - "crypto_scout.cmc_fgi"
- `CMC_KLINE_1D` - "crypto_scout.cmc_kline_1d"
- `CMC_KLINE_1W` - "crypto_scout.cmc_kline_1w"
- `BYBIT_LPL` - "crypto_scout.bybit_lpl"
- `BTC_PRICE_RISK` - "crypto_scout.btc_price_risk"
- `BTC_RISK_PRICE` - "crypto_scout.btc_risk_price"

### Bybit Spot Tables
- `BYBIT_SPOT_TICKERS` - "crypto_scout.bybit_spot_tickers"
- `BYBIT_SPOT_KLINE_1M` - "crypto_scout.bybit_spot_kline_1m"
- `BYBIT_SPOT_KLINE_5M` - "crypto_scout.bybit_spot_kline_5m"
- `BYBIT_SPOT_KLINE_15M` - "crypto_scout.bybit_spot_kline_15m"
- `BYBIT_SPOT_KLINE_60M` - "crypto_scout.bybit_spot_kline_60m"
- `BYBIT_SPOT_KLINE_240M` - "crypto_scout.bybit_spot_kline_240m"
- `BYBIT_SPOT_KLINE_1D` - "crypto_scout.bybit_spot_kline_1d"
- `BYBIT_SPOT_PUBLIC_TRADE` - "crypto_scout.bybit_spot_public_trade"
- `BYBIT_SPOT_ORDER_BOOK_1` - "crypto_scout.bybit_spot_order_book_1"
- `BYBIT_SPOT_ORDER_BOOK_50` - "crypto_scout.bybit_spot_order_book_50"
- `BYBIT_SPOT_ORDER_BOOK_200` - "crypto_scout.bybit_spot_order_book_200"
- `BYBIT_SPOT_ORDER_BOOK_1000` - "crypto_scout.bybit_spot_order_book_1000"

### Bybit Linear Tables
- `BYBIT_LINEAR_TICKERS` - "crypto_scout.bybit_linear_tickers"
- `BYBIT_LINEAR_KLINE_1M` - "crypto_scout.bybit_linear_kline_1m"
- `BYBIT_LINEAR_KLINE_5M` - "crypto_scout.bybit_linear_kline_5m"
- `BYBIT_LINEAR_KLINE_15M` - "crypto_scout.bybit_linear_kline_15m"
- `BYBIT_LINEAR_KLINE_60M` - "crypto_scout.bybit_linear_kline_60m"
- `BYBIT_LINEAR_KLINE_240M` - "crypto_scout.bybit_linear_kline_240m"
- `BYBIT_LINEAR_KLINE_1D` - "crypto_scout.bybit_linear_kline_1d"
- `BYBIT_LINEAR_PUBLIC_TRADE` - "crypto_scout.bybit_linear_public_trade"
- `BYBIT_LINEAR_ORDER_BOOK_1` - "crypto_scout.bybit_linear_order_book_1"
- `BYBIT_LINEAR_ORDER_BOOK_50` - "crypto_scout.bybit_linear_order_book_50"
- `BYBIT_LINEAR_ORDER_BOOK_200` - "crypto_scout.bybit_linear_order_book_200"
- `BYBIT_LINEAR_ORDER_BOOK_1000` - "crypto_scout.bybit_linear_order_book_1000"
- `BYBIT_LINEAR_ALL_LIQUIDATION` - "crypto_scout.bybit_linear_all_liquidation"

## Build, Test, and Lint Commands

### Build
```bash
mvn clean install
mvn -q -DskipTests install
```

### Run All Tests
```bash
mvn test
mvn -q test
```

### Run Single Test
```bash
mvn test -Dtest=MockBybitSpotDataTest
mvn test -Dtest=MockBybitSpotDataTest#shouldSpotKline1DataReturnMap
mvn -q test -Dtest=MockBybitSpotDataTest
```

### Run Tests with System Properties
```bash
mvn -q -Dpodman.compose.up.timeout.min=5 test
mvn -q -Dtest.db.jdbc.url=jdbc:postgresql://localhost:5432/crypto_scout test
```

### Clean
```bash
mvn clean
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `podman.compose.cmd` | `podman-compose` | Podman Compose executable |
| `podman.cmd` | `podman` | Podman executable |
| `test.db.jdbc.url` | `jdbc:postgresql://localhost:5432/crypto_scout` | Database URL |
| `test.db.user` | `crypto_scout_db` | Database username |
| `test.db.password` | `crypto_scout_db` | Database password |
| `test.mq.host` | `localhost` | RabbitMQ host |
| `test.mq.port` | `5552` | RabbitMQ Streams port |
| `test.mq.user` | `crypto_scout_mq` | RabbitMQ username |
| `test.mq.password` | `crypto_scout_mq` | RabbitMQ password |
| `test.mq.stream` | `bybit-stream` | Default stream name |
| `podman.compose.up.timeout.min` | `3` | Startup timeout (minutes) |
| `podman.compose.down.timeout.min` | `1` | Shutdown timeout (minutes) |
| `podman.compose.ready.interval.sec` | `2` | Ready check interval (seconds) |

## Code Style Guidelines

### File Structure
```
1-23:   MIT License header (see template below)
25:     Package declaration
26:     Blank line
27+:    Imports: java.* → third-party → static imports (blank lines between groups)
        Blank line
        Class/enum/interface declaration
```

### MIT License Header Template
```java
/*
 * MIT License
 *
 * Copyright (c) 2026 Andrey Karazhev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
```

### Import Organization
```java
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import com.rabbitmq.stream.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.akarazhev.cryptoscout.test.Constants.DB.JDBC_URL;
import static com.github.akarazhev.cryptoscout.test.Constants.MockData.BYBIT_SPOT;
```

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `StreamTestPublisher`, `MockData` |
| Methods | camelCase with verb prefix | `waitForDatabaseReady`, `deleteFromTables` |
| Constants | UPPER_SNAKE_CASE in nested static classes | `JDBC_URL`, `DB_USER`, `BYBIT_SPOT` |
| Parameters/locals | `final var` | `final var timeout`, `final var data` |
| Test classes | `<ClassName>Test` suffix | `MockBybitSpotDataTest` |
| Test methods | `should<Subject><Action>` pattern | `shouldSpotKline1DataReturnMap` |
| Enums | PascalCase values | `Source.BYBIT_SPOT`, `Type.KLINE_1` |

### Access Modifiers

**Utility Classes:**
```java
final class Constants {
    private Constants() {
        throw new UnsupportedOperationException();
    }

    static final String PATH_SEPARATOR = "/";

    final static class DB {
        private DB() {
            throw new UnsupportedOperationException();
        }

        static final String JDBC_URL = System.getProperty("test.db.jdbc.url", "...");
    }
}
```

**Factory Pattern:**
```java
public final class StreamTestPublisher extends AbstractReactive {
    public static StreamTestPublisher create(final NioReactor reactor, final Executor executor,
                                           final Environment env, final String stream) {
        return new StreamTestPublisher(reactor, executor, env, stream);
    }

    private StreamTestPublisher(final NioReactor reactor, final Executor executor,
                               final Environment env, final String stream) {
        super(reactor);
        // initialization
    }
}
```

### Error Handling

**Unchecked Exceptions:**
```java
if (resource == null) {
    throw new IllegalStateException("Resource not found: " + name);
}
```

**Try-with-Resources:**
```java
try (final var conn = dataSource.getConnection();
     final var stmt = conn.prepareStatement(sql);
     final var rs = stmt.executeQuery()) {
    while (rs.next()) {
        // Process results
    }
} catch (final SQLException e) {
    throw new IllegalStateException("Database error", e);
}
```

**Interrupt Handling:**
```java
try {
    Thread.sleep(duration.toMillis());
} catch (final InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

**Exception Chaining:**
```java
throw new IllegalStateException("Failed to initialize service", e);
```

### Logging
```java
private static final Logger LOGGER = LoggerFactory.getLogger(ClassName.class);

LOGGER.info("Service started on port {}", port);
LOGGER.warn("Connection lost, retrying...");
LOGGER.error("Failed to process message", exception);
```

### Testing (JUnit 6/Jupiter)

**Test Class Structure:**
```java
final class ExampleTest {

    @BeforeAll
    static void setUp() {
        PodmanCompose.up();
    }

    @AfterAll
    static void tearDown() {
        PodmanCompose.down();
    }

    @Test
    void shouldBehaviorReturnExpected() throws Exception {
        final var result = service.doSomething();
        assertNotNull(result);
        assertEquals(expected, result);
    }
}
```

### Constants Organization

Group related constants in nested static classes:
```java
final class Constants {
    static final String PATH_SEPARATOR = "/";

    final static class Stream {
        static final String BYBIT_STREAM = "bybit-stream";
    }

    final static class Amqp {
        static final String CONSUMER_CLIENT_NAME = "amqp-test-consumer";
        static final String PUBLISHER_CLIENT_NAME = "amqp-test-publisher";
    }

    final static class DB {
        static final String JDBC_URL = System.getProperty("test.db.jdbc.url", "...");
        static final String DB_USER = System.getProperty("test.db.user", "...");
    }

    final static class MockData {
        static final String BYBIT_SPOT = "bybit-spot";
        static final String KLINE_1 = "kline.1";
    }

    final static class PodmanCompose {
        static final Duration UP_TIMEOUT = Duration.ofMinutes(3);
    }
}
```

### System Properties Pattern
```java
static final String VALUE = System.getProperty("property.key", "defaultValue");
static final int PORT = Integer.parseInt(System.getProperty("port.key", "5552"));
static final Duration TIMEOUT = Duration.ofMinutes(Long.getLong("timeout.key", 3L));
```

### Concurrency
- **Volatile fields**: For lazy-initialized singleton-style fields
- **Thread naming**: Provide names for background threads
- **Interruption**: Always restore interrupt status when catching `InterruptedException`
- **Daemon threads**: Set for background readers that shouldn't block JVM shutdown
- **AtomicReference**: Use for thread-safe state management in publishers/consumers

### Resource Management
- **Try-with-resources**: Required for all closeable resources (SQL, streams, connections)
- **Null checks**: Throw `IllegalStateException` for null resources
- **Timeout handling**: Throw `IllegalStateException` with descriptive message including timeout value
- **Process management**: `destroyForcibly()` after timeout, preserve partial output in exception message

### Code Organization
- **Static imports**: From project's `Constants` class heavily used
- **Method length**: Keep reasonable, extract private helpers if too long
- **Static blocks**: Used for initialization that may throw (e.g., resource resolution in `PodmanCompose`)
- **Enum design**: Enums can have fields and methods (see `MockData.Source` and `MockData.Type`)
- **Thread safety**: All publishers and consumers use `AtomicReference` for state management

## Container Services

### TimescaleDB (PostgreSQL 17)
- Port: 5432
- Default DB: `crypto_scout`
- Credentials: `crypto_scout_db` / `crypto_scout_db`
- Tables: Bybit Spot, Bybit Linear, Crypto Scout

### RabbitMQ 4.1.4
- AMQP: 5672
- Streams: 5552
- Management UI: 15672
- Credentials: `crypto_scout_mq` / `crypto_scout_mq`
- Streams: `bybit-stream`, `crypto-scout-stream`

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
