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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Objects;

import dk.martinu.ao.client.targets.Target;
import dk.martinu.ao.client.util.Sound;

public class Component {

    public static final int MOUSEOVER = 1;
    public static final int PRESSED = 2;

    // TODO remove?
    @NotNull
    protected static Color getDisabledColor(@NotNull final Color color) {
        Objects.requireNonNull(color, "color is null");
        final int greyscale = (int) (color.getRed() * 0.21F + color.getGreen() * 0.72F + color.getBlue() * 0.07F);
        return new Color(greyscale, greyscale, greyscale);
    }

    // actions to perform when doActions() is called
    protected final ArrayList<Action> actions = new ArrayList<>();
    // actions to perform when doMouseWheelActions is called
    protected final ArrayList<Action> actionsMW = new ArrayList<>();
    // name of component, used for debugging
    @NotNull
    protected String name = "";
    // owner of this component
    @Nullable
    protected Container parent = null;
    // variables that determine where the component is painted
    protected int x = 0;
    protected int y = 0;
    protected int width = 0;
    protected int height = 0;
    @Nullable
    protected Delegate ui;
    // flags that determine how the component is painted and interacted with
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean focusable = false;
    protected boolean focused = false;
    protected boolean mouseover = false;
    protected boolean pressed = false;
    protected boolean scrollable = false;
    protected boolean draggable = false;
    // component references to return when the focus is traversed up, left,
    // down or right from this component
    @Nullable
    protected Component ftUp = null;
    @Nullable
    protected Component ftLeft = null;
    @Nullable
    protected Component ftDown = null;
    @Nullable
    protected Component ftRight = null;

    public Component() {
        this(null);
    }

    public Component(final Delegate ui) {
        setComponentUI(ui);
    }

    public void addAction(@NotNull final Action action) {
        actions.add(Objects.requireNonNull(action, "action is null"));
    }

    public void addMouseWheelAction(@NotNull final Action action) {
        actionsMW.add(Objects.requireNonNull(action, "action is null"));
    }

    public void doActions(@NotNull final Target source, @NotNull final InputEvent event) {
        for (final Action action : actions)
            action.trigger(source, event, this);
    }

    public void doMouseWheelActions(@NotNull final Target src, @NotNull final MouseWheelEvent event) {
        for (final Action action : actionsMW)
            action.trigger(src, event, this);
    }

    @NotNull
    public Action[] getActions() {
        return actions.toArray(new Action[actions.size()]);
    }

    @Nullable
    public Delegate getComponentUI() {
        return ui;
    }

    @Nullable
    public Component getFocusTraverseDown() {
        return ftDown;
    }

    @Nullable
    public Component getFocusTraverseLeft() {
        return ftLeft;
    }

    @Nullable
    public Component getFocusTraverseRight() {
        return ftRight;
    }

    @Nullable
    public Component getFocusTraverseUp() {
        return ftUp;
    }

