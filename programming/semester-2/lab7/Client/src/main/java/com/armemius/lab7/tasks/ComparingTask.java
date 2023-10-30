package com.armemius.lab7.tasks;

import com.armemius.lab7.commands.exceptions.CommandRuntimeException;

public abstract class ComparingTask extends RequestTask {
    protected static <T extends Number> T getValue(Class<T> clazz, String arg) {
        T value;
        if (clazz == Double.class) {
            value = clazz.cast(Double.parseDouble(arg));
        } else if (clazz == Integer.class) {
            value = clazz.cast(Integer.parseInt(arg));
        } else if (clazz == Long.class) {
            value = clazz.cast(Long.parseLong(arg));
        } else if (clazz == Float.class) {
            value = clazz.cast(Float.parseFloat(arg));
        } else {
            throw new CommandRuntimeException("Unsupported number class");
        }
        return value;
    }
}
