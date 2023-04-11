package com.armemius.lab6.tasks;

import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.params.Param;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;
import com.armemius.lab6.network.*;

import java.io.IOException;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "f", name = "force")
        }
)
public class ClearTask extends InputTask {
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

        var payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST, new TaskContextData(this.getClass(), 0));
        NetworkHandler.send(payload);
    }
}
