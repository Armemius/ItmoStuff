package com.armemius.lab7.tasks;

import com.armemius.lab7.collection.data.Person;
import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.exceptions.CommandArgumentException;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.commands.params.Conflict;
import com.armemius.lab7.commands.params.Param;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.commands.params.RequireAuth;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;
import com.armemius.lab7.network.*;

@RequireAuth
@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "l", name = "lower"),
                @Param(letter = "g", name = "greater"),
                @Param(letter = "s", name = "students"),
                @Param(letter = "e", name = "expelled"),
                @Param(letter = "a", name = "avg-mark"),
                @Param(letter = "d", name = "admin")
        },
        incompatible = {
                @Conflict({"l", "g"}),
                @Conflict({"s", "e", "a", "d"})
        }
)
public class RemoveTask extends ComparingTask {
    /**
     * Action for <b>remove</b> command
     * Receives up to one argument
     * @param context Context of executing command
     */
    @Override
    public void execute(CommandContext context) {
        OutputHandler outputHandler = context.outputHandler();
        InputHandler inputHandler = context.inputHandler();
        if (context.params().contains("h")) {
            outputHandler.println("""
                        Syntax:
                        > remove <value>
                        Command responsive for removal of elements from collection
                        PARAMS:
                        -h / --help\tShow this menu
                        --lower\t\tRemoves elements with value lower than specified (default - id)
                        --greater\tRemoves elements with value lower than specified (default - id)
                        --students\tMakes --lower and --greater parameters work with studentsCount field
                        --expelled\tMakes --lower and --greater parameters work with expelledStudents field
                        --avg-mark\tMakes --lower and --greater parameters work with averageMark field
                        --admin\t\tRemoves one group from collection where groupAdmin matches specified one
                        """);
            return;
        }
        if (context.params().contains("d")) {
            if (context.args().size() > 0)
                throw new CommandArgumentException("Too much arguments");
            Person admin = requestAdmin(inputHandler, outputHandler);
            var payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST, new TaskContextData(this.getClass(), 10, admin));
            NetworkHandler.send(payload);
            return;
        }
        if (context.args().size() < 1)
            throw new CommandArgumentException("Argument wasn't provided");
        try {
            String fieldName = "getId";
            Class<? extends Number> type = Integer.class;
            if ((context.params().contains("s"))) {
                fieldName = "getStudentsCount";
                type = Long.class;
            } else if ((context.params().contains("e"))) {
                fieldName = "getExpelledStudents";
            } else if ((context.params().contains("a"))) {
                fieldName = "getAverageMark";
                type = Double.class;
            }
            C2SPayload payload;
            if (context.params().contains("g")) {
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                        new TaskContextData(this.getClass(),
                                1,
                                type,
                                fieldName,
                                context.args().get(0)));
            } else if (context.params().contains("l")) {
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                        new TaskContextData(this.getClass(),
                                -1,
                                type,
                                fieldName,
                                context.args().get(0)));
            } else {
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                        new TaskContextData(this.getClass(),
                                0,
                                type,
                                fieldName,
                                context.args().get(0)));
            }
            NetworkHandler.send(payload);
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value type provided");
        }
    }
}
