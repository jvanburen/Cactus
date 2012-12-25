
package api;

/**
 * A class to represent a blob as detected by the IR camera in full mode.
 * @author Jacob Van Buren
 * @version 0.0.1
 * @since 2.0.2
 */
public class Blob {
    /** The x-coordinate of the blob. **/
    public char x;
    
    /** The y-coordinate of the blob. **/
    public char y;
    
    /** The size of the blob. */
    public byte size;
    
    /** Part of the bounding box of the blob. */
    public byte xMin;
    
    /** Part of the bounding box of the blob. */
    public byte xMax;
    
    /** Part of the bounding box of the blob. */
    public byte yMin;
    
    /** Part of the bounding box of the blob. */
    public byte yMax;

    /** The intensity of the blob. */
    public char intensity;
    
    
}
