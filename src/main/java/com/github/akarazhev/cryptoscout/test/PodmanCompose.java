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

package com.github.akarazhev.cryptoscout.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.stream.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.akarazhev.cryptoscout.test.Constants.DB.JDBC_HOST;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.BYBIT_LINEAR_TABLES_SQL;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.BYBIT_SPOT_TABLES_SQL;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.CRYPTO_SCOUT_TABLES_SQL;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.COMPOSE_FILE_LOCATION;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.COMPOSE_FILE_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.DB_CONTAINER_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.DETACHED_ARG;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.DOWN_CMD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.DOWN_VOLUMES_ARG;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.DOWN_TIMEOUT;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_CMD_FAILED_MIDDLE;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_CMD_FAILED_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_CMD_TIMEOUT_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_COMPOSE_DIR_INVALID;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_COMPOSE_FILE_NOT_FOUND;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_COMPOSE_URI_RESOLVE;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_CONTAINER_STILL_PRESENT;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_DB_NOT_READY_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_DB_NOT_READY_SUFFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_MQ_NOT_READY_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_MQ_NOT_READY_SUFFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_OUTPUT_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_PARTIAL_OUTPUT_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_RESOURCE_NOT_FOUND;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_RUN_CMD_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.FILE_ARG;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.INIT_SQL;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.LINE_SPLIT_REGEX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MIN_MILLIS;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MIN_SECONDS;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_HOST;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_PASSWORD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_PORT;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_STREAM;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_USER;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_CONTAINER_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.OUTPUT_THREAD_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PODMAN_CMD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PODMAN_COMPOSE_CMD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PS_ALL_ARG;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PS_CMD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PS_FORMAT_ARG;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PS_NAMES_TEMPLATE;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PS_TIMEOUT_SEC;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.READY_RETRY_INTERVAL;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.UP_CMD;
import static com.github.akarazhev.cryptoscout.test.Constants.PATH_SEPARATOR;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.PROTOCOL_FILE;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.TEMP_DIR_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.SCRIPT_DIR_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_EXTRACT_RESOURCES;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.RABBITMQ_DIR_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.RABBITMQ_ENABLED_PLUGINS;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.RABBITMQ_CONF_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.RABBITMQ_DEFINITIONS_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.UP_TIMEOUT;

public final class PodmanCompose {
    private static final Logger LOGGER = LoggerFactory.getLogger(PodmanCompose.class);
    private static final Path COMPOSE_DIR;

    static {
        final var resourcePath = COMPOSE_FILE_LOCATION + PATH_SEPARATOR + COMPOSE_FILE_NAME;
        final var resourceUrl = PodmanCompose.class.getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalStateException(ERR_RESOURCE_NOT_FOUND + resourcePath);
        }

