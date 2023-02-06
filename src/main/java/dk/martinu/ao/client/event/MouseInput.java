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
package dk.martinu.ao.client.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import dk.martinu.ao.client.core.GameThread;
import dk.martinu.ao.client.targets.AbstractTarget;
import dk.martinu.ao.client.util.Resolution;

/**
 * Container class for storing and processing mouse events used by
 * {@link AbstractTarget}.
 * <p>
 * <b>NOTE:</b> this implementation is only semi-threadsafe; multiple threads
 * can add mouse events to the container concurrently, but it must only be
 * processed by a single thread.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-06
 * @since 1.0
 */
public final class MouseInput {

    // TODO should maybe make fields private and expose with getter methods?
    public int x = 0;
    public int y = 0;
    private Point point = new Point(x, y);
    @Nullable
    private MouseEvent mouseDragged = null;
    @Nullable
    private MouseEvent mouseMoved = null;
    @Nullable
    private MouseEvent mousePressed = null;
    @Nullable
    private MouseEvent mouseReleased = null;
    @Nullable
    private MouseWheelEvent mouseWheelMoved = null;

    /**
     * Called by {@link AbstractTarget#mouseDragged(MouseEvent)}.
     */
    public synchronized void mouseDragged(@NotNull final MouseEvent event) {
        point = event.getPoint();
        mouseDragged = event;
    }

    /**
     * Called by {@link AbstractTarget#mouseMoved(MouseEvent)}.
     */
    public synchronized void mouseMoved(@NotNull final MouseEvent event) {
        point = event.getPoint();
        mouseMoved = event;
    }

    /**
     * Called by {@link AbstractTarget#mousePressed(MouseEvent)}.
     */
    public synchronized void mousePressed(@NotNull final MouseEvent event) {
        point = event.getPoint();
        mousePressed = event;
    }

    /**
     * Called by {@link AbstractTarget#mouseReleased(MouseEvent)}.
     */
    public synchronized void mouseReleased(@NotNull final MouseEvent event) {
        point = event.getPoint();
        mouseReleased = event;
    }

    /**
     * Called by {@link AbstractTarget#mouseWheelMoved(MouseWheelEvent)}.
     */
    public synchronized void mouseWheelMoved(@NotNull final MouseWheelEvent event) {
        point = event.getPoint();
        mouseWheelMoved = event;
    }

    /**
     * Processes all events that have been stored. The corresponding
     * {@link MouseAction} method is called for each event. The event for each
     * method call is translated with the specified resolution's offsets before
     * the call.
     *
     * @param r      the current resolution of the {@link GameThread} at the
     *               time this method was called
     * @param action the action that is called for each mouse event
     */
    public void processInput(@NotNull final Resolution r, @NotNull final MouseAction action) {
        final MouseEvent mouseDragged, mouseMoved, mousePressed, mouseReleased;
        final MouseWheelEvent mouseWheelMoved;

        synchronized (this) {
            if (point != null) {
                x = point.x - r.offsetX;
                y = point.y - r.offsetY;
                point = null;
            }
            mouseDragged = this.mouseDragged;
            this.mouseDragged = null;
            mouseMoved = this.mouseMoved;
            this.mouseMoved = null;
            mousePressed = this.mousePressed;
            this.mousePressed = null;
            mouseReleased = this.mouseReleased;
            this.mouseReleased = null;
            mouseWheelMoved = this.mouseWheelMoved;
            this.mouseWheelMoved = null;
        }

        if (mouseDragged != null) {
            mouseDragged.translatePoint(-r.offsetX, -r.offsetY);
            action.mouseDragged(mouseDragged);
        }
        if (mouseMoved != null) {
            mouseMoved.translatePoint(-r.offsetX, -r.offsetY);
            action.mouseMoved(mouseMoved);
        }
        if (mousePressed != null) {
            mousePressed.translatePoint(-r.offsetX, -r.offsetY);
            action.mousePressed(mousePressed);
        }
        if (mouseReleased != null) {
            mouseReleased.translatePoint(-r.offsetX, -r.offsetY);
            action.mouseReleased(mouseReleased);
        }
        if (mouseWheelMoved != null) {
            mouseWheelMoved.translatePoint(-r.offsetX, -r.offsetY);
            action.mouseWheelMoved(mouseWheelMoved);
        }
    }
}
