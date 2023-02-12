package dk.martinu.ao.client.text;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A glyph instance is a visual representation (image) of characters. The word
 * "glyph" is used loosely, as this class is also used for composed glyphs
 * (ligatures), meaning that a single {@code Glyph} is not necessarily a
 * representation of a single character.
 *
 * @author Adam Martinu
 * @version 1.0, 23-02-11
 * @since 1.0
 */
public class Glyph {

    public final BufferedImage image;
    public final boolean isWhitespace;
    private final int[] positions;

    public Glyph(@NotNull final BufferedImage image) {
        this(image, true, null);
    }

    public Glyph(@NotNull final BufferedImage image, final int[] positions) {
        this(image, false, positions);
    }

    private Glyph(@NotNull final BufferedImage image, final boolean isWhitespace, final int[] positions) {
        this.image = Objects.requireNonNull(image, "image is null");
        this.isWhitespace = isWhitespace;
        this.positions = positions;
    }

    public int getPosition(final int id) {
        if (positions != null)
            for (int i = 0; i < positions.length; i += 2)
                if (positions[i] == id)
                    return positions[i + 1];
        return 0;
    }
}