        try {
            if (PROTOCOL_FILE.equalsIgnoreCase(resourceUrl.getProtocol())) {
                final var composeFile = Paths.get(resourceUrl.toURI());
                if (!Files.exists(composeFile)) {
                    throw new IllegalStateException(ERR_COMPOSE_FILE_NOT_FOUND + composeFile);
                }

                COMPOSE_DIR = composeFile.getParent();
                if (COMPOSE_DIR == null || !Files.isDirectory(COMPOSE_DIR)) {
                    throw new IllegalStateException(ERR_COMPOSE_DIR_INVALID + COMPOSE_DIR);
                }
            } else {
                // Resource is inside a JAR; extract the whole 'podman' directory to a temp location
                COMPOSE_DIR = extractPodmanDirToTemp();
            }
        } catch (final URISyntaxException e) {
            throw new IllegalStateException(ERR_COMPOSE_URI_RESOLVE, e);
        }
    }

    private PodmanCompose() {
        throw new UnsupportedOperationException();
    }

    public static void up() {
        // Start containers
        runCommand(COMPOSE_DIR, UP_TIMEOUT, PODMAN_COMPOSE_CMD, FILE_ARG, COMPOSE_FILE_NAME, UP_CMD, DETACHED_ARG);
        // Wait for DB readiness
        waitForDatabaseReady(UP_TIMEOUT);
        // Wait for MQ readiness
        waitForMqReady(UP_TIMEOUT);
    }

    public static void down() {
        // Stop and remove containers + volumes (prevents stale DB state across test runs)
        runCommand(COMPOSE_DIR, DOWN_TIMEOUT, PODMAN_COMPOSE_CMD, FILE_ARG, COMPOSE_FILE_NAME, DOWN_CMD, DOWN_VOLUMES_ARG);
        // Wait until containers are removed
        waitForContainerRemoval(DB_CONTAINER_NAME, DOWN_TIMEOUT);
        waitForContainerRemoval(MQ_CONTAINER_NAME, DOWN_TIMEOUT);
    }

    private static void waitForDatabaseReady(final Duration timeout) {
        final var deadline = System.nanoTime() + timeout.toNanos();
        final var loginTimeoutSec = (int) Math.max(MIN_SECONDS, READY_RETRY_INTERVAL.getSeconds());
        DriverManager.setLoginTimeout(loginTimeoutSec);

        while (System.nanoTime() < deadline) {
            if (canConnectToDb()) {
                return;
            }

            sleep(READY_RETRY_INTERVAL);
        }

        throw new IllegalStateException(ERR_DB_NOT_READY_PREFIX + timeout.toSeconds() + ERR_DB_NOT_READY_SUFFIX);
    }

    private static void waitForMqReady(final Duration timeout) {
        final var deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {
            if (canConnectToMq()) {
                return;
            }

            sleep(READY_RETRY_INTERVAL);
        }

        throw new IllegalStateException(ERR_MQ_NOT_READY_PREFIX + timeout.toSeconds() + ERR_MQ_NOT_READY_SUFFIX);
    }

    private static boolean canConnectToDb() {
        return DBUtils.canConnect();
    }

    private static boolean canConnectToMq() {
        try (final var environment = Environment.builder()
                .host(MQ_HOST)
                .port(MQ_PORT)
                .username(MQ_USER)
                .password(MQ_PASSWORD)
                .build();
             final var _ = environment.producerBuilder()
                     .name(MQ_STREAM)
                     .stream(MQ_STREAM)
                     .build()) {
            LOGGER.info("Connected to RabbitMQ: {}", environment);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private static void waitForContainerRemoval(final String containerName, final Duration timeout) {
        final var deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (!isContainerPresent(containerName)) {
                return;
            }

            sleep(READY_RETRY_INTERVAL);
        }

        throw new IllegalStateException(ERR_CONTAINER_STILL_PRESENT + containerName);
    }

    private static boolean isContainerPresent(final String containerName) {
        final var out = runAndCapture(COMPOSE_DIR, Duration.ofSeconds(PS_TIMEOUT_SEC), PODMAN_CMD, PS_CMD, PS_ALL_ARG,
                PS_FORMAT_ARG, PS_NAMES_TEMPLATE);
        final var lines = out.split(LINE_SPLIT_REGEX);
        for (final String line : lines) {
            if (containerName.equals(line.trim())) {
                return true;
            }
        }

        return false;
    }

    private static void runCommand(final Path dir, final Duration timeout, final String... command) {
        final var output = runAndCapture(dir, timeout, command);
        // best-effort to show output on success in debug scenarios
        if (!output.isEmpty()) {
            LOGGER.info(output);
        }
    }

    private static String runAndCapture(final Path dir, final Duration timeout, final String... command) {
        final var cmd = Arrays.asList(command);
        final var pb = new ProcessBuilder(cmd);
        pb.directory(dir.toFile());
        pb.redirectErrorStream(true);
        try {
            final var p = pb.start();
            final var out = new StringBuilder();
            final var reader = new Thread(() -> {
                try (final var br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        out.append(line).append(System.lineSeparator());
                    }

                } catch (final IOException _) {
                }
            }, OUTPUT_THREAD_NAME);
            reader.setDaemon(true);
            reader.start();

            final var finished = p.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                p.destroyForcibly();
                throw new IllegalStateException(ERR_CMD_TIMEOUT_PREFIX + String.join(" ", cmd) +
                        ERR_PARTIAL_OUTPUT_PREFIX + out);
            }

            final var exit = p.exitValue();
            if (exit != 0) {
                throw new IllegalStateException(ERR_CMD_FAILED_PREFIX + exit + ERR_CMD_FAILED_MIDDLE +
                        String.join(" ", cmd) + ERR_OUTPUT_PREFIX + out);
            }

            return out.toString();
        } catch (final IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new IllegalStateException(ERR_RUN_CMD_PREFIX + String.join(" ", command), e);
        }
    }

    private static Path extractPodmanDirToTemp() {
        try {
            // Create temp podman directory
            final var tempRoot = Files.createTempDirectory(TEMP_DIR_PREFIX);
            final var podmanTargetDir = tempRoot.resolve(COMPOSE_FILE_LOCATION);
            final var scriptDir = podmanTargetDir.resolve(SCRIPT_DIR_NAME);
            final var rabbitDir = podmanTargetDir.resolve(RABBITMQ_DIR_NAME);
            Files.createDirectories(scriptDir);
            Files.createDirectories(rabbitDir);

            // Copy required resources from classpath
            copyClasspathFile(COMPOSE_FILE_LOCATION + PATH_SEPARATOR + COMPOSE_FILE_NAME,
                    podmanTargetDir.resolve(COMPOSE_FILE_NAME));
            // Copy RabbitMQ resources
            copyClasspathFile(COMPOSE_FILE_LOCATION + PATH_SEPARATOR + RABBITMQ_DIR_NAME + PATH_SEPARATOR +
                    RABBITMQ_ENABLED_PLUGINS, rabbitDir.resolve(RABBITMQ_ENABLED_PLUGINS));
            copyClasspathFile(COMPOSE_FILE_LOCATION + PATH_SEPARATOR + RABBITMQ_DIR_NAME + PATH_SEPARATOR +
                    RABBITMQ_CONF_NAME, rabbitDir.resolve(RABBITMQ_CONF_NAME));
            copyClasspathFile(COMPOSE_FILE_LOCATION + PATH_SEPARATOR + RABBITMQ_DIR_NAME + PATH_SEPARATOR +
                    RABBITMQ_DEFINITIONS_NAME, rabbitDir.resolve(RABBITMQ_DEFINITIONS_NAME));
            // Copy sql scripts
            copyScript(scriptDir, INIT_SQL);
            copyScript(scriptDir, BYBIT_LINEAR_TABLES_SQL);
            copyScript(scriptDir, BYBIT_SPOT_TABLES_SQL);
            copyScript(scriptDir, CRYPTO_SCOUT_TABLES_SQL);
            return podmanTargetDir;
        } catch (final Exception e) {
            throw new IllegalStateException(ERR_EXTRACT_RESOURCES, e);
        }
    }

    private static void copyScript(final Path scriptDir, final String scriptName) throws IOException {
        final var scriptPath = COMPOSE_FILE_LOCATION + PATH_SEPARATOR + SCRIPT_DIR_NAME + PATH_SEPARATOR + scriptName;
        try (final var is = PodmanCompose.class.getClassLoader().getResourceAsStream(scriptPath)) {
            if (is != null) {
                Files.copy(is, scriptDir.resolve(scriptName), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static void copyClasspathFile(final String classpath, final Path target) throws IOException {
        try (final var is = PodmanCompose.class.getClassLoader().getResourceAsStream(classpath)) {
            if (is == null) {
                throw new IllegalStateException(ERR_RESOURCE_NOT_FOUND + classpath);
            }

            Files.createDirectories(target.getParent());
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void sleep(final Duration duration) {
        try {
            Thread.sleep(Math.max(MIN_MILLIS, duration.toMillis()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
