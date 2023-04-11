package com.armemius.lab6.commands.exceptions;

public class CommandRuntimeException extends RuntimeException {
    public CommandRuntimeException(String message) {
        super(message);
    }
}
