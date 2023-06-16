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

import java.util.*;
import java.util.function.Consumer;

import dk.martinu.kofi.Property;

/**
 * A style containing a list of properties that can alter the visual and
 * audible representation of components. Styles can inherit and override the
 * properties of a parent style.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-06-16
 * @see Theme
 * @see Component#installStyle(Style)
 * @since 1.0
 */
public abstract class Style {

    /**
     * The parent of this style, can be {@code null}.
     */
    @Nullable
    public final Style parent;
    /**
     * An unmodifiable list of the properties of this style.
     */
    public final List<Property<?>> propertyList;

    /**
     * Constructs a new style with the specified properties.
     *
     * @param properties a collection of properties
     * @throws NullPointerException     if {@code properties} is {@code null}
     * @throws IllegalArgumentException if {@code properties} contains
     *                                  {@code null} elements
     */
    public Style(@NotNull final Collection<Property<?>> properties) {
        this(properties, null);
    }

    /**
     * Constructs a new style with the specified properties and parent.
     *
     * @param properties a collection of properties
     * @throws NullPointerException     if {@code properties} is {@code null}
     * @throws IllegalArgumentException if {@code properties} contains
     *                                  {@code null} elements
     */
    public Style(@NotNull final Collection<Property<?>> properties, @Nullable final Style parent) {
        propertyList = List.copyOf(Objects.requireNonNull(properties, "properties collection is null"));
        if (propertyList.contains(null))
            throw new IllegalArgumentException("properties collection contains null elements");
        this.parent = parent;
    }

    /**
     * Gets the property that matches the specified key and accepts it with the
     * specified action. This method does nothing if no property was found.
     *
     * @param key    the property key
     * @param action the action to perform on the matching property
     * @throws NullPointerException if {@code key} or {@code action} is
     *                              {@code null}
     * @see #getProperty(String)
     */
    @Contract(pure = true)
    public void accept(@NotNull final String key, @NotNull final Consumer<Property<?>> action) {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(action, "action is null");
        final Property<?> property = getProperty(key);
        if (property != null)
            action.accept(property);
    }

    /**
     * Returns the property that matches the specified key. If no property was
     * found, then the parent, if any, is queried for a property. Otherwise
     * {@code null} is returned.
     *
     * @param key the property key
     * @return the matching property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    @Nullable
    public Property<?> getProperty(@NotNull final String key) {
        Objects.requireNonNull(key, "key is null");
        final Property<?> p = propertyList.stream()
                .filter(property -> property.matches(key))
                .findFirst()
                .orElse(null);
        if (p != null)
            return p;
        else if (parent != null)
            return parent.getProperty(key);
        else
            return null;
    }
}
