/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

/**
 * An exception class for Cactus that does not create a detailed call stack.
 * Such a class is warranted because of limited call stack space.
 * @author Jacob
 */
public class CactusError extends Exception {

    /**
     * Creates a new instance of
     * <code>CactusError</code> without detail message.
     */
    public CactusError() {
        
    }

    /**
     * Constructs an instance of
     * <code>CactusError</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CactusError(String msg) {
        super(msg);
    }
    
    @Override
    public Throwable fillInStackTrace() {
        
        return this;
        
    }
    
    
    
}
