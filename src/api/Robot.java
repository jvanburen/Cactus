
package api;

/**
 * A class that contains things relevant to all robot APIs.
 * Stuff that should be included but shouldn't clutter up a ton of space.
 * @author Jacob
 */
public abstract class Robot {
    // Error Constants

    /** A value to return when a sensor reading fails */
    public static final double  SENSOR_FAILURE_DOUBLE = Double.NaN;
    /** A value to return when a sensor reading fails */
    public static final long    SENSOR_FAILURE_LONG = 0xffffffffffffffffL;
    /** A value to return when a sensor reading fails */
    public static final float   SENSOR_FAILURE_FLOAT = Float.NaN;
    /** A value to return when a sensor reading fails */
    public static final int     SENSOR_FAILURE_INT = 0xffffffff;
    /** A value to return when a sensor reading fails */
    public static final char    SENSOR_FAILURE_CHAR = '\0';
    /** A value to return when a sensor reading fails */
    public static final short   SENSOR_FAILURE_SHORT = 0xffffffff;
    /** A value to return when a sensor reading fails */
    public static final byte    SENSOR_FAILURE_BYTE = 0xffffffff;
    /** A value to return when a sensor reading fails */
    public static final boolean SENSOR_FAILURE_BOOL = false;

    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param d The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static strictfp boolean sensorFailed(double d) {
        // Do some tricky stuff to ensure NaN still works
        return !(d < SENSOR_FAILURE_DOUBLE || SENSOR_FAILURE_DOUBLE < d);
    }

    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param l The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static boolean sensorFailed(long l)
        { return l == SENSOR_FAILURE_LONG; }
    

    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param f The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static boolean sensorFailed(float f) {
        // Do some tricky stuff to ensure NaN still works
        return !(f < SENSOR_FAILURE_FLOAT || SENSOR_FAILURE_FLOAT < f);
    }

    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param i The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static boolean sensorFailed(int i)
        { return i == SENSOR_FAILURE_INT; }


    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param s The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static boolean sensorFailed(short s)
        { return s == SENSOR_FAILURE_SHORT; }


    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param c The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static boolean sensorFailed(char c)
        { return c == SENSOR_FAILURE_CHAR; }


    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param b The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static boolean sensorFailed(byte b)
        { return b == SENSOR_FAILURE_BYTE; }


    /**
     * Tests whether the value returned is equal to that the value returned
     * when a sensor cannot complete its reading.
     *
     * @param b The value to check
     * @return {@code false} IFF the value could not be the result of a sensor
     * failure.
     */
    public static boolean sensorFailed(boolean b)
        { return b == SENSOR_FAILURE_BOOL; }
    
    
    /**
     * Tells the current thread to sleep for a certain duration.
     * Accurate to within a couple milliseconds
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
