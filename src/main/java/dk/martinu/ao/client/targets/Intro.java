/*
 * Copyright (c) 2020, Adam Martinu. All rights reserved. Altering or removing
 * copyright notices or this file header is not allowed.
 *
 * This code is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License (CC BY-SA 4.0). To view a copy of the license, visit
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode.
 */
package dk.martinu.ao.client.targets;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Objects;

import dk.martinu.ao.client.core.GameThread;
import dk.martinu.ao.client.event.KeyAction;
import dk.martinu.ao.client.event.OnPressKeyAction;
import dk.martinu.ao.client.util.Resolution;

public class Intro extends AbstractTarget {

    @NotNull
    public final Target target;
    public boolean skip = false;

    public Intro(@NotNull final GameThread gameThread, @NotNull final Target target) {
        super(gameThread);
        this.target = Objects.requireNonNull(target, "target is null");
    }

    @Override
    public void logic(@NotNull final Resolution r) {
        super.logic(r);
        if (skip || timer.getTime() >= 4000L) {
            // TODO cancel any sound output here when implemented
            thread.setTarget(target);
        }
    }

    @Override
    public void paint(@NotNull final Graphics2D g, @NotNull final Resolution r) {
        // draw a rotating square
        g.setColor(Color.WHITE);
        g.translate((r.width - 200) / 2 + 100, (r.height - 200) / 2 + 100);
        g.rotate(Math.toRadians(360) * (timer.getTime() / 8000d));
        g.fillRect(-100, -100, 200, 200);
    }

    @Override
    protected void initKeyBindings() {
        final KeyAction skip = new OnPressKeyAction(100, (action, event) -> {
            this.skip = true;
            return false;
        });
        bindKeys(skip, KeyEvent.VK_ESCAPE, KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
    }
}
