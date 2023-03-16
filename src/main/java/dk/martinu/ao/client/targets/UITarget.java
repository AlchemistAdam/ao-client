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
import org.jetbrains.annotations.Nullable;

import java.awt.Graphics2D;
import java.awt.event.*;

import dk.martinu.ao.client.core.GameThread;
import dk.martinu.ao.client.event.KeyInput;
import dk.martinu.ao.client.event.MouseAction;
import dk.martinu.ao.client.ui.Component;
import dk.martinu.ao.client.ui.Scene;
import dk.martinu.ao.client.util.Resolution;

public class UITarget extends AbstractTarget {

    @Nullable
    protected Scene scene = null;

    public UITarget(@NotNull final GameThread thread) {
        super(thread);
        mouseAction = new UIMouseAction();
    }

    @Override
    public void paint(@NotNull final Graphics2D g, @NotNull final Resolution r) {
        super.paint(g, r);
        if (scene != null)
            scene.paint(g, r);
    }

    public void setScene(@Nullable final Scene scene) {
        if (this.scene != null) {
            this.scene.setMouseoverComponent(null);
            this.scene.setPressedComponent(null);
        }
        this.scene = scene;
    }

    @Override
    public void keyTyped(@NotNull final KeyEvent event) {
        super.keyTyped(event);
        keyInputBuffer.add(new KeyInput(null, event));
//        if (scene != null) {
//            final Component focus = scene.getFocusedComponent();
//            if (focus instanceof TextField textField)
//                textField.onKeyTyped(event);
//        }
    }

    @Override
    protected void initKeyBindings() {

    }

    public class UIMouseAction implements MouseAction {

        @Override
        public void mouseDragged(@NotNull final MouseEvent event) {
            if (scene == null)
                return;
            final Component pc = scene.getPressedComponent();
            if (pc != null && pc.isDraggable())
                ; // TODO drag component
            else
                mouseMoved(event);
        }

        @Override
        public void mouseMoved(@NotNull final MouseEvent event) {
            if (scene == null)
                return;
            final Component c = scene.getComponent(event.getPoint());
            final Component mc = scene.getMouseoverComponent();
            if (c != mc) {
                scene.setMouseoverComponent(c);

                final Component pc = scene.getPressedComponent();
                if (pc != null && pc != mc)
                    scene.setPressedComponent(null);
            }
        }

        @Override
        public void mousePressed(@NotNull final MouseEvent event) {
            if (scene == null)
                return;
            final Component mc = scene.getMouseoverComponent();
            if (event.getButton() == MouseEvent.BUTTON1 && mc != null && mc.isEnabled()) {
                scene.setPressedComponent(mc);
            }
        }

        @Override
        public void mouseReleased(@NotNull final MouseEvent event) {
            if (scene == null)
                return;
            final Component pc = scene.getPressedComponent();
            if (pc != null) {
                if (pc == scene.getMouseoverComponent())
                    pc.doActions(UITarget.this, event);
                scene.setPressedComponent(null);
            }
        }

        @Override
        public void mouseWheelMoved(@NotNull final MouseWheelEvent event) {
            if (scene == null)
                return;
            final Component mc = scene.getMouseoverComponent();
            if (mc != null && mc.isScrollable())
                mc.doMouseWheelActions(UITarget.this, event);
        }
    }
}
