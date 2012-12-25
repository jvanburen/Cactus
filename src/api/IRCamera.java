
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
        {0x33, EXTENDED_MODE},
        // ?
        {0x30, 0x08},
    };

    /** Read/write frequency in hertz. */
    private static final int frequency = 400000; // 400kHz

    /** The address to send to the I2C bus when writing. */
    private final int slaveWriteAddress = 0xB0;

    /** The address to send to the I2C bus when reading. */
    private final int slaveReadAddress = 0xB1;

    /** first 8-byte read buffer for ext mode. */
    private final static byte[] rb1 = new byte[18];
    /** second 4-byte read buffer for ext mode. */
    private final static byte[] rb2 = new byte[18];

    /** 1-byte write buffer (yes i know this is silly). */
    private final static byte[] wb = {0x37}; // magic number!

    /** I2C port controller. */
    private final I2CMaster master;

    public IRCamera(I2CMaster master) throws IOException {

        // Set the master (from the IntelliBrain controller)
        this.master = master;
        try {
            init();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
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

    /**
     * Fetch data from the camera and update an array of 4 blobs.
     * @param blobs The blobs to update
     */
    public void detect(Blob[] blobs) throws IOException {
        read();
        for (int i = 0; i < 3; ++i) {
            int off = i * 3; // The buffer offset
            blobs[i].x = 0;
            blobs[i].x |= rb1[off + 0];
            blobs[i].x |= (rb1[off + 2] & 0b00110000) << 4; // get the top 2 bits
            blobs[i].y = 0;
            blobs[i].y |= rb1[off + 1];
            blobs[i].y |= (rb1[off + 2] & 0b11000000) << 2; // get the top 2 bits
            blobs[i].size =(byte) (rb1[off + 2] & 0b00001111);
        }
        blobs[3].x = 0;
        blobs[3].x |= rb2[0];
        blobs[3].x |= (rb2[2] & 0b00110000) << 4; // get the top 2 bits
        blobs[3].y = 0;
        blobs[3].y |= rb2[1];
        blobs[3].y |= (rb2[2] & 0b11000000) << 2; // get the top 2 bits
        blobs[3].size =(byte) (rb2[2] & 0b00001111);
    }

    /**
     * Populates the read buffers with new data.
     * @throws IOException if the thread is interrupted or the read fails
     */
    private void read() throws IOException {
        try {
            write(wb);
            Thread.sleep(0, 25000); // Wait 25us
            read(rb1);
            Thread.sleep(0, 380000); // Wait 380us
            read(rb2);
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
    }
    /**
     * Reads data into the read buffer.
     * @param rb The buffer to populate
     * @throws IOException If the read fails
     */
    private void read(byte[] rb) throws IOException {
        master.transfer(slaveReadAddress, null, rb);
    }

    /**
     * Writes the buffer to the camera
     * @param wb The buffer to write
     * @throws IOException If the write fails
     */
    private void write(byte[] wb) throws IOException {
        master.transfer(slaveWriteAddress, wb, null);
    }
}
