package dk.martinu.ao.client.util;

/**
 * Simple timer utility class for measured elapsed time in millisecond
 * precision.
 *
 * @author Adam Martinu
 */
// TODO remove/replace
public class Timer {

    /**
     * The measured amount of time that has passed in milliseconds since this
     * timer was started.
     *
     * @see #measure()
     */
    private Long time;
    /**
     * Timestamp of when this timer was started.
     *
     * @see #start()
     */
    private Long startTime;

    /**
     * Returns the measured amount of time in milliseconds that has passed
     * since this timer was started.
     *
     * @see #measure()
     */
    public long getTime() throws IllegalStateException {
        if (startTime == null)
            startTime = System.currentTimeMillis();
        return time;
    }

    /**
     * Measures the amount of time that has passed in milliseconds since this
     * timer was started. For information about the precision of the
     * measurement, see {@link System#currentTimeMillis()}.
     */
    public void measure() throws IllegalStateException {
        if (startTime == null)
            startTime = System.currentTimeMillis();
        time = System.currentTimeMillis() - startTime;
    }

    /**
     * Starts this timer. If this timer has already been started then the timer
     * is reset.
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }
}
