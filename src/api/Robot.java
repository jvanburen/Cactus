
package api;

/**
 * A class that contains things relevant to all robot APIs.
 * Stuff that should be included but shouldn't clutter up a ton of space.
 * @author Jacob
 */
public abstract class Robot {
    // Error Constants

    /**
     * A static exception to throw that avoids the overhead of creating a new
     * exception every time a sensor fails.
     */
    public static final SensorFailure SENSOR_FAIL = new SensorFailure();


    /**
     * Tells the current thread to sleep for a certain duration.
     * Accurate to within a couple milliseconds.
     * Will not stop on {@code InterruptedException}.
     * @param milliseconds The number of milliseconds to sleep for.
     */
    public static void sleepFor(long milliseconds) {
        // The time at which to return from this method
        long end = System.currentTimeMillis() + milliseconds;

        while (System.currentTimeMillis() < end)
            try {
                Thread.sleep(end - System.currentTimeMillis());
            } catch (InterruptedException e)
                { /* Do Nothing. */ }
    }
}
