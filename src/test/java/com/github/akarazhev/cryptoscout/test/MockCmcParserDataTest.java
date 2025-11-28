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

import java.util.List;
import java.util.Map;

import static com.github.akarazhev.cryptoscout.test.Constants.MockData.THREE_ROWS;
import static com.github.akarazhev.cryptoscout.test.Constants.MockData.FIVE_ROWS;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.CLOSE;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.HIGH;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.LOW;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.MARKET_CAP2;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.OPEN;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.QUOTE;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.QUOTES;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.TIMESTAMP;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.TIME_CLOSE;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.TIME_HIGH;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.TIME_LOW;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.TIME_OPEN;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.UPDATE_TIME;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.VALUE;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.VALUE_CLASSIFICATION;
import static com.github.akarazhev.jcryptolib.cmc.Constants.Response.VOLUME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MockCmcParserDataTest {

    @Test
    void shouldFgiDataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.CMC_PARSER, MockData.Type.FGI);
        assertNotNull(data);
        assertEquals(THREE_ROWS, data.size());
        assertTrue(data.containsKey(VALUE));
        assertTrue(data.containsKey(UPDATE_TIME));
        assertTrue(data.containsKey(VALUE_CLASSIFICATION));
    }

    @Test
    void shouldKline1dDataReturnMap() throws Exception {
        final var data = MockData.get(MockData.Source.CMC_PARSER, MockData.Type.KLINE_D);
        assertNotNull(data);
        assertEquals(FIVE_ROWS, data.size());
        assertTrue(data.containsKey(QUOTES));
        final var quotes = (List<Map<String, Object>>) data.get(QUOTES);
        assertFalse(quotes.isEmpty());
        for (final var quote : quotes) {
            assertTrue(quote.containsKey(TIME_OPEN));
            assertTrue(quote.containsKey(TIME_CLOSE));
            assertTrue(quote.containsKey(TIME_HIGH));
            assertTrue(quote.containsKey(TIME_LOW));
            assertTrue(quote.containsKey(QUOTE));
            final var value = (Map<String, Object>) quote.get(QUOTE);
            assertFalse(value.isEmpty());
            assertTrue(value.containsKey(OPEN));
            assertTrue(value.containsKey(CLOSE));
            assertTrue(value.containsKey(HIGH));
            assertTrue(value.containsKey(LOW));
            assertTrue(value.containsKey(VOLUME));
            assertTrue(value.containsKey(MARKET_CAP2));
            assertTrue(value.containsKey(TIMESTAMP));
        }
    }
}