    public int getHeight() {
        return height;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public Container getParent() {
        return parent;
    }

    public @NotNull Size getSize() {
        return new Size(getWidth(), getHeight());
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public boolean isFocused() {
        return focused;
    }

    public boolean isMouseover() {
        return mouseover;
    }

    public boolean isPositionInBounds(final int x, final int y) {
        return x >= getX() && y >= getY() && x < (getX() + getWidth()) && y < (getY() + getHeight());
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void paint(final Graphics2D g) {
        if (ui != null)
            ui.paint(g, this);
    }

    public boolean removeAction(@NotNull final Action action) {
        return actions.remove(Objects.requireNonNull(action, "action is null"));
    }

    public void setComponentUI(@Nullable final Delegate ui) {
        if (this.ui != null)
            this.ui.uninstallComponent(this);
        if ((this.ui = ui) != null)
            ui.installComponent(this);
    }

    public void setDraggable(final boolean draggable) {
        this.draggable = draggable;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setFocusTraverse(@Nullable final Component ftUp, @Nullable final Component ftDown,
            @Nullable final Component ftLeft, @Nullable final Component ftRight) {
        this.ftUp = ftUp;
        this.ftDown = ftDown;
        this.ftLeft = ftLeft;
        this.ftRight = ftRight;
    }

    public void setFocusTraverseDown(@Nullable final Component ftDown) {
        this.ftDown = ftDown;
    }

    public void setFocusTraverseH(@Nullable final Component ftLeft, @Nullable final Component ftRight) {
        this.ftLeft = ftLeft;
        this.ftRight = ftRight;
    }

    public void setFocusTraverseLeft(@Nullable final Component ftLeft) {
        this.ftLeft = ftLeft;
    }

    public void setFocusTraverseRight(@Nullable final Component ftRight) {
        this.ftRight = ftRight;
    }

    public void setFocusTraverseUp(@Nullable final Component ftUp) {
        this.ftUp = ftUp;
    }

    public void setFocusTraverseV(@Nullable final Component ftUp, @Nullable final Component ftDown) {
        this.ftUp = ftUp;
        this.ftDown = ftDown;
    }

    public void setFocusable(final boolean focusable) {
        this.focusable = focusable;
    }

    public void setFocused(final boolean focused) {
        this.focused = focused;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public void setMouseover(final boolean mouseover) {
        this.mouseover = mouseover;
        if (ui == null)
            return;
        final Sound soundMouseover = ui.getSound(MOUSEOVER, mouseover);
        if (soundMouseover != null) {
            final Sound soundPressed = ui.getSound(MOUSEOVER, mouseover);
            if (soundPressed != null)
                soundPressed.reset();
            soundMouseover.reset();
            soundMouseover.play();
        }
    }

    public void setName(@NotNull final String name) {
        this.name = Objects.requireNonNull(name, "name is null");
    }

    public void setParent(@Nullable final Container parent) {
        this.parent = parent;
    }

    public void setPosition(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public void setPressed(final boolean pressed) {
        this.pressed = pressed;
        if (ui == null)
            return;
        final Sound soundPressed = ui.getSound(PRESSED, pressed);
        if (soundPressed != null) {
            final Sound soundMouseover = ui.getSound(MOUSEOVER, mouseover);
            if (soundMouseover != null)
                soundMouseover.reset();
            soundPressed.reset();
            soundPressed.play();
        }
    }

    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(@NotNull final Size size) {
        Objects.requireNonNull(size, "size is null");
        width = size.getWidth();
        height = size.getHeight();
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public void setY(final int y) {
        this.y = y;
    }

    @NotNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append(getClass().getName());
        sb.append('@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append("[name=");
        sb.append(name);
        sb.append(", bounds=(");
        sb.append(x);
        sb.append(',');
        sb.append(y);
        sb.append(',');
        sb.append(width);
        sb.append(',');
        sb.append(height);
        sb.append("), parent=");
        if (parent != null) {
            sb.append(parent.getClass().getName());
            sb.append('@');
            sb.append(parent.hashCode());
        }
        else
            sb.append((Object) null);
        sb.append(", visible=");
        sb.append(visible);
        sb.append(", enabled=");
        sb.append(enabled);
        sb.append(", focusable=");
        sb.append(focusable);
        sb.append(", focused=");
        sb.append(focused);
        sb.append(", mouseover=");
        sb.append(mouseover);
        sb.append(", pressed=");
        sb.append(pressed);
        sb.append(", scrollable=");
        sb.append(scrollable);
        sb.append(", ftUp=");
        if (ftUp != null) {
            sb.append(ftUp.getClass().getName());
            sb.append('@');
            sb.append(ftUp.hashCode());
        }
        else
            sb.append((Object) null);
        sb.append(", ftLeft=");
        if (ftLeft != null) {
            sb.append(ftLeft.getClass().getName());
            sb.append('@');
            sb.append(ftLeft.hashCode());
        }
        else
            sb.append((Object) null);
        sb.append(", ftDown=");
        if (ftDown != null) {
            sb.append(ftDown.getClass().getName());
            sb.append('@');
            sb.append(ftDown.hashCode());
        }
        else
            sb.append((Object) null);
        sb.append(", ftRight=");
        if (ftRight != null) {
            sb.append(ftRight.getClass().getName());
            sb.append('@');
            sb.append(ftRight.hashCode());
        }
        else
            sb.append((Object) null);
        sb.append(']');

        final String rv = sb.toString();
        sb.setLength(0);
        return rv;
    }
}
