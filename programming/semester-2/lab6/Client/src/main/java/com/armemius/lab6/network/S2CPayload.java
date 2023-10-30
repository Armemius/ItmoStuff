package com.armemius.lab6.network;

import java.io.Serializable;

/**
 * Record that contains information which is sent from server to client
 * <div style="margin-top: 10px;">Status codes:</div>
 * <ul style="margin-top: 0px;">
 *      <li><span style="color:green">200</span> - OK</li>
 *      <li><span style="color:green">202</span> - Accepted</li>
 *      <li><span style="color:red">400</span> - Bad request</li>
 *      <li><span style="color:red">401</span> - Auth error</li>
 *      <li><span style="color:red">403</span> - Permission denied</li>
 *      <li><span style="color:red">418</span> - I'm a teapot</li>
 *      <li><span style="color:red">429</span> - Server overload</li>
 *      <li><span style="color:red">500</span> - Server error</li>
 * </ul>
 * @param type Payload type
 * @param status Response status code
 * @param message Message to transmit to client
 */
public record S2CPayload(
        PayloadTypes type,
        StatusCodes status,
        String message
) implements Serializable {}
