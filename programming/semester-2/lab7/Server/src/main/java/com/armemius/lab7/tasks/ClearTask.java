package com.armemius.lab7.tasks;

import com.armemius.lab7.collection.CollectionManager;
import com.armemius.lab7.collection.DatabaseManager;
import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.params.Param;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "f", name = "force")
        }
)
public class ClearTask extends InputTask implements RemoteExecutable {
    /**
     * Action for <b>clear</b> command
     * Doesn't receive arguments
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        InputHandler inputHandler = context.inputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > clear
                        Command responsive for clearing the collection
                        PARAMS:
                        -h / --help\tShow this menu
                        -f / --force\tSkip all confirmation steps
                        """);
            return;
        }
        if (!context.params().contains("f")) {
            outputHandler.print("Are you sure, all unsaved data will be lost? (Empty input if yes) ");
            String response = inputHandler.get();
            if (!response.isBlank()) {
                outputHandler.println("Operation aborted");
                return;
            }
        }
        outputHandler.println("Clearing the collection");
        CollectionManager.clear(DatabaseManager.ADMIN_LOGIN);
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
        outputHandler.println("Clearing the collection");
        CollectionManager.clear(login);
    }
}
