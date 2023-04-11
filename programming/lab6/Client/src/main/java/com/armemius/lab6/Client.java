package com.armemius.lab6;

import com.armemius.lab6.commands.CommandParser;
import com.armemius.lab6.commands.TreeCommandParser;
import com.armemius.lab6.commands.exceptions.CommandBuildException;
import com.armemius.lab6.io.console.ConsoleOutputHandler;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;
import com.armemius.lab6.io.console.ScannerInputHandler;
import com.armemius.lab6.network.NetworkHandler;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Main class for lab 6
 * Loads collection into collection manager, generates
 * commands tree and then starts console manager
 * @see TreeCommandParser
 * @see ConsoleManager#start(InputHandler, OutputHandler, CommandParser)
 * @author Stepanov Arseniy P3109
 */
public class Client {
    private final static Logger LOGGER = Logger.getLogger(Client.class);
    public static void main(String[] args) {
        LOGGER.info("Starting client");
        InputHandler inputHandler = new ScannerInputHandler();
        OutputHandler outputHandler = new ConsoleOutputHandler();

        try {
            TreeCommandParser parser = new TreeCommandParser(inputHandler, outputHandler);
            ParserRegistry.buildTree(parser);
            NetworkHandler.start(outputHandler);
            ConsoleManager.start(inputHandler, outputHandler, parser);
            NetworkHandler.stop();
        } catch (SocketTimeoutException ex) {
            LOGGER.error("Connection timeout: no further information");
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Received invalid data from server: " + ex.getMessage());
        } catch (CommandBuildException ex) {
            LOGGER.error("Unable to build command tree: " + ex.getMessage());
        } catch (UnknownHostException ex) {
            LOGGER.error("Invalid hostname: " + ex.getMessage());
        } catch (IOException ex) {
            LOGGER.error("Critical: " + ex.getMessage());
        } finally {
            inputHandler.close();
        }
    }
}
