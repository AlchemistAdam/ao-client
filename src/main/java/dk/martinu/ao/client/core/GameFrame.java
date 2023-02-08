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

/**
 * An implementation of AWT {@link Frame} used by {@link GameThread} to obtain
 * graphical resources for rendering.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-08
 * @since 1.0
 */
final class GameFrame extends Frame {

    public static final int MINIMUM_WIDTH = 800;
    public static final int MINIMUM_HEIGHT = 600;

    /**
     * The current resolution of this frame. Changes whenever the frame is
     * resized.
     */
    @NotNull
    volatile Resolution resolution;
    /**
     * The game thread that owns this frame.
     */
    @NotNull
    private final GameThread thread;
    /**
     * Flag that indicates if this frame has been shut down. Used to ensure
     * {@link #dispose()} is only called once, otherwise the frame might not
     * close properly.
     *
     * @see #shutdown()
     * @see GameThread#shutdown()
     */
    private boolean shutdown = false;

    /**
     * Constructs a new frame that is owned by the specified game thread and
     * using the specified graphics configuration.
     *
     * @param thread the owning thread
     * @param config the configuration used to create the frame, can be
     *               {@code null}
     * @throws NullPointerException     if {@code thread} is null
     * @throws IllegalArgumentException see {@link Frame#Frame(GraphicsConfiguration)}
     * @throws HeadlessException        see {@link Frame#Frame(GraphicsConfiguration)}
     */
    GameFrame(@NotNull final GameThread thread, @Nullable final GraphicsConfiguration config) {
        super(config);
        this.thread = Objects.requireNonNull(thread, "thread must not be null");
        init();
        resolution = new Resolution(this);
    }

    /**
     * Disposes the frame (see {@link Frame#dispose()}). Also
     * {@link GameThread#shutdown() shuts down} the thread that owns this
     * frame, in case the frame was closed manually by the user and not through
     * the user interface.
     */
    @Override
    public synchronized void dispose() {
        shutdown = true;
        thread.shutdown();
        super.dispose();
    }

    /**
     * Does nothing. All graphical work is done in {@link GameThread}.
     */
    @Contract(pure = true)
    @Override
    public void paint(final Graphics g) { }

    /**
     * Creates and returns a new buffer strategy with the specified amount of
     * buffers.
     *
     * @param buffers number of buffers to create
     * @return a new buffer strategy
     * @throws IllegalArgumentException if {@code buffers} is less than
     *                                  {@code 1}
     * @throws IllegalStateException    if this frame is not displayable
     * @see #createBufferStrategy(int)
     * @see #isDisplayable()
     */
    @Contract("_ -> new")
    @NotNull
    BufferStrategy getNewBufferStrategy(final int buffers) {
        createBufferStrategy(buffers);
        return getBufferStrategy();
    }

    /**
     * Shuts down this frame if it has not already been shut down.
     * <p>
     * <b>NOTE:</b> the frame must be shut down on the Event Dispatch Thread
     * (EDT). If this method is called from a different thread (such as a
     * {@link GameThread}), then the frame will be scheduled to shut down on
     * the EDT.
     *
     * @see #dispose()
     * @see EventQueue#invokeLater(Runnable)
     */
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

    /**
     * Updates/Initializes the cursor position on the frame by firing a
     * synthetic mouse moved event. If the cursor is not over the frame, then
     * a position of (0; 0) is used.
     *
     * @see #getMousePosition()
     * @see #processMouseMotionEvent(MouseEvent)
     */
    void updateCursorPosition() {
        final Point position = getMousePosition();
        if (position != null)
            processMouseMotionEvent(new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                    0, position.x, position.y, 0, false));
        else
            processMouseMotionEvent(new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                    0, 0, 0, 0, false));
    }

    /**
     * Initializes this frame.
     */
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
