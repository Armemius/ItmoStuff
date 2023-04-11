package com.armemius.lab6.tasks;

import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.OutputHandler;
import com.armemius.lab6.network.NetworkHandler;
import com.armemius.lab6.network.PayloadTypes;
import com.armemius.lab6.network.TaskContextData;

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
            output.println("""
                        Syntax:
                        > show
                        Shows elements in collection
                        PARAMS:
                        -h / --help\t\tShow this menu
                        """);
            return;
        }
        var payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                new TaskContextData(this.getClass(), 0));
        NetworkHandler.send(payload);
    }
}
