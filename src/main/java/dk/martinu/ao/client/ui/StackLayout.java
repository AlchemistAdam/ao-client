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

public class StackLayout implements Layout {

    // @formatter:off

    // orientation constants
    public static final int VERTICAL   = 16;
    public static final int HORIZONTAL = 32;

    // alignment constants
    public static final int TOP    = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT   = 4;
    public static final int RIGHT  = 8;

    // @formatter:on

    public final int gap;
    public final int orientation;
    public final int alignment;

    public StackLayout() {
        this(24, VERTICAL, 0);
    }

    public StackLayout(final int gap) {
        this(gap, HORIZONTAL, 0);
    }

    public StackLayout(final int gap, final int orientation) {
        this(gap, orientation, 0);
    }

    public StackLayout(final int gap, final int orientation, final int alignment) {
        this.gap = gap;
        if (orientation != VERTICAL && orientation != HORIZONTAL)
            throw new IllegalArgumentException("invalid orientation {" + orientation + "}");
        this.orientation = orientation;
        if (alignment >>> 4 != 0 || (alignment & 3) == 3 || (alignment & 12) == 12)
            throw new IllegalArgumentException("invalid alignment {" + alignment + "}");
        this.alignment = alignment;
    }

    @Override
    public void layoutContainer(@NotNull final Container con) {
        final int n = con.getComponents().size();
        if (n == 0)
            return;

        // box size
        int width, height;
        if (orientation == VERTICAL) {
            width = 0;
            height = gap * n - 1;
            for (Component c : con) {
                if (c.width > width)
                    width = c.width;
                height += c.height;
            }
        }
        else { // orientation == HORIZONTAL
            width = gap * n - 1;
            height = 0;
            for (Component c : con) {
                width += c.width;
                if (c.height > height)
                    height = c.height;
            }
        }

        // horizontal alignment
        int x = switch (alignment & 12) {
            case LEFT -> 0;
            case RIGHT -> con.width - width;
            default -> (con.width - width) / 2;
        };
        // vertical alignment
        int y = switch (alignment & 3) {
            case TOP -> 0;
            case BOTTOM -> con.height - height;
            default -> (con.height - height) / 2;
        };

        // layout components
        if (orientation == VERTICAL)
            for (Component c : con) {
                if (c.width < width)
                    c.setX(x + (width - c.width) / 2);
                else
                    c.setX(x);
                c.setY(y);
                y += gap + c.height;
            }
        else // orientation == HORIZONTAL
            for (Component c : con) {
                c.setX(x);
                x += gap + c.width;
                if (c.height < height)
                    c.setY(y + (height - c.height) / 2);
                else
                    c.setY(y);
            }
    }
}
