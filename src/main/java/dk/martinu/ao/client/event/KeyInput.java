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

import java.awt.event.KeyEvent;
import java.util.Objects;

import dk.martinu.ao.client.AbstractTarget;

/**
 * Container class used by {@link AbstractTarget} objects when moving key
 * events from the Event Dispatch Thread (EDT), used by the AWT, to a target's
 * {@link KeyInputBuffer}. {@code KeyInput} objects store a key event that
 * occurred on the EDT and an {@link KeyAction action} that will be performed
 * when the input is {@link #process() processed}.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-06-02
 * @since 1.0
 */
public record KeyInput(@NotNull KeyAction action, @NotNull KeyEvent event) {

    /**
     * Constructs a new {@code KeyInput} object with the specified key action
     * and key event.
     * <p>
     * <b>NOTE:</b> this method does not check for null pointers. The action
     * and event must not be null, otherwise it might result in an exception.
     *
     * @param action The key action
     * @param event  The key event
     */
    public KeyInput(@NotNull final KeyAction action, @NotNull final KeyEvent event) {
        this.action = action;
        this.event = event;
    }

    /**
     * Performs the key action and returns the boolean value returned by the
     * call.
     *
     * @return The boolean value returned by the key action call.
     * @see KeyAction
     */
    public boolean process() {
        return action.perform(event);
    }
}
