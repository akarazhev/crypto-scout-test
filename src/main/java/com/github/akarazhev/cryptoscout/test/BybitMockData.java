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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public final class BybitMockData {

    public enum Source {
        SPOT("bybit-spot"), LINEAR("bybit-linear");

        private final String source;

        Source(final String source) {
            this.source = source;
        }
    }

    public enum Type {
        // Klines
        KLINE_1("kline.1"),
        KLINE_5("kline.5"),
        KLINE_15("kline.15"),
        KLINE_60("kline.60"),
        KLINE_240("kline.240"),
        KLINE_D("kline.D"),
        // Ticker
        TICKERS("tickers"),
        // Public trade
        PUBLIC_TRADE("publicTrade"),
        // Order books
        ORDER_BOOK_1("orderbook.1"),
        ORDER_BOOK_50("orderbook.50"),
        ORDER_BOOK_200("orderbook.200"),
        ORDER_BOOK_1000("orderbook.1000");

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        public String getPath(final Source source) {
            return source.source + File.separator + type + ".json";
        }
    }

    private BybitMockData() {
        throw new UnsupportedOperationException();
    }

    public static Map<String, Object> get(final Source source, final Type type) throws Exception {
        final var file = PodmanCompose.class.getClassLoader().getResource(type.getPath(source));
        if (file == null) {
            throw new IllegalStateException("File not found: " + type.getPath(source));
        }

        final var diskFile = Paths.get(file.toURI());
        if (!Files.exists(diskFile)) {
            throw new IllegalStateException("File not found on disk: " + diskFile);
        }

        return JsonUtils.json2Map(new String(Files.readAllBytes(diskFile)));
    }
}
