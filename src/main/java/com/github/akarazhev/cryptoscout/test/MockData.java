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

import com.github.akarazhev.jcryptolib.util.JsonUtils;

import java.util.Map;

import static com.github.akarazhev.cryptoscout.test.Constants.MockData.ERR_FILE_NOT_FOUND_PREFIX;
import static com.github.akarazhev.cryptoscout.test.Constants.MockData.JSON_EXTENSION;
import static com.github.akarazhev.cryptoscout.test.Constants.PATH_SEPARATOR;

public final class MockData {

    public enum Source {
        CMC_PARSER(Constants.MockData.CMC_PARSER),
        BYBIT_PARSER(Constants.MockData.BYBIT_PARSER),
        BYBIT_SPOT(Constants.MockData.BYBIT_SPOT),
        BYBIT_TA_SPOT(Constants.MockData.BYBIT_TA_SPOT),
        BYBIT_LINEAR(Constants.MockData.BYBIT_LINEAR),
        BYBIT_TA_LINEAR(Constants.MockData.BYBIT_TA_LINEAR);

        private final String source;

        Source(final String source) {
            this.source = source;
        }
    }

    public enum Type {
        // Bybit parser
        LPL(Constants.MockData.LPL),
        // CMC parser
        FGI(Constants.MockData.FGI),
        // Klines
        KLINE_1(Constants.MockData.KLINE_1),
        KLINE_5(Constants.MockData.KLINE_5),
        KLINE_15(Constants.MockData.KLINE_15),
        KLINE_60(Constants.MockData.KLINE_60),
        KLINE_240(Constants.MockData.KLINE_240),
        KLINE_D(Constants.MockData.KLINE_D),
        // Ticker
        TICKERS(Constants.MockData.TICKERS),
        // Public trade
        PUBLIC_TRADE(Constants.MockData.PUBLIC_TRADE),
        // Order books
        ORDER_BOOK_1(Constants.MockData.ORDER_BOOK_1),
        ORDER_BOOK_50(Constants.MockData.ORDER_BOOK_50),
        ORDER_BOOK_200(Constants.MockData.ORDER_BOOK_200),
        ORDER_BOOK_1000(Constants.MockData.ORDER_BOOK_1000),
        // All liquidations
        ALL_LIQUIDATION(Constants.MockData.ALL_LIQUDATION);

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        public String getPath(final Source source) {
            return source.source + PATH_SEPARATOR + type + JSON_EXTENSION;
        }
    }

    private MockData() {
        throw new UnsupportedOperationException();
    }

    public static Map<String, Object> get(final Source source, final Type type) throws Exception {
        try (final var is = MockData.class.getClassLoader().getResourceAsStream(type.getPath(source))) {
            if (is == null) {
                throw new IllegalStateException(ERR_FILE_NOT_FOUND_PREFIX + type.getPath(source));
            }

            return JsonUtils.json2Map(is);
        }
    }
}
