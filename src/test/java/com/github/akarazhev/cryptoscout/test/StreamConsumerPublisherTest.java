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
import com.rabbitmq.stream.Environment;
import io.activej.eventloop.Eventloop;
import io.activej.promise.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_HOST;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_PASSWORD;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_PORT;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.MQ_USER;
import static com.github.akarazhev.cryptoscout.test.Constants.Stream.BYBIT_CRYPTO_STREAM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class StreamConsumerPublisherTest {
    private static ExecutorService executor;
    private static Eventloop reactor;
    private static Environment environment;
    private static StreamTestPublisher publisher;
    private static StreamTestConsumer consumer;

    @BeforeAll
    static void setup() {
        PodmanCompose.up();
        executor = Executors.newVirtualThreadPerTaskExecutor();
        reactor = Eventloop.builder().withCurrentThread().build();
        environment = Environment.builder()
                .host(MQ_HOST)
                .port(MQ_PORT)
                .username(MQ_USER)
                .password(MQ_PASSWORD)
                .build();
        publisher = StreamTestPublisher.create(reactor, executor, environment, BYBIT_CRYPTO_STREAM);
        consumer = StreamTestConsumer.create(reactor, executor, environment, BYBIT_CRYPTO_STREAM);
        TestUtils.await(publisher.start(), consumer.start());
    }

    @Test
    void testPublishConsume() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_SPOT, MockData.Type.KLINE_1);
        assertNotNull(data);
        publisher.publish(Payload.of(Provider.BYBIT, Source.PM, data));
        final var result = TestUtils.await(consumer.getResult());
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
        environment.close();
        executor.shutdown();
        PodmanCompose.down();
    }
}