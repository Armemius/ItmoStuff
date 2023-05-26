package com.armemius.lab7.tasks;

import com.armemius.lab7.collection.CollectionManager;
import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.exceptions.CommandArgumentException;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;

@Parametrized
public class CountTask implements Task, RemoteExecutable {
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
            if (context.args().size() > 1) {
                double delta = Double.parseDouble(context.args().get(1).replace(',', '.'));
                outputHandler.println("Count: " + CollectionManager.countAvgMarkDelta(mark, delta));
            } else {
                outputHandler.println("Count: " + CollectionManager.countAvgMark(mark));
            }
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value type provided");
        }
    }

    @Override
    public void validate(int id, Object[] data) throws IllegalArgumentException {
        if (id != 0 && id != 1) {
            throw new IllegalArgumentException("Invalid operation id");
        }
        if (id == 0) {
            if (data.length != 2) {
                throw new IllegalArgumentException("Suspicious number of arguments");
            }
            if (!(data[0] instanceof Double) || !(data[1] instanceof Double)) {
                throw new IllegalArgumentException("Invalid type of arguments");
            }
        } else {
            if (data.length != 1) {
                throw new IllegalArgumentException("Suspicious number of arguments");
            }
            if (!(data[0] instanceof Double)) {
                throw new IllegalArgumentException("Invalid type of arguments");
            }
        }
    }

    @Override
    public void execute(int id, Object[] data, OutputHandler outputHandler, String login) {
        if (id == 0) {
            outputHandler.println("Count: " + CollectionManager.countAvgMarkDelta((Double) data[0], (Double) data[1]));
        } else {
            outputHandler.println("Count: " + CollectionManager.countAvgMark((Double) data[0]));
        }
    }
}
