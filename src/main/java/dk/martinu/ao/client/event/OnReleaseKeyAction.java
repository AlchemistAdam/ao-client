package dk.martinu.ao.client.event;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * An implementation of {@link KeyAction} with a mask of {@code KEY_RELEASED}
 * that invokes a delegate {@link KeyActionFunction function} when called.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-05
 * @since 1.0
 */
public final class OnReleaseKeyAction extends KeyAction {

    /**
     * The function to invoke when this action is called.
     */
    @NotNull
    private final KeyActionFunction function;

    /**
     * Constructs a new key action with the specified priority which invokes
     * the specified function when called.
     *
     * @throws NullPointerException if {@code function} is {@code null}
     */
    public OnReleaseKeyAction(final int priority, @NotNull final KeyActionFunction function) {
        super(priority, KeyAction.KEY_PRESSED);
        this.function = Objects.requireNonNull(function, "function is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean perform(final @NotNull KeyEvent event) {
        return function.perform(this, event);
    }
}
