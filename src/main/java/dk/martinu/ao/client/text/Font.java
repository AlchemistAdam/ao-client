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

import org.jetbrains.annotations.*;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

/**
 * A class that maps characters to {@link Glyph}s.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-11
 * @since 1.0
 */
public class Font {

    public final int size;
    @NotNull
    final Glyph[] glyphs;

    Font(final int size, @NotNull final Glyph[] glyphs) {
        this.size = size;
        this.glyphs = glyphs;
    }

    public int[] getGlyphIds(@NotNull final String s) {
        // TODO
        return null;
    }

    // TEST
    @Contract(value = "null -> fail", pure = true)
    @NotNull
    public BufferedImage getImage(final int[] ids) {
        Objects.requireNonNull(ids, "ids array is null");
        if (ids.length == 0)
            throw new IllegalArgumentException("empty ids array");

        // variables for iterating IDs
        int id0 = ids[0];
        int id1;
        Glyph g0 = glyphs[id0];
        Glyph g1;

        // current horizontal glyph position
        int x = 0;

        // x positions for glyphs
        final int[] xa = new int[ids.length];
        xa[0] = x;

        // advance x by width of previous glyph + position of new glyph
        int i = 1;
        do {
            id1 = ids[i];
            g1 = glyphs[id1];

            x += g0.image.getWidth() + g1.getPosition(id0);

            id0 = id1;
            g0 = g1;

            xa[i++] = x;
        }
        while (i < ids.length);

        // image to return
        final BufferedImage image =
                new BufferedImage(x + g0.image.getWidth(), size, BufferedImage.TYPE_4BYTE_ABGR);

        // initialize graphics for drawing glyphs to image
        final Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // draw glyphs
        for(i = 0; i < ids.length; i++)
            g.drawImage(glyphs[ids[i]].image, xa[i], 0, null);

        g.dispose();
        return image;
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
        int width = g0.image.getWidth();

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

            x += g0.image.getWidth() + g1.getPosition(id0);

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
     * @see #getGlyphIds(String)
     */
    @Contract(value = "null -> fail", pure = true)
    public int getWidth(final int[] ids) {
        Objects.requireNonNull(ids, "ids array is null");
        if (ids.length == 0)
            throw new IllegalArgumentException("empty ids array");

        int id0 = ids[0];
        int width = glyphs[id0].image.getWidth();
        Glyph glyph;
        for (int i = 1, id1; i < ids.length; i++) {
            id1 = ids[i];
            glyph = glyphs[id1];
            width += glyph.getPosition(id0) + glyph.image.getWidth();
            id0 = id1;
        }

        return width;
    }

    public void paint(@NotNull final Graphics2D g, final int[] ids) {
        // TODO
    }

    public void paint(@NotNull final Graphics2D g, final int[][] lines) {
        // TODO
    }
}
