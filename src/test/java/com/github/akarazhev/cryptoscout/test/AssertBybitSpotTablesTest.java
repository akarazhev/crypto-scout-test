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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_SPOT_TICKERS;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_SPOT_KLINE_1M;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_SPOT_KLINE_5M;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_SPOT_KLINE_15M;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_SPOT_KLINE_60M;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_SPOT_KLINE_240M;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_SPOT_KLINE_1D;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.ZERO_ROWS;

final class AssertBybitSpotTablesTest {

    @BeforeAll
    static void setup() {
        PodmanCompose.up();
    }

    @AfterAll
    static void cleanup() {
        PodmanCompose.down();
    }

    @Test
    void shouldBybitSpotTickersTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_SPOT_TICKERS, ZERO_ROWS);
    }

    @Test
    void shouldBybitSpotKline1mTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_SPOT_KLINE_1M, ZERO_ROWS);
    }

    @Test
    void shouldBybitSpotKline5mTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_SPOT_KLINE_5M, ZERO_ROWS);
    }

    @Test
    void shouldBybitSpotKline15mTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_SPOT_KLINE_15M, ZERO_ROWS);
    }

    @Test
    void shouldBybitSpotKline60mTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_SPOT_KLINE_60M, ZERO_ROWS);
    }

    @Test
    void shouldBybitSpotKline240mTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_SPOT_KLINE_240M, ZERO_ROWS);
    }

    @Test
    void shouldBybitSpotKline1dTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_SPOT_KLINE_1D, ZERO_ROWS);
    }
}
