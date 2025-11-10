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

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.akarazhev.cryptoscout.test.Constants.DB.DB_PASSWORD;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.DB_USER;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.FIRST_ROW;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.JDBC_URL;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.SELECT_COUNT;
import static com.github.akarazhev.cryptoscout.test.Constants.PodmanCompose.ERR_TABLE_ROW_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class Assertions {

    public static void assertTableCount(final String table, final long expected) throws SQLException {
        try (final var c = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             final var rs = c.createStatement().executeQuery(String.format(SELECT_COUNT, table))) {
            assertTrue(rs.next());
            assertEquals(expected, rs.getLong(FIRST_ROW), ERR_TABLE_ROW_COUNT + table);
        }
    }
}
