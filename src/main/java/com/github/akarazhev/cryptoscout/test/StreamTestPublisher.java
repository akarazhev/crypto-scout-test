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
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import io.activej.async.service.ReactiveService;
import io.activej.promise.Promise;
import io.activej.promise.SettablePromise;
import io.activej.reactor.AbstractReactive;
import io.activej.reactor.nio.NioReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;

public final class StreamTestPublisher extends AbstractReactive implements ReactiveService {
    private final static Logger LOGGER = LoggerFactory.getLogger(StreamTestPublisher.class);
    private final Executor executor;
    private final Environment environment;
    private final String stream;
    private volatile Producer producer;

    public static StreamTestPublisher create(final NioReactor reactor, final Executor executor,
                                             final Environment environment, final String stream) {
        return new StreamTestPublisher(reactor, executor, environment, stream);
    }

    private StreamTestPublisher(final NioReactor reactor, final Executor executor, final Environment environment,
                                final String stream) {
        super(reactor);
        this.executor = executor;
        this.environment = environment;
        this.stream = stream;
    }

    @Override
    public Promise<?> start() {
        return Promise.ofBlocking(executor, () -> {
            try {
                producer = environment.producerBuilder()
                        .name(stream)
                        .stream(stream)
                        .build();
            } catch (final Exception ex) {
                LOGGER.error("Failed to start StreamTestPublisher", ex);
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public Promise<?> stop() {
        return Promise.ofBlocking(executor, () -> {
            try {
                if (producer != null) {
                    producer.close();
                    producer = null;
                }
            } catch (final Exception ex) {
                LOGGER.warn("Error closing stream producer", ex);
            }
        });
    }

    public Promise<?> publish(final Payload<Map<String, Object>> payload) {
        final var settablePromise = new SettablePromise<Void>();
        try {
            final var message = producer.messageBuilder()
                    .addData(JsonUtils.object2Bytes(payload))
                    .build();
            producer.send(message, confirmationStatus ->
                    reactor.execute(() -> {
                        if (confirmationStatus.isConfirmed()) {
                            settablePromise.set(null);
                        } else {
                            settablePromise.setException(new RuntimeException("Stream publish not confirmed: " +
                                    confirmationStatus));
                        }
                    })
            );
        } catch (final Exception ex) {
            LOGGER.error("Failed to publish payload to stream: {}", ex.getMessage(), ex);
            settablePromise.setException(ex);
        }

        return settablePromise;
    }
}
