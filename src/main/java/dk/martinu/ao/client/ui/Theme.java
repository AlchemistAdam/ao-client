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

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import dk.martinu.ao.client.util.SimpleHashMap;
import dk.martinu.kofi.*;
import dk.martinu.kofi.codecs.KofiCodec;

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
     * Reads a theme from the KoFi {@link Document} file at the
     * specified path and returns it.
     * DOC syntax of theme document
     *
     * @param path the path to read from
     * @return a theme
     * @throws IOException if an error occurred when reading from the path
     * @throws UIException if an error occurred when converting the contents of
     *                     the file to a theme
     */
    @Contract(value = "_ -> new", pure = true)
    public static Theme readFrom(@NotNull final Path path) throws IOException, UIException {
        // get theme document
        final Document doc = KofiCodec.provider().readFile(path);
        // namespace used to locate classes for class styles
        String namespace = doc.getString(null, "namespace", "");
        if (!namespace.isEmpty())
            namespace += '.';
        // style maps
        final SimpleHashMap<String, Style> nameStyles = new SimpleHashMap<>();
        final SimpleHashMap<Class<? extends Component>, Style> classStyles = new SimpleHashMap<>();
        // list of style-parent pairs that need to be resolved
        final ArrayList<Object> resolve = new ArrayList<>();
        // create styles and populate maps
        for (Section section : doc.getSections()) {
            final List<Property<?>> propertyList = doc.getProperties(section.name);
            // filter out style properties before style is constructed
            final ArrayList<Property<?>> styleProperties = new ArrayList<>();
            for (int i = propertyList.size() - 1; i >= 0; i--) {
                if (propertyList.get(i).key.startsWith("$"))
                    styleProperties.add(propertyList.remove(i));
                ;
            }
            // create style
            final Style style;
            try {
                style = new Style(propertyList);
            }
            catch (Exception e) {
                throw new UIException("could not create style", e);
            }
            // name style
            if (section.name.startsWith("@")) {
                nameStyles.put(section.name.substring(1), style);
            }
            // class style
            else {
                final String className = namespace + section.name;
                try {
                    final Class<?> cls = Class.forName(className);
                    if (Component.class.isAssignableFrom(cls)) {
                        //noinspection unchecked
                        classStyles.put((Class<? extends Component>) cls, style);
                    }
                    else
                        throw new UIException("class style {" + className + "} is not assignable to Component");
                }
                catch (ClassNotFoundException e) {
                    throw new UIException("cannot find class for class style {" + className + "}", e);
                }
            }
            // style properties
            for (Property<?> p : styleProperties) {
                if (p.matches("$parent")) {
                    resolve.add(style);
                    resolve.add(String.valueOf(p.value));
                }
            }
        }
        // resolve parent styles
        for (int i = 0; i < resolve.size(); i += 2) {
            final Style style = (Style) resolve.get(i);
            final String parentName = (String) resolve.get(i + 1);
            // name style parent
            if (parentName.startsWith("@")) {
                final Style nameStyle = nameStyles.get(parentName.substring(1));
                if (nameStyle != null)
                    style.setParent(nameStyle);
                else
                    throw new UIException("could not resolve parent name style {" + parentName + "}");
            }
            // class style parent
            else {
                final String className = namespace + parentName;
                try {
                    // runtime type of the component the parent style applies to
                    final Class<?> cls = Class.forName(className);
                    if (Component.class.isAssignableFrom(cls)) {
                        //noinspection unchecked
                        final Style classStyle = classStyles.get((Class<? extends Component>) cls);
                        if (classStyle != null)
                            style.setParent(classStyle);
                        else
                            throw new UIException("could not resolve parent class style {" + className + "}");
                    }
                    else
                        throw new UIException("class identifier for parent class style {" + className + "} is not assignable to Component");
                }
                catch (ClassNotFoundException e) {
                    throw new UIException("cannot find class for parent class style {" + className + "}", e);
                }
            }
        }
        // create and return theme
        return new Theme(nameStyles, classStyles);
    }

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
