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
package dk.martinu.ao.client.text;

import org.jetbrains.annotations.Contract;

/**
 * A glyph instance is a visual representation (image) of a character.
 *
 * @author Adam Martinu
 * @version 1.0, 23-02-11
 * @see Font
 * @since 1.0
 */
public class Glyph {

    private static final int[] EMPTY_OFFSET_X = new int[0];
    private static final byte[] EMPTY_DATA = new byte[0];

    static int hash(final char value) {
        return value * 31;
    }

    /**
     * The character that this glyph represents.
     */
    public final char value;
    /**
     * Width of the glyph in pixels.
     */
    public final int width;
    /**
     * Height of the glyph in pixels.
     */
    public final int height;
    /**
     * {@code true} if this glyph represents whitespace, otherwise
     * {@code false}.
     */
    public final boolean isWhitespace;
    /**
     * Vertical offset of the glyph in pixels. When drawing the glyph, this is
     * the y-position of the glyph relative to the origin.
     */
    public final int offsetY;
    /**
     * Horizontal offsets between the glyph and other preceding glyphs.
     */
    public final int[] offsetX;
    /**
     * The image data (alpha values) of the glyph.
     */
    public final byte[] data;

    /**
     * Constructs a {@link #isWhitespace whitespace} glyph with the
     * specified width and {@code char} value.
     *
     * @param width the width
     * @param value the character
     */
    @Contract(pure = true)
    public Glyph(final int width, final char value) {
        this(true, width, 0, value, 0, null, null);
    }

    /**
     * Constructs a glyph with the specified properties.
     *
     * @param isWhitespace {@code true} if the glyph represents whitespace
     * @param width        the width
     * @param height       the height
     * @param value        the character
     * @param offsetY      the vertical offset
     * @param offsetX      horizontal offsets relative to other glyphs, or
     *                     {@code null}
     * @param data         the image data (alpha values), or {@code null}
     */
    @Contract(pure = true)
    public Glyph(final boolean isWhitespace, final int width, final int height, final char value, final int offsetY,
            final int[] offsetX, final byte[] data) {
        this.isWhitespace = isWhitespace;
        this.width = width;
        this.height = height;
        this.value = value;
        this.offsetY = offsetY;
        this.offsetX = offsetX != null ? offsetX : EMPTY_OFFSET_X;
        this.data = data != null ? data : EMPTY_DATA;
    }

    /**
     * Returns the horizontal offset (on the left side) between this glyph and
     * the glyph with the specified id.
     */
    @Contract(pure = true)
    public int getOffsetX(final int id) {
        if (offsetX.length != 0)
            for (int i = 0; i < offsetX.length; i += 2)
                if (offsetX[i] == id)
                    return offsetX[i + 1];
        return 0;
    }
}
