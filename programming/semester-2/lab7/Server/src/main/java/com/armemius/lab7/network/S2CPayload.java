package com.armemius.lab7.network;

import java.io.Serializable;

/**
 * Record that contains information which is sent from server to client
 * @param type Payload type
 * @param status Response status code
 * @param message Message to transmit to client
 */
public record S2CPayload(
        PayloadTypes type,
        StatusCodes status,
        String message
) implements Serializable {}
