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

import com.github.akarazhev.jcryptolib.stream.Command;
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
import java.util.concurrent.Executor;

import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.CONSUMER_CLIENT_NAME;
import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.PREFETCH_COUNT;

public final class AmqpTestConsumer extends AbstractReactive implements ReactiveService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AmqpTestConsumer.class);
    private final Executor executor;
    private final ConnectionFactory connectionFactory;
    private final String queue;
    private final SettablePromise<Command<?>> command = new SettablePromise<>();
    private volatile Connection connection;
    private volatile Channel channel;
    private volatile String consumerTag;

    public static AmqpTestConsumer create(final NioReactor reactor, final Executor executor,
                                          final ConnectionFactory connectionFactory, final String queue) {
        return new AmqpTestConsumer(reactor, executor, connectionFactory, queue);
    }

    private AmqpTestConsumer(final NioReactor reactor, final Executor executor,
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
                connection = connectionFactory.newConnection(CONSUMER_CLIENT_NAME);
                channel = connection.createChannel();
                channel.basicQos(PREFETCH_COUNT);
                channel.queueDeclarePassive(queue);

                final DeliverCallback deliver = (_, delivery) -> {
                    try {
                        command.set((Command<?>) JsonUtils.bytes2Object(delivery.getBody(), Command.class));
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (final Exception e) {
                        try {
                            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                        } catch (final IOException ex) {
                            LOGGER.debug("Error cancelling AMQP consumer", ex);
                        }

                        command.setException(e);
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

    public Promise<Command<?>> getCommand() {
        return command;
    }

    @Override
    public Promise<Void> stop() {
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
