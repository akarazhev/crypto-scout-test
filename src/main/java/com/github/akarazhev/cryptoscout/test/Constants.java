/*
 * MIT License
 *
 * Copyright (c) 2025 Andrey Karazhev
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

package com.github.akarazhev.cryptoscout.test;

import java.time.Duration;

final class Constants {
    private Constants() {
        throw new UnsupportedOperationException();
    }

    // Common path separator for resource paths
    static final String PATH_SEPARATOR = "/";

    final static class Stream {
        private Stream() {
            throw new UnsupportedOperationException();
        }

        static final String BYBIT_CRYPTO_STREAM = "bybit-crypto-stream";
    }

    final static class Amqp {
        private Amqp() {
            throw new UnsupportedOperationException();
        }

        static final String CONSUMER_CLIENT_NAME = "amqp-test-consumer";
        static final String PUBLISHER_CLIENT_NAME = "amqp-test-publisher";
        static final String AMQP_COLLECTOR_QUEUE = "collector-queue";
        static final String CONTENT_TYPE_JSON = "application/json";
        static final int DELIVERY_MODE_PERSISTENT = 2;
        static final int PREFETCH_COUNT = 1;
    }

    final static class DB {
        private DB() {
            throw new UnsupportedOperationException();
        }

        static final String JDBC_HOST = "localhost";
        static final String JDBC_URL = System.getProperty("test.db.jdbc.url",
                "jdbc:postgresql://" + JDBC_HOST + ":5432/crypto_scout");
        static final String DB_USER = System.getProperty("test.db.user", "crypto_scout_db");
        static final String DB_PASSWORD = System.getProperty("test.db.password", "crypto_scout_db");
        static final String SELECT_COUNT = "SELECT COUNT(*) FROM %s";
        static final String SELECT_ONE = "SELECT 1";
        static final String DELETE_FROM_TABLE = "DELETE FROM %s";
        static final int ZERO_ROWS = 0;
        static final int FIRST_ROW = 1;
        static final String CMC_FGI = "crypto_scout.cmc_fgi";
        // Bybit parser
        static final String BYBIT_LPL = "crypto_scout.bybit_lpl";
        // Bybit spot
        static final String BYBIT_SPOT_TICKERS = "crypto_scout.bybit_spot_tickers";
        static final String BYBIT_SPOT_KLINE_1M = "crypto_scout.bybit_spot_kline_1m";
        static final String BYBIT_SPOT_KLINE_5M = "crypto_scout.bybit_spot_kline_5m";
        static final String BYBIT_SPOT_KLINE_15M = "crypto_scout.bybit_spot_kline_15m";
        static final String BYBIT_SPOT_KLINE_60M = "crypto_scout.bybit_spot_kline_60m";
        static final String BYBIT_SPOT_KLINE_240M = "crypto_scout.bybit_spot_kline_240m";
        static final String BYBIT_SPOT_KLINE_1D = "crypto_scout.bybit_spot_kline_1d";
        // Bybit linear
        static final String BYBIT_LINEAR_TICKERS = "crypto_scout.bybit_linear_tickers";
        static final String BYBIT_LINEAR_KLINE_1M = "crypto_scout.bybit_linear_kline_1m";
        static final String BYBIT_LINEAR_KLINE_5M = "crypto_scout.bybit_linear_kline_5m";
        static final String BYBIT_LINEAR_KLINE_15M = "crypto_scout.bybit_linear_kline_15m";
        static final String BYBIT_LINEAR_KLINE_60M = "crypto_scout.bybit_linear_kline_60m";
        static final String BYBIT_LINEAR_KLINE_240M = "crypto_scout.bybit_linear_kline_240m";
        static final String BYBIT_LINEAR_KLINE_1D = "crypto_scout.bybit_linear_kline_1d";
        // Bybit TA spot
        static final String BYBIT_TA_SPOT_PUBLIC_TRADE = "crypto_scout.bybit_ta_spot_public_trade";
        static final String BYBIT_TA_SPOT_ORDER_BOOK_1 = "crypto_scout.bybit_ta_spot_order_book_1";
        static final String BYBIT_TA_SPOT_ORDER_BOOK_50 = "crypto_scout.bybit_ta_spot_order_book_50";
        static final String BYBIT_TA_SPOT_ORDER_BOOK_200 = "crypto_scout.bybit_ta_spot_order_book_200";
        static final String BYBIT_TA_SPOT_ORDER_BOOK_1000 = "crypto_scout.bybit_ta_spot_order_book_1000";
        // Bybit TA linear
        static final String BYBIT_TA_LINEAR_PUBLIC_TRADE = "crypto_scout.bybit_ta_linear_public_trade";
        static final String BYBIT_TA_LINEAR_ORDER_BOOK_1 = "crypto_scout.bybit_ta_linear_order_book_1";
        static final String BYBIT_TA_LINEAR_ORDER_BOOK_50 = "crypto_scout.bybit_ta_linear_order_book_50";
        static final String BYBIT_TA_LINEAR_ORDER_BOOK_200 = "crypto_scout.bybit_ta_linear_order_book_200";
        static final String BYBIT_TA_LINEAR_ORDER_BOOK_1000 = "crypto_scout.bybit_ta_linear_order_book_1000";
        static final String BYBIT_TA_LINEAR_ALL_LIQUDATION = "crypto_scout.bybit_ta_linear_all_liqudation";
    }

    final static class MockData {
        private MockData() {
            throw new UnsupportedOperationException();
        }

        static final int THREE_ROWS = 3;
        static final int FOUR_ROWS = 4;
        static final int FIVE_ROWS = 5;
        static final int TWENTY_ONE_ROWS = 21;
        // Source directories
        static final String CMC_PARSER = "cmc-parser";
        static final String BYBIT_PARSER = "bybit-parser";
        static final String BYBIT_SPOT = "bybit-spot";
        static final String BYBIT_TA_SPOT = "bybit-ta-spot";
        static final String BYBIT_LINEAR = "bybit-linear";
        static final String BYBIT_TA_LINEAR = "bybit-ta-linear";
        // Stream types
        static final String FGI = "cmc_fgi";
        static final String LPL = "bybit_lpl";
        static final String KLINE_1 = "kline.1";
        static final String KLINE_5 = "kline.5";
        static final String KLINE_15 = "kline.15";
        static final String KLINE_60 = "kline.60";
        static final String KLINE_240 = "kline.240";
        static final String KLINE_D = "kline.D";
        static final String TICKERS = "tickers";
        static final String PUBLIC_TRADE = "publicTrade";
        static final String ORDER_BOOK_1 = "orderbook.1";
        static final String ORDER_BOOK_50 = "orderbook.50";
        static final String ORDER_BOOK_200 = "orderbook.200";
        static final String ORDER_BOOK_1000 = "orderbook.1000";
        static final String ALL_LIQUDATION = "allLiquidation";
        // File extension
        static final String JSON_EXTENSION = ".json";
        // Error messages
        static final String ERR_FILE_NOT_FOUND_PREFIX = "File not found: ";
    }

    final static class PodmanCompose {
        private PodmanCompose() {
            throw new UnsupportedOperationException();
        }

        // Default podman values
        static final String COMPOSE_FILE_LOCATION = "podman";
        static final String COMPOSE_FILE_NAME = "podman-compose.yml";
        static final String DB_CONTAINER_NAME = "crypto-scout-collector-db";
        //
        static final String PODMAN_COMPOSE_CMD = System.getProperty("podman.compose.cmd", "podman-compose");
        static final String PODMAN_CMD = System.getProperty("podman.cmd", "podman");
        static final String MQ_HOST = System.getProperty("test.mq.host", "localhost");
        static final int MQ_PORT = Integer.parseInt(System.getProperty("test.mq.port", Integer.toString(5552)));
        static final String MQ_USER = System.getProperty("test.mq.user", "crypto_scout_mq");
        static final String MQ_PASSWORD = System.getProperty("test.mq.password", "crypto_scout_mq");
        static final String MQ_STREAM = System.getProperty("test.mq.stream", "bybit-crypto-stream");
        static final Duration UP_TIMEOUT = Duration.ofMinutes(Long.getLong("podman.compose.up.timeout.min",
                3L));
        static final Duration DOWN_TIMEOUT = Duration.ofMinutes(Long.getLong("podman.compose.down.timeout.min",
                1L));
        static final Duration READY_RETRY_INTERVAL = Duration.ofSeconds(Long.getLong("podman.compose.ready.interval.sec",
                2L));
        // Thread names
        static final String OUTPUT_THREAD_NAME = "podman-compose-output";
        // Podman resource files
        static final String SCRIPT_DIR_NAME = "script";
        static final String INIT_SQL = "init.sql";
        static final String BYBIT_LINEAR_TABLES_SQL = "bybit_linear_tables.sql";
        static final String BYBIT_PARSER_TABLES_SQL = "bybit_parser_tables.sql";
        static final String BYBIT_SPOT_TABLES_SQL = "bybit_spot_tables.sql";
        static final String BYBIT_TA_LINEAR_TABLES_SQL = "bybit_ta_linear_tables.sql";
        static final String BYBIT_TA_SPOT_TABLES_SQL = "bybit_ta_spot_tables.sql";
        static final String CMC_PARSER_TABLES_SQL = "cmc_parser_tables.sql";
        static final String RABBITMQ_DIR_NAME = "rabbitmq";
        static final String RABBITMQ_ENABLED_PLUGINS = "enabled_plugins";
        static final String RABBITMQ_CONF_NAME = "rabbitmq.conf";
        static final String RABBITMQ_DEFINITIONS_NAME = "definitions.json";
        static final String TEMP_DIR_PREFIX = "crypto-scout-podman-";
        static final String PROTOCOL_FILE = "file";
        // Podman command args
        static final String FILE_ARG = "-f";
        static final String UP_CMD = "up";
        static final String DOWN_CMD = "down";
        static final String DETACHED_ARG = "-d";
        // Podman ps invocation
        static final String PS_CMD = "ps";
        static final String PS_ALL_ARG = "-a";
        static final String PS_FORMAT_ARG = "--format";
        static final String PS_NAMES_TEMPLATE = "{{.Names}}";
        static final long PS_TIMEOUT_SEC = 15L;
        // Misc
        static final String LINE_SPLIT_REGEX = "\\R";
        // Numeric boundaries
        static final long MIN_SECONDS = 1L;
        static final long MIN_MILLIS = 1L;
        // Error/message templates
        static final String ERR_RESOURCE_NOT_FOUND = "Resource not found: ";
        static final String ERR_COMPOSE_FILE_NOT_FOUND = "Compose file not found on disk: ";
        static final String ERR_COMPOSE_DIR_INVALID = "Compose directory is invalid: ";
        static final String ERR_COMPOSE_URI_RESOLVE = "Failed to resolve compose file URI";
        static final String ERR_DB_NOT_READY_PREFIX = "Database was not ready within ";
        static final String ERR_DB_NOT_READY_SUFFIX = " seconds";
        static final String ERR_CONTAINER_STILL_PRESENT = "Container still present after timeout: ";
        static final String ERR_CMD_TIMEOUT_PREFIX = "Command timed out: ";
        static final String ERR_PARTIAL_OUTPUT_PREFIX = "\nPartial output:\n";
        static final String ERR_CMD_FAILED_PREFIX = "Command failed (";
        static final String ERR_CMD_FAILED_MIDDLE = "): ";
        static final String ERR_OUTPUT_PREFIX = "\nOutput:\n";
        static final String ERR_RUN_CMD_PREFIX = "Failed to run command: ";
        static final String ERR_EXTRACT_RESOURCES = "Failed to extract podman resources";
        static final String ERR_MQ_NOT_READY_PREFIX = "MQ was not ready within ";
        static final String ERR_MQ_NOT_READY_SUFFIX = " seconds";
        static final String ERR_TABLE_ROW_COUNT = "Unexpected row count for table: ";
    }
}
