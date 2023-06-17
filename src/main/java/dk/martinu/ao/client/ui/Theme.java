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

import java.util.Objects;

import dk.martinu.ao.client.util.SimpleHashMap;

/**
 * A theme containing styles mapped to identifiers.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-06-16
 * @see Style
 * @see Component#installTheme(Theme)
 * @since 1.0
 */
public class Theme {

    /**
     * A map of styles using a name identifier.
     *
     * @see #getStyle(Component)
     */
    @NotNull
    protected final SimpleHashMap<String, Style> nameStyleMap;
    /**
     * A map of styles using a class identifier.
     *
     * @see #getStyle(Component)
     */
    @NotNull
    protected final SimpleHashMap<Class<? extends Component>, Style> classStyleMap;

    /**
     * Constructs a new empty theme.
     */
    public Theme() {
        this(new SimpleHashMap<>(), new SimpleHashMap<>());
    }

    /**
     * Constructs a new theme with the specified style mappings.
     *
     * @param nameStyles  the name styles map
     * @param classStyles the class styles map
     */
    protected Theme(@NotNull final SimpleHashMap<String, Style> nameStyles,
            @NotNull final SimpleHashMap<Class<? extends Component>, Style> classStyles) {
        nameStyleMap = Objects.requireNonNull(nameStyles, "nameStyles map is null");
        classStyleMap = Objects.requireNonNull(classStyles, "classStyles map is null");
    }

    /**
     * Returns the appropriate style for the specified component. If the
     * component has an assigned style name, then the theme will look for a
     * name style with a matching name. If the component does not have an
     * assigned style name or no name style was found, then the theme will look
     * for a class style with a class that is assignable from the component's
     * class. If no class style was found, then {@code null} is returned.
     *
     * @param component the component to get a style for
     * @return an appropriate style, or {@code null}
     * @throws NullPointerException if {@code component} is {@code null}
     */
    @Contract(pure = true)
    @Nullable
    public Style getStyle(@NotNull final Component component) {
        Objects.requireNonNull(component, "component is null");
        // lookup name style
        if (component.styleName != null) {
            final Style style = nameStyleMap.get(component.styleName);
            if (style != null)
                return style;
        }
        // lookup class style
        Class<?> cls = component.getClass();
        while (Component.class.isAssignableFrom(cls)) {
            //noinspection unchecked
            final Style style = classStyleMap.get((Class<? extends Component>) cls);
            if (style != null)
                return style;

            else
                cls = cls.getSuperclass();
        }
        // no style was found
        return null;
    }

    /**
     * Adds the specified style to this theme using the specified name as its
     * identifier. If the theme already contained a style with that name, then
     * it is replaced.
     *
     * @param name  the name
     * @param style the style
     * @throws NullPointerException if {@code name} or {@code style} is
     *                              {@code null}
     */
    @Contract(mutates = "this")
    public void putNameStyle(@NotNull final String name, @NotNull final Style style) {
        nameStyleMap.put(
                Objects.requireNonNull(name, "name is null"),
                Objects.requireNonNull(style, "style is null"));
    }
}
