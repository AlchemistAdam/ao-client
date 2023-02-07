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
package dk.martinu.ao.client.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import dk.martinu.ao.client.Log;
import dk.martinu.ao.client.ZUtil;
import dk.martinu.ao.client.targets.Target;
import dk.martinu.ao.client.util.Resolution;
import dk.martinu.kofi.Document;

import static java.lang.System.currentTimeMillis;

// TODO would it make sense to move tps into ThreadTarget? different targets
//  might have different tps
public final class GameThread extends Thread {

    public static final int DEFAULT_TICKS_PER_SECOND = 100;

    @NotNull
    final GameFrame frame;
    private volatile boolean shutdown = false;
    private Target target;
    private int ticksPerSecond;
    // how much time that must pass between logic ticks
    private long logicTickMs;
    private boolean paused = false;
    private boolean pauseOnFocusLost = false;
    private boolean printPerformance = true;

    public GameThread(@NotNull final Document config) {
        Objects.requireNonNull(config, "config is null");
        frame = new GameFrame(this, null); // TODO create graphics config from kofi config
        setTicksPerSecond(DEFAULT_TICKS_PER_SECOND);
    }

    public long getLogicTickMs() {
        return logicTickMs;
    }

    @Contract(pure = true)
    @NotNull
    public Target getTarget() {
        if (target == null)
            throw new IllegalStateException("target has not been set");
        return target;
    }

    public int getTicksPerSecond() {
        return ticksPerSecond;
    }

    @Contract(pure = true)
    public boolean isPauseOnFocusLost() {
        return pauseOnFocusLost;
    }

    @Contract(pure = true)
    public boolean isPaused() {
        return paused;
    }

    @Contract(pure = true)
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void run() {
        frame.setVisible(true);
        try {
            // tiny spool-up to let AWT/Swing initialize
            Thread.sleep(100);
        }
        catch (InterruptedException ignored) {
        }

        // TODO experiment more with different buffer counts and VSYNC
        final BufferStrategy bs = frame.getNewBufferStrategy(2);
        if (bs == null) {
            Log.e("cannot create buffer strategy");
            throw new RuntimeException("cannot create buffer strategy");
        }

        Target target;
        Resolution r;
        Graphics2D g;
        long time = 0L;
        long timeOld;
        long timeNew;
        long delta;
        long logicTickMs;

        long timestamp;
        final Performance performance = new Performance();

        timeOld = currentTimeMillis();
        // TODO maybe separate logic() and paint()
        while (!isShutdown()) {

            while (paused) {
                timestamp = currentTimeMillis();
                synchronized (this) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                timeOld += currentTimeMillis() - timestamp;
            }

            timeNew = currentTimeMillis();
            delta = timeNew - timeOld;
            time += delta;

            logicTickMs = getLogicTickMs();
            if (time >= logicTickMs) {

                target = getTarget();
                r = frame.getResolution();

                if (printPerformance) {
                    // logic timestamp
                    timestamp = currentTimeMillis();
                    target.logic(r);
                    performance.addLogic(currentTimeMillis() - timestamp);
                }
                else
                    target.logic(r);

                paint:
                do {
                    do {
                        try {
                            g = (Graphics2D) bs.getDrawGraphics();
                        }
                        catch (final Exception e) {
                            Log.w("could not get buffer graphics", e);
                            break paint;
                        }

                        // TEST is it necessary to do this every time getDrawGraphics() is called?
                        // initialize graphics context
                        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g.setComposite(AlphaComposite.SrcOver);
                        g.setColor(Color.BLACK);
                        g.translate(r.offsetX, r.offsetY);
                        g.setClip(0, 0, r.width, r.height);
                        g.fillRect(0, 0, r.width, r.height);

                        if (printPerformance) {
                            // TODO why is this necessary?
                            // create a copy of the graphics object to ensure performance is printed correctly
                            final Graphics2D copy = (Graphics2D) g.create();
                            // paint timestamp
                            timestamp = currentTimeMillis();
                            target.paint(copy, r);
                            copy.dispose();
                            performance.addPaint(currentTimeMillis() - timestamp);
                            performance.paint(g, r);
                        }
                        else
                            target.paint(g, r);

                        g.dispose();
                    }
                    while (bs.contentsRestored());

                    // show buffer content in try-catch clause
                    // for information see bug note B0001
                    try {
                        bs.show();
                    }
                    catch (final Exception e) {
                        Log.w("could not show buffer", e);
                        break;
                    }
                }
                while (bs.contentsLost());
                time -= logicTickMs;
            }
            if (printPerformance)
                performance.update(delta);
            timeOld = timeNew;
        }

        frame.shutdown();
    }

    @Contract("_ -> this")
    @NotNull
    public GameThread setPauseOnFocusLost(final boolean b) {
        pauseOnFocusLost = b;
        return this;
    }

    public void setPaused(final boolean b) {
        paused = b;
        if (!paused)
            synchronized (this) {
                notify();
            }
    }

