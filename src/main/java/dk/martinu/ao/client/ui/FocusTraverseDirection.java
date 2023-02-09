package dk.martinu.ao.client.ui;

import dk.martinu.ao.client.targets.UITarget;

/**
 * Direction enum used by focus traversal of UI components.
 *
 * @see UITarget#traverseFocus(FocusTraverseDirection)
 */
public enum FocusTraverseDirection {

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
