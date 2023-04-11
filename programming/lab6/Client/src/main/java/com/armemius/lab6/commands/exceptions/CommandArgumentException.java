package com.armemius.lab6.commands.exceptions;

public class CommandArgumentException extends CommandRuntimeException {
    public CommandArgumentException(String message) {
        super(message);
    }
}
