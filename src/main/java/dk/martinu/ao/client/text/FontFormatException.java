package dk.martinu.ao.client.text;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class FontFormatException extends IOException {

    public FontFormatException(final int position, @NotNull final String msg) {
        super(position + ": " + Objects.requireNonNull(msg, "message is null"));
    }

    public FontFormatException(@NotNull final String msg) {
        super(Objects.requireNonNull(msg, "message is null"));
    }
}
