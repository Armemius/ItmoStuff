package com.armemius.lab7.network;

import java.io.Serializable;

public record C2SPayload(
        PayloadTypes type,
        String login,
        String password,
        TaskContextData data
) implements Serializable {}
