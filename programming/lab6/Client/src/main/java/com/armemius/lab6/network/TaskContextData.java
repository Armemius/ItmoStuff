package com.armemius.lab6.network;

import com.armemius.lab6.tasks.Task;

import java.io.Serializable;

public record TaskContextData(
        Class<? extends Task> task,
        int operationId,
        Object... data
) implements Serializable {}
