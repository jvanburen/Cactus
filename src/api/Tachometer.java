
package api;

import com.ridgesoft.intellibrain.IntelliBrain;
import com.ridgesoft.robotics.AnalogInput;

/**
 *
 * @author Jacob
 */
public class Tachometer extends Thread {
    public static final int TICKS_PER_ROTATION = 16;
    public static final int DEFAULT_REFRESH_RATE = 100;
    
    public final int refreshRate;
    
    private Motor m;
    
    private int side;
    
    private final AnalogInput sensor;
    
    private volatile int tickCount = 0;
    
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
        this.side = m.side();
        this.prevReading = ((Tachometer)this).sensed();
        this.start();
    }
    
    public int ticks() {
        return tickCount;
    }

    @Override
    public void run() {
        synchronized(this) {
            while(true) {
                
                try {
                    CactusBase.print("Got into the run() method");
                    this.sleep(refreshRate);
                } catch (InterruptedException ex) {
                    // Do nothing
                }
                boolean detected = sensed();
                if (detected != prevReading) {
                    if (m.direction()) //forward
                        ++tickCount;
                    else 
                        --tickCount;
                    prevReading = detected;
                }
                
            }
        }
    }
    
    public boolean sensed() {
        int sample = sensor.sample();
        while (sample < 800 && sample > 200) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
            sample = sensor.sample();
        }
        return sample <= 200;
    }
    
    
    
    
}
