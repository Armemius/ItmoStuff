package com.armemius.lab6.tasks;

import com.armemius.lab6.collection.CollectionManager;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandArgumentException;
import com.armemius.lab6.commands.params.Param;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "r", name = "regex")
        }
)
public class FilterTask implements Task, RemoteExecutable {
    /**
     * Action for <b>filter</b> command
     * Receives one argument
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        InputHandler inputHandler = context.inputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > filter <pattern>
                        This command allows you to get all the groups where name contains specified substring
                        PARAMS:
                        -h / --help\tShow this menu
                        -r / --regex\tSubstring will be interpreted as regex pattern
                        """);
            return;
        }
        if (context.args().size() < 1)
            throw new CommandArgumentException("Argument wasn't provided");
        String substring = context.args().get(0);
        outputHandler.println("Filtering results" + (context.params().contains("r") ? "(regex)" : ""));
        if (context.params().contains("r")) {
            for (var it : CollectionManager.filterContentRegex(substring)) {
                outputHandler.println(it.toString());
            }
        } else {
            for (var it : CollectionManager.filterContent(substring)) {
                outputHandler.println(it.toString());
            }
        }
    }

    @Override
    public void validate(int id, Object[] data) throws IllegalArgumentException {
        if (id != 0 && id != 1) {
            throw new IllegalArgumentException("Invalid operation id");
        }
        if (data.length != 1) {
            throw new IllegalArgumentException("Suspicious number of arguments");
        }
        if (!(data[0] instanceof String)) {
            throw new IllegalArgumentException("Invalid type of arguments");
        }
    }

    @Override
    public void execute(int id, Object[] data, OutputHandler outputHandler) {
        outputHandler.println("Filtering results");
        if (id == 0) {
            for (var it : CollectionManager.filterContentRegex((String) data[0])) {
                outputHandler.println(it.toString());
            }
        } else {
            for (var it : CollectionManager.filterContent((String) data[0])) {
                outputHandler.println(it.toString());
            }
        }
    }
}
