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

    public void addComponent(@Nullable final Component component) throws UIException {
        if (component == null)
            throw new NullPointerException("component must not be null");
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
        components.forEach(action);
    }

    @NotNull
    public Component getComponent(final int index) {
        final List<Component> components = getComponents();
        if (index < 0 || index >= components.size())
            throw new IndexOutOfBoundsException();
        return components.get(index);
    }

    @Nullable
    public Component getComponent(final int x, final int y) {
        for (final Component component : getComponents()) {
            if (component.isVisible() && component.isPositionInBounds(x, y)) {
                if (component instanceof Container) {
                    final Container c = (Container) component;
                    return c.getComponent(x, y);
                }
                return component;
            }
        }
        return this;
    }

    @Nullable
    public Component getComponent(@NotNull final Point point) {
        return getComponent(point.x, point.y);
    }

    @Nullable
    public Component getComponent(final String name) {
        for (final Component c : getComponents()) {
            if (Objects.equals(c.getName(), name))
                return c;
        }
        return null;
    }

    @NotNull
    public List<Component> getComponents() {
        return components;
    }

    @Nullable
    public Layout getLayout() {
        return layout;
    }

    @Contract(value = "-> new")
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

    public void paint(final Graphics2D g) {
        super.paint(g);
        // paint components
        for (final Component c : getComponents())
            if (c.isVisible())
                c.paint(g);
    }

    public boolean removeComponent(@NotNull final Component component) {
        if (component == null)
            throw new NullPointerException("component must not be null");
        final boolean wasRemoved = getComponents().remove(component);
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
    public Component removeComponent(final String name) {
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

    @Contract(value = "-> new")
    @NotNull
    @Override
    public Spliterator<Component> spliterator() {
        return components.spliterator();
    }

}
