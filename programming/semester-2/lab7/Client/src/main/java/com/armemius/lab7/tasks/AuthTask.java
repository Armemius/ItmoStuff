package com.armemius.lab7.tasks;

import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.commands.params.Conflict;
import com.armemius.lab7.commands.params.Param;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.network.NetworkHandler;
import com.armemius.lab7.network.PayloadTypes;
import com.armemius.lab7.network.StatusCodes;
import com.armemius.lab7.network.TaskContextData;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "u", name = "user"),
                @Param(letter = "o", name = "out")
        },
        incompatible = {
                @Conflict({"h", "u", "o"})
        }
)
public class AuthTask extends InputTask {
    @Override
    public void execute(CommandContext context) {
        OutputHandler output = context.outputHandler();
        if (context.params().contains("h")) {
            output.println("""
                        Syntax:
                        > auth <login> <password>
                        This command performs authentication
                        PARAMS:
                        -h / --help\t\tShow this menu
                        -u / --user\t\tShow current user
                        -o / --out\t\tRemove information about current user
                        """);
            return;
        }
        if (context.params().contains("u")) {
            String user = NetworkHandler.getLogin();
            if (user == null) {
                output.println("Not authenticated");
            } else {
                output.println("Current user: " + user);
            }
            return;
        }
        if (context.params().contains("o")) {
            NetworkHandler.setLogin(null);
            NetworkHandler.setPassword(null);
            output.println("Login and password were reset");
            return;
        }
        if (context.args().size() != 2) {
            throw new CommandRuntimeException("Values are not provided");
        }
        String login = context.args().get(0);
        String password = context.args().get(1);

        var payload = NetworkHandler.genPayload(PayloadTypes.AUTH_REQUEST,
                login,
                password,
                null);
        NetworkHandler.send(payload);

        var serverResponse = NetworkHandler.wait4response();

        if (serverResponse == StatusCodes.OK) {
            NetworkHandler.setLogin(login);
            NetworkHandler.setPassword(password);
        } else {
            return;
        }

        output.println("Current user: " + login);
    }
}
