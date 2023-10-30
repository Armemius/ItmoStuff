package com.armemius.lab7;

import com.armemius.lab7.commands.TreeCommandParser;
import com.armemius.lab7.commands.exceptions.CommandBuildException;
import com.armemius.lab7.commands.nodes.CommandNode;
import com.armemius.lab7.commands.nodes.DataNode;
import com.armemius.lab7.tasks.*;

/**
 * <b>ParserRegistry</b> class stands for initialization
 * of parsers that needs that
 */
public class ParserRegistry {
    /**
     * Method that initializes {@link TreeCommandParser} with default commands
     * @param parser Link to {@link TreeCommandParser}, which needs to be initialized
     * @throws CommandBuildException Throws an exception if there are troubles with tree structure
     */
    public static void buildTree(TreeCommandParser parser) throws CommandBuildException {
        final var clearTask = new ClearTask();
        final var countTask = new CountTask();
        final var executeTask = new ExecuteTask();
        final var exitTask = new ExitTask();
        final var filterTask = new FilterTask();
        final var helpTask = new HelpTask();
        final var infoTask = new InfoTask();
        final var insertTask = new InsertTask();
        final var removeTask = new RemoveTask();
        final var replaceTask = new ReplaceTask();
        final var showTask = new ShowTask();
        final var updateTask = new UpdateTask();
        final var registerTask = new RegisterTask();
        final var authTask = new AuthTask();

        parser.add(
                new CommandNode("help")
                        .executes(helpTask)
        ).add(
                new CommandNode("info")
                        .executes(infoTask)
        ).add(
                new CommandNode("show")
                        .executes(showTask)
        ).add(
                new CommandNode("insert")
                        .executes(insertTask)
        ).add(
                new CommandNode("update")
                        .then(
                                new DataNode()
                                        .executes(updateTask)
                        )
                        .executes(updateTask)
        ).add(
                new CommandNode("remove")
                        .then(
                                new DataNode()
                                        .executes(removeTask)
                        )
                        .executes(removeTask)
        ).add(
                new CommandNode("clear")
                        .executes(clearTask)
        ).add(
                new CommandNode("execute")
                        .then(
                                new DataNode()
                                        .executes(executeTask)
                        )
                        .executes(executeTask)
        ).add(
                new CommandNode("exit")
                        .executes(exitTask)
        ).add(
                new CommandNode("replace")
                        .then(
                                new DataNode().executes(replaceTask)
                        )
                        .executes(replaceTask)
        ).add(
                new CommandNode("count")
                        .then(
                                new DataNode()
                                        .executes(countTask)
                                        .then(
                                                new DataNode()
                                                        .executes(countTask)
                                        )
                        )
                        .executes(countTask)
        ).add(
                new CommandNode("filter")
                        .then(
                                new DataNode()
                                        .executes(filterTask)
                        )
                        .executes(filterTask)
        ).add(
                new CommandNode("register")
                        .then(
                                new DataNode().then(
                                        new DataNode()
                                                .executes(registerTask)
                                )
                                .executes(registerTask)
                        )
                        .executes(registerTask)
        ).add(
                new CommandNode("auth")
                        .then(
                                new DataNode().then(
                                                new DataNode()
                                                        .executes(authTask)
                                        )
                                        .executes(authTask)
                        )
                        .executes(authTask)
        );
    }
}
