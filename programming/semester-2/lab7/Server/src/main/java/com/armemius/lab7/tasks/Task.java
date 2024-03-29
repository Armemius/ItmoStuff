package com.armemius.lab7.tasks;

import com.armemius.lab7.commands.CommandContext;

/**
 * Represents the execution task for command
 */
@FunctionalInterface
public interface Task {
    /**
     * Method that executes the command
     * @param context Context of executing command
     */
    void execute(CommandContext context);
}
