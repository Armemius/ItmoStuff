package com.armemius.lab7.tasks;

import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.exceptions.CommandArgumentException;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.network.*;

@Parametrized
public class CountTask implements Task {
    /**
     * Action for <b>count</b> command
     * Receives one or two arguments
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        InputHandler inputHandler = context.inputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > count <mark> [delta]
                        This command allows you to count the number of the groups with specified average mark (you can specify delta)
                        PARAMS:
                        -h / --help\tShow this menu
                        """);
            return;
        }
        if (context.args().size() < 1)
            throw new CommandArgumentException("Argument wasn't provided");
        try {
            double mark = Double.parseDouble(context.args().get(0).replace(',', '.'));
            C2SPayload payload;
            if (context.args().size() > 1) {
                double delta = Double.parseDouble(context.args().get(1).replace(',', '.'));
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST, new TaskContextData(this.getClass(), 0, mark, delta));
            } else {
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST, new TaskContextData(this.getClass(), 1, mark));
            }
            NetworkHandler.send(payload);
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value type provided");
        }
    }
}
