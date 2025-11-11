# Issue 2: Implement `AssertBybitLinearTablesTest`, `AssertBybitParserTablesTest` , `AssertBybitSpotTablesTest`,
`AssertBybitTaLinearTablesTest` , `AssertBybitTaSpotTablesTest` tests

In this `crypto-scout-test` project we are going to implement:

- `com.github.akarazhev.cryptoscout.test.AssertBybitLinearTablesTest`,
- `com.github.akarazhev.cryptoscout.test.AssertBybitParserTablesTest`,
- `com.github.akarazhev.cryptoscout.test.AssertBybitSpotTablesTest`,
- `com.github.akarazhev.cryptoscout.test.AssertBybitTaLinearTablesTest`,
- `com.github.akarazhev.cryptoscout.test.AssertBybitTaSpotTablesTest`

tests by finishing them.

## Roles

Take the following roles:

- Expert java engineer.

## Conditions

- Use the best practices and design patterns.
- Use the current technological stack, that's: `ActiveJ 6.0`, `Java 25`, `maven 3.9.1`, `podman 5.6.2`,
  `podman-compose 1.5.0`, `timescale/timescaledb:latest-pg17`, `rabbitmq:4.1.4-management`, `JUnit 5.13.4`.
- Rely on the `sample` section.
- Implementation must be production ready.
- Do not hallucinate.

## Tasks

- As the `expert java engineer` review the current `AssertCmcParserTablesTest.java` implementation in
  `crypto-scout-test` project and update other assert tables tests by implementing similar methods. Define table names
  in the `Constants.DB` class.
- As the `expert java engineer` recheck your proposal and make sure that they are correct and haven't missed any
  important points.

## Sample

This is the sample how the `AssertCmcParserTablesTest` implementation can look like:

```java
package com.github.akarazhev.cryptoscout.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.akarazhev.cryptoscout.test.Constants.DB.CMC_FGI;

final class AssertCmcParserTablesTest {

    @BeforeAll
    static void setup() {
        PodmanCompose.up();
    }

    @AfterAll
    static void cleanup() {
        PodmanCompose.down();
    }

    @Test
    void shouldCmcFgiTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(CMC_FGI, 0);
    }
}
```