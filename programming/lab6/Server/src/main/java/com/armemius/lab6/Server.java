package com.armemius.lab6;

import com.armemius.lab6.collection.CollectionManager;
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
import java.net.UnknownHostException;

/**
 * Main class for lab 5
 * Loads collection into collection manager, generates
 * commands tree and then starts console manager
 * @see TreeCommandParser
 * @see ConsoleManager#start(InputHandler, OutputHandler, CommandParser)
 * @see CollectionManager#load()
 * @author Stepanov Arseniy P3109
 */
public class Server {
    private final static Logger logger = Logger.getLogger(Server.class);
    public static void main(String[] args) {
        logger.info("Starting server");
        InputHandler inputHandler = new ScannerInputHandler();
        OutputHandler outputHandler = new ConsoleOutputHandler();
        CollectionManager.load();

        try {
            TreeCommandParser parser = new TreeCommandParser(inputHandler, outputHandler);
            ParserRegistry.buildTree(parser);
            NetworkHandler.start();
            ConsoleManager.start(inputHandler, outputHandler, parser);
            NetworkHandler.stop();
        } catch (IllegalArgumentException ex) {
            logger.error("Unable to create server: " + ex.getMessage());
        } catch (CommandBuildException ex) {
            logger.error("Unable to create command tree");
        } catch (UnknownHostException ex) {
            logger.error("Invalid hostname: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("Critical: " + ex.getMessage());
        } finally {
            inputHandler.close();
        }
    }
}
