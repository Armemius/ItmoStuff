package com.armemius.lab6.tasks;

import com.armemius.lab6.ConsoleManager;
import com.armemius.lab6.collection.CollectionManager;
import com.armemius.lab6.collection.exceptions.CollectionFileException;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandRuntimeException;
import com.armemius.lab6.commands.params.Param;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "f", name = "force"),
                @Param(letter = "s", name = "save")
        }
)
public class ExitTask extends InputTask {
    /**
     * Action for <b>exit</b> command
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
                        > exit
                        Command responsive for stopping the program
                        PARAMS:
                        -h / --help\tShow this menu
                        -s / --save\tSaves the collection before exit (won't continue if save was unsuccessful and --force is not set)
                        -f / --force\tSkips all the confirmation steps
                        """);
            return;
        }
        if (context.params().contains("s")) {
            try {
                outputHandler.println("Saving the collection");
                CollectionManager.save();
                outputHandler.println("Done");
            }
            catch (CommandRuntimeException ex) {
                if (!context.params().contains("f")) {
                    outputHandler.println("Error while saving the collection");
                    outputHandler.println("Operation aborted");
                    return;
                }
            }
            catch (CollectionFileException ex) {
                throw new CommandRuntimeException(ex.getMessage());
            }
        }
        if (!context.params().contains("f") && !context.params().contains("s")) {
            outputHandler.print("Are you sure, all unsaved data will be lost? (Empty input if yes) ");
            String response = inputHandler.get();
            if (!response.isBlank()) {
                outputHandler.println("Operation aborted");
                return;
            }
        }
        outputHandler.println("Stopping");
        ConsoleManager.stop();
    }
}
