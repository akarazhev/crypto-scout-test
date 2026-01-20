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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.akarazhev.cryptoscout.test.Constants.DB.DB_PASSWORD;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.DB_USER;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.DELETE_FROM_TABLE;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.JDBC_URL;
import static com.github.akarazhev.cryptoscout.test.Constants.DB.SELECT_ONE;

public class DBUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBUtils.class);

    static boolean canConnect() {
        try (final var conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             final var st = conn.createStatement(); final var rs = st.executeQuery(SELECT_ONE)) {
            LOGGER.info("Connected to DB: {}", conn.getClientInfo());
            return rs.next();
        } catch (final SQLException e) {
            return false;
        }
    }

    public static void deleteFromTables(final DataSource dataSource, final String... tables) {
        try (final var conn = dataSource.getConnection();
             final var st = conn.createStatement()) {
            for (final var table : tables) {
                st.execute(String.format(DELETE_FROM_TABLE, table));
            }
        } catch (final SQLException e) {
            LOGGER.error("Failed to delete tables", e);
        }
    }
}
