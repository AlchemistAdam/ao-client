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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseWheelEvent;

import dk.martinu.ao.client.targets.Target;

public class ScrollView extends Container {

    protected int scrollWidth = 0;
    protected int scrollHeight = 0;
    protected @Nullable ScrollPane scrollPane;

    @Override
    public void doMouseWheelActions(final @NotNull Target src, final @NotNull MouseWheelEvent event) {
        if (scrollPane != null)
            scrollPane.doMouseWheelActions(src, event);
    }

    public int getScrollableHeight() {
        return scrollHeight;
    }

    public int getScrollableWidth() {
        return scrollWidth;
    }

    @Override
    public boolean isScrollable() {
        return true;
    }

    // TODO
    @Override
    public void paint(@NotNull final Graphics2D g) {
        if (ui != null)
            ui.paint(g, this);
        final Shape clip = g.getClip();
        g.setClip(getX(), getY(), getWidth(), getHeight());
        // paint components
        for (final Component c : getComponents())
            if (c.isVisible() && isWithinBounds(c))
                c.paint(g);
        g.setClip(clip);
    }

    @Override
    public void setParent(final Container parent) {
        super.setParent(parent);
        if (parent instanceof ScrollPane)
            scrollPane = (ScrollPane) parent;
        else
            scrollPane = null;
    }

    protected boolean isWithinBounds(@NotNull final Component c) {
        int offX = 0, offY = 0;
        if (scrollPane != null)
            if (scrollPane.getOrientation() == ScrollPane.HORIZONTAL)
                offX = scrollPane.getScrollValue();
            else if (scrollPane.getOrientation() == ScrollPane.VERTICAL)
                offY = scrollPane.getScrollValue();
        return c.getX() - offX < (getX() + getWidth())
                && (c.getX() + c.getWidth() - offX) > getX()
                && c.getY() - offY < (getY() + getHeight())
                && (c.getY() + c.getHeight() - offY) > getY();
    }
}
