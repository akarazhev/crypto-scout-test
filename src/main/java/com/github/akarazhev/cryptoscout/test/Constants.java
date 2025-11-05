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

final class Constants {
    private Constants() {
        throw new UnsupportedOperationException();
    }

    // Common path separator for resource paths
    static final String PATH_SEPARATOR = "/";

    final static class MockData {
        private MockData() {
            throw new UnsupportedOperationException();
        }

        // Source directories
        static final String CMC_PARSER = "cmc-parser";
        static final String BYBIT_PARSER = "bybit-parser";
        static final String BYBIT_SPOT = "bybit-spot";
        static final String BYBIT_LINEAR = "bybit-linear";
        // Stream types
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
        // System property keys
        static final String READY_INTERVAL_SEC_PROP = "podman.compose.ready.interval.sec";
        static final String PODMAN_COMPOSE_CMD_PROP = "podman.compose.cmd";
        static final String PODMAN_CMD_PROP = "podman.cmd";
        static final String DB_JDBC_URL_PROP = "test.db.jdbc.url";
        static final String DB_USER_PROP = "test.db.user";
        static final String DB_PASSWORD_PROP = "test.db.password";
        static final String UP_TIMEOUT_MIN_PROP = "podman.compose.up.timeout.min";
        static final String DOWN_TIMEOUT_MIN_PROP = "podman.compose.down.timeout.min";
        // Default values for system properties
        static final String PODMAN_COMPOSE_CMD_DEFAULT = "podman-compose";
        static final String PODMAN_CMD_DEFAULT = "podman";
        static final String DB_JDBC_URL_DEFAULT = "jdbc:postgresql://localhost:5432/crypto_scout";
        static final String DB_USER_DEFAULT = "crypto_scout_db";
        static final String DB_PASSWORD_DEFAULT = "crypto_scout_db";
        static final long UP_TIMEOUT_MIN_DEFAULT = 3L;
        static final long READY_INTERVAL_SEC_DEFAULT = 2L;
        static final long DOWN_TIMEOUT_MIN_DEFAULT = 1L;
        // DB health check
        static final String DB_HEALTH_QUERY = "SELECT 1";
        // Thread names
        static final String OUTPUT_THREAD_NAME = "podman-compose-output";
        // Podman resource files
        static final String SCRIPT_DIR_NAME = "script";
        static final String INIT_SCRIPT_NAME = "init.sql";
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
    }
}
