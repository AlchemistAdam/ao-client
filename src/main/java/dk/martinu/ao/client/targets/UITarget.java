/*
 * Copyright (c) 2018, Adam Martinu. All rights reserved. Altering or removing
 * copyright notices or this file header is not allowed.
 *
 * This code is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License (CC BY-SA 4.0). To view a copy of the license, visit
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode.
 */
package dk.martinu.ao.client.targets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Graphics2D;
import java.awt.event.*;
import java.util.Objects;
import java.util.function.Function;

import dk.martinu.ao.client.event.MouseAction;
import dk.martinu.ao.client.core.GameThread;
import dk.martinu.ao.client.event.KeyAction;
import dk.martinu.ao.client.event.OnPressAndReleaseKeyAction;
import dk.martinu.ao.client.ui.Component;
import dk.martinu.ao.client.ui.Scene;
import dk.martinu.ao.client.util.Resolution;

public class UITarget extends AbstractTarget {

    public static final String KA_FOCUS_TRAV_UP = "UITarget.focusTraverseUp";
    public static final String KA_FOCUS_TRAV_LEFT = "UITarget.focusTraverseLeft";
    public static final String KA_FOCUS_TRAV_DOWN = "UITarget.focusTraverseDown";
    public static final String KA_FOCUS_TRAV_RIGHT = "UITarget.focusTraverseRight";
    public static final String KA_FOCUS_DO_ACTION = "UITarget.focusDoAction";

    @Nullable
    protected Scene scene = null;

    public UITarget(@NotNull final GameThread thread) {
        super(thread);
        mouseAction = new UIMouseAction();
    }

    @Nullable
    public Scene getScene() {
        return scene;
    }

    @Override
    public void paint(@NotNull final Graphics2D g, @NotNull final Resolution r) {
        paintScene(g, r);
    }

    public void setScene(@Nullable final Scene scene) {
        if (this.scene != null) {
            this.scene.setFocusedComponent(null);
            this.scene.setMouseoverComponent(null);
            this.scene.setPressedComponent(null);
        }
        this.scene = scene;
    }

    @Nullable
    public Component traverseFocus(@NotNull final FocusTraverseDirection ftd) {
        Objects.requireNonNull(ftd);
        if (scene != null && scene.isFocusTraversable()) {

            // traverse from currently focused component
            Component c = scene.getFocusedComponent();
            if (c != null) {

                // lambda for traversing components depending on direction
                final Function<Component, Component> nextFocus = switch (ftd) {
                    case UP -> Component::getFocusTraverseUp;
                    case DOWN -> Component::getFocusTraverseDown;
                    case LEFT -> Component::getFocusTraverseLeft;
                    case RIGHT -> Component::getFocusTraverseRight;
                };

                // begin traversal
                while ((c = nextFocus.apply(c)) != null) {
                    // traversal is cyclic
                    if (c == scene.getFocusedComponent())
                        return null;
                    // a new focusable component is found
                    if (c.isFocusable() && c.isEnabled())
                        break;
                }
            }
            // no component is focused - use default
            else
                c = scene.getDefaultFocusComponent();

            // set new focused component if one is found
            if (c != null) {
                scene.setPressedComponent(null);
                scene.setFocusedComponent(c);
                return c;
            }
        }
        return null;
    }

    // TODO document key action priorities and bindings
    @Override
    protected void initKeyBindings() {
        final KeyAction focusTraverseUp = new OnPressAndReleaseKeyAction(200, (action, event) -> {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                if (action.getUserObject() == null && traverseFocus(FocusTraverseDirection.UP) != null) {
                    action.setUserObject(event);
                    return false;
                }
            }
            else if (event.getID() == KeyEvent.KEY_RELEASED && action.getUserObject() != null) {
                action.setUserObject(null);
                return false;
            }
            return true;
        });
        bindKeys(focusTraverseUp, KeyEvent.VK_UP, KeyEvent.VK_W);

        final KeyAction focusTraverseLeft = new OnPressAndReleaseKeyAction(200, (action, event) -> {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                if (action.getUserObject() == null && traverseFocus(FocusTraverseDirection.LEFT) != null) {
                    action.setUserObject(event);
                    return false;
                }
            }
            else if (event.getID() == KeyEvent.KEY_RELEASED && action.getUserObject() != null) {
                action.setUserObject(null);
                return false;
            }
            return true;
        });
        bindKeys(focusTraverseLeft, KeyEvent.VK_LEFT, KeyEvent.VK_A);

        final KeyAction focusTraverseDown = new OnPressAndReleaseKeyAction(200, (action, event) -> {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                if (action.getUserObject() == null && traverseFocus(FocusTraverseDirection.DOWN) != null) {
                    action.setUserObject(event);
                    return false;
                }
            }
            else if (event.getID() == KeyEvent.KEY_RELEASED && action.getUserObject() != null) {
                action.setUserObject(null);
                return false;
            }
            return true;
        });
        bindKeys(focusTraverseDown, KeyEvent.VK_DOWN, KeyEvent.VK_S);

        final KeyAction focusTraverseRight = new OnPressAndReleaseKeyAction(200, (action, event) -> {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                if (action.getUserObject() == null && traverseFocus(FocusTraverseDirection.RIGHT) != null) {
                    action.setUserObject(event);
                    return false;
                }
            }
            else if (event.getID() == KeyEvent.KEY_RELEASED && action.getUserObject() != null) {
                action.setUserObject(null);
                return false;
            }
            return true;
        });
        bindKeys(focusTraverseRight, KeyEvent.VK_RIGHT, KeyEvent.VK_D);

        final KeyAction focusDoAction = new OnPressAndReleaseKeyAction(200, (action, event) -> {
            if (scene == null)
                return true;
            final Component fc = scene.getFocusedComponent();
            if (fc != null) {
                if (event.getID() == KeyEvent.KEY_PRESSED) {
                    scene.setPressedComponent(fc);
                    return false;
                }
                else if (fc.isPressed()) {
                    scene.setPressedComponent(null);
                    fc.doActions(this, event);
                    return false;
                }
            }
            return true;
        });
        bindKeys(focusDoAction, KeyEvent.VK_ENTER, KeyEvent.VK_SPACE);
    }

    protected void paintScene(@NotNull final Graphics2D g, @NotNull final Resolution r) {
        if (scene != null)
            scene.paint(g, r);
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
                if (mc.isFocusable())
                    scene.setFocusedComponent(mc);
                else
                    scene.setFocusedComponent(null);
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

    /**
     * Direction enum used by focus traversal of UI components.
     *
     * @see UITarget#traverseFocus(FocusTraverseDirection)
     */
    protected enum FocusTraverseDirection {

        /**
         * The focus is traversing upwards.
         */
        UP,
        /**
         * The focus is traversing towards the left.
         */
        LEFT,
        /**
         * The focus is traversing downwards.
         */
        DOWN,
        /**
         * The focus is traversing towards the right.
         */
        RIGHT
    }
}
