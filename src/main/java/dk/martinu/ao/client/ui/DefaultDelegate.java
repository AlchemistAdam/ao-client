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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import dk.martinu.ao.client.util.Sound;

public class DefaultDelegate implements Delegate {

    private static @Nullable DefaultDelegate instance = null;

    public static @Nullable Delegate getInstance() {
        if (instance == null)
            synchronized (DefaultDelegate.class) {
                if (instance == null)
                    instance = new DefaultDelegate();
            }
        return instance;
    }

    public Color background = Color.BLACK;
    public Color foreground = Color.WHITE;
    public Color mouseover = new Color(0x20C020);
    public Color pressed = new Color(0x00B0FF);
    public Color disabled = Color.GRAY;
    public Color border = Color.WHITE;
    public int borderThickness = 1;
    public Font font = Font.decode("Arial BOLD 12");

    private DefaultDelegate() { }

    public @NotNull DefaultDelegate background(final Color background) {
        this.background = background;
        return this;
    }

    public @NotNull DefaultDelegate border(final Color border) {
        this.border = border;
        return this;
    }

    public @NotNull DefaultDelegate borderThickness(final int borderThickness) {
        this.borderThickness = borderThickness;
        return this;
    }

    public @NotNull DefaultDelegate font(final Font font) {
        this.font = font;
        return this;
    }

    public @NotNull DefaultDelegate foreground(final Color foreground) {
        this.foreground = foreground;
        return this;
    }

    @Override
    public @Nullable Sound getSound(final int flag, final boolean state) {
        return null;
    }

    @Override
    public void installComponent(@NotNull final Component component) {

    }

    public @NotNull DefaultDelegate mouseover(final Color mouseover) {
        this.mouseover = mouseover;
        return this;
    }

    @Override
    public void paint(@NotNull final Graphics2D g, final @NotNull Component c) {
        if (c instanceof Label) {
            final Label l = (Label) c;

            if (borderThickness > 0) {
                if (!l.isEnabled())
                    g.setColor(disabled);
                else
                    g.setColor(border);
                g.drawRect(l.getX(), l.getY(), l.getWidth() - 1, l.getHeight() - 1);

                g.setColor(background);
                g.fillRect(l.getX() + borderThickness, l.getY() + borderThickness,
                        l.getWidth() - borderThickness * 2, l.getHeight() - borderThickness * 2);

            }
            else {
                g.setColor(background);
                g.fillRect(l.getX(), l.getY(), l.getWidth(), l.getHeight());
            }

            if (!l.isEnabled())
                g.setColor(disabled);
            else
                g.setColor(foreground);
            g.setFont(font);
            final FontMetrics fm = g.getFontMetrics();
            final String text = l.getText();
            final float h = l.getHorizontalAlignment();
            final float v = l.getVerticalAlignment();
            g.drawString(text, (int) (l.getX() + (l.getWidth() - fm.stringWidth(text)) * h),
                    (int) (l.getY() + (l.getHeight() - fm.getHeight()) * v + fm.getAscent()));
        }
        else if (c instanceof Button) {
            final Button b = (Button) c;

            if (borderThickness > 0) {
                if (!b.isEnabled())
                    g.setColor(disabled);
                else if (b.isPressed())
                    g.setColor(pressed);
                else if (b.isMouseover())
                    g.setColor(mouseover);
                else
                    g.setColor(border);
                g.drawRect(b.getX(), b.getY(), b.getWidth() - 1, b.getHeight() - 1);

                g.setColor(background);
                g.fillRect(b.getX() + borderThickness, b.getY() + borderThickness,
                        b.getWidth() - borderThickness * 2, b.getHeight() - borderThickness * 2);
            }
            else {
                g.setColor(background);
                g.fillRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
            }

            if (!b.isEnabled())
                g.setColor(disabled);
            else if (b.isPressed())
                g.setColor(pressed);
            else if (b.isMouseover())
                g.setColor(mouseover);
            else
                g.setColor(foreground);
            g.setFont(font);
            final FontMetrics fm = g.getFontMetrics();
            final String text = b.getText();
            g.drawString(text, b.getX() + (b.getWidth() - fm.stringWidth(text)) / 2,
                    b.getY() + (b.getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        }
        else {
            if (borderThickness > 0) {
                g.setColor(border);
                g.drawRect(c.getX(), c.getY(), c.getWidth() - 1, c.getHeight() - 1);

                g.setColor(background);
                g.fillRect(c.getX() + borderThickness, c.getY() + borderThickness,
                        c.getWidth() - borderThickness * 2, c.getHeight() - borderThickness * 2);

            }
            else {
                g.setColor(background);
                g.fillRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
            }
        }
    }

    public @NotNull DefaultDelegate pressed(final Color pressed) {
        this.pressed = pressed;
        return this;
    }

    @Override
    public void uninstallComponent(final @NotNull Component component) { }
}
