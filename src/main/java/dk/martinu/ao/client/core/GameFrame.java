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

import org.jetbrains.annotations.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.Objects;

import dk.martinu.ao.client.util.Resolution;

final class GameFrame extends Frame {

    public static final int MINIMUM_WIDTH = 800;
    public static final int MINIMUM_HEIGHT = 600;

    @NotNull
    private final GameThread thread;
    @NotNull
    private volatile Resolution resolution;
    private volatile boolean shutdown = false;

    GameFrame(@NotNull final GameThread thread, @Nullable final GraphicsConfiguration config) {
        super(config);
        this.thread = Objects.requireNonNull(thread, "thread must not be null");
        init();
        resolution = new Resolution(this);
    }

    @Override
    public synchronized void dispose() {
        shutdown = true;
        thread.shutdown();
        super.dispose();
    }

    @Contract(pure = true)
    @Override
    public void paint(final Graphics g) { }

    @Nullable
    BufferStrategy getNewBufferStrategy(final int buffers) {
        createBufferStrategy(buffers);
        return getBufferStrategy();
    }

    @Contract(pure = true)
    @NotNull
    Resolution getResolution() {
        return resolution;
    }

    void shutdown() {
        if (EventQueue.isDispatchThread())
            synchronized (this) {
                if (!shutdown)
                    dispose();
            }
        else
            EventQueue.invokeLater(() -> {
                synchronized (this) {
                    if (!shutdown)
                        dispose();
                }
            });
    }

    void updateCursorPosition() {
        final Point position = getMousePosition();
        if (position != null)
            processMouseMotionEvent(new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                    0, position.x, position.y, 0, false));
        else
            processMouseMotionEvent(new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                    0, 0, 0, 0, false));
    }

    private void init() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent event) {
                resolution = new Resolution(GameFrame.this);
            }
        });
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent event) {
                dispose();
            }

            @Override
            public void windowGainedFocus(final WindowEvent event) {
                if (thread.isPaused())
                    thread.setPaused(false);
            }

            @Override
            public void windowLostFocus(final WindowEvent event) {
                if (thread.isPauseOnFocusLost())
                    thread.setPaused(true);
            }
        });
        {
			/*
			final Toolkit toolkit = Toolkit.getDefaultToolkit();
			final Dimension d = toolkit.getBestCursorSize(32, 32);
			final int colors = toolkit.getMaximumCursorColors();

			// TODO implement custom cursor
			final boolean isCursorImplemented = false;
			if (isCursorImplemented && d.width != 0 && d.height != 0 && colors != 0) {
				final Cursor cursor = toolkit.createCustomCursor(
						new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE), new Point(0, 0), "");
				setCursor(cursor);
			}
			*/
        }
        setBackground(Color.BLACK);
        setIgnoreRepaint(true);
        setLayout(null);
        setName(this.getClass().getSimpleName() + "@" + hashCode());
        setResizable(true);
        final Insets insets = getInsets();
        setPreferredSize(new Dimension(MINIMUM_WIDTH + insets.left + insets.right,
                MINIMUM_HEIGHT + insets.top + insets.bottom));
        pack();
        setLocationRelativeTo(null);
    }
}
