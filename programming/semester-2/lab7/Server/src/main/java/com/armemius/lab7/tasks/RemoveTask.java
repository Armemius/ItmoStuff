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
                @Param(letter = "a", name = "avg-mark"),
                @Param(letter = "d", name = "admin")
        },
        incompatible = {
                @Conflict({"l", "g"}),
                @Conflict({"s", "e", "a", "d"})
        }
)
public class RemoveTask extends ComparingTask implements RemoteExecutable {
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
            if (CollectionManager.removeAnyByGroupAdmin(admin, DatabaseManager.ADMIN_LOGIN)) {
                outputHandler.println("Successfully removed element");
            } else {
                outputHandler.println("Unable to find element with matching group admin");
            }
            return;
        }
        if (context.args().size() < 1)
            throw new CommandArgumentException("Argument wasn't provided");
        try {
            String fieldName = "getId";
            Class<? extends Number> type = Integer.class;
            recalculateTypes(context, fieldName, type);
            int removals;
            if (context.params().contains("g")) {
                removals = removeGroups(type, fieldName, context.args().get(0), 1, DatabaseManager.ADMIN_LOGIN);
            } else if (context.params().contains("l")) {
                removals = removeGroups(type, fieldName, context.args().get(0), -1, DatabaseManager.ADMIN_LOGIN);
            } else {
                removals = removeGroups(type, fieldName, context.args().get(0), 0, DatabaseManager.ADMIN_LOGIN);
            }
            outputHandler.println("Removed " + removals + " element(s)");
        }
        catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value type provided");
        }
    }

    /**
     * Auxiliary function, contains logic
     * for removing groups
     */
    private static <T extends Number> int removeGroups(Class<T> clazz, String methodName, String arg, int mode, String login) {
        T value;
        try {
            value = getValue(clazz, arg);
            Method method = StudyGroup.class.getMethod(methodName);
            return CollectionManager.remove((group) -> {
                try {
                    return Math.signum(clazz.cast(method.invoke(group)).doubleValue() - value.doubleValue()) == Math.signum(mode);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new CommandRuntimeException(e.getMessage());
                }
            }, login);
        } catch (NoSuchMethodException | NumberFormatException e) {
            throw new CommandRuntimeException("Incorrect value type provided");
        }
    }

    @Override
    public void validate(int id, Object[] data) throws IllegalArgumentException {
        if (id != -1 && id != 0 && id != 1 && id != 10) {
            throw new IllegalArgumentException("Invalid operation id");
        }
        if (id == -1 || id == 0 || id == 1) {
            if (data.length != 3) {
                throw new IllegalArgumentException("Suspicious number of arguments");
            }
            if (!(data[0] instanceof Class<?>)
                    || !(data[1] instanceof String)
                    || !(data[2] instanceof String)) {
                throw new IllegalArgumentException("Invalid type of arguments");
            }
            try {
                if (!CollectionManager.checkId(Integer.parseInt((String) data[2]))) {
                    throw new IllegalArgumentException("Element with such id is not in collection");
                }
            } catch (NumberFormatException ex) {
                throw new CommandRuntimeException("Incorrect value type provided");
            }
        } else {
            if (data.length != 1) {
                throw new IllegalArgumentException("Suspicious number of arguments");
            }
            if (!(data[0] instanceof Person)) {
                throw new IllegalArgumentException("Invalid type of arguments");
            }
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
            outputHandler.println("Removed "
                    + removeGroups((Class<? extends Number>)data[0], (String)data[1], (String)data[2], id, login)
                    + " element(s)");
        }
    }
}
