package com.armemius.lab6.tasks;

import com.armemius.lab6.collection.data.StudyGroup;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandRuntimeException;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;
import com.armemius.lab6.network.NetworkHandler;
import com.armemius.lab6.network.PayloadTypes;
import com.armemius.lab6.network.TaskContextData;

@Parametrized
public class UpdateTask extends RequestTask {
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
            var payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                    new TaskContextData(this.getClass(),
                            0,
                            id,
                            group));
            NetworkHandler.send(payload);
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value for argument");
        }
    }
}
