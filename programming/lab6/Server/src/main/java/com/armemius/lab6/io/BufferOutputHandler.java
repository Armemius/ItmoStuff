package com.armemius.lab6.io;

/**
 * BufferOutputHandler uses StringBuffer for
 * output, then you can get the output data
 * from the buffer
 */
public class BufferOutputHandler implements OutputHandler {
    StringBuffer buffer;

    /**
     * <b>BufferOutputHandler</b> needs only one value for constructor:
     * @param buffer Link to buffer, where you need to store output
     */
    public BufferOutputHandler(StringBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * @see OutputHandler#println(String)
     * @param line String to output
     */
    @Override
    public void println(String line) {
        buffer.append(line).append("\n");
    }

    /**
     * @see OutputHandler#print(String)
     * @param line String to output
     */
    @Override
    public void print(String line) {
        buffer.append(line);
    }
}
