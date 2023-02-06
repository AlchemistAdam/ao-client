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

import org.jetbrains.annotations.NotNull;

import java.awt.Graphics2D;
import java.awt.event.*;

import dk.martinu.ao.client.util.Resolution;

/**
 * A target used by {@link GameThread}. A target is an abstract graphics
 * concept and can be many things. The purpose of this interface is to define
 * the contracts for the {@link #logic(Resolution)} and
 * {@link #paint(Graphics2D, Resolution)} methods which will be called by
 * {@code GameThread} on the active target. An intro cinematic, a main menu or
 * gameplay are all examples of different targets.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-06-02
 * @see GameThread#getTarget()
 * @see GameThread#setTarget(Target)
 * @since 1.0
 */
public interface Target extends KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * Performs the logic of this target, such as processing events, updating
     * positions of entities and so on.
     *
     * @param r the current resolution of the {@link GameThread} at the time
     *          this method was called
     */
    void logic(@NotNull final Resolution r);

    /**
     * Renders this target using the specified graphics object.
     *
     * @param g the graphics used to render this target
     * @param r the current resolution of the {@link GameThread} at the time
     *          this method was called
     */
    void paint(@NotNull final Graphics2D g, @NotNull final Resolution r);
}
