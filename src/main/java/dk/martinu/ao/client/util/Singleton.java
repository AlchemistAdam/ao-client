package dk.martinu.ao.client.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class Singleton<T> {

    @NotNull
    protected final Producer<T> producer;
    protected volatile T value = null;

    public Singleton(@NotNull final Producer<T> producer) {
        this.producer = Objects.requireNonNull(producer, "producer is null");
    }

    @NotNull
    public T getValue() {
        if (value == null)
            synchronized (this) {
                if (value == null)
                    value = producer.get();
            }
        return value;
    }

    public void initialize(@NotNull final Consumer<T> func) {
        if (value == null)
            synchronized (this) {
                if (value == null) {
                    value = producer.get();
                    func.accept(value);
                }
            }
    }

    @Contract(pure = true)
    public synchronized boolean isSet() {
        return value != null;
    }
}
