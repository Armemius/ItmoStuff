package com.armemius.lab7.network;

import com.armemius.lab7.tasks.Task;

import java.io.Serializable;

public record TaskContextData(
        Class<? extends Task> task,
        int operationId,
        Object... data
) implements Serializable {}
