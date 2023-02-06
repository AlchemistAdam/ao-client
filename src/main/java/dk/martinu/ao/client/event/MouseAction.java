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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import dk.martinu.ao.client.targets.AbstractTarget;

/**
 * Action interface for defining responses to corresponding mouse events, for
 * example when the mouse is moved or a mouse button is pressed.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-06
 * @since 1.0
 */
public interface MouseAction {

    /**
     * Called for events received by
     * {@link AbstractTarget#mouseDragged(MouseEvent)}.
     */
    void mouseDragged(@NotNull final MouseEvent event);

    /**
     * Called for events received by
     * {@link AbstractTarget#mouseMoved(MouseEvent)}.
     */
    void mouseMoved(@NotNull final MouseEvent event);

    /**
     * Called for events received by
     * {@link AbstractTarget#mousePressed(MouseEvent)}.
     */
    void mousePressed(@NotNull final MouseEvent event);

    /**
     * Called for events received by
     * {@link AbstractTarget#mouseReleased(MouseEvent)}.
     */
    void mouseReleased(@NotNull final MouseEvent event);

    /**
     * Called for events received by
     * {@link AbstractTarget#mouseWheelMoved(MouseWheelEvent)}.
     */
    void mouseWheelMoved(@NotNull final MouseWheelEvent event);
}
