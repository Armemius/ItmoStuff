package com.armemius.lab6.tasks;

import com.armemius.lab6.collection.data.StudyGroup;
import com.armemius.lab6.commands.CommandContext;
import com.armemius.lab6.commands.exceptions.CommandArgumentException;
import com.armemius.lab6.commands.exceptions.CommandRuntimeException;
import com.armemius.lab6.commands.params.Conflict;
import com.armemius.lab6.commands.params.Param;
import com.armemius.lab6.commands.params.Parametrized;
import com.armemius.lab6.io.InputHandler;
import com.armemius.lab6.io.OutputHandler;
import com.armemius.lab6.network.*;

@Parametrized(
        params = {
                @Param(letter = "h", name = "help"),
                @Param(letter = "l", name = "lower"),
                @Param(letter = "g", name = "greater"),
                @Param(letter = "s", name = "students"),
                @Param(letter = "e", name = "expelled"),
                @Param(letter = "a", name = "avg-mark")
        },
        incompatible = {
                @Conflict({"l", "g"}),
                @Conflict({"s", "e", "a"})
        }
)
public class ReplaceTask extends ComparingTask {
    /**
     * Action for <b>replace</b> command
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
                        > replace <id>
                        Command responsive for replacement of element from collection
                        PARAMS:
                        -h / --help\tShow this menu
                        -l / --lower\tReplaces element if value lower than specified (default - id)
                        -g / --greater\tReplaces element if value lower than specified (default - id)
                        -s / --students\tMakes --lower and --greater parameters work with studentsCount field
                        -e / --expelled\tMakes --lower and --greater parameters work with expelledStudents field
                        -a / --avg-mark\tMakes --lower and --greater parameters work with averageMark field
                        """);
            return;
        }
        if (context.args().size() < 1)
            throw new CommandArgumentException("Argument wasn't provided");
        var params = context.params();
        if (!params.contains("s") && !params.contains("e") && !params.contains("a")
            && (params.contains("g") || params.contains("l")))
            throw new CommandArgumentException("Can't apply comparator modifiers to id");
        try {
            int id = Integer.parseInt(context.args().get(0));
            StudyGroup group = requestGroup(inputHandler, outputHandler);
            String fieldName = "getId";
            Class<? extends Number> type = Integer.class;;
            if ((context.params().contains("s"))) {
                fieldName = "getStudentsCount";
                type = Long.class;
            } else if ((context.params().contains("e"))) {
                fieldName = "getExpelledStudents";
            } else if ((context.params().contains("a"))) {
                fieldName = "getAverageMark";
                type = Double.class;
            }
            boolean update;
            C2SPayload payload;
            if (context.params().contains("g")) {
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                        new TaskContextData(this.getClass(),
                                1,
                                type,
                                fieldName,
                                context.args().get(0),
                                id,
                                group));
            } else if (context.params().contains("l")) {
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                        new TaskContextData(this.getClass(),
                                -1,
                                type,
                                fieldName,
                                context.args().get(0),
                                id,
                                group));
            } else {
                payload = NetworkHandler.genPayload(PayloadTypes.EXECUTE_REQUEST,
                        new TaskContextData(this.getClass(),
                                0,
                                type,
                                fieldName,
                                context.args().get(0),
                                id,
                                group));
            }
            NetworkHandler.send(payload);
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value for argument");
        }
    }
}
