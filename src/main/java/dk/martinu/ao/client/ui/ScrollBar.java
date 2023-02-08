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
import java.awt.event.MouseWheelEvent;

import dk.martinu.ao.client.targets.Target;

public class ScrollBar extends Component {

    protected @Nullable ScrollPane scrollPane = null;
    protected int size = 0;

    @Override
    public void doMouseWheelActions(final @NotNull Target src, final @NotNull MouseWheelEvent event) {
        if (scrollPane != null)
            scrollPane.doMouseWheelActions(src, event);
    }

    public int getKnobSize() {
        return size;
    }

    public int getOrientation() {
        return scrollPane != null ? scrollPane.getOrientation() : ScrollPane.VERTICAL;
    }

    @Override
    public boolean isScrollable() {
        return true;
    }

    @Override
    public void paint(final Graphics2D g) {
        super.paint(g); // TODO
    }

    @Override
    public void setParent(final Container parent) {
        super.setParent(parent);
        if (parent instanceof ScrollPane)
            scrollPane = (ScrollPane) parent;
        else
            scrollPane = null;
    }
}
