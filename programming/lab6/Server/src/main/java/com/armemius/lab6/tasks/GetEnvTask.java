package com.armemius.lab6.tasks;

import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;

@Parametrized
public class GetEnvTask implements Task {
    /**
     * Action for <b>getenv</b> command
     * Doesn't receive arguments
     * @param context Context of executing command
     */
    @Override
    public void execute (CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        InputHandler inputHandler = context.inputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > getenv
                        This command shows the value of the environment variable 'LAB_5_PATH'
                        PARAMS:
                        -h / --help\tShow this menu
                        """);
            return;
        }
        String path = System.getenv("LAB_5_PATH");
        if (path == null)
            context.outputHandler().println("Environment variable 'LAB_5_PATH' is not set");
        else
            context.outputHandler().println("Environment variable 'LAB_5_PATH' value: '" + path + "'");
    }
}
