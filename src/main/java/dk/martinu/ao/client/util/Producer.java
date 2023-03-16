package dk.martinu.ao.client.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@FunctionalInterface
public interface Producer<T> extends Supplier<T> {

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    T get();
}
