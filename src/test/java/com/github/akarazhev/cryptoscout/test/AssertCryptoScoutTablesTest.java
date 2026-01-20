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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.akarazhev.cryptoscout.test.Constants.DB.BTC_PRICE_RISK;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BTC_RISK_PRICE;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.BYBIT_LPL;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.CMC_FGI;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.CMC_KLINE_1D;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.CMC_KLINE_1W;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.ZERO_ROWS;

final class AssertCryptoScoutTablesTest {

    @BeforeAll
    static void setup() {
        PodmanCompose.up();
    }

    @AfterAll
    static void cleanup() {
        PodmanCompose.down();
    }

    @Test
    void shouldBtcPriceRiskTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BTC_PRICE_RISK, ZERO_ROWS);
    }

    @Test
    void shouldBtcRiskPriceTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BTC_RISK_PRICE, ZERO_ROWS);
    }

    @Test
    void shouldBybitLplTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(BYBIT_LPL, ZERO_ROWS);
    }

    @Test
    void shouldCmcFgiTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(CMC_FGI, ZERO_ROWS);
    }

    @Test
    void shouldCmcKline1dTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(CMC_KLINE_1D, ZERO_ROWS);
    }

    @Test
    void shouldCmcKline1wTableCountReturnZero() throws Exception {
        Assertions.assertTableCount(CMC_KLINE_1W, ZERO_ROWS);
    }
}
