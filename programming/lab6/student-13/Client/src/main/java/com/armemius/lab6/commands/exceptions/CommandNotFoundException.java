package com.armemius.lab6.commands.exceptions;

public class CommandNotFoundException extends CommandRuntimeException {
    public CommandNotFoundException(String message) {
        super(message);
    }
}