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
import com.github.akarazhev.jcryptolib.stream.Provider;
import com.github.akarazhev.jcryptolib.stream.Source;
import com.rabbitmq.client.ConnectionFactory;
import io.activej.eventloop.Eventloop;
import io.activej.promise.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.akarazhev.cryptoscout.test.Constants.Amqp.DEFAULT_QUEUE;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_HOST;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_PASSWORD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_USER;
import static com.github.akarazhev.cryptoscout.test.Constants.Stream.CONNECTION_ESTABLISHED_MS;
import static com.rabbitmq.client.ConnectionFactory.DEFAULT_AMQP_PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class AmqpConsumerPublisherTest {
    private static ExecutorService executor;
    private static Eventloop reactor;
    private static AmqpTestPublisher publisher;
    private static AmqpTestConsumer consumer;

    @BeforeAll
    static void setup() throws InterruptedException {
        PodmanCompose.up();
        executor = Executors.newVirtualThreadPerTaskExecutor();
        reactor = Eventloop.builder().withCurrentThread().build();
        final var factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setPort(DEFAULT_AMQP_PORT);
        factory.setUsername(MQ_USER);
        factory.setPassword(MQ_PASSWORD);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);
        factory.setRequestedHeartbeat(30);

        publisher = AmqpTestPublisher.create(reactor, executor, factory, DEFAULT_QUEUE);
        publisher.start();
        Thread.sleep(CONNECTION_ESTABLISHED_MS);
        consumer = AmqpTestConsumer.create(reactor, executor, factory, DEFAULT_QUEUE);
        consumer.start();
        Thread.sleep(CONNECTION_ESTABLISHED_MS);
    }

    @Test
    void testPublishConsume() {
        final Map<String, Object> data = Map.of("key", "value");
        publisher.publish(Payload.of(Provider.BYBIT, Source.PM, data));
        @SuppressWarnings("unchecked") final var result = (Payload<Map<String, Object>>) TestUtils.await(consumer.getResult());
        assertNotNull(result);
        assertEquals(Provider.BYBIT, result.getProvider());
        assertEquals(Source.PM, result.getSource());
        assertEquals(data, result.getData());
    }

    @AfterAll
    static void cleanup() {
        reactor.post(() -> consumer.stop()
                .whenComplete(() -> publisher.stop()
                        .whenComplete(() -> reactor.breakEventloop())));
        reactor.run();
        executor.shutdown();
        PodmanCompose.down();
    }
}