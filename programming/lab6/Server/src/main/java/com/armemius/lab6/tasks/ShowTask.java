package com.armemius.lab6.tasks;

import com.armemius.lab6.collection.CollectionManager;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.OutputHandler;

@Parametrized
public class ShowTask implements Task, RemoteExecutable {
    /**
     * Action for <b>show</b> command
     * Doesn't receive arguments
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        if (context.params().contains("h") ) {
            outputHandler.println("""
                        Syntax:
                        > show
                        Shows elements in collection
                        PARAMS:
                        -h / --help\t\tShow this menu
                        """);
            return;
        }
        var groups = CollectionManager.getAll();
        if (groups.isEmpty()) {
            outputHandler.println("Collection is empty");
            return;
        }
        outputHandler.println("Collection elements:");
        for (var it : groups) {
            outputHandler.println(it.toString());
        }
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
    public void execute(int id, Object[] data, OutputHandler outputHandler) {
        var groups = CollectionManager.getAll();
        if (groups.isEmpty()) {
            outputHandler.println("Collection is empty");
            return;
        }
        outputHandler.println("Collection elements:");
        for (var it : groups) {
            outputHandler.println(it.toString());
        }
    }
}
