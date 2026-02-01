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
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
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
import java.util.concurrent.atomic.AtomicReference;

public final class StreamTestConsumer extends AbstractReactive implements ReactiveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamTestConsumer.class);
    private final Executor executor;
    private final Environment environment;
    private final String stream;
    private final AtomicReference<SettablePromise<Payload<Map<String, Object>>>> resultRef = new AtomicReference<>();
    private volatile Consumer consumer;

    public static StreamTestConsumer create(final NioReactor reactor, final Executor executor,
                                            final Environment environment, final String stream) {
        return new StreamTestConsumer(reactor, executor, environment, stream);
    }

    private StreamTestConsumer(final NioReactor reactor, final Executor executor,
                               final Environment environment, final String stream) {
        super(reactor);
        this.executor = executor;
        this.environment = environment;
        this.stream = stream;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Promise<Void> start() {
        return Promise.ofBlocking(executor, () -> {
            final var result = new SettablePromise<Payload<Map<String, Object>>>();
            if (!resultRef.compareAndSet(null, result)) {
                throw new IllegalStateException("Consumer already started");
            }
            consumer = environment.consumerBuilder()
                    .name(stream)
                    .stream(stream)
                    .offset(OffsetSpecification.first())
                    .manualTrackingStrategy().
                    builder()
                    .messageHandler((_, message) -> {
                        final var promise = resultRef.get();
                        if (promise != null && !promise.isComplete()) {
                            try {
                                promise.set(JsonUtils.bytes2Object(message.getBodyAsBinary(), Payload.class));
                            } catch (final IOException e) {
                                promise.setException(e);
                            }
                        }
                    })
                    .build();
        });
    }

    public Promise<Payload<Map<String, Object>>> getResult() {
        final var result = resultRef.get();
        if (result == null) {
            return Promise.ofException(new IllegalStateException("Consumer not started"));
        }
        return result;
    }

    @Override
    public Promise<Void> stop() {
        return Promise.ofBlocking(executor, () -> {
            try {
                if (consumer != null) {
                    consumer.close();
                    consumer = null;
                }
            } catch (final Exception ex) {
                LOGGER.warn("Error closing stream consumer", ex);
            } finally {
                resultRef.set(null);
            }
        });
    }
}
