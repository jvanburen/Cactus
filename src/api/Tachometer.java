
package api;

import com.ridgesoft.intellibrain.IntelliBrain;
import com.ridgesoft.robotics.AnalogInput;

/**
 *
 * @author Jacob
 */
public class Tachometer extends Thread {
    /** The number of ticks per 1 rotation of the wheel. */
    public static final int TICKS_PER_ROTATION = 16;

    /** The default number of milliseconds to wait per refresh. */
    public static final int DEFAULT_REFRESH_RATE = 100;

    /** The number of milliseconds to wait after a failed refresh. */
    private static final int RESAMPLE_RATE = 20;

    /** How many milliseconds to wait per refresh. */
    public final int refreshRate;

    /** The motor to listen to. */
    private Motor m;

    /** The last recorded speed of the motor. */
    private volatile int motorSpeed;

    /** The pseudo-tachometer sensor. */
    private final AnalogInput sensor;

    /** The number of ticks FORWARD the wheel has spun. */
    private volatile int tickCount = 0;

    /** The last recorded reading of the sensor. */
    private boolean prevReading;

    public Tachometer(Motor m) {
        this(m, DEFAULT_REFRESH_RATE);
    }

    public Tachometer(Motor m, int refreshRate) {
        if (refreshRate <= 0)
            throw new IllegalArgumentException("Refresh rate must be positive");
        if (m == null)
            throw new IllegalArgumentException("Cannot pass a null motor arg");
        this.m = m;
        if (this.m.side() == Motor.LEFT)
            this.sensor = IntelliBrain.getAnalogInput(CactusBase.LEFT_TACHO_PORT);
        else
            this.sensor = IntelliBrain.getAnalogInput(CactusBase.RIGHT_TACHO_PORT);
        this.refreshRate = refreshRate;
        this.prevReading = ((Tachometer)this).sensed();
    }

    public int ticks() {
        return tickCount;
    }

    @Override
    public void run() {
        synchronized(this) {
            while(true) {
                while (motorSpeed == 0)
                try {
                    new java.lang.Object().wait(refreshRate);
                } catch (InterruptedException ex) {
                    // Do nothing
                }
                boolean detected = sensed();
                if (detected != prevReading) {
                    if (m.speed() >= 0) //forward
                        ++tickCount;
                    else
                        --tickCount;
                    prevReading = detected;
                }

            }
        }
    }

    private boolean sensed() {
        synchronized (this) {
            int sample = sensor.sample();
            int sampleCount = 1;
            do {
            if (sample <= 200 || sample >= 800)
                return sample <= 200;
                try {
                    new java.lang.Object().wait(RESAMPLE_RATE);
                } catch (InterruptedException ex)
                    { /* Do Nothing. */ }

            } while (++sampleCount * RESAMPLE_RATE < this.refreshRate);
            throw new RuntimeException("Distance could not be determined after "
                    + (sampleCount - 1) + " tries" );
        }
    }



}
