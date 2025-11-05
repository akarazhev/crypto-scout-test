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

import java.util.Map;

import static com.github.akarazhev.cryptoscout.test.Constants.BybitMockData.ERR_FILE_NOT_FOUND_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.BybitMockData.JSON_EXTENSION;
import static com.github.akarazhev.cryptoscout.test.Constants.PATH_SEPARATOR;
import static com.github.akarazhev.cryptoscout.test.Constants.BybitMockData.SOURCE_LINEAR;
import static com.github.akarazhev.cryptoscout.test.Constants.BybitMockData.SOURCE_SPOT;

public final class BybitMockData {

    public enum Source {
        SPOT(SOURCE_SPOT),
        LINEAR(SOURCE_LINEAR);

        private final String source;

        Source(final String source) {
            this.source = source;
        }
    }

    public enum Type {
        // Klines
        KLINE_1(Constants.BybitMockData.KLINE_1),
        KLINE_5(Constants.BybitMockData.KLINE_5),
        KLINE_15(Constants.BybitMockData.KLINE_15),
        KLINE_60(Constants.BybitMockData.KLINE_60),
        KLINE_240(Constants.BybitMockData.KLINE_240),
        KLINE_D(Constants.BybitMockData.KLINE_D),
        // Ticker
        TICKERS(Constants.BybitMockData.TICKERS),
        // Public trade
        PUBLIC_TRADE(Constants.BybitMockData.PUBLIC_TRADE),
        // Order books
        ORDER_BOOK_1(Constants.BybitMockData.ORDER_BOOK_1),
        ORDER_BOOK_50(Constants.BybitMockData.ORDER_BOOK_50),
        ORDER_BOOK_200(Constants.BybitMockData.ORDER_BOOK_200),
        ORDER_BOOK_1000(Constants.BybitMockData.ORDER_BOOK_1000);

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        public String getPath(final Source source) {
            return source.source + PATH_SEPARATOR + type + JSON_EXTENSION;
        }
    }

    private BybitMockData() {
        throw new UnsupportedOperationException();
    }

    public static Map<String, Object> get(final Source source, final Type type) throws Exception {
        try (final var is = BybitMockData.class.getClassLoader().getResourceAsStream(type.getPath(source))) {
            if (is == null) {
                throw new IllegalStateException(ERR_FILE_NOT_FOUND_PREFIX + type.getPath(source));
            }

            return JsonUtils.json2Map(is);
        }
    }
}
