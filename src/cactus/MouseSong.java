package cactus;

import com.ridgesoft.intellibrain.IntelliBrain;
import com.ridgesoft.io.DisplayOutputStream;
import com.ridgesoft.io.Speaker;
import com.ridgesoft.robotics.AnalogInput;
import com.ridgesoft.robotics.RangeFinder;
import com.ridgesoft.robotics.Servo;
import com.ridgesoft.robotics.sensors.SharpGP2D12;

/**
 * A class that makes Mouse2.0 sing nyan cat and move while avoiding obstacles.
 * @author Jacob Van Buren
 * @author Nicolas Firbas
 */
public final class MouseSong {

	public static final AnalogInput leftWheelInput = IntelliBrain.getAnalogInput(4);
	public static final AnalogInput rightWheelInput = IntelliBrain.getAnalogInput(5);

    public static final int
        // Notes and frequencies
        C = 523, C1 = 554,
        D = 587, D1 = 311,
        E = 330,
        F = 698, F1 = 370,
        G = 784, G1 = 415,
        A = 440, A1 = 466,
        B = 247,
        //Note durations (quarter note / half note)
        Q = 400/3, H = 800/3;

	public static final int[] notes =   { F, G,C1, D, C,C1, C,A1,A1, C,C1, C,A1, C, D, F, G, D, F, C, D,A1, C,A1, D, F, G, D, F, C, D,A1,C1, D,C1, C,A1, C};
	public static final int[] lengths = { H, H, Q, H, Q, Q, Q, H, H, H, H, Q, Q, Q, Q, Q, Q, Q, Q, Q, Q, Q, Q, Q, Q, H, H, Q, Q, Q, Q, Q, Q, Q, Q, Q, Q, Q};

    public static final DisplayOutputStream display = new DisplayOutputStream(IntelliBrain.getLcdDisplay());

    public static final Servo leftServo = IntelliBrain.getServo(1);
    public static final Servo rightServo = IntelliBrain.getServo(2);

    public static final Motor leftMotor = new Motor(leftServo);
    public static final Motor rightMotor = new Motor(rightServo, true);

    public static final RangeFinder leftIR = new SharpGP2D12(IntelliBrain.getAnalogInput(1), null);
    public static final RangeFinder rightIR = new SharpGP2D12(IntelliBrain.getAnalogInput(2), null);

    public static final Speaker buzzer = IntelliBrain.getBuzzer();
    
    public static final int MAX_ROT_COUNT = 20; // the maximum number of turns before cactus turns around

    public static void main(String args[]) {
        try {
            run();
        } catch (Throwable t) {
            print(t.getMessage());
        }
    }

    public static void run() {
        printWelcome();
        sleepFor(3000);

        // Sing nyan cat
        //play(notes, lengths);


        // Movement routine
        float distL = 0xDEADBEEF, distR = 0xDEADBEEF;
		leftIR.ping();
		rightIR.ping();

        int rotCount = 0;
         
        while (true) {
            leftIR.ping();
			distL = leftIR.getDistanceCm();
            rightIR.ping();
            distR = rightIR.getDistanceCm();
            if (distL < 0)
                distL = Float.POSITIVE_INFINITY;
            if (distR < 0)
                distR = Float.POSITIVE_INFINITY;

            if (distL > 20 && distR > 20) {
				rightMotor.forward();
                leftMotor.forward();
                rotCount = rotCount > 0 ? rotCount - 1 : 0;
            } else if (distL < 10 || distR < 10) {
                ++rotCount;
                if (distL < distR) {
                    leftMotor.stop();
                    rightMotor.backward();
                } else {
                    leftMotor.backward();
                    rightMotor.stop(); 
                }
                sleepFor(500);
             
            } else {
                ++rotCount;
                if (distL < distR) {
                    leftMotor.forward();
                    rightMotor.backward();
                } else {
                    leftMotor.backward();
                    rightMotor.forward();
                }
                sleepFor(25);
            }
            
            if (rotCount > MAX_ROT_COUNT) {
                turnAround();
                rotCount = 0;
            }
            
            // } else {
                // leftMotor.backward();
                // rightMotor.backward(); 
            // }
            
			//print("Left:  " + (short)distL + "cm");
            // sleepFor(200);
			// print("################");
			// sleepFor(200);
			//print("Right: " + (short)distR + "cm");
			// sleepFor(200);
            // print("################");
            sleepFor(75);
        }
    }

    /**
     * Play a sequence of notes.
     * @param notes The frequencies to play
     * @param dur The durations of the notes
     */
    public static void play(int[] notes, int[] dur) {
        if (notes.length != dur.length)
            throw new IllegalArgumentException("Arrays must be of the same size");
        for (int cactus = 0; cactus < notes.length; ++cactus)
            buzzer.play(notes[cactus], dur[cactus]);
    }

    public static void sleepFor(long milliseconds) {
        long end = System.currentTimeMillis() + milliseconds;
        while (System.currentTimeMillis() < end);
    }

    public static void print(String s, Character end) {
        try {
            if (s != null)
                display.write(s.getBytes());
            if (end != null)
                display.write((int)end.charValue());
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        if (s != null || end != null)
            display.flush();
    }

    public static void print(String s) {
        try {
            if (s != null)
                display.write(s.getBytes());
            display.write((int)'\n');
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        if (s != null)
            display.flush();
    }

    public static void print() {
        display.write((int)'\n');
        display.flush();
    }

    public static void turnAround() {
        print("I'm turning this");
        print("  robot around! ");
        leftMotor.backward();
        rightMotor.forward();
        do {
            leftIR.ping();
            rightIR.ping();
        } while (leftIR.getDistanceCm() < 30
              && rightIR.getDistanceCm() < 30);
        printWelcome();
    }
    
    public static void printWelcome() {
        // Greet the user
        print("  Hello World,");
        print("  I am Cactus!");
    }
}

class Motor {
    public static final int SERVO_MAX_VALUE = 100;
    public static final int MIDPOINT_VALUE = SERVO_MAX_VALUE / 2;

    private final boolean invert;
    protected final Servo s;

    public Motor(Servo s) {
        this(s, false);
    }

    public Motor(Servo s, boolean invert) {
        this.s = s;
        this.invert = invert;
    }

    public synchronized void forward() {
        this.move(SERVO_MAX_VALUE);
    }

    public synchronized void forward(int value) {
        if (value > MIDPOINT_VALUE)
            throw new IllegalArgumentException("value must be less than " + SERVO_MAX_VALUE / 2);
        if (value < 0)
            throw new IllegalArgumentException("Value must be positive");

        this.move(value + MIDPOINT_VALUE);
    }

    public synchronized void backward() {
        this.move(0);
    }

    public synchronized void backward(int value) {
        if (value > MIDPOINT_VALUE)
            throw new IllegalArgumentException("value must be less than " + SERVO_MAX_VALUE / 2);
        if (value < 0)
            throw new IllegalArgumentException("Value must be positive");

        this.move(MIDPOINT_VALUE - value);
    }

    public synchronized void move(int value) {
        if (value > SERVO_MAX_VALUE)
            throw new IllegalArgumentException("value must be less than " + SERVO_MAX_VALUE);
        if (value < 0)
            throw new IllegalArgumentException("Value must be positive");
        s.setPosition(invert ? SERVO_MAX_VALUE - value : value);
    }

    public synchronized void stop() {
        this.move(MIDPOINT_VALUE);
        s.off();
    }
}


