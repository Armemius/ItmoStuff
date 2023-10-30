package com.armemius.lab7;

import com.armemius.lab7.collection.CollectionManager;
import com.armemius.lab7.collection.DatabaseManager;
import com.armemius.lab7.commands.CommandParser;
import com.armemius.lab7.commands.TreeCommandParser;
import com.armemius.lab7.commands.exceptions.CommandBuildException;
import com.armemius.lab7.io.console.ConsoleOutputHandler;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.io.console.ScannerInputHandler;
import com.armemius.lab7.network.NetworkHandler;
import com.jcraft.jsch.JSchException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

/**
 * Main class for lab 7
 * Loads collection into collection manager, generates
 * commands tree and then starts console manager
 * @see TreeCommandParser
 * @see ConsoleManager#start(InputHandler, OutputHandler, CommandParser)
 * @see CollectionManager#load()
 * @author Stepanov Arseniy P3109
 */
public class Server {
    private final static Logger logger = Logger.getLogger(Server.class);
    public static void main(String[] args) throws IOException {
        logger.info("Starting server");
        InputHandler inputHandler = new ScannerInputHandler();
        OutputHandler outputHandler = new ConsoleOutputHandler();
        try {
            TreeCommandParser parser = new TreeCommandParser(inputHandler, outputHandler);
            ParserRegistry.buildTree(parser);
            DatabaseManager.connect();
            CollectionManager.load();
            NetworkHandler.start();
            ConsoleManager.start(inputHandler, outputHandler, parser);
        } catch (IllegalArgumentException ex) {
            logger.error("Unable to create server: " + ex.getMessage());
        } catch (CommandBuildException ex) {
            logger.error("Unable to create command tree");
        } catch (UnknownHostException ex) {
            logger.error("Invalid hostname: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("Critical: " + ex.getMessage());
        } catch (SQLException | JSchException ex) {
            logger.error("Error while connecting to DB");
            ex.printStackTrace();
        } catch (ClassNotFoundException
                 | InstantiationException
                 | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } finally {
            NetworkHandler.stop();
            inputHandler.close();
            DatabaseManager.close();
        }
    }
}
