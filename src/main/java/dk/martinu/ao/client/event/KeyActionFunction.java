package dk.martinu.ao.client.event;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;

/**
 * A functional interface used as a delegate for {@link KeyAction}s.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-05
 * @see OnPressAndReleaseKeyAction
 * @see OnPressKeyAction
 * @see OnReleaseKeyAction
 * @since 1.0
 */
@FunctionalInterface
public interface KeyActionFunction {

    /**
     * Performs a task for the specified key action in response to the
     * specified key event.
     *
     * @param action the key action that invoked this function
     * @param event  the event that caused the action to be called
     * @return {@code true} if this function consumed the event, otherwise
     * {@code false}
     */
    boolean perform(@NotNull final KeyAction action, @NotNull final KeyEvent event);
}
