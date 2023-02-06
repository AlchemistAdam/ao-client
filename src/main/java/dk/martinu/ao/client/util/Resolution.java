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
package dk.martinu.ao.client.util;

import org.jetbrains.annotations.NotNull;

import java.awt.Container;
import java.awt.Insets;
import java.util.Objects;

/**
 * Immutable data class for storing 2D graphics information.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-06
 * @since 1.0
 */
public class Resolution {

    public final int width;
    public final int height;
    public final int centerX;
    public final int centerY;
    public final int offsetX;
    public final int offsetY;

    /**
     * Creates a new {@code Resolution} object from the specified container.
     * {@code width} and {@code height} are set to the width and height of the
     * container minus the horizontal and vertical insets, if any, and
     * {@code centerX} and {@code centerY} are set to half of {@code width} and
     * {@code height}. If the container has any insets, then {@code offsetX}
     * and {@code offsetY} are set to the left and top inset, otherwise they
     * will both be {@code 0}.
     *
     * @param container The container to create this resolution from
     * @throws NullPointerException if {@code container} is equal to
     *                              {@code null}
     */
    public Resolution(@NotNull final Container container) {
        Objects.requireNonNull(container, "container is null");
        final Insets insets = container.getInsets();
        if (insets != null) {
            width = container.getWidth() - (insets.left + insets.right);
            height = container.getHeight() - (insets.top + insets.bottom);
            offsetX = insets.left;
            offsetY = insets.top;
        }
        else {
            width = container.getWidth();
            height = container.getHeight();
            offsetX = offsetY = 0;
        }
        centerX = width >>> 2;
        centerY = height >>> 2;
    }
}
