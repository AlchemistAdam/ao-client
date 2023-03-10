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

/**
 * An implementation of {@link KeyAction} with a mask of {@code KEY_PRESSED}
 * that invokes a delegate {@link KeyActionFunction function} when called.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-05
 * @since 1.0
 */
public final class OnPressKeyAction extends KeyAction {

    /**
     * The function to invoke when this action is called.
     */
    @NotNull
    private final KeyActionFunction function;

    /**
     * Constructs a new key action with the specified priority which invokes
     * the specified function when called.
     *
     * @throws NullPointerException if {@code function} is {@code null}
     */
    public OnPressKeyAction(final int priority, @NotNull final KeyActionFunction function) {
        super(priority, KeyAction.KEY_PRESSED);
        this.function = Objects.requireNonNull(function, "function is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean perform(final @NotNull KeyEvent event) {
        return function.perform(this, event);
    }
}
