
package api;

import com.ridgesoft.io.I2CMaster;

/**
 * A class that abstracts the PixArt IR sensor/camera (taken from a Wii(r)
 * remote) attached to an I2C port on Cactus.
 * @author Jacob
 * @version 0.0.3
 * @since 2.0.1
 */
public final class IRCamera {
    /** 
     * Data to send that initializes the camera.
     * {@link 
     * taken from http://procrastineering.blogspot.com/2008_09_01_archive.html
     */
    private static final byte[][] INIT_DATA = {
            {~0x31, 0x30, 0x01},
            {~0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, ~0x6F},
            {~0x31, 0x07, 0x00, 0x41},
            {~0x31, 0x1A, 0x40, 0x00},
            {~0x31, 0x33, 0x03},
            {~0x31, 0x30, 0x08},
        };
    
    /** Read/write frequency in hertz. */
    private final int frequency;
    
    /** The address of the IR Camera. */
    private final int cameraAddress = 0xB0;
    
    /** The address to send to the I2C bus. */
    private final int slaveAddress = 0xB0;
    
    /** Write buffer (to avoid reallocating it). */
    private byte[] writeBuffer;
    /** Read buffer (to avoid reallocating it). */
    private byte[] readBuffer;
    
    /** I2C port controller. */
    private I2CMaster master;
    
    public IRCamera(I2CMaster master) {
        this.master = master;
        this.frequency = 115200;
        
    }
    
    
    
    /** Initialize the camera and be ready to read data. */
    private void init() {
        // http://procrastineering.blogspot.com/2008_09_01_archive.html
        // Since java hates unsigned bytes,
        // for any bytes over 127 take the compliment
        
        
        
    }
    
    /**
     * Writes to the I2C bus
     * @param type The type of the write to perform
     * @param data The data to write
     */
    private void write(int type, int... data) {
        
    }
}
