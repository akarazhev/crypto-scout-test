# Issue 4: Implement `AmqpTestPublisher` service

In this `crypto-scout-test` project we are going to implement the
`com.github.akarazhev.cryptoscout.test.AmqpTestPublisher` service by finishing it.

## Roles

Take the following roles:

- Expert java engineer.

## Conditions

- Use the best practices and design patterns.
- Use the current technological stack, that's: `ActiveJ 6.0`, `Java 25`, `maven 3.9.1`, `podman 5.7.0`,
  `podman-compose 1.5.0`, `timescale/timescaledb:latest-pg17`, `JUnit 6.0.1`, `Amqp Client 5.27.1`.
- Rely on the `sample` section. Use implementation of the `StreamTestPublisher` service as an example how to implement
  the `AmqpTestPublisher` service that publishes messages to a RabbitMQ queue.
- Implementation must be production ready.
- Do not hallucinate.

## Tasks

- As the `expert java engineer` review the current `AmqpTestPublisher.java` implementation in `crypto-scout-test` project
  and update it by implementing all methods needed methods. The service should publish messages to a RabbitMQ queue.
- As the `expert java engineer` recheck your proposal and make sure that they are correct and haven't missed any
  important points.

## Sample

This is the sample how the `AmqpTestPublisher` implementation can look like:

```java
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
    public Promise<Void> start() {
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
    public Promise<Void> stop() {
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

    public Promise<Void> publish(final Payload<Map<String, Object>> payload) {
        return Promise.ofBlocking(executor, () -> {
            try {
                final var message = producer.messageBuilder()
                        .addData(JsonUtils.object2Bytes(payload))
                        .build();
                producer.send(message, _ -> {
                });
            } catch (final Exception ex) {
                LOGGER.error("Failed to publish payload to stream: {}", ex.getMessage(), ex);
            }
        });
    }
}
```