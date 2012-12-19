
package api;

/**
 * A checked exception that represents a sensor failure.
 * @author Jacob
 */
public class SensorFailure extends Exception {

    /**
     * Creates a new instance of
     * <code>SensorFailure</code> without detail message.
     */
    public SensorFailure() {
    }

    /**
     * Constructs an instance of
     * <code>SensorFailure</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public SensorFailure(String msg) {
        super(msg);
    }

    /**
     * Overriding this makes catching exceptions MUCH cheaper
     * @return
     */
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

}
