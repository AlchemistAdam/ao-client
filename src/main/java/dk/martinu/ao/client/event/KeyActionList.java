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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.util.Objects;

import dk.martinu.ao.client.targets.AbstractTarget;

/**
 * A sorted, resizable list of {@link KeyAction} objects used by {@link KeyMap}
 * and {@link AbstractTarget} to manage key actions that
 * are inserted with/bound to the same key code. key actions are sorted by
 * {@link KeyAction#getPriority() priority} in descending order.
 * <p>
 * <b>NOTE:</b> lists are resized everytime they are structurally modified
 * such that their capacity is always equal to their size. Lists can also never
 * be empty and contain at least one key action.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-05
 * @since 1.0
 */
public final class KeyActionList {

    /**
     * The key code which all key actions in this list are inserted with/bound
     * to.
     */
    public final int keyCode;
    /**
     * Array of stored key actions.
     */
    private KeyAction[] actions;

    /**
     * Constructs a new list with the specified key code that contains the
     * specified key action.
     *
     * @param keyCode the key code
     * @param action  the initial key action to store
     * @throws NullPointerException if {@code action} is {@code null}
     */
    public KeyActionList(final int keyCode, @NotNull final KeyAction action) {
        Objects.requireNonNull(action, "action is null");
        this.keyCode = keyCode;
        actions = new KeyAction[] {action};
    }

    public KeyAction[] actions() {
        return actions;
    }

    /**
     * Adds the specified key action to this list.
     *
     * @throws NullPointerException if {@code action} is {@code null}
     */
    public void add(@NotNull final KeyAction action) {
        Objects.requireNonNull(action, "action is null");

        // find array index to insert action
        int index = 0;
        for (final KeyAction keyAction : actions) {
            if (action.compareTo(keyAction) >= 0)
                break;
            else
                index++;
        }

        // allocate new actions array
        KeyAction[] newActions = new KeyAction[actions.length + 1];
        // copy lower bounds into new array
        if (index >= 0)
            System.arraycopy(actions, 0, newActions, 0, index);
        // copy upper bounds into new array
        if (actions.length - index >= 0)
            System.arraycopy(actions, index, newActions, index + 1, actions.length - index);
        // insert action
        newActions[index] = action;

        actions = newActions;
    }

    /**
     * Maps all actions in this list to {@link KeyInput} objects for the
     * specified event.
     *
     * @param event the key event
     * @return a new array of key input objects
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public KeyInput[] mapToInput(@NotNull final KeyEvent event) {
        final KeyInput[] input = new KeyInput[actions.length];
        for (int i = 0; i < actions.length; i++)
            input[i] = new KeyInput(actions[i], event);
        return input;
    }
}
