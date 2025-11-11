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
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.activej.async.service.ReactiveService;
import io.activej.promise.Promise;
import io.activej.promise.SettablePromise;
import io.activej.reactor.AbstractReactive;
import io.activej.reactor.nio.NioReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.github.akarazhev.cryptoscout.test.Constants.MQ.DEFAULT_QUEUE;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_HOST;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_PASSWORD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_USER;
import static com.rabbitmq.client.ConnectionFactory.DEFAULT_AMQP_PORT;

public final class AmqpTestConsumer extends AbstractReactive implements ReactiveService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AmqpTestConsumer.class);
    private final Executor executor;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String queue;
    private final SettablePromise<Payload<Map<String, Object>>> result = new SettablePromise<>();
    private volatile Connection connection;
    private volatile Channel channel;
    private volatile String consumerTag;

    public static AmqpTestConsumer create(final NioReactor reactor, final Executor executor) {
        return new AmqpTestConsumer(reactor, executor, MQ_HOST, DEFAULT_AMQP_PORT, MQ_USER, MQ_PASSWORD, DEFAULT_QUEUE);
    }

    public static AmqpTestConsumer create(final NioReactor reactor, final Executor executor,
                                          final String host, final int port,
                                          final String username, final String password,
                                          final String queue) {
        return new AmqpTestConsumer(reactor, executor, host, port, username, password, queue);
    }

    private AmqpTestConsumer(final NioReactor reactor, final Executor executor,
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

                connection = factory.newConnection("amqp-test-consumer");
                channel = connection.createChannel();
                channel.basicQos(1);
                channel.queueDeclarePassive(queue);

                final DeliverCallback deliver = (_, delivery) -> {
                    try {
                        @SuppressWarnings("unchecked") final var payload =
                                (Payload<Map<String, Object>>) JsonUtils.bytes2Object(delivery.getBody(), Payload.class);
                        result.set(payload);
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (final Exception e) {
                        try {
                            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                        } catch (final IOException ex) {
                            LOGGER.debug("Error cancelling AMQP consumer", ex);
                        }

                        result.setException(e);
                    } finally {
                        if (consumerTag != null && channel.isOpen()) {
                            try {
                                channel.basicCancel(consumerTag);
                            } catch (final Exception ex) {
                                LOGGER.debug("Error cancelling AMQP consumer", ex);
                            }
                        }
                    }
                };

                final CancelCallback cancel = tag -> LOGGER.debug("AMQP consumer cancelled: {}", tag);
                consumerTag = channel.basicConsume(queue, false, deliver, cancel);
            } catch (final Exception ex) {
                LOGGER.error("Failed to start AmqpTestConsumer", ex);
                throw new RuntimeException(ex);
            }
        });
    }

    public Promise<?> getResult() {
        return result;
    }

    @Override
    public Promise<?> stop() {
        return Promise.ofBlocking(executor, () -> {
            try {
                if (channel != null) {
                    try {
                        if (consumerTag != null) {
                            channel.basicCancel(consumerTag);
                            consumerTag = null;
                        }
                    } catch (final Exception ex) {
                        LOGGER.debug("Error cancelling AMQP consumer on stop", ex);
                    }

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
}
