package com.armemius.lab6.tasks;

import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandArgumentException;
import com.armemius.lab6.commands.params.Param;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;
import com.armemius.lab6.network.*;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "r", name = "regex")
        }
)
public class FilterTask implements Task {
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
        C2SPayload payload;
        if (context.params().contains("r")) {
            payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST, new TaskContextData(this.getClass(), 0, substring));
        } else {
            payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST, new TaskContextData(this.getClass(), 1, substring));
        }
        NetworkHandler.send(payload);
    }
}
