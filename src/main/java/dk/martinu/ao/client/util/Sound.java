package dk.martinu.ao.client.util;

import dk.martinu.ao.client.SoundListener;

public interface Sound {

    void addListener(final SoundListener listener);

    void close();

    boolean isClosed();

    boolean isPlaying();

    boolean isStopped();

    void play();

    void removeListener(final SoundListener listener);

    void reset();

    default void resume() {
        if (isStopped())
            play();
    }

    void setPan(float value);

    void setVolume(double value);

    void stop();
}
