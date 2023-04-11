package com.armemius.lab6.io;

/**
 * <b>InputHandler</b> handles input in
 * the program in different approaches
 */
public interface InputHandler {

    /**
     * <b>get</b> function is used to receive
     * a line of input from user or any other source
     * @return Value to be input
     */
    String get();

    /**
     * <b>hasNext</b> function is used to receive
     * a line of input from user or any other source
     * @return Boolean that shows if handler has data to proceed
     */
    boolean hasNextLine();

    /**
     * <b>close</b> function is used to close
     * InputHandler if needed (usually for networking
     * or file streams)
     */
    void close();
}
