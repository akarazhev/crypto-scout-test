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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class MockBybitTaSpotDataTest {

    @Test
    void shouldSpotPublicTradeDataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.PUBLIC_TRADE);
        assertNotNull(data);
        assertEquals(4, data.size());
    }

    @Test
    void shouldSpotOrderBook1DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.ORDER_BOOK_1);
        assertNotNull(data);
        assertEquals(5, data.size());
    }

    @Test
    void shouldSpotOrderBook50DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.ORDER_BOOK_50);
        assertNotNull(data);
        assertEquals(5, data.size());
    }

    @Test
    void shouldSpotOrderBook200DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.ORDER_BOOK_200);
        assertNotNull(data);
        assertEquals(5, data.size());
    }

    @Test
    void shouldSpotOrderBook1000DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.ORDER_BOOK_1000);
        assertNotNull(data);
        assertEquals(5, data.size());
    }
}
