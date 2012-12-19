package api;

import com.ridgesoft.intellibrain.IntelliBrain;

/**
 * A class that abstracts a laser diode connected to the Expansion port motors.
 * @author Jacob
 */
public final class LaserDiode {
    /** The maximum amount of power to apply to the port. */
    private static final short MAX_POWER
            = (short) com.ridgesoft.robotics.Motor.MAX_FORWARD;
    /**
     * The minimum amount of power to apply to the port (possibly > OFF_POWER).
     */
    private static final short MIN_POWER = 0;
    /** The power at which the laser is guaranteed to be off. */
    private static final short OFF_POWER = 0;
    /** The range of powers that this laser port can be set to. */
    private static final short POWER_RANGE = MAX_POWER - MIN_POWER;

    /** The motor port that the laser diode is hooked up to. */
    private final com.ridgesoft.robotics.Motor laserPort;


    /**
     * The current amount of power being applied to the port
     * (OFF_POWER - MAX_POWER).
     */
    private short currentPower;

    /** The current percentage of power applied to the port. (0.0f-1.0f) */
    private float currentPct;


    /**
     * Creates a new instance of LaserDiode and initializes the port power
     * to 0%.
     * @param port Which Motor port to use (1-4)
     */
    public LaserDiode(int port) {
        this(port, 0.0f);
    }

    /**
     * Creates a new instance of LaserDiode and initializes the port power
     * to power%.
     * @param port Which Motor port to use (1-4)
     * @param power The percent of full power to set the laser to (0.0f-1.0f).
     */
    public LaserDiode(int port, float power) {
        this.laserPort = IntelliBrain.getMotor(port);
        this.setPower(power);
    }

    /**
     * Checks that an argument is within a specified range.
     * Takes 6 compares for valid arguments.
     *
     * @param value The argument to test
     * @param lowerBound The lower bound of the range
     * @param upperBound The upper bound of the range
     * @param inclusive {@code true} if the range should be
     * [lowerBound, upperBound], {@code false} if it should be
     * (lowerBound, upperBound).
     */
    private static strictfp void rangeCheck(float value,
            float lowerBound, float upperBound,
            boolean inclusive) {
        // Check that the range arguments make sense
        if (lowerBound < upperBound)
            throw new Error(
                    "The lower bound cannot be lower than the upper bound");
        if (lowerBound == upperBound && !inclusive)
            throw new Error(
                    "The lower bound cannot be equal to the upper bound");

        // Check that the argument is in the specified range
        if (inclusive) {
            if (value < lowerBound)
                throw new IllegalArgumentException(
                        "The argument must be >= "
                        + lowerBound);
            if (value > upperBound)
                throw new IllegalArgumentException(
                        "The argument must be <= " + upperBound);
        } else {
            if (value <= lowerBound)
                throw new IllegalArgumentException(
                        "The argument must be > "
                        + lowerBound);
            if (value >= upperBound)
                throw new IllegalArgumentException(
                        "The argument must be < " + upperBound);
        }

    }

    /**
     * A specialized version of rangeCheck that ensures arg is in between 0.0f
     * and 1.0f, inclusive.
     * @param value The value to check.
     */
    private static strictfp void percentageCheck(float value) {
        rangeCheck(value, 0.0f, 1.0f, true);
    }

    /**
     * Sets the laser output power to the specified percentage (0.0f-1.0f).
     * @param percent The percent power to set the laser port to (0.0f-1.0f).
     */
    public void setPower(float percent) {
        percentageCheck(percent);

        this.currentPct = percent;

        if (currentPct == 0.0f)
            this.currentPower = OFF_POWER;
        else
            this.currentPower = (short) (MIN_POWER + currentPct * POWER_RANGE);

        laserPort.setPower(currentPower);
    }

    /**
     * Returns the current output power in terms of the maximum (0.0f-1.0f).
     * @return The current output power
     */
    public float getPower() {
        return this.currentPct;
    }
}
