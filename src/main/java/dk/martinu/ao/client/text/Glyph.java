package dk.martinu.ao.client.text;

/**
 * A glyph instance is a visual representation (image) of a character.
 *
 * @author Adam Martinu
 * @version 1.0, 23-02-11
 * @see Font
 * @since 1.0
 */
public class Glyph {

    static int hash(final char value) {
        return value * 31;
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
     * value, all other properties are {@code 0} or {@code null};
     */
    public final boolean isWhitespace;
    /**
     * The character that this glyph represents.
     */
    public final char value;
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
     * <p>
     * /**
     * Constructs a {@link #isWhitespace whitespace} glyph with the
     * specified width and {@code char} value.
     *
     * @param width the width
     * @param value the character
     */
    Glyph(final int width, final char value) {
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
    Glyph(final boolean isWhitespace, final int width, final int height, final char value, final int offsetY,
            final int[] offsetX, final byte[] data) {
        this.isWhitespace = isWhitespace;
        this.width = width;
        this.height = height;
        this.value = value;
        this.offsetY = offsetY;
        this.offsetX = offsetX;
        this.data = data;
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
}
