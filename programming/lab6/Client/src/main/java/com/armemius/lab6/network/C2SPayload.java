package com.armemius.lab6.network;

import java.io.Serializable;

public record C2SPayload(
        PayloadTypes type,
        TaskContextData data
) implements Serializable {}
