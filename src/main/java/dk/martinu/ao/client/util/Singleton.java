package dk.martinu.ao.client.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Singleton<T> {

    @NotNull
    protected final Producer<T> producer;
    protected volatile T value = null;

    @Contract(pure = true)
    public Singleton(@NotNull final Producer<T> producer) {
        this.producer = Objects.requireNonNull(producer, "producer is null");
    }

    @NotNull
    public T get() {
        if (value == null)
            synchronized (this) {
                if (value == null)
                    value = producer.get();
            }
        return value;
    }
}
