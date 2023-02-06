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

/**
 * A functional interface used as a delegate for {@link KeyAction}s.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-05
 * @see OnPressAndReleaseKeyAction
 * @see OnPressKeyAction
 * @see OnReleaseKeyAction
 * @since 1.0
 */
@FunctionalInterface
public interface KeyActionFunction {

    /**
     * Performs a task for the specified key action in response to the
     * specified key event.
     *
     * @param action the key action that invoked this function
     * @param event  the event that caused the action to be called
     * @return {@code true} if this function consumed the event, otherwise
     * {@code false}
     */
    boolean perform(@NotNull final KeyAction action, @NotNull final KeyEvent event);
}
