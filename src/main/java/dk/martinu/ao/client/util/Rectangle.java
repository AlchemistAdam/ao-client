package dk.martinu.ao.client.util;

import org.jetbrains.annotations.NotNull;

public class Rectangle {

    public int x, y, width, height;

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Returns {@code true} if any point within the specified rectangle is
     * within the bounds of this rectangle, otherwise {@code false} is
     * returned.
     */
    public boolean isInBounds(@NotNull final Rectangle r) {
        return r.x < x + width
                && r.x + r.width >= x
                && r.y < y + height
                && r.y + r.height >= y;
    }
}
