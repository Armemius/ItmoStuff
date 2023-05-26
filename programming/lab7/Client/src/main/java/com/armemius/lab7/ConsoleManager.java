package com.armemius.lab7;

import com.armemius.lab7.commands.CommandParser;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.network.NetworkHandler;

/**
 * Singleton class responsible for interactions between user and computer
 * Generates commands tree and then starts console manager that
 * handles all the interactions between user and machine
 * @author Stepanov Arseniy P3109<br>
 */
public class ConsoleManager {
    private static boolean isRunning = false;
    private static CommandParser parser = null;
    private static String lastRequest = "";


    private ConsoleManager() {}

    /**
     * Starts the console manager, to start it you
     * need to specify following parameters:
     * @param inputHandler Class that handles logic for input, read more {@link InputHandler}
     * @param outputHandler Class that handles logic for output, read more {@link OutputHandler}
     * @param parser Class that describes how the parsing will be done, read more {@link CommandParser}
     *
     * Console manager has only one instance, that means you need to stop
     * the manager to run it with new parameters
     */
    public static void start(InputHandler inputHandler, OutputHandler outputHandler, CommandParser parser) {
        if (isRunning)
            return;
        ConsoleManager.parser = parser;
        isRunning = true;
        outputHandler.println("Console manager initialized!\n\rType 'help' for information about commands");
        while (isRunning && inputHandler.hasNextLine()) {
            if (!NetworkHandler.isTimeout()) {
                lastRequest = inputHandler.get();
            }
            ConsoleManager.parser.parse(lastRequest);
        }
        isRunning = false;
    }

    /**
     * Stops running console manager instance
     */
    public static void stop() {
        if (!isRunning)
            return;
        isRunning = false;
    }
}