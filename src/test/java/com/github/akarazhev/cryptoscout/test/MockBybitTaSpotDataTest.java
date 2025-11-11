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
    void shouldSpotKline1DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.KLINE_1);
        assertNotNull(data);
        assertEquals(4, data.size());
    }

    @Test
    void shouldSpotKline5DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.KLINE_5);
        assertNotNull(data);
        assertEquals(4, data.size());
    }

    @Test
    void shouldSpotKline15DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.KLINE_15);
        assertNotNull(data);
        assertEquals(4, data.size());
    }

    @Test
    void shouldSpotKline60DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.KLINE_60);
        assertNotNull(data);
        assertEquals(4, data.size());
    }

    @Test
    void shouldSpotKline240DataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.KLINE_240);
        assertNotNull(data);
        assertEquals(4, data.size());
    }

    @Test
    void shouldSpotKlineDDataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.KLINE_D);
        assertNotNull(data);
        assertEquals(4, data.size());
    }

    @Test
    void shouldSpotTickersDataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.BYBIT_TA_SPOT, MockData.Type.TICKERS);
        assertNotNull(data);
        assertEquals(5, data.size());
    }
}
