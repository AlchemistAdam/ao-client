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

import java.awt.event.MouseWheelEvent;

import dk.martinu.ao.client.targets.Target;

public class ScrollPane extends Container {

    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;

    protected int scrollValue = 0;
    protected int scrollMax = 0;
    protected int orientation = VERTICAL;
    protected ScrollBar scrollBar;
    protected ScrollView view;

    public ScrollPane() {
        setLayout(new Layout());
        setScrollBar(new ScrollBar());
        setView(new ScrollView());
    }

    @Override
    public void doMouseWheelActions(final @NotNull Target src, final @NotNull MouseWheelEvent event) {
        // TODO
    }

    public int getOrientation() {
        return orientation;
    }

    public ScrollBar getScrollBar() {
        return scrollBar;
    }

    public int getScrollMax() {
        return scrollMax;
    }

    public int getScrollValue() {
        return scrollValue;
    }

    public ScrollView getView() {
        return view;
    }

    @Override
    public boolean isScrollable() {
        return true;
    }

    public void setOrientation(final int orientation) {
        this.orientation = orientation;
    }

    public void setScrollBar(final ScrollBar scrollBar) {
        if (this.scrollBar != null)
            removeComponent(this.scrollBar);
        if ((this.scrollBar = scrollBar) != null)
            super.addComponent(scrollBar);
    }

    public void setScrollMax(final int scrollMax) {
        this.scrollMax = scrollMax;
    }

    public void setScrollValue(final int scrollValue) {
        this.scrollValue = scrollValue;
    }

    public void setView(final ScrollView view) {
        if (this.view != null)
            removeComponent(this.view);
        if ((this.view = view) != null)
            addComponent(view);
    }

    public static class Layout implements dk.martinu.ao.client.ui.Layout {

        @Override
        public void layoutContainer(final Container con) {
            if (con instanceof ScrollPane) {
                final ScrollPane sp = (ScrollPane) con;

                // variables for bounds
                int x = sp.getX(), y = sp.getY(), w = sp.getWidth(), h = sp.getHeight();

                // set bounds of scrollBar
                final ScrollBar sb = sp.getScrollBar();
                if (sb != null) {
                    final int knobSize = sb.getKnobSize();
                    if (sp.getOrientation() == VERTICAL) {
                        w -= knobSize;
                        sb.setPosition(x + w, y);
                        sb.setSize(knobSize, h);
                    }
                    else if (sp.getOrientation() == HORIZONTAL) {
                        y += knobSize;
                        h -= knobSize;
                        sb.setPosition(x, sp.getY());
                        sb.setSize(w, knobSize);
                    }
                }

                // set bounds of scroll view
                final ScrollView view = sp.getView();
                if (view != null) {
                    view.setPosition(x, y);
                    view.setSize(w, h);
                    if (sp.getOrientation() == VERTICAL)
                        sp.setScrollMax(view.getScrollableHeight());
                    else if (sp.getOrientation() == HORIZONTAL)
                        sp.setScrollMax(view.getScrollableWidth());
                }
            }
        }
    }
}
