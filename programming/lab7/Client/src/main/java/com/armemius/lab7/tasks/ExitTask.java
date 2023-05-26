package com.armemius.lab7.tasks;

import com.armemius.lab7.ConsoleManager;
import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.params.Param;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;

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
                        -f / --force\tSkips all the confirmation steps
                        """);
            return;
        }
        if (!context.params().contains("f")) {
            outputHandler.print("Are you sure? (Empty input if yes) ");
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
