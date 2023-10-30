package com.armemius.lab5.tasks;

import com.armemius.lab5.collection.CollectionManager;
import com.armemius.lab5.commands.CommandContext;
import com.armemius.lab5.commands.params.Param;
import com.armemius.lab5.commands.params.Parametrized;
import com.armemius.lab5.io.OutputHandler;
import com.armemius.lab5.tasks.Task;

@Parametrized
public class ShowTask implements Task {
    /**
     * Action for <b>show</b> command
     * Doesn't receive arguments
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler output = context.outputHandler();
        if (context.params().contains("h") ) {
            output.put("""
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
            output.put("Collection is empty");
            return;
        }
        output.put("Collection elements:");
        for (var it : groups) {
            output.put(it.toString());
        }
    }
}
