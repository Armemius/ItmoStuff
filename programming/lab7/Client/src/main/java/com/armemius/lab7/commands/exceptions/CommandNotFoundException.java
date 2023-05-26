package com.armemius.lab7.commands.exceptions;

public class CommandNotFoundException extends CommandRuntimeException {
    public CommandNotFoundException(String message) {
        super(message);
    }
}