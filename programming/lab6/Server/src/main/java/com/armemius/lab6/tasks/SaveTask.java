package com.armemius.lab6.tasks;

import com.armemius.lab6.collection.CollectionManager;
import com.armemius.lab6.collection.exceptions.CollectionFileException;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandRuntimeException;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.OutputHandler;

@Parametrized
public class SaveTask implements Task {
    /**
     * Action for <b>save</b> command
     * Doesn't receive arguments
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > save
                        Command responsive for saving collection
                        PARAMS:
                        -h / --help\tShow this menu
                        """);
            return;
        }
        try {
            outputHandler.println("Saving the collection");
            CollectionManager.save();
            outputHandler.println("Done");
        } catch (CollectionFileException ex) {
            throw new CommandRuntimeException(ex.getMessage());
        }

    }
}
