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

import com.github.akarazhev.jcryptolib.stream.Payload;
import com.github.akarazhev.jcryptolib.util.JsonUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.activej.async.service.ReactiveService;
import io.activej.promise.Promise;
import io.activej.reactor.AbstractReactive;
import io.activej.reactor.nio.NioReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;

import static com.github.akarazhev.cryptoscout.test.Constants.MQ.DEFAULT_QUEUE;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_HOST;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_PASSWORD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_USER;
import static com.rabbitmq.client.ConnectionFactory.DEFAULT_AMQP_PORT;

public final class AmqpTestPublisher extends AbstractReactive implements ReactiveService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AmqpTestPublisher.class);
    private final Executor executor;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String queue;
    private volatile Connection connection;
    private volatile Channel channel;

    public static AmqpTestPublisher create(final NioReactor reactor, final Executor executor) {
        return new AmqpTestPublisher(reactor, executor, MQ_HOST, DEFAULT_AMQP_PORT, MQ_USER, MQ_PASSWORD, DEFAULT_QUEUE);
    }

    public static AmqpTestPublisher create(final NioReactor reactor, final Executor executor,
                                           final String host, final int port,
                                           final String username, final String password,
                                           final String queue) {
        return new AmqpTestPublisher(reactor, executor, host, port, username, password, queue);
    }

    private AmqpTestPublisher(final NioReactor reactor, final Executor executor,
                              final String host, final int port,
                              final String username, final String password,
                              final String queue) {
        super(reactor);
        this.executor = executor;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.queue = queue;
    }

    @Override
    public Promise<?> start() {
        return Promise.ofBlocking(executor, () -> {
            try {
                final var factory = new ConnectionFactory();
                factory.setHost(host);
                factory.setPort(port);
                factory.setUsername(username);
                factory.setPassword(password);
                factory.setAutomaticRecoveryEnabled(true);
                factory.setNetworkRecoveryInterval(5000);
                factory.setRequestedHeartbeat(30);

                connection = factory.newConnection("amqp-test-publisher");
                channel = connection.createChannel();
                channel.confirmSelect();
                // Ensure the queue exists (will throw if it doesn't)
                channel.queueDeclarePassive(queue);
            } catch (final Exception ex) {
                LOGGER.error("Failed to start AmqpTestPublisher", ex);
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public Promise<?> stop() {
        return Promise.ofBlocking(executor, () -> {
            try {
                if (channel != null) {
                    channel.close();
                    channel = null;
                }
            } catch (final Exception ex) {
                LOGGER.warn("Error closing AMQP channel", ex);
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                        connection = null;
                    }
                } catch (final Exception ex) {
                    LOGGER.warn("Error closing AMQP connection", ex);
                }
            }
        });
    }

    public Promise<?> publish(final Payload<Map<String, Object>> payload) {
        return Promise.ofBlocking(executor, () -> {
            if (channel == null || !channel.isOpen()) {
                throw new IllegalStateException("AMQP channel is not open. Call start() before publish().");
            }

            try {
                final var body = JsonUtils.object2Bytes(payload);
                final var props = new AMQP.BasicProperties.Builder()
                        .contentType("application/json")
                        .deliveryMode(2) // persistent
                        .build();
                // Publish to default exchange with routing key = queue name
                channel.basicPublish("", queue, props, body);
                channel.waitForConfirmsOrDie();
            } catch (final Exception ex) {
                LOGGER.error("Failed to publish payload to AMQP queue {}: {}", queue, ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        });
    }
}
