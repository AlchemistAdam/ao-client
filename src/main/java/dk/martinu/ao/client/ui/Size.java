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

public class Size implements Cloneable, Comparable<Size> {

    public final int width;
    public final int height;

    public Size(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public @NotNull Size clone() {
        try {
            return (Size) super.clone();
        }
        catch (@NotNull final CloneNotSupportedException e) {
            e.printStackTrace();
            return new Size(width, height);
        }
    }

    @Override
    public int compareTo(final @Nullable Size size) {
        if (size == null)
            throw new NullPointerException("size must not be null");
        if (width < size.width)
            return -1;
        if (width > size.width)
            return +1;
        if (height < size.height)
            return -1;
        if (height > size.height)
            return +1;
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (o instanceof Size) {
            final Size size = (Size) o;
            return size.width == width && size.height == height;
        }
        return false;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public int hashCode() {
        return width + (height << 1);
    }

    @Override
    public @NotNull String toString() {
        return width + " \u00D7 " + height;
    }

    public @NotNull Size transform(final int width, final int height) {
        return new Size(this.width + width, this.height + height);
    }
}
