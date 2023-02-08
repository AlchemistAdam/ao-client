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
