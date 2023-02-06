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
import java.util.Objects;

import dk.martinu.ao.client.*;
import dk.martinu.ao.client.core.GameThread;
import dk.martinu.ao.client.event.*;
import dk.martinu.ao.client.util.Resolution;

import static dk.martinu.ao.client.event.KeyAction.KEY_PRESSED;
import static dk.martinu.ao.client.event.KeyAction.KEY_RELEASED;

// TODO be able to remove key bindings
// TODO be able to edit key bindings (maybe as remove+add)
public abstract class AbstractTarget implements Target {

    // unused
//    static void addKeyAction(@NotNull final KeyAction keyAction, @NotNull final KeyAction[] array) {
//        for (int i = 0; i < array.length; i++) {
//            if (array[i] == null) {
//                array[i] = keyAction;
//                return;
//            }
//            else if (keyAction.compareTo(array[i]) > 0) {
//                // shifts all key actions with index greater than or equal to i
//                // to the right
//                for (int k = array.length - 1; k > i; k--)
//                    array[k] = array[k - 1];
//                array[i] = keyAction;
//                return;
//            }
//        }
//    }

    public final GameThread thread;
    public final Timer timer = new Timer();
    public final MouseInput mouseInput = new MouseInput();
    @Nullable
    MouseAction mouseAction = null;

    private final KeyInputBuffer keyInputBuffer = new KeyInputBuffer();
    private final KeyMap keyActionsPressed = new KeyMap();
    private final KeyMap keyActionsReleased = new KeyMap();

    public AbstractTarget(@NotNull final GameThread thread) {
        this.thread = Objects.requireNonNull(thread, "thread is null");
        initKeyBindings();
    }

    public void bindKeyAction(@NotNull final KeyAction action, final int keyCode) {
        Objects.requireNonNull(action, "action is null");
        if ((action.getMask() & KEY_PRESSED) != 0)
            keyActionsPressed.insert(action, keyCode);
        if ((action.getMask() & KEY_RELEASED) != 0)
            keyActionsReleased.insert(action, keyCode);
    }

    public void bindKeys(@NotNull final KeyAction keyAction, final int... keyCodes) {
        Objects.requireNonNull(keyAction, "keyAction is null");
        Objects.requireNonNull(keyCodes, "keyCodes is null");
        if (keyCodes.length == 0)
            throw new IllegalArgumentException("KeyCodes is empty");
        for (Integer keyCode : keyCodes)
            bindKeyAction(keyAction, keyCode);
    }

    @NotNull
    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public GameThread getThread() {
        return thread;
    }

    @Override
    public void keyPressed(@NotNull final KeyEvent event) {
        final KeyActionList list = keyActionsPressed.getList(event.getKeyCode());
        if (list != null)
            keyInputBuffer.add(list.mapToInput(event));
    }

    @Override
    public void keyReleased(@NotNull final KeyEvent event) {
        final KeyActionList list = keyActionsReleased.getList(event.getKeyCode());
        if (list != null)
            keyInputBuffer.add(list.mapToInput(event));
    }

    @Override
    public void keyTyped(@NotNull final KeyEvent event) {

    }

    @Override
    public void logic(final Resolution r) {
        timer.measure();

        keyInputBuffer.processInput();

        if (mouseAction != null)
            mouseInput.processInput(r, mouseAction);
    }

    @Deprecated
    @Override
    public void mouseClicked(final MouseEvent event) { }

    @Override
    public void mouseDragged(@NotNull final MouseEvent event) {
        mouseInput.mouseDragged(event);
    }

    @Deprecated
    @Override
    public void mouseEntered(final MouseEvent event) { }

    @Deprecated
    @Override
    public void mouseExited(final MouseEvent event) { }

    @Override
    public void mouseMoved(@NotNull final MouseEvent event) {
        mouseInput.mouseMoved(event);
    }

    @Override
    public void mousePressed(@NotNull final MouseEvent event) {
        mouseInput.mousePressed(event);
    }

    @Override
    public void mouseReleased(@NotNull final MouseEvent event) {
        mouseInput.mouseReleased(event);
    }

    @Override
    public void mouseWheelMoved(@NotNull final MouseWheelEvent event) {
        mouseInput.mouseWheelMoved(event);
    }

    @Override
    public void paint(final Graphics2D g, final Resolution r) {
//        timer.measure();
    }

    protected abstract void initKeyBindings();
}
