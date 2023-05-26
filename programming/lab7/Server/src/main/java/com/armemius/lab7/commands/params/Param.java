package com.armemius.lab7.commands.params;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <b>Param</b> annotation represents possible
 * parameter of the Task
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String letter();
    String name();
}
