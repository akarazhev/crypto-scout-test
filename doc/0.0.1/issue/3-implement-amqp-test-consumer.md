# Issue 3: Implement `AmqpTestConsumer` service

In this `crypto-scout-test` project we are going to implement the
`com.github.akarazhev.cryptoscout.test.AmqpTestConsumer` service by finishing it.

## Roles

Take the following roles:

- Expert java engineer.

## Conditions

- Use the best practices and design patterns.
- Use the current technological stack, that's: `ActiveJ 6.0`, `Java 25`, `maven 3.9.1`, `podman 5.6.2`,
  `podman-compose 1.5.0`, `timescale/timescaledb:latest-pg17`, `JUnit 6.0.1`, `Amqp Client 5.27.1`.
- Rely on the `sample` section. Use implementation of the `StreamTestConsumer` service as an example how to implement
  the `AmqpTestConsumer` service that consumes messages from a RabbitMQ queue.
- Implementation must be production ready.
- Do not hallucinate.

## Tasks

- As the `expert java engineer` review the current `AmqpTestConsumer.java` implementation in `crypto-scout-test` project
  and update it by implementing all methods needed methods. The service should consume messages from a RabbitMQ queue.
- As the `expert java engineer` recheck your proposal and make sure that they are correct and haven't missed any
  important points.

## Sample

This is the sample how the `AmqpTestConsumer` implementation can look like:

```java
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

public final class StreamTestConsumer extends AbstractReactive implements ReactiveService {
    private final static Logger LOGGER = LoggerFactory.getLogger(StreamTestConsumer.class);
    private final Executor executor;
    private final Environment environment;
    private final String stream;
    private final SettablePromise<Payload<Map<String, Object>>> result = new SettablePromise<>();
    private volatile Consumer consumer;

    public static StreamTestConsumer create(final NioReactor reactor, final Executor executor,
                                            final Environment environment, final String stream) {
        return new StreamTestConsumer(reactor, executor, environment, stream);
    }

    private StreamTestConsumer(final NioReactor reactor, final Executor executor, final Environment environment,
                               final String stream) {
        super(reactor);
        this.executor = executor;
        this.environment = environment;
        this.stream = stream;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Promise<Void> start() {
        return Promise.ofBlocking(executor, () -> {
            consumer = environment.consumerBuilder()
                    .name(stream)
                    .stream(stream)
                    .offset(OffsetSpecification.first())
                    .manualTrackingStrategy().
                    builder()
                    .messageHandler((_, message) -> {
                        try {
                            result.set(JsonUtils.bytes2Object(message.getBodyAsBinary(), Payload.class));
                        } catch (final IOException e) {
                            result.setException(e);
                        }
                    })
                    .build();
        });
    }

    public Promise<Payload<Map<String, Object>>> getResult() {
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
            }
        });
    }
}
```