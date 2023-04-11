package com.armemius.lab6.tasks;

import com.armemius.lab6.collection.CollectionManager;
import com.armemius.lab6.collection.data.StudyGroup;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandRuntimeException;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;

@Parametrized
public class UpdateTask extends RequestTask implements RemoteExecutable {
    /**
     * Action for <b>update</b> command
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
                        > update <id>
                        Command responsive for updating collection
                        PARAMS:
                        -h / --help\tShow this menu
                        """);
            return;
        }
        try {
            int id = Integer.parseInt(context.args().get(0));
            if (!CollectionManager.checkId(id))
                throw new CommandRuntimeException("Collection manager doesn't have element with such id");
            outputHandler.println("Updating element with id " + id);
            StudyGroup group = null;
            while (group == null) {
                group = requestGroup(inputHandler, outputHandler);
                group.setId(id);
                outputHandler.println("You want to update group with id " + id + " with the following group: " + group);
                outputHandler.print("Proceed? (Input empty line if yes) ");
                String response = inputHandler.get();
                if (!response.isBlank())
                    group = null;
            }
            CollectionManager.update(id, group);
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value for argument");
        }
    }

    @Override
    public void validate(int id, Object[] data) throws IllegalArgumentException {
        if (id != 0) {
            throw new IllegalArgumentException("Invalid operation id");
        }
        if (data.length != 2) {
            throw new IllegalArgumentException("Suspicious number of arguments");
        }
        if (!(data[0] instanceof Integer) || !(data[1] instanceof StudyGroup)) {
            throw new IllegalArgumentException("Invalid type of arguments");
        }
        if (!CollectionManager.checkId((Integer)data[0])) {
            throw new IllegalArgumentException("Element with such id is not in collection");
        }
    }

    @Override
    public void execute(int id, Object[] data, OutputHandler outputHandler) {
        outputHandler.println("Element updated");
        CollectionManager.update((Integer) data[0], (StudyGroup) data[1]);
    }
}