    @Contract("_ -> this")
    @NotNull
    public GameThread setPrintPerformance(final boolean b) {
        printPerformance = b;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public synchronized GameThread setTarget(@NotNull final Target target) {
        // need null pointer check, target is initially null
        if (this.target != null) {
            frame.removeKeyListener(this.target);
            frame.removeMouseListener(this.target);
            frame.removeMouseMotionListener(this.target);
            frame.removeMouseWheelListener(this.target);
        }

        frame.addKeyListener(target);
        frame.addMouseListener(target);
        frame.addMouseMotionListener(target);
        frame.addMouseWheelListener(target);
        // update cursor position programmatically to
        // initialize mouse data without cursor input
        frame.updateCursorPosition();

        this.target = target;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public GameThread setTicksPerSecond(final int ticksPerSecond) {
        if (ticksPerSecond <= 0)
            throw new IllegalArgumentException("ticksPerSecond must be greater than 0");
        this.ticksPerSecond = ticksPerSecond;
        logicTickMs = 1000L / ticksPerSecond;
        return this;
    }

    public void shutdown() {
        shutdown = true;
    }

//    class DebugMouse {
//
//        Color color = Color.WHITE;
//        Point[] points;
//        int index = 0;
//
//        DebugMouse(final int bufferSize) {
//            points = new Point[bufferSize];
//        }
//
//        public Color getColor() {
//            return color;
//        }
//
//        public void paint(final Graphics2D g) {
//            g.setColor(color);
//            for (int i = 0; i < points.length && points[i] != null; i++) {
//                final Point p = points[i];
//                g.drawLine(p.x, p.y - 1, p.x, p.y + 1);
//                g.drawLine(p.x - 1, p.y, p.x + 1, p.y);
//            }
//        }
//
//        public void put(final int x, final int y) {
//            if (index < points.length)
//                points[index++] = new Point(x, y);
//            else if (index > 0) {
//                index = 0;
//                points[index++] = new Point(x, y);
//            }
//        }
//
//        public void setBufferSize(final int bufferSize) {
//            points = new Point[bufferSize];
//            index = 0;
//        }
//
//        public void setColor(final Color color) {
//            if (color == null)
//                throw new NullPointerException("color must not be null");
//            this.color = color;
//        }
//    }

    private static class Performance {

        public Color color = Color.WHITE;
        @NotNull
        public Font font = Font.decode("monospaced bold 14");
        @NotNull
        public Orientation orientation = Orientation.NORTH_WEST;

        protected volatile long time = 0;
        protected volatile long ticks = 0;
        protected volatile long logic = 0;
        protected volatile long paint = 0;
        protected volatile long tps = 0;
        protected volatile double logicAvg = 0;
        protected volatile double paintAvg = 0;

        public synchronized void addLogic(final long time) {
            ticks++;
            logic += time;
        }

        public synchronized void addPaint(final long time) {
            paint += time;
        }

        public void paint(@NotNull final Graphics2D g, @NotNull final Resolution r) {
            g.setColor(color);
            g.setFont(font);

            final FontMetrics fm = g.getFontMetrics();
            final int lineHeight = fm.getHeight(),
                    textHeight = lineHeight * 3 + 20;
            final int[] widths = {
                    fm.stringWidth("tps"),
                    fm.stringWidth("avg. logic"),
                    fm.stringWidth("avg. paint"),
                    fm.stringWidth(" = "),
                    fm.stringWidth(String.valueOf(tps)),
                    fm.stringWidth(String.valueOf(logicAvg)),
                    fm.stringWidth(String.valueOf(paintAvg))
            };
            final int width1 = ZUtil.max(widths[0], widths[1], widths[2]),
                    width2 = widths[3],
                    width3 = ZUtil.max(widths[4], widths[5], widths[6]),
                    textWidth = width1 + width2 + width3 + 20;

            final int textX, textY;
            switch (orientation) {
                case NORTH_EAST -> {
                    textX = r.width - textWidth;
                    textY = 0;
                }
                case SOUTH_EAST -> {
                    textX = r.width - textWidth;
                    textY = r.height - textHeight;
                }
                case SOUTH_WEST -> {
                    textX = 0;
                    textY = r.height - textHeight;
                }
                default -> {
                    textX = 0;
                    textY = 0;
                }
            }

            int x = textX + 10, y = textY + 10 + fm.getAscent();
            g.drawString("tps", x, y);
            g.drawString("avg. logic", x, y + lineHeight);
            g.drawString("avg. paint", x, y + lineHeight * 2);
            x += width1;
            g.drawString(" = ", x, y);
            g.drawString(" = ", x, y + lineHeight);
            g.drawString(" = ", x, y + lineHeight * 2);
            x += width2;
            g.drawString(String.valueOf(tps), x, y);
            g.drawString(String.valueOf(logicAvg), x, y + lineHeight);
            g.drawString(String.valueOf(paintAvg), x, y + lineHeight * 2);
        }

        synchronized void update(final long delta) {
            time += delta;
            if (time >= 1000L) {
                tps = ticks;
                logicAvg = /*logic;*/ BigDecimal.valueOf(logic)
                        .divide(BigDecimal.valueOf(tps), 1, RoundingMode.UP).doubleValue();
                paintAvg = /*paint;*/ BigDecimal.valueOf(paint)
                        .divide(BigDecimal.valueOf(tps), 1, RoundingMode.UP).doubleValue();
                time -= 1000L;
                ticks = 0;
                logic = 0;
                paint = 0;
            }
        }

        protected enum Orientation {
            NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST
        }
    }
}
