package com.armemius.lab7.tasks;

import com.armemius.lab7.collection.data.StudyGroup;
import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.commands.params.RequireAuth;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.network.*;

@RequireAuth
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

            NetworkHandler.send(new C2SPayload(PayloadTypes.EXECUTE_REQUEST,
                    NetworkHandler.getLogin(),
                    NetworkHandler.getPassword(),
                    new TaskContextData(null, 0, id)));
            var serverResponse = NetworkHandler.wait4response();
            if (serverResponse != StatusCodes.OK) {
                return;
            }

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
