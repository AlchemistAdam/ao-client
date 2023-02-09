/*
 * Copyright (c) 2023, Adam Martinu. All rights reserved. Altering or
 * removing copyright notices or this file header is not allowed.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package dk.martinu.ao.client.util;

import org.jetbrains.annotations.*;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Adam Martinu
 * @version 1.0, 2023-02-09
 * @since 1.0
 */
public final class Log {

    private static final LogThread thread = new LogThread();
    private static final LinkedBlockingQueue<Object> records = new LinkedBlockingQueue<>();
    private static final MutableString buffer = new MutableString(256);

    static {
        thread.start();
    }

    public static void d(@NotNull final String msg, @NotNull final Object... params) {
        records.add(new Record(Level.DEBUG, msg, null, params));
    }

    public static void d(@NotNull final String msg, @Nullable final Throwable ex, @NotNull final Object... params) {
        records.add(new Record(Level.DEBUG, msg, ex, params));
    }

    public static void e(@NotNull final String msg, @NotNull final Object... params) {
        records.add(new Record(Level.ERROR, msg, null, params));
    }

    public static void e(@NotNull final String msg, @Nullable final Throwable ex, @NotNull final Object... params) {
        records.add(new Record(Level.ERROR, msg, ex, params));
    }

    public static void i(@NotNull final String msg, @NotNull final Object... params) {
        records.add(new Record(Level.INFO, msg, null, params));
    }

    public static void i(@NotNull final String msg, @Nullable final Throwable ex, @NotNull final Object... params) {
        records.add(new Record(Level.INFO, msg, ex, params));
    }

    public static void w(@NotNull final String msg, @NotNull final Object... params) {
        records.add(new Record(Level.WARNING, msg, null, params));
    }

    public static void w(@NotNull final String msg, @Nullable final Throwable ex, @NotNull final Object... params) {
        records.add(new Record(Level.WARNING, msg, ex, params));
    }

    private enum Level {
        DEBUG, INFO, WARNING, ERROR,
    }

    private static class LogThread extends Thread {

        LogThread() {
            super(Thread.currentThread().getThreadGroup(), (Runnable) null);
            setDaemon(true);
        }

        @SuppressWarnings("InfiniteLoopStatement")
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println(records.take());
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Record {

        private static final String DEFAULT_DATE_PATTERN = "yy-MM-dd'T'hh:mm:ss,SSSX";
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.ROOT);

        private final long when;
        @NotNull
        private final String threadName;
        @NotNull
        private final Level level;
        @NotNull
        private final String msg;
        @Nullable
        private final Throwable ex;
        @Nullable
        private final Object[] params;

        Record(@NotNull final Level level, @NotNull final String msg, @Nullable final Throwable ex,
                @NotNull final Object... params) {
            this.level = Objects.requireNonNull(level, "level is null");
            this.msg = Objects.requireNonNull(msg, "msg is null");
            this.ex = ex;
            this.params = Objects.requireNonNull(params, "params is null");
            when = System.currentTimeMillis();
            threadName = '[' + Thread.currentThread().getName() + ']';
        }

        @Contract(value = "-> new", pure = true)
        @NotNull
        @Override
        public String toString() {
            synchronized (buffer) {

                buffer.add(dateFormat.format(when))
                        .append(' ')
                        .add(threadName)
                        .append(' ')
                        .add(level.name())
                        .append(' ');

                if (params != null)
                    buffer.add(String.format(Locale.ROOT, msg, params));
                else
                    buffer.add(msg);

                if (ex != null)
                    buffer.add(ex.toString());

                final String rv = buffer.toString();
                buffer.clear();
                return rv;
            }
        }
    }
}
