package com.armemius.lab7.tasks;

import com.armemius.lab7.collection.CollectionManager;
import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.OutputHandler;

import java.time.format.DateTimeFormatter;

@Parametrized
public class InfoTask implements Task, RemoteExecutable {
    /**
     * Action for <b>info</b> command
     * Doesn't receive arguments
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > info
                        This command outputs info about collection
                        PARAMS:
                        -h / --help\t\tShow this menu
                        """);
            return;
        }
        outputHandler.println("Collection statistics:");
        outputHandler.println("Init time:\t" + CollectionManager.getCreationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
        outputHandler.println("Elements:\t" + CollectionManager.getElementsCount());
        outputHandler.println("Type:\t\t" + CollectionManager.getCollectionType());
    }

    @Override
    public void validate(int id, Object[] data) throws IllegalArgumentException {
        if (id != 0) {
            throw new IllegalArgumentException("Invalid operation id");
        }
        if (data.length > 0) {
            throw new IllegalArgumentException("Suspicious number of arguments");
        }
    }

    @Override
    public void execute(int id, Object[] data, OutputHandler outputHandler, String login) {
        outputHandler.println("Collection statistics:");
        outputHandler.println("Init time:\t" + CollectionManager.getCreationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
        outputHandler.println("Elements:\t" + CollectionManager.getElementsCount());
        outputHandler.println("Type:\t\t" + CollectionManager.getCollectionType());
    }
}
