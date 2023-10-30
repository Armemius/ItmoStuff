package com.armemius.lab7;

import com.armemius.lab7.commands.CommandParser;
import com.armemius.lab7.commands.TreeCommandParser;
import com.armemius.lab7.commands.exceptions.CommandBuildException;
import com.armemius.lab7.io.console.ConsoleOutputHandler;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.io.console.ScannerInputHandler;
import com.armemius.lab7.network.NetworkHandler;
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
    private final static Logger logger = Logger.getLogger(Client.class);
    public static void main(String[] args) {
        logger.info("Starting client");
        InputHandler inputHandler = new ScannerInputHandler();
        OutputHandler outputHandler = new ConsoleOutputHandler();

        try {
            TreeCommandParser parser = new TreeCommandParser(inputHandler, outputHandler);
            ParserRegistry.buildTree(parser);
            NetworkHandler.start(outputHandler);
            ConsoleManager.start(inputHandler, outputHandler, parser);
            NetworkHandler.stop();
        } catch (SocketTimeoutException ex) {
            logger.error("Connection timeout: no further information");
        } catch (ClassNotFoundException ex) {
            logger.error("Received invalid data from server: " + ex.getMessage());
        } catch (CommandBuildException ex) {
            logger.error("Unable to build command tree: " + ex.getMessage());
        } catch (UnknownHostException ex) {
            logger.error("Invalid hostname: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("Critical: " + ex.getMessage());
        } finally {
            inputHandler.close();
        }
    }
}
