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
package dk.martinu.ao.client.ui;

import org.jetbrains.annotations.*;

import java.awt.Graphics2D;

import dk.martinu.ao.client.util.Resolution;

public class Scene extends Container {

    protected boolean focusTraversable = false;
    @Nullable
    protected Component defaultFocusComponent = null;
    @Nullable
    protected Component mouseoverComponent = null;
    @Nullable
    protected Component pressedComponent = null;
    @Nullable
    protected Component focusedComponent = null;
    protected int caretPosition = -1;
    protected long caretTime = 500L;
    protected boolean drawCaret = true;

    @Contract(pure = true)
    @Nullable
    @Override
    public Component getComponent(final int x, final int y) {
        final Component c = super.getComponent(x, y);
        return c == this ? null : c;
    }

    @Contract(pure = true)
    @Nullable
    public Component getDefaultFocusComponent() {
        return defaultFocusComponent;
    }

    @Contract(pure = true)
    @Nullable
    public Component getFocusedComponent() {
        return focusedComponent;
    }

    @Contract(pure = true)
    @Nullable
    public Component getMouseoverComponent() {
        return mouseoverComponent;
    }

    @Contract(pure = true)
    @Nullable
    public Component getPressedComponent() {
        return pressedComponent;
    }

    public boolean invalidate(@NotNull final Resolution r) {
        if (width != r.width || height != r.height) {
            setSize(r.width, r.height);
            return true;
        }
        return false;
    }

    @Contract(pure = true)
    public boolean isFocusTraversable() {
        return focusTraversable;
    }

    public void paint(@NotNull final Graphics2D g, @NotNull final Resolution r) {
        if (invalidate(r))
            layout();
        paint(g);
        // TODO paint caret
    }

    @Override
    public boolean removeComponent(@NotNull final Component component) {
        final boolean wasRemoved = super.removeComponent(component);
        if (wasRemoved && component.equals(defaultFocusComponent))
            defaultFocusComponent = null;
        return wasRemoved;
    }

    @NotNull
    @Override
    public Component removeComponent(final int index) {
        final Component c = super.removeComponent(index);
        if (c.equals(defaultFocusComponent))
            defaultFocusComponent = null;
        return c;
    }

    @Override
    public Component removeComponent(@NotNull final String name) {
        final Component c = super.removeComponent(name);
        if (c != null && c.equals(defaultFocusComponent))
            defaultFocusComponent = null;
        return c;
    }

    public void setMouseoverComponent(@Nullable final Component component) {
        if (mouseoverComponent != null)
            mouseoverComponent.setMouseover(false);
        if ((mouseoverComponent = component) != null)
            component.setMouseover(true);
    }

    public void setPressedComponent(final @Nullable Component component) {
        if (component == null) {
            if (pressedComponent != null) {
                pressedComponent.setPressed(false);
                pressedComponent = null;
            }
        }
        else {
            if (pressedComponent != null)
                pressedComponent.setPressed(false);
            (pressedComponent = component).setPressed(true);
        }
    }
}
