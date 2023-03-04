package dk.martinu.ao.client.text;

import org.jetbrains.annotations.Contract;

/**
 * A glyph instance is a visual representation (image) of characters. The word
 * "glyph" is used loosely, as this class is also used for composed glyphs
 * (ligatures), meaning that a single {@code Glyph} instance is not necessarily
 * a representation of a single character.
 *
 * @author Adam Martinu
 * @version 1.0, 23-02-11
 * @see Font
 * @since 1.0
 */
public class Glyph {

    // DOC
    @Contract(value = "null -> fail", pure = true)
    static int hash(final char[] chars) {
        int h = 0;
        // TODO this needs to be reworked if chars with values > 0xFF are used
        for (char c : chars)
            h = 31 * h + (c & 0xFF);
        return h;
    }

    // DOC
    @Contract(pure = true)
    static int hash(final char c) {
        // TODO this needs to be reworked if chars with values > 0xFF are used
        return c & 0xFF;
    }

    /**
     * Width of the glyph in pixels.
     */
    public final int width;
    /**
     * Height of the glyph in pixels. Will be {@code 0} if the glyph is
     * whitespace.
     */
    public final int height;
    /**
     * Vertical offset of the glyph in pixels. When drawing the glyph, this is
     * the y-position of the glyph relative to the origin.
     */
    public final int offsetY;
    /**
     * {@code true} if this glyph represents whitespace, otherwise
     * {@code false}. Whitespace glyphs only have a width and a character
     * value, other properties are {@code 0} or {@code null};
     */
    public final boolean isWhitespace;
    /**
     * The character(s) that this glyph represents.
     */
    final char[] chars;
    /**
     * The image data (alpha values) of the glyph. Will be {@code null} if the
     * glyph is whitespace.
     */
    final byte[] data;
    /**
     * Horizontal offsets of the glyph relative to other glyphs. Will be
     * {@code null} if the glyph is whitespace.
     */
    final int[] offsetX;
    /**
     * Cached hash code.
     */
    // TODO were is this used? can maybe remove and move hash function into GlyphIndexMap
    private final int hash;

    /**
     * Constructs a new {@link #isWhitespace whitespace} glyph with the
     * specified width, representing one or more characters.
     *
     * @param width the width
     * @param chars the character(s)
     */
    Glyph(final int width, final char[] chars) {
        this(true, width, 0, chars, 0, null, null);
    }

    /**
     * Constructs a new glyph representing one or more characters.
     *
     * @param width   the width
     * @param height  the height
     * @param chars   the character(s)
     * @param offsetY the vertical offset
     * @param offsetX horizontal offsets relative to other glyphs
     * @param data    the image data (alpha values)
     */
    Glyph(final int width, final int height, final char[] chars, final int offsetY, final int[] offsetX, final byte[] data) {
        this(false, width, height, chars, offsetY, offsetX, data);
    }

    /**
     * private constructor.
     *
     * @see #Glyph(int, char[])
     * @see #Glyph(int, int, char[], int, int[], byte[])
     */
    Glyph(final boolean isWhitespace, final int width, final int height, final char[] chars, final int offsetY, final int[] offsetX,
            final byte[] data) {
        this.isWhitespace = isWhitespace;
        this.width = width;
        this.height = height;
        this.chars = chars;
        this.offsetY = offsetY;
        this.offsetX = offsetX;
        this.data = data;
        hash = hash(chars);
    }

    /**
     * Returns the horizontal offset between this glyph and the glyph with the
     * specified id.
     */
    public int getOffsetX(final int id) {
        if (offsetX != null)
            for (int i = 0; i < offsetX.length; i += 2)
                if (offsetX[i] == id)
                    return offsetX[i + 1];
        return 0;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
