package dk.martinu.ao.client.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import dk.martinu.ao.client.util.Log;

public class FontCodec {

    public static final int TAG = 'F' << 24 | 'O' << 16 | 'N' << 8 | 'T';

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static Font readFile(@NotNull final File file) throws IOException {
        Objects.requireNonNull(file, "file is null");
        Log.i("reading font from file {" + file + "}");
        if (!file.getName().endsWith(".font"))
            Log.w("suspicious font file extension {" + file.getName() + "}");
        return readImpl(file);
    }

    public static void saveFile(@NotNull final Font font, @NotNull final File file) throws IOException {
        Objects.requireNonNull(font, "font is null");
        Objects.requireNonNull(file, "file is null");
        if (!file.getName().endsWith(".font"))
            Log.w("suspicious font file extension {" + file.getName() + "}");
        saveImpl(font, file);
    }

    @Contract(mutates = "param2", value = "_, _ -> param2")
    private static byte[] getBytes(final int i, final byte[] b) {
        b[0] = (byte) (i << 24 & 0xFF);
        b[1] = (byte) (i << 16 & 0xFF);
        b[2] = (byte) (i << 8 & 0xFF);
        b[3] = (byte) (i & 0xFF);
        return b;
    }

    @Contract(mutates = "param2", value = "_, _ -> param2")
    private static byte[] getBytes(final char c, final byte[] b) {
        b[2] = (byte) (c << 8 & 0xFF);
        b[3] = (byte) (c & 0xFF);
        return b;
    }

    @Contract(pure = true)
    private static char getChar(final byte[] b) {
        return (char) (b[0] << 8 | b[1]);
    }

