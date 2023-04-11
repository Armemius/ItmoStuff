package com.armemius.lab6.tasks;

import com.armemius.lab6.commands.exceptions.CommandRuntimeException;
import com.armemius.lab6.io.InputHandler;

/**
 * <b>InputTask</b> class represents tasks for commands with input
 */
public abstract class InputTask implements Task {
    /**
     * Auxiliary function, contains logic
     * for inputting String
     */
    protected String getString(InputHandler inputHandler, boolean isNullable) {
        var value = inputHandler.get();
        value = (value.isBlank() || value.isEmpty() ? null : value);
        if (value == null && !isNullable)
            throw new CommandRuntimeException("Incorrect value for argument");
        return value;
    }

    /**
     * Auxiliary function, contains logic
     * for inputting numeric values
     */
    protected  <T extends Number> T getNumber(Class<T> clazz, InputHandler inputHandler, boolean isNullable) {
        String value = getString(inputHandler, isNullable);
        if (value == null) {
            return null;
        }
        try {
            if (clazz == Double.class) {
                return clazz.cast(Double.parseDouble(value.replace(',', '.')));
            } else if (clazz == Integer.class) {
                return clazz.cast(Integer.parseInt(value));
            } else if (clazz == Long.class) {
                return clazz.cast(Long.parseLong(value));
            } else if (clazz == Float.class) {
                return clazz.cast(Float.parseFloat(value.replace(',', '.')));
            } else {
                throw new IllegalArgumentException("Unsupported number class");
            }
        } catch (NumberFormatException ex) {
            throw new CommandRuntimeException("Incorrect value for argument");
        }
    }

    /**
     * Auxiliary function, contains logic
     * for inputting Enum fields
     */
    protected  <T extends Enum<T>> T getEnumField(Class<T> enumClass, InputHandler inputHandler, boolean isNullable) {
        String value = getString(inputHandler, isNullable);
        if (value == null) {
            return null;
        } else {
            try {
                int id = Integer.parseInt(value) - 1;
                T[] possibleValues = enumClass.getEnumConstants();
                if (id >= 0 && id < possibleValues.length) {
                    return possibleValues[id];
                }
            }
            catch (NumberFormatException ignore){}

            value = value.toUpperCase();
            try {
                return Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException ex) {
                throw new CommandRuntimeException("Incorrect value for " + enumClass.getSimpleName());
            }
        }
    }
}
