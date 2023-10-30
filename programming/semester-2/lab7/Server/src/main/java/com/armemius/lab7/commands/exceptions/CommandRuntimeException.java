package com.armemius.lab7.commands.exceptions;

public class CommandRuntimeException extends RuntimeException {
    public CommandRuntimeException(String message) {
        super(message);
    }
}
