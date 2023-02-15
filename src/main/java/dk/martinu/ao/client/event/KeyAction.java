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

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.*;

import java.awt.event.KeyEvent;

import dk.martinu.ao.client.targets.AbstractTarget;

/**
 * This abstract class defines the outline for an action to perform in response
 * to a key event. {@code KeyAction} defines a priority and mask that are used
 * as filters to determine if a key action is performed for a given key event.
 * Key Actions can be bound to specific key codes on an {@link AbstractTarget}
 * and can store user objects to retain state between calls.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-05
 * @see AbstractTarget#bindKey(int, KeyAction)
 * @since 1.0
 */
public abstract class KeyAction implements Comparable<KeyAction> {

    /**
     * Key mask for key pressed events. Can be combined with
     * {@link #KEY_RELEASED}.
     */
    public static final int KEY_PRESSED = 1 << 1;
    /**
     * Key mask for key released events. Can be combined with
     * {@link #KEY_PRESSED}.
     */
    public static final int KEY_RELEASED = 1 << 2;
    /**
     * The priority of this key action. Actions with higher priority values are
     * called first.
     */
    public final int priority;
    /**
     * The mask of this key action. Can be {@link #KEY_PRESSED},
     * {@link #KEY_RELEASED}, or the two constants combined.
     */
    public final int mask;
    /**
     * The stored user object. Can be {@code null}.
     */
    @Nullable
    private Object obj = null;

    /**
     * Creates a new key action with the specified priority and mask. The mask
     * is one of, or a combination of, the mask constants {@link #KEY_PRESSED}
     * and {@link #KEY_RELEASED}.
     *
     * @param priority The priority of the key action.
     * @param mask     The mask of the key action.
     */
    public KeyAction(final int priority, @MagicConstant(flags = {KEY_PRESSED, KEY_RELEASED}) final int mask) {
        this.priority = priority;
        this.mask = mask;
    }

    /**
     * Compares this key action to the specified key action. The returned
     * integer value indicates if this key action's priority is less than
     * (<code>value < 0</code>), equal to (<code>value == 0</code>) or greater
     * than (<code>value > 0</code>) the priority of the specified key action.
     */
    @Contract(pure = true)
    @Override
    public final int compareTo(@NotNull final KeyAction action) {
        return priority - action.getPriority();
    }

    /**
     * Returns this key action's event mask.
     */
    public final int getMask() {
        return mask;
    }

    /**
     * Returns this key action's priority.
     */
    public final int getPriority() {
        return priority;
    }

    /**
     * Returns the user object stored in this action, or {@code null}.
     */
    @Contract(pure = true)
    @Nullable
    public final Object getUserObject() {
        return obj;
    }

    /**
     * Performs this action in response to the specified event and returns
     * a {@code boolean} that indicates if the event was consumed.
     *
     * @param event the event that caused the action to be called
     * @return {@code true} if this action consumed the event, otherwise
     * {@code false}
     */
    public abstract boolean perform(@NotNull final KeyEvent event);

    /**
     * Sets the user object stored in this action to the specified object.
     */
    public final void setUserObject(final Object obj) {
        this.obj = obj;
    }

}
