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

import dk.martinu.ao.client.Timer;
import dk.martinu.ao.client.core.GameThread;
import dk.martinu.ao.client.event.*;
import dk.martinu.ao.client.util.Resolution;

import static dk.martinu.ao.client.event.KeyAction.KEY_PRESSED;
import static dk.martinu.ao.client.event.KeyAction.KEY_RELEASED;

/**
 * Abstract implementation of the {@link Target} interface. Provides support
 * for keybindings, and stores and processes key and mouse events.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-07
 * @see dk.martinu.ao.client.event
 * @since 1.0
 */
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

    /**
     * The game thread that owns this target.
     */
    @NotNull
    public final GameThread thread;
    // TODO remove, utility class seems excessive
    public final Timer timer = new Timer();

    /**
     * The current mouse action, can be {@code null}.
     */
    @Nullable
    MouseAction mouseAction = null;
    /**
     * Container of received mouse events.
     */
    final MouseInput mouseInput = new MouseInput();

    /**
     * Key bindings ({@link KeyAction}s) for released key events.
     */
    private final KeyMap keyActionsPressed = new KeyMap();
    /**
     * Key bindings ({@link KeyAction}s) for pressed key events.
     */
    private final KeyMap keyActionsReleased = new KeyMap();
    /**
     * Buffer containing received key events.
     */
    private final KeyInputBuffer keyInputBuffer = new KeyInputBuffer();

    /**
     * Constructs a new target with the specified {@link GameThread} set as the
     * owner of this target.
     *
     * @param thread the owner of this target
     * @throws NullPointerException if {@code thread} is {@code null}
     */
    public AbstractTarget(@NotNull final GameThread thread) {
        this.thread = Objects.requireNonNull(thread, "thread is null");
        initKeyBindings();
    }

    /**
     * Binds the specified {@link KeyAction} to the specified key code on this
     * target. Multiple key actions bound to the same key code are sorted by
     * their {@link KeyAction#priority priority} in descending order.
     *
     * @param keyAction the key action to bind
     * @param keyCode   the key code to bind to
     * @throws NullPointerException if {@code keyAction} is {@code null}
     * @see KeyActionList
     * @see KeyInput
     * @see KeyInputBuffer#processInput()
     */
    public void bindKey(@NotNull final KeyAction keyAction, final int keyCode) {
        Objects.requireNonNull(keyAction, "keyAction is null");
        if ((keyAction.getMask() & KEY_PRESSED) != 0)
            keyActionsPressed.insert(keyAction, keyCode);
        if ((keyAction.getMask() & KEY_RELEASED) != 0)
            keyActionsReleased.insert(keyAction, keyCode);
    }

    /**
     * Binds the specified key action to multiple key codes. See
     * {@link #bindKey(KeyAction, int)} for details.
     *
     * @throws NullPointerException     if {@code keyAction} or {@code keyCodes} is
     *                                  {@code null}
     * @throws IllegalArgumentException if {@code keyCodes} is empty
     */
    public void bindKeys(@NotNull final KeyAction keyAction, final int... keyCodes) {
        Objects.requireNonNull(keyAction, "keyAction is null");
        Objects.requireNonNull(keyCodes, "keyCodes is null");
        if (keyCodes.length == 0)
            throw new IllegalArgumentException("KeyCodes is empty");
        for (int keyCode : keyCodes)
            bindKey(keyAction, keyCode);
    }

    /**
     * Called when a key has been pressed. Gets a list of all
     * {@code KeyAction}s with a {@link KeyAction#KEY_PRESSED} mask that are
     * bound to the key code of the event on this target, maps the list to
     * {@link KeyInput} objects and stores them in this target's
     * {@link KeyInputBuffer} for later processing.
     *
     * @param event the event to be processed
     * @see #bindKey(KeyAction, int)
     * @see KeyActionList#mapToInput(KeyEvent)
     * @see KeyInputBuffer#processInput()
     */
    @Override
    public void keyPressed(@NotNull final KeyEvent event) {
        final KeyActionList list = keyActionsPressed.getList(event.getKeyCode());
        if (list != null)
            keyInputBuffer.add(list.mapToInput(event));
    }

    /**
     * Called when a key has been released. Gets a list of all
     * {@code KeyAction}s with a {@link KeyAction#KEY_RELEASED} mask that are
     * bound to the key code of the event on this target, maps the list to
     * {@link KeyInput} objects and stores them in this target's
     * {@link KeyInputBuffer} for later processing.
     *
     * @param event the event to be processed
     * @see #bindKey(KeyAction, int)
     * @see KeyActionList#mapToInput(KeyEvent)
     * @see KeyInputBuffer#processInput()
     */
    @Override
    public void keyReleased(@NotNull final KeyEvent event) {
        final KeyActionList list = keyActionsReleased.getList(event.getKeyCode());
        if (list != null)
            keyInputBuffer.add(list.mapToInput(event));
    }

    @Override
    public void keyTyped(@NotNull final KeyEvent event) { }

    // DOC
    @Override
    public void logic(@NotNull final Resolution r) {
        timer.measure();

        keyInputBuffer.processInput();
        if (mouseAction != null)
            mouseInput.processInput(r, mouseAction);
    }

    /**
     * Not used.
     */
    @Deprecated
    @Override
    public void mouseClicked(final MouseEvent event) { }

    /**
     * Called when a mouse button is pressed and then dragged. Stores the event
     * in this target's {@link MouseInput} for later processing.
     *
     * @param event the event to be processed
     * @see MouseAction
     * @see MouseInput#processInput(Resolution, MouseAction)
     */
    @Override
    public void mouseDragged(@NotNull final MouseEvent event) {
        mouseInput.mouseDragged(event);
    }

    /**
     * Not used.
     */
    @Deprecated
    @Override
    public void mouseEntered(final MouseEvent event) { }

    /**
     * Not used.
     */
    @Deprecated
    @Override
    public void mouseExited(final MouseEvent event) { }

    /**
     * Called when the mouse has moved. Stores the event in this target's
     * {@link MouseInput} for later processing.
     *
     * @param event the event to be processed
     * @see MouseAction
     * @see MouseInput#processInput(Resolution, MouseAction)
     */
    @Override
    public void mouseMoved(@NotNull final MouseEvent event) {
        mouseInput.mouseMoved(event);
    }

    /**
     * Called when a mouse button is pressed. Stores the event in this target's
     * {@link MouseInput} for later processing.
     *
     * @param event the event to be processed
     * @see MouseAction
     * @see MouseInput#processInput(Resolution, MouseAction)
     */
    @Override
    public void mousePressed(@NotNull final MouseEvent event) {
        mouseInput.mousePressed(event);
    }

    /**
     * Called when a mouse button is released. Stores the event in this
     * target's {@link MouseInput} for later processing.
     *
     * @param event the event to be processed
     * @see MouseAction
     * @see MouseInput#processInput(Resolution, MouseAction)
     */
    @Override
    public void mouseReleased(@NotNull final MouseEvent event) {
        mouseInput.mouseReleased(event);
    }

    /**
     * Called when the mouse wheel is rotated. Stores the event in this
     * target's {@link MouseInput} for later processing.
     *
     * @param event the event to be processed
     * @see MouseAction
     * @see MouseInput#processInput(Resolution, MouseAction)
     */
    @Override
    public void mouseWheelMoved(@NotNull final MouseWheelEvent event) {
        mouseInput.mouseWheelMoved(event);
    }

    // DOC
    @Override
    public void paint(final Graphics2D g, final Resolution r) {
//        timer.measure();
    }

    protected abstract void initKeyBindings();
}
