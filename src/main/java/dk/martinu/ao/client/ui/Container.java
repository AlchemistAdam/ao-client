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
import java.awt.Point;
import java.util.*;
import java.util.function.Consumer;

public class Container extends Component implements Iterable<Component> {

    @NotNull
    protected final ArrayList<Component> components;
    @Nullable
    protected Layout layout = null;

    protected Container() {
        this(8);
    }

    protected Container(final int size) {
        components = new ArrayList<>(size);
    }

    public void addComponent(@NotNull final Component component) throws UIException {
        Objects.requireNonNull(component, "component must not be null");
        final List<Component> components = getComponents();
        if (components.contains(component))
            throw new UIException("component is already added");
        if (component.getParent() != null)
            component.getParent().removeComponent(component);
        components.add(component);
        component.setParent(this);
    }

    @Override
    public void forEach(@NotNull final Consumer<? super Component> action) {
        components.forEach(Objects.requireNonNull(action, "action is null"));
    }

    @Contract(pure = true)
    @NotNull
    public Component getComponent(final int index) {
        final List<Component> components = getComponents();
        if (index < 0 || index >= components.size())
            throw new IndexOutOfBoundsException();
        return components.get(index);
    }

    @Contract(pure = true)
    @Nullable
    public Component getComponent(final int x, final int y) {
        for (final Component component : getComponents())
            if (component.isVisible() && component.isPositionInBounds(x, y))
                if (component instanceof Container c)
                    return c.getComponent(x, y);
                else
                    return component;
        return this;
    }

    @Contract(pure = true)
    @Nullable
    public Component getComponent(@NotNull final Point point) {
        return getComponent(point.x, point.y);
    }

    @Contract(pure = true)
    @Nullable
    public Component getComponent(@NotNull final String name) {
        Objects.requireNonNull(name, "name is null");
        for (final Component c : getComponents()) {
            if (Objects.equals(c.getName(), name))
                return c;
        }
        return null;
    }

    @Contract(pure = true)
    @NotNull
    public List<Component> getComponents() {
        return components;
    }

    @Contract(pure = true)
    @Nullable
    public Layout getLayout() {
        return layout;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public ListIterator<Component> iterator() {
        return components.listIterator();
    }

    public void layout() {
        if (layout != null)
            layout.layoutContainer(this);
        for (final Component component : getComponents())
            if (component instanceof Container)
                ((Container) component).layout();
    }

    public void paint(@NotNull final Graphics2D g) {
        super.paint(g);
        // paint components
        g.translate(x, y);
        for (final Component c : getComponents())
            if (c.isVisible())
                c.paint(g);
        g.translate(-x, -y);
    }

    public boolean removeComponent(@NotNull final Component component) {
        final boolean wasRemoved = getComponents().remove(
                Objects.requireNonNull(component, "component is null"));
        if (wasRemoved)
            component.setParent(null);
        return wasRemoved;
    }

    @NotNull
    public Component removeComponent(final int index) {
        final List<Component> components = getComponents();
        if (index < 0 || index >= components.size())
            throw new IndexOutOfBoundsException();
        final Component c = components.remove(index);
        c.setParent(null);
        return c;
    }

    @Nullable
    public Component removeComponent(@NotNull final String name) {
        Objects.requireNonNull(name, "name is null");
        final List<Component> components = getComponents();
        for (Component c : components)
            if (Objects.equals(c.getName(), name)) {
                components.remove(c);
                c.setParent(null);
                return c;
            }
        return null;
    }

    public void setLayout(@Nullable final Layout layout) {
        this.layout = layout;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Spliterator<Component> spliterator() {
        return components.spliterator();
    }

}