    @Contract(pure = true)
    private static int getInt(final byte[] b) {
        return b[0] << 24 | b[1] << 16 | b[2] << 8 | b[3];
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    private static Font readImpl(@NotNull final File file) throws IOException {

        final String fontName;
        final int fontHeight;
        final Glyph[] glyphs;

        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {

            final int INT = 4;
            final byte[] iBuffer = new byte[INT];
            final int CHAR = 2;
            final byte[] cBuffer = new byte[CHAR];

            // bytes read
            int n;
            // position in stream
            int pos = 0;

            // FONT tag
            n = in.readNBytes(iBuffer, 0, INT);
            if (n != INT)
                throw new FontFormatException(pos, "missing tag");
            if (getInt(iBuffer) != TAG)
                throw new FontFormatException(pos, "invalid tag");
            pos += n;


            // font name
            n = in.readNBytes(iBuffer, 0, INT);
            if (n != INT)
                throw new FontFormatException(pos, "missing font height");
            final int nameLength = getInt(iBuffer);
            if (nameLength < 0)
                throw new FontFormatException(pos, "invalid name length");
            pos += n;
            {
                final byte[] nameBytes = new byte[nameLength];
                n = in.readNBytes(nameBytes, 0, nameLength);
                if (n != nameLength)
                    throw new FontFormatException(pos, "missing font name");
                fontName = new String(nameBytes, StandardCharsets.UTF_8);
            }

            // font height
            n = in.readNBytes(iBuffer, 0, INT);
            if (n != INT)
                throw new FontFormatException(pos, "missing font height");
            fontHeight = getInt(iBuffer);
            if (fontHeight <= 0)
                throw new FontFormatException(pos, "invalid font height");
            pos += n;

            // glyph count
            n = in.readNBytes(iBuffer, 0, INT);
            if (n != INT)
                throw new FontFormatException(pos, "missing glyph count");
            final int glyphCount = getInt(iBuffer);
            if (glyphCount < 0)
                throw new FontFormatException(pos, "invalid glyph count");
            pos += n;


            // read glyphs
            glyphs = new Glyph[glyphCount];
            for (int i = 0; i < glyphCount; i++) {

                // isWhitespace
                final int b = in.read();
                if (b == -1)
                    throw new FontFormatException(pos, "missing isWhitespace");
                if (b != 0 && b != 1)
                    throw new FontFormatException(pos, "invalid isWhitespace");
                final boolean isWhitespace = b == 1;
                pos += 1;

                // width
                n = in.readNBytes(iBuffer, 0, INT);
                if (n != INT)
                    throw new FontFormatException(pos, "missing width");
                final int width = getInt(iBuffer);
                if (width <= 0)
                    throw new FontFormatException(pos, "invalid width");
                pos += n;

                // height
                n = in.readNBytes(iBuffer, 0, INT);
                if (n != INT)
                    throw new FontFormatException(pos, "missing height");
                final int height = getInt(iBuffer);
                if (height < 0 || height > fontHeight || (!isWhitespace && height == 0))
                    throw new FontFormatException(pos, "invalid height");
                pos += n;

                // read char value
                n = in.readNBytes(cBuffer, 0, CHAR);
                if (n != CHAR)
                    throw new FontFormatException(pos, "missing character");
                final char value = getChar(cBuffer);
                pos += n;

                // offsetY
                n = in.readNBytes(iBuffer, 0, INT);
                if (n != INT)
                    throw new FontFormatException(pos, "missing offsetY");
                final int offsetY = getInt(iBuffer);
                if (offsetY < 0 || height + offsetY > fontHeight)
                    throw new FontFormatException(pos, "invalid offsetY");
                pos += n;

                // offsetX count
                n = in.readNBytes(iBuffer, 0, INT);
                if (n != INT)
                    throw new FontFormatException(pos, "missing offsetX count");
                final int offsetXCount = getInt(iBuffer);
                if (offsetXCount < 0)
                    throw new FontFormatException(pos, "invalid offsetX count");
                pos += n;

                // read offsetX
                final int[] offsetX = new int[offsetXCount * 2];
                for (int j = 0; j < offsetXCount; j += 2) {

                    n = in.readNBytes(iBuffer, 0, INT);
                    if (n != INT)
                        throw new FontFormatException(pos, "missing offsetX ID");
                    final int i0 = getInt(iBuffer);
                    if (i0 < 0 || i0 >= glyphCount)
                        throw new FontFormatException(pos, "invalid offsetX ID");
                    pos += n;

                    n = in.readNBytes(iBuffer, 0, INT);
                    if (n != INT)
                        throw new FontFormatException(pos, "missing offsetX");
                    final int i1 = getInt(iBuffer);
                    pos += n;

                    offsetX[j] = i0;
                    offsetX[j + 1] = i1;
                }


                // read data
                final int len = width * height;
                final byte[] data = new byte[len];
                n = in.readNBytes(data, 0, len);
                if (n != len)
                    throw new FontFormatException(pos, "missing data");
                pos += n;


                glyphs[i] = new Glyph(isWhitespace, width, height, value, offsetY, offsetX, data);
            }
        }

        return new Font(fontName, fontHeight, glyphs);
    }

    private static void saveImpl(@NotNull final Font font, @NotNull final File file) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {

            final int INT = 4;
            final byte[] iBuffer = new byte[INT];
            final int CHAR = 2;
            final byte[] cBuffer = new byte[CHAR];

            out.write(getBytes(TAG, iBuffer));

            out.write(getBytes(font.name.length(), iBuffer));
            out.write(font.name.getBytes(StandardCharsets.UTF_8));
            out.write(getBytes(font.height, iBuffer));
            out.write(getBytes(font.getGlyphCount(), iBuffer));

            // glyphs
            for (Glyph glyph : font.glyphs) {
                out.writeBoolean(glyph.isWhitespace);
                out.write(getBytes(glyph.width, iBuffer));
                out.write(getBytes(glyph.height, iBuffer));
                out.write(getBytes(glyph.value, cBuffer));
                out.write(getBytes(glyph.offsetY, iBuffer));
                out.write(getBytes(glyph.offsetX.length, iBuffer));
                for (int offset : glyph.offsetX)
                    out.write(getBytes(offset, iBuffer));
                out.write(getBytes(glyph.data.length, iBuffer));
                out.write(glyph.data);
            }
        }
    }
}
