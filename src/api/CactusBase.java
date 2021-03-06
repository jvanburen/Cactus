package api;

import com.ridgesoft.intellibrain.IntelliBrain;
import com.ridgesoft.io.DisplayOutputStream;
import com.ridgesoft.io.Speaker;
import com.ridgesoft.robotics.AnalogInput;
import com.ridgesoft.robotics.RangeFinder;
import com.ridgesoft.robotics.sensors.SharpGP2D12;
import java.io.IOException;

/**
 * The base abstraction for all code to be built onto Cactus the robot.
 * @author Jacob Van Buren
 * @version 2.1.0
 * @since 2.0.0
 */
public abstract class CactusBase extends Robot {
    /** The port number of {@code leftWheelInput}. */
    public static final byte LEFT_WHEEL_INPUT_PORT = 4;
    /** The port number of {@code rightWheelInput}. */
    public static final byte RIGHT_WHEEL_INPUT_PORT = 5;
    /** The port number of {@code leftIR}. */
    public static final byte LEFT_IR_PORT = 1;
    /** The port number of {@code rightIR}. */
    public static final byte RIGHT_IR_PORT = 2;
    /** The port number of the left wheel tick sensor. */
    public static final byte LEFT_TACHO_PORT = 4;
    /** The port number of the right wheel tick sensor. */
    public static final byte RIGHT_TACHO_PORT = 5;
    /** The port number of the laser diode. */
    public static final byte LASER_PORT = 1;


    /** The Infrared proximity sensor that tracks Cactus' left wheel. */
    public static final AnalogInput leftWheelInput
            = IntelliBrain.getAnalogInput(LEFT_WHEEL_INPUT_PORT);

    /** The Infrared proximity sensor that tracks Cactus' right wheel. */
    public static final AnalogInput rightWheelInput
            = IntelliBrain.getAnalogInput(RIGHT_WHEEL_INPUT_PORT);

    /** The display screen on the top of Cactus. */
    public static final DisplayOutputStream display
            = new DisplayOutputStream(IntelliBrain.getLcdDisplay());

    /** Cactus' left continuous servomotor. */
    public static final Motor leftMotor = new Motor(1, Motor.LEFT);

    /** Cactus' right continuous servomotor. */
    public static final Motor rightMotor = new Motor(2, Motor.RIGHT);

    /** The Infrared Range Sensor that detects objects to Cactus' left. */
    public static final RangeFinder leftIR
            = new SharpGP2D12(IntelliBrain.getAnalogInput(LEFT_IR_PORT), null);

    /** The Infrared Range Sensor that detects objects to Cactus' right. */
    public static final RangeFinder rightIR
            = new SharpGP2D12(IntelliBrain.getAnalogInput(RIGHT_IR_PORT), null);

    /** The laser diode on the front of Cactus. */
    public static final LaserDiode laser = new LaserDiode(LASER_PORT);

    /** The "Buzzer" on the IntelliBrain PCB. */
    public static final Speaker buzzer = IntelliBrain.getBuzzer();

    /** The PixArt IR camera on Cactus. */
    public static final IRCamera camera;
    // initialize the camera safely
    static {
        IRCamera cInit;
        try {
            cInit = new IRCamera(IntelliBrain.getI2CMaster());
        } catch (IOException ex) {
            cInit = null;
            System.err.println("Camera not initialized");
        }
        camera = cInit;
    }

    /**
     * The display to which data from the print methods will go.
     * Defaults to {@code display}.
     */
    static volatile DisplayOutputStream stdout = display;

    /**
     * Gets the distance to the nearest object as determined by leftIR.
     * @throws SensorFailure If no reading can be made.
     * @return The distance (in cm) to the nearest object on the left
     */
    public static float leftCM() throws SensorFailure {
        leftIR.ping();
        float ret = leftIR.getDistanceCm();
        // SensorFailure if failed reading
        if (ret == -1)
            throw SENSOR_FAIL;
        return ret;
    }

    /**
     * Gets the distance to the nearest object as determined by rightIR.
     * @throws SensorFailure If no reading can be made.
     * @return The distance (in cm) to the nearest object on the right.
     */
    public static float rightCM() throws SensorFailure {
        rightIR.ping();
        float ret = rightIR.getDistanceCm();
        // SensorFailure if failed reading
        if (ret == -1)
            throw SENSOR_FAIL;
        return ret;
    }

    /**
     * Plays a sequence of notes.
     * @param notes The frequencies to play
     * @param dur The durations of the notes
     */
    public static void play(int[] notes, int[] dur) {
        if (notes.length != dur.length)
            throw new IllegalArgumentException(
                    "Arrays must be of the same size");
        for (int cactus = 0; cactus < notes.length; ++cactus)
            buzzer.play(notes[cactus], dur[cactus]);
    }

    /**
     * Prints the specified string to {@code stdout} followed by end.
     * @param s The String to print.
     * @param end The Character to append to the end.
     */
    public static void print(String s, Character end) {
        boolean flushBuffer = false;
        try {
            if (s != null) {
                flushBuffer = true;
                stdout.write(s.getBytes());
            }
            if (end != null) {
                flushBuffer = true;
                stdout.write((int) end.charValue());
            }
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (flushBuffer)
                stdout.flush();
        }
    }

    /**
     * Prints the specified string to {@code stdout} followed by a newline.
     * @param s The String to print.
     */
    public static void print(String s) {
        try {
            if (s != null)
                stdout.write(s.getBytes());
            stdout.write((int) '\n');
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (s != null)
                stdout.flush();
        }
    }

    /** Prints a newline to {@code stdout}. */
    public static void print() {
        stdout.write((int) '\n');
        stdout.flush();
    }

    /** Prints a welcome message to {@code stdout}. */
    public static void printWelcome() {
        // Greet the user
        print("  Hello World,");
        print("  I am Cactus!");
    }

}
