package com.armemius.lab7.tasks;

import com.armemius.lab7.collection.CollectionManager;
import com.armemius.lab7.collection.DatabaseManager;
import com.armemius.lab7.collection.data.Person;
import com.armemius.lab7.collection.data.StudyGroup;
import com.armemius.lab7.commands.CommandContext;
import com.armemius.lab7.commands.exceptions.CommandArgumentException;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.commands.params.Conflict;
import com.armemius.lab7.commands.params.Param;
import com.armemius.lab7.commands.params.Parametrized;
import com.armemius.lab7.io.InputHandler;
import com.armemius.lab7.io.OutputHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
public class ReplaceTask extends ComparingTask implements RemoteExecutable {
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
            if (!CollectionManager.checkId(id))
                throw new CommandRuntimeException("Collection manager doesn't have element with such id");
            StudyGroup group = requestGroup(inputHandler, outputHandler);
            String fieldName = "getId";
            Class<? extends Number> type = Integer.class;
            recalculateTypes(context, fieldName, type);
            boolean update;
            if (context.params().contains("g")) {
                update = replaceGroup(type, fieldName, context.args().get(0), 1, id, group, DatabaseManager.ADMIN_LOGIN);
            } else if (context.params().contains("l")) {
                update = replaceGroup(type, fieldName, context.args().get(0), -1, id, group, DatabaseManager.ADMIN_LOGIN);
            } else {
                update = replaceGroup(type, fieldName, context.args().get(0), 0, id, group, DatabaseManager.ADMIN_LOGIN);
            }
            outputHandler.println(update ? "Element updated" : "Element wasn't updated");
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value for argument");
        }

    }

    /**
     * Auxiliary function, contains logic
     * for replacing groups
     */
    private <T extends Number> boolean replaceGroup(Class<T> clazz, String methodName, String arg, int mode, int id, StudyGroup newGroup, String login) {
        T value;
        try {
            value = getValue(clazz, arg);
            Method method = StudyGroup.class.getMethod(methodName);
            return CollectionManager.replace((group) -> {
                try {
                    return Math.signum(clazz.cast(method.invoke(group)).doubleValue() - value.doubleValue()) == Math.signum(mode);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new CommandRuntimeException(e.getMessage());
                }
            }, id, newGroup, login);
        } catch (NoSuchMethodException | NumberFormatException ex) {
            throw new CommandRuntimeException(ex.getMessage());
        }
    }

    @Override
    public void validate(int id, Object[] data) throws IllegalArgumentException {
        if (id != -1 && id != 0 && id != 1) {
            throw new IllegalArgumentException("Invalid operation id");
        }
        if (data.length != 5) {
            throw new IllegalArgumentException("Suspicious number of arguments");
        }
        if (!(data[0] instanceof Class<?>)
                || !(data[1] instanceof String)
                || !(data[2] instanceof String)
                || !(data[3] instanceof Integer)
                || !(data[4] instanceof StudyGroup)) {
            throw new IllegalArgumentException("Invalid type of arguments");
        }
        if (!CollectionManager.checkId((Integer)data[3])) {
            throw new IllegalArgumentException("Element with such id is not in collection");
        }
    }

    @Override
    public void execute(int id, Object[] data, OutputHandler outputHandler, String login) {
        if (id == 10) {
            if (CollectionManager.removeAnyByGroupAdmin((Person) data[0], login)) {
                outputHandler.println("Successfully removed element");
            } else {
                outputHandler.println("Unable to find element with matching group admin");
            }
        } else {
            boolean update = replaceGroup((Class<? extends Number>)data[0],
                    (String)data[1],
                    (String)data[2],
                    id,
                    (Integer)data[3],
                    (StudyGroup)data[4],
                    login);
            outputHandler.println(update ? "Element updated" : "Element wasn't updated");
        }
    }
}
