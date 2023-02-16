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
import org.jetbrains.annotations.NotNull;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A class that maps characters to {@link Glyph glyphs}.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-11
 * @since 1.0
 */
// DOC
public class Font {

    public final int height;
    @NotNull
    final Glyph[] glyphs;
    @NotNull
    final GlyphIndexMap map = new GlyphIndexMap();

    private Font(final int height, @NotNull final Glyph[] glyphs) {
        if (height <= 0)
            throw new IllegalArgumentException("font height must be greater than 0");
        this.height = height;
        this.glyphs = Objects.requireNonNull(glyphs, "glyphs array is null");
        for (int i = 0; i < glyphs.length; i++) {
            Glyph glyph = Objects.requireNonNull(glyphs[i], "glyphs array contains null elements");
            map.putIndex(glyph.chars, i);
        }
    }

    public int[] getGlyphIndices(@NotNull final String s) {
        // TODO this will never return ligature glyphs
        final char[] chars = s.toCharArray();
        int[] ints = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            ints[i] = map.getIndex(chars[i]);
        }
        return ints;
    }

    // TEST
    @Contract(value = "null -> fail", pure = true)
    @NotNull
    public BufferedImage getImage(final int[] indices) {
        Objects.requireNonNull(indices, "indices array is null");
        if (indices.length == 0)
            throw new IllegalArgumentException("empty indices array");

        // variables for iterating indices
        int i0 = indices[0];
        int i1;
        Glyph g0 = glyphs[i0];
        Glyph g1;

        // current horizontal glyph position
        int x = 0;

        // x positions for glyphs
        final int[] xa = new int[indices.length];
        xa[0] = x;

        // advance x by width of previous glyph + position of new glyph
        int i = 1;
        do {
            i1 = indices[i];
            g1 = glyphs[i1];

            x += g0.width + g1.getOffsetX(i0);

            i0 = i1;
            g0 = g1;

            xa[i++] = x;
        }
        while (i < indices.length);

        // image to return
        final BufferedImage img = new BufferedImage(x + g0.width, height, BufferedImage.TYPE_4BYTE_ABGR);

        // initialize graphics for drawing glyphs to image
        final Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // draw glyphs
        for (i = 0; i < indices.length; i++) {
            // TODO draw glyphs
        }

        g.dispose();
        return img;
    }

    // TODO implement getImage for multiple lines
    @Contract(value = "null, _, _ -> fail", pure = true)
    @NotNull
    public BufferedImage getImage(final int[] ids, final int maxWidth, final int maxLines) {
        Objects.requireNonNull(ids, "ids array is null");
        if (ids.length == 0)
            throw new IllegalArgumentException("empty ids array");

        // variables for iterating IDs
        int id0 = ids[0];
        int id1;
        Glyph g0 = glyphs[id0];
        Glyph g1;

        // current line width
        int width = g0.width;

        // current horizontal glyph position
        int x = 0;

        // temporary array for x positions on a single line
        final int[] txa = new int[ids.length];
        int m = 0; // tx index
        txa[m] = x;

        // x positions for glyphs
        final int[][] xa = new int[maxLines][];
        int k = 0; // xa index

        // advance x by width of previous glyph + position of new glyph
        int i = 1;
        do {
            id1 = ids[i++];
            g1 = glyphs[id1];

            x += g0.width + g1.getOffsetX(id0);

            // start new line
//            if (g1.isWhitespace && x + g1.image.getWidth() > maxWidth) {
//                xa[k++] = Arrays.copyOf(txa, m);
//                m = 0;
//                x = 0;
//            }

            id0 = id1;
            g0 = g1;

            txa[m++] = x;
        }
        while (i < ids.length && k < maxLines);

        return null;
    }

    /**
     * Returns the total width of the {@link Glyph}s with the specified IDs
     * laid out on a single line.
     *
     * @param ids an array of {@code Glyph} IDs
     * @return the total width of the {@code Glyphs}
     * @throws NullPointerException     if {@code ids} is {@code null}
     * @throws IllegalArgumentException if {@code ids} is empty
     * @see #getGlyphIndices(String) 
     */
    // TEST
    @Contract(value = "null -> fail", pure = true)
    public int getWidth(final int[] ids) {
        Objects.requireNonNull(ids, "ids array is null");
        if (ids.length == 0)
            throw new IllegalArgumentException("empty ids array");

        int id0 = ids[0];
        int width = glyphs[id0].width;
        Glyph glyph;
        for (int i = 1, id1; i < ids.length; i++) {
            id1 = ids[i];
            glyph = glyphs[id1];
            width += glyph.getOffsetX(id0) + glyph.width;
            id0 = id1;
        }

        return width;
    }

    public void paint(@NotNull final Graphics2D g, final int[] ids) {
        // TODO paint single line
    }

    public void paintLines(@NotNull final Graphics2D g, final int[][] lines) {
        // TODO paint multiple lines
    }
}
