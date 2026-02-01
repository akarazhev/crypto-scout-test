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

import com.github.akarazhev.jcryptolib.stream.Payload;
import com.github.akarazhev.jcryptolib.util.JsonUtils;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import io.activej.async.service.ReactiveService;
import io.activej.promise.Promise;
import io.activej.reactor.AbstractReactive;
import io.activej.reactor.nio.NioReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public final class StreamTestPublisher extends AbstractReactive implements ReactiveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamTestPublisher.class);
    private final Executor executor;
    private final Environment environment;
    private final String stream;
    private final AtomicReference<Producer> producerRef = new AtomicReference<>();

    public static StreamTestPublisher create(final NioReactor reactor, final Executor executor,
                                             final Environment environment, final String stream) {
        return new StreamTestPublisher(reactor, executor, environment, stream);
    }

    private StreamTestPublisher(final NioReactor reactor, final Executor executor,
                                final Environment environment, final String stream) {
        super(reactor);
        this.executor = executor;
        this.environment = environment;
        this.stream = stream;
    }

    @Override
    public Promise<Void> start() {
        return Promise.ofBlocking(executor, () -> {
            try {
                final var producer = environment.producerBuilder()
                        .name(stream)
                        .stream(stream)
                        .build();
                if (!producerRef.compareAndSet(null, producer)) {
                    producer.close();
                    throw new IllegalStateException("Publisher already started");
                }
            } catch (final Exception ex) {
                LOGGER.error("Failed to start StreamTestPublisher", ex);
                throw new IllegalStateException("Failed to start StreamTestPublisher", ex);
            }
        });
    }

    @Override
    public Promise<Void> stop() {
        return Promise.ofBlocking(executor, () -> {
            final var producer = producerRef.getAndSet(null);
            if (producer != null) {
                try {
                    producer.close();
                } catch (final Exception ex) {
                    LOGGER.warn("Error closing stream producer", ex);
                }
            }
        });
    }

    public Promise<Void> publish(final Payload<Map<String, Object>> payload) {
        return Promise.ofBlocking(executor, () -> {
            final var producer = producerRef.get();
            if (producer == null) {
                throw new IllegalStateException("Publisher not started. Call start() before publish().");
            }
            try {
                final var message = producer.messageBuilder()
                        .addData(JsonUtils.object2Bytes(payload))
                        .build();
                producer.send(message, _ -> {
                });
            } catch (final Exception ex) {
                LOGGER.error("Failed to publish payload to stream: {}", ex.getMessage(), ex);
                throw new IllegalStateException("Failed to publish payload to stream", ex);
            }
        });
    }
}
