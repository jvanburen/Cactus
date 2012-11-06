package api;


import com.ridgesoft.intellibrain.IntelliBrain;
import com.ridgesoft.robotics.DirectionListener;
import com.ridgesoft.robotics.Servo;


/**
 * An abstraction of a continuous servomotor.
 *
 * @author Jacob Van Buren
 * @version 1.0.0
 * @since 1.0.0
 */
public class Motor {
    /** Indicates the motor is on the left side of Cactus. */
    public static final int LEFT = 0;
    /** Indicates the motor is on the left side of Cactus. */
    public static final int RIGHT = 1;
    /**
     * The maximum value that the internal Servo object can take (minimum is
     * zero).
     */
    static final int SERVO_MAX_VALUE = 100;
    /**
     * The value halfway in between zero and the maximum value. Represents the
     * value at which the servo is stopped.
     */
    static final int MIDPOINT_VALUE = SERVO_MAX_VALUE / 2;
    /** The internal Servo object. */
    protected final Servo s;
    /**
     * Indicates whether to mirror the direction of this servo.
     */
    private final boolean invert;
    /** The DirectionListener to notify when the direction changes. */
    private DirectionListener directionListener = null;
    /** The position that the Servo is currently at. */
    private int currentPosition = MIDPOINT_VALUE;

    /**
     * Creates a Motor object from the specified servo. Defaults to the left
     * side.
     *
     * @param servoNumber Which servomotor to use.
     */
    public Motor(int servoNumber) {
        this(servoNumber, Motor.LEFT);
    }

    /**
     * Create a Motor object representing a servo on the specified side of
     * Cactus.
     *
     * @param servoNumber Which servomotor to use.
     * @param side The side on which the servo is mounted (see {@code LEFT} and {@code RIGHT}).
     */
    public Motor(int servoNumber, int side) {
        this.s = IntelliBrain.getServo(servoNumber);
        this.invert = (side != 0);

        // Stop the motor initially
        this.move(MIDPOINT_VALUE);
        s.off();
    }
    
    /**
     * Gives true for forward and false for backward.
     */
    public boolean direction() {
        return this.currentPosition >= MIDPOINT_VALUE;
    }

    /**
     * Sets a DirectionLister for this Motor.
     *
     * @param listener The DirectionListener to notify when the Motor's
     * direction changes.
     */
    public void addDirectionListener(DirectionListener listener) {
        this.directionListener = listener;
    }
    
    /**
     * Gives the side of the robot this motor is on.
     * @return The side of the robot this motor is on.
     */
    public int side() {
        return invert ? RIGHT : LEFT;
    }
    
    /** Moves the servo forward at its maximum rotational speed. */
    public void forward() {
        this.move(SERVO_MAX_VALUE);
    }

    /**
     * Moves the servo forward at the specified speed.
     *
     * @param percent The speed to spin the motor at (0-100)%.
     */
    public void forward(byte percent) {
        if (percent > 100) {
            throw new IllegalArgumentException(
                    "percent must be <= 100\n(recieved: " + percent + ")");
        }
        if (percent < 0) {
            throw new IllegalArgumentException(
                    "percent must be >= 0\n(recieved: " + percent + ")");
        }

        this.move(MIDPOINT_VALUE + MIDPOINT_VALUE * percent / 100);
    }

    /** Moves the servo backward at its maximum rotational speed. */
    public void backward() {
        this.move(0);
    }

    /**
     * Moves the servo backward at the specified speed.
     *
     * @param percent The speed to spin the motor at (0-100)%.
     */
    public void backward(byte percent) {
        if (percent > 100) {
            throw new IllegalArgumentException(
                    "percent must be <= 100\n(recieved: " + percent + ")");
        }
        if (percent < 0) {
            throw new IllegalArgumentException(
                    "percent must be >=0\n(recieved: " + percent + ")");
        }

        this.move(MIDPOINT_VALUE - MIDPOINT_VALUE * percent / 100);
    }

    /**
     * Sets the internal Servo object to the specified position, accounting for
     * the physical position of the servo.
     *
     * @param value The value to set the Servo to.
     */
    private void move(int value) {
        synchronized (this) {
            if (value == currentPosition) {
                return;
            }
            if (directionListener != null) {
                boolean currentDirection = currentPosition >= MIDPOINT_VALUE;
                boolean newDirection = value >= MIDPOINT_VALUE;

                if (currentDirection != newDirection) {
                    directionListener.updateDirection(true);
                } else if (value == MIDPOINT_VALUE) {
                    directionListener.updateDirection(currentDirection);
                }
            }
            currentPosition = value;
            s.setPosition(invert ? SERVO_MAX_VALUE - value : value);
        }
    }

    /** Kills power to the Motor (active braking not supported). */
    public void stop() {
        this.move(MIDPOINT_VALUE);
        s.off();
    }
}