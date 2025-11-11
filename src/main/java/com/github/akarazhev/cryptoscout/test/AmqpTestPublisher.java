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

import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.CONTENT_TYPE_JSON;
import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.DELIVERY_MODE_PERSISTENT;
import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.PUBLISHER_CLIENT_NAME;

public final class AmqpTestPublisher extends AbstractReactive implements ReactiveService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AmqpTestPublisher.class);
    private final Executor executor;
    private final ConnectionFactory connectionFactory;
    private final String queue;
    private volatile Connection connection;
    private volatile Channel channel;

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
                connection = connectionFactory.newConnection(PUBLISHER_CLIENT_NAME);
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
    public Promise<Void> stop() {
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

    public Promise<Void> publish(final String exchange, final String routingKey,
                                 final Payload<Map<String, Object>> payload) {
        return Promise.ofBlocking(executor, () -> {
            if (channel == null || !channel.isOpen()) {
                throw new IllegalStateException("AMQP channel is not open. Call start() before publish().");
            }

            try {
                final var body = JsonUtils.object2Bytes(payload);
                final var props = new AMQP.BasicProperties.Builder()
                        .contentType(CONTENT_TYPE_JSON)
                        .deliveryMode(DELIVERY_MODE_PERSISTENT)
                        .build();
                channel.basicPublish(exchange, routingKey, props, body);
                channel.waitForConfirmsOrDie();
            } catch (final Exception ex) {
                LOGGER.error("Failed to publish payload to AMQP queue {}: {}", queue, ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        });
    }
}
