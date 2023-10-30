package com.armemius.lab7.tasks;

import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.network.NetworkHandler;
import com.armemius.lab7.network.PayloadTypes;
import com.armemius.lab7.network.TaskContextData;

@Parametrized
public class RegisterTask extends InputTask {
    @Override
    public void execute(CommandContext context) {
        OutputHandler output = context.outputHandler();
        InputHandler input = context.inputHandler();
        if (context.params().contains("h")) {
            output.println("""
                        Syntax:
                        > register <login> <password>
                        This command performs registration
                        PARAMS:
                        -h / --help\t\tShow this menu
                        """);
            return;
        }
        if (context.args().size() != 2) {
            throw new CommandRuntimeException("Values are not provided");
        }
        String login = context.args().get(0);
        String password = context.args().get(1);

        var payload = NetworkHandler.genPayload(PayloadTypes.REGISTER_REQUEST,
                login,
                password,
                null);
        NetworkHandler.send(payload);
    }
}
