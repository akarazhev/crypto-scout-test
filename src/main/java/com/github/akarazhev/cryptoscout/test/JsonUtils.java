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

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

final class JsonUtils {
    private static final DslJson<Object> DSL_JSON;

    static {
        DSL_JSON = new DslJson<>(Settings.withRuntime().includeServiceLoader());
    }

    private JsonUtils() {
        throw new UnsupportedOperationException();
    }

    public static String object2Json(final Object object) throws IOException {
        final var os = new ByteArrayOutputStream();
        DSL_JSON.serialize(object, os);
        return os.toString(StandardCharsets.UTF_8);
    }

    public static String map2Json(final Map<String, Object> map) throws IOException {
        final var os = new ByteArrayOutputStream();
        DSL_JSON.serialize(map, os);
        return os.toString(StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(final String json) throws IOException {
        final var bytes = json.getBytes(StandardCharsets.UTF_8);
        return DSL_JSON.deserialize(Map.class, new ByteArrayInputStream(bytes));
    }

    public static <T> byte[] object2Bytes(final T object) throws IOException {
        final var os = new ByteArrayOutputStream();
        DSL_JSON.serialize(object, os);
        return os.toByteArray();
    }

    public static <T> T bytes2Object(final byte[] bytes, final Class<T> clazz) throws IOException {
        return DSL_JSON.deserialize(clazz, new ByteArrayInputStream(bytes));
    }
}
