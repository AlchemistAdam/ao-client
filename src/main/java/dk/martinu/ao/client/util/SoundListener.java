package dk.martinu.ao.client.util;

@FunctionalInterface
public interface SoundListener {

    void onFinish(final Sound sound);
}
