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

import com.github.akarazhev.jcryptolib.stream.Message;
import com.github.akarazhev.jcryptolib.util.JsonUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.activej.async.service.ReactiveService;
import io.activej.promise.Promise;
import io.activej.reactor.AbstractReactive;
import io.activej.reactor.nio.NioReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.CONTENT_TYPE_JSON;
import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.DELIVERY_MODE_PERSISTENT;
import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.PUBLISHER_CLIENT_NAME;

public final class AmqpTestPublisher extends AbstractReactive implements ReactiveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpTestPublisher.class);
    private static final long CONFIRM_TIMEOUT_MS = 5000;
    private final Executor executor;
    private final ConnectionFactory connectionFactory;
    private final String queue;
    private final AtomicReference<Connection> connectionRef = new AtomicReference<>();
    private final AtomicReference<Channel> channelRef = new AtomicReference<>();

    public static AmqpTestPublisher create(final NioReactor reactor, final Executor executor,
                                           final ConnectionFactory connectionFactory, final String queue) {
        return new AmqpTestPublisher(reactor, executor, connectionFactory, queue);
    }

    private AmqpTestPublisher(final NioReactor reactor, final Executor executor,
                              final ConnectionFactory connectionFactory, final String queue) {
        super(reactor);
        this.executor = executor;
        this.connectionFactory = connectionFactory;
        this.queue = queue;
    }

    @Override
    public Promise<Void> start() {
        return Promise.ofBlocking(executor, () -> {
            try {
                final var connection = connectionFactory.newConnection(PUBLISHER_CLIENT_NAME);
                final var channel = connection.createChannel();
                channel.confirmSelect();
                // Ensure the queue exists (will throw if it doesn't)
                channel.queueDeclarePassive(queue);
                if (!connectionRef.compareAndSet(null, connection)) {
                    closeQuietly(channel, connection);
                    throw new IllegalStateException("Publisher already started");
                }
                channelRef.set(channel);
            } catch (final Exception e) {
                LOGGER.error("Failed to start AmqpTestPublisher", e);
                throw new IllegalStateException("Failed to start AmqpTestPublisher", e);
            }
        });
    }

    @Override
    public Promise<Void> stop() {
        return Promise.ofBlocking(executor, () -> {
            final var channel = channelRef.getAndSet(null);
            final var connection = connectionRef.getAndSet(null);
            closeQuietly(channel, connection);
        });
    }

    private void closeQuietly(final Channel channel, final Connection connection) {
        if (channel != null) {
            try {
                channel.close();
            } catch (final Exception e) {
                LOGGER.warn("Error closing AMQP channel", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (final Exception e) {
                LOGGER.warn("Error closing AMQP connection", e);
            }
        }
    }

    public Promise<Void> publish(final String exchange, final String routingKey, final Message<?> message) {
        return Promise.ofBlocking(executor, () -> {
            final var channel = channelRef.get();
            if (channel == null) {
                throw new IllegalStateException("Publisher not started. Call start() before publish().");
            }

            try {
                final var props = new AMQP.BasicProperties.Builder()
                        .contentType(CONTENT_TYPE_JSON)
                        .deliveryMode(DELIVERY_MODE_PERSISTENT)
                        .build();
                channel.basicPublish(exchange, routingKey, props, JsonUtils.object2Bytes(message));
                channel.waitForConfirmsOrDie(CONFIRM_TIMEOUT_MS);
            } catch (final AlreadyClosedException e) {
                LOGGER.error("AMQP channel closed during publish", e);
                throw new IllegalStateException("AMQP channel closed", e);
            } catch (final TimeoutException e) {
                LOGGER.error("Timeout waiting for publish confirmation", e);
                throw new IllegalStateException("Publish confirmation timeout", e);
            } catch (final Exception e) {
                LOGGER.error("Failed to publish payload to AMQP queue {}: {}", queue, e.getMessage(), e);
                throw new IllegalStateException("Failed to publish payload to AMQP queue: " + queue, e);
            }
        });
    }
}
