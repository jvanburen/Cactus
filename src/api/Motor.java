package api;


import com.ridgesoft.intellibrain.IntelliBrain;
import com.ridgesoft.robotics.AnalogInput;
import com.ridgesoft.robotics.Servo;


/**
 * An abstraction of a continuous servomotor.
 *
 * @author Jacob Van Buren
 * @version 1.0.0
 * @since 1.0.0
 */
public class Motor {
    /** The number of ticks per 1 rotation of the wheel. */
    public static final int TICKS_PER_ROTATION = 16;
    /** The number of ticks per 1 rotation of the wheel. */
    public static final float CM_PER_TICK = 1.33f;
    /** The number of milliseconds to wait per tachometer refresh. */
    public static final int REFRESH_RATE = 100;

    /** The number of milliseconds to wait after a failed sensor read. */
    private static final int RESAMPLE_RATE = 12;


    /** Indicates the motor is on the left side of Cactus. */
    public static final int LEFT = 0;
    /** Indicates the motor is on the left side of Cactus. */
    public static final int RIGHT = 1;
    /** The maximum value that the internal Servo object can take. */
    static final int SERVO_MAX_VALUE = 100;
    /**
     * The value halfway in between zero and the maximum value.
     * Represents the value at which the servo is stopped.
     */
    static final int MIDPOINT_VALUE = SERVO_MAX_VALUE / 2;
    /** The internal Servo object. */
    protected final Servo s;
    /** Indicates whether to mirror the direction of this servo. */
    private final boolean invert;
    /** The DirectionListener to notify when the direction changes. */
    private TachoListener listener = null;

    /** The position that the Servo is currently at. */
    private int currentPosition = MIDPOINT_VALUE;

    private Tachometer tacho;

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
        tacho = new Tachometer();

        // Stop the motor initially
        this.move(MIDPOINT_VALUE);
        s.off();
        Thread tachoThread = new Thread(tacho);
        tachoThread.setDaemon(true);
        tachoThread.start();
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
    public void forward(int percent) {
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
    public void backward(int percent) {
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
            if (value == currentPosition)
                return;
            else
                currentPosition = value;

            currentPosition = value;
            s.setPosition(invert ? SERVO_MAX_VALUE - value : value);
        }
    }

    /** Kills power to the Motor (active braking not supported). */
    public void stop() {
        this.move(MIDPOINT_VALUE);
        s.off();
    }

    /**
     * Gives the speed of the motor as a percentage from -100 to 100.
     * @return the speed of the motor.
     */
    public int speed() {
        if (currentPosition == MIDPOINT_VALUE)
            return 0;
        return MIDPOINT_VALUE *100 / (MIDPOINT_VALUE - currentPosition);
    }

    /**
     * Gets the number of ticks on this motor's tachometer.
     * @return the number of ticks on this motor's tachometer.
     */
    public int ticks() {
        return tacho.tickCount;
    }

    private class Tachometer implements Runnable {

        /** The pseudo-tachometer sensor. */
        private final AnalogInput sensor;

        /** The number of ticks FORWARD the wheel has spun. */
        private volatile int tickCount;

        /** The last recorded reading of the sensor. */
        private int prevReading;

        public Tachometer() {
            if (invert)
                this.sensor = IntelliBrain.getAnalogInput(
                        CactusBase.RIGHT_TACHO_PORT);
            else
                this.sensor = IntelliBrain.getAnalogInput(
                        CactusBase.LEFT_TACHO_PORT);
            prevReading = sensed();

        }

        @Override
        public void run() {
            synchronized (this) {

                while (true) {
                    try {
                        this.wait(REFRESH_RATE);
                    } catch (InterruptedException ex)
                        { /* Do Nothing. */ }
                    int pos = currentPosition;
                    int reading;
                    if (pos < MIDPOINT_VALUE) {
                        reading = sensed();
                        if (reading != prevReading)
                            --tickCount;
                    } else if (pos > MIDPOINT_VALUE) {
                        reading = sensed();
                        if (reading != prevReading)
                            ++tickCount;
                    } else {
                        continue;
                    }
                    prevReading = reading;
                    listener.tick(reading);
                }
            }
        }

        /**
         * Gets a reading from the pseudo-tachometer.
         * @return the reading if successful, otherwise negative the number of
         * milliseconds waited trying to obtain a successful reading
         */
        private int sensed() {
            return sensor.sample() < 500 ? 0 : 1;
//            synchronized (this) {
//                int sample = sensor.sample();
//                int sampleCount = 1;
//                do {
//                    if (sample <= 200 || sample >= 800)
//                        return sample <= 200 ? 1 : 0;
//                    try {
//                        if (currentPosition == MIDPOINT_VALUE)
//                                this.wait();
//                        new java.lang.Object().wait(RESAMPLE_RATE);
//                    } catch (InterruptedException ex)
//                        { /* Motor's speed has changed from zero */ }
//
//                    // 150ms is the absolute maximum that can be waited
//                    // between refreshes
//                } while (++sampleCount * RESAMPLE_RATE < 150 - REFRESH_RATE);
//                return -(sampleCount - 1) * RESAMPLE_RATE - 1;
//            }
        }
    }

}