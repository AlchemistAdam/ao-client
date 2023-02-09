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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Adam Martinu
 * @version 1.1 2021-04-27
 */
@SuppressWarnings("unused")
public class Log {

    public static final String DEFAULT_DATE_PATTERN = "yy-MM-dd'T'hh:mm:ss,SSSX";
    private static Log log;

    public static void d(final String msg, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.DEBUG, msg, null, params));
    }

    public static void d(final String msg, final Throwable ex, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.DEBUG, msg, ex, params));
    }

    public static void e(final String msg, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.ERROR, msg, null, params));
    }

    public static void e(final String msg, final Throwable ex, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.ERROR, msg, ex, params));
    }

    public static void i(final String msg, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.INFO, msg, null, params));
    }

    public static void i(final String msg, final Throwable ex, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.INFO, msg, ex, params));
    }

    public static boolean isPrintDate() {
        return getInstance().printDate;
    }

    public static boolean isPrintLevel() {
        return getInstance().printLevel;
    }

    public static boolean isPrintThread() {
        return getInstance().printThread;
    }

    public static void record(final Object record) {
        getInstance().add(Objects.requireNonNull(record, "record is null"));
    }

    public static void setDateFormat(final String pattern) {
        getInstance().dateFormat = new SimpleDateFormat(pattern);
    }

    public static void setDateFormat(final DateFormat dateFormat) {
        getInstance().dateFormat = Objects.requireNonNull(dateFormat, "dateFormat is null");
    }

    public static void setPrintDate(final boolean b) {
        getInstance().printDate = b;
    }

    public static void setPrintLevel(final boolean b) {
        getInstance().printLevel = b;
    }

    public static void setPrintThread(final boolean b) {
        getInstance().printThread = b;
    }

    public static void w(final String msg, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.WARNING, msg, null, params));
    }

    public static void w(final String msg, final Throwable ex, final Object... params) {
        final Log log = getInstance();
        log.add(log.new Record(Level.WARNING, msg, ex, params));
    }

    protected static Log getInstance() {
        if (log != null)
            return log;
        synchronized (Log.class) {
            if (log == null)
                log = new Log();
            return log;
        }
    }

    protected final LogThreadDaemon logThread;
    protected final LinkedBlockingQueue<Object> records;
    protected final MutableString buffer = new MutableString(256);
    protected DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.ROOT);
    protected boolean printDate = true;
    protected boolean printThread = true;
    protected boolean printLevel = true;

    private Log() {
        records = new LinkedBlockingQueue<>();
        logThread = new LogThreadDaemon();
        logThread.start();
    }

    protected void add(final Object record) {
        records.add(record);
    }

    public class Record {

        public final Level level;
        public final String msg;
        public final Throwable ex;
        public final Object[] params;
        public final long when = System.currentTimeMillis();
        public final String threadName = '[' + Thread.currentThread().getName() + ']';
        private String cache;

        public Record(final Level level, final String msg, final Throwable ex, final Object... params) {
            this.level = level;
            this.msg = msg;
            this.ex = ex;
            this.params = params;
        }

        @Override
        public String toString() {
            if (cache != null)
                return cache;

            synchronized (buffer) {
                if (isPrintDate())
                    buffer.add(dateFormat.format(when)).append(' ');
                if (isPrintThread())
                    buffer.add(threadName).append(' ');
                if (isPrintLevel())
                    buffer.add(level.name()).append(' ');
                if (params != null)
                    buffer.add(String.format(Locale.ROOT, msg, params));
                else
                    buffer.add(msg);
                if (ex != null)
                    buffer.add(ex.toString());

                cache = buffer.toString();
                buffer.clear();
                return cache;
            }
        }
    }

    protected enum Level {
        DEBUG, INFO, WARNING, ERROR,
    }

    protected class LogThreadDaemon extends Thread {

        public LogThreadDaemon() {
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
}
