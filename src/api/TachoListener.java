
package api;

/**
 * Something that detects changes to the motor
 * @author Jacob
 */
public interface TachoListener {
    /**
     * Called when the wheel makes 1/{@value Motor#TICKS_PER_ROTATION} of a
     * revolution.
     * @param power the speed of the motor (-100% to 100%)
     */
    public void tick(int ticks);
}
