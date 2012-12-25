
package api;

import com.ridgesoft.io.I2CMaster;
import java.io.IOException;

/**
 * A class that abstracts the PixArt IR sensor/camera (taken from a Wii(r)
 * remote) attached to an I2C port on Cactus.
 * @author Jacob
 * @version 0.0.3
 * @since 2.0.1
 */
public final class IRCamera {
    /** Mode number for basic mode. */
    private static final byte BASIC_MODE = 0x01;
    /** Mode number for extended mode. */
    private static final byte EXTENDED_MODE = 0x03;
    /** Mode number for full mode. */
    private static final byte FULL_MODE = 0x05;
    /** 
     * Data to send that initializes the camera.
     * taken from http://procrastineering.blogspot.com/2008_09_01_archive.html
     */
    private static final byte[][] INIT_DATA = {
        // ?
        {0x30, 0x01},
        // Sensitivity part 1
        {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, ~0x6F},
        // Sensitivity part 2
        {0x07, 0x00, 0x41},
        // Sensitivity part 3
        {0x1A, 0x40, 0x00},
        // Set mode
        {0x33, FULL_MODE},
        // ?
        {0x30, 0x08},
    };
    
    /** Read/write frequency in hertz. */
    private static final int frequency = 400000; // 400kHz

    /** The address to send to the I2C bus when writing. */
    private final int slaveWriteAddress = 0xB0;
    
    /** The address to send to the I2C bus when reading. */
    private final int slaveReadAddress = 0xB1;
    
    /** first 18-byte read buffer for full mode. */
    private final static byte[] rb1 = new byte[18];
    /** second 18-byte read buffer for full mode. */
    private final static byte[] rb2 = new byte[18];
    
    /** 1-byte write buffer (yes i know this is silly). */
    private final static byte[] wb = {0x37}; // magic number!
    
    /** I2C port controller. */
    private final I2CMaster master;
    
    public IRCamera(I2CMaster master) {
        
        // Set the master (from the IntelliBrain controller)
        this.master = master;
        try {
            init(); 
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
    
    /** Initialize the camera and be ready to read data. */
    private void init() throws IOException, InterruptedException {
        // http://procrastineering.blogspot.com/2008_09_01_archive.html
        
        master.setFrequency(frequency);
        
        for (byte[] data : INIT_DATA) {
            write(data);
            Thread.sleep(0, 100000); // wait 100us
        }
        
    }
    
    
    
    private void read() throws IOException, InterruptedException {
        write(wb);
        Thread.sleep(0, 25000); // Wait 25us
        read(rb1);
        Thread.sleep(1); // Wait 1ms for good measure
        read(rb2);
    }
    /**
     * Reads data into the read buffer.
     * @param rb The buffer to populate
     */
    private void read(byte[] rb) throws IOException {
        master.transfer(slaveReadAddress, null, rb);
    }
    
    private void write(byte[] wb) throws IOException {
        master.transfer(slaveWriteAddress, wb, null);
    }
}
