package com.armemius.lab6.io.console;

import com.armemius.lab6.io.OutputHandler;

/**
 * ConsoleOutputHandler uses System.out to
 * output data
 */
public class ConsoleOutputHandler implements OutputHandler {
    /**
     * @see OutputHandler#println(String)
     * @param line String to output
     */
    @Override
    public void println(String line) {
        System.out.println(line);
    }

    /**
     * @see OutputHandler#print(String)
     * @param line String to output
     */
    @Override
    public void print(String line) {
        System.out.print(line);
    }
}
