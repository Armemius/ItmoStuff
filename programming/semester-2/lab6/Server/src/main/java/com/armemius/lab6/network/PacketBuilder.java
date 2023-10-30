package com.armemius.lab6.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PacketBuilder {

    private final ByteArrayOutputStream outputStream;

    public PacketBuilder() {
        outputStream = new ByteArrayOutputStream();
    }

    boolean append(byte[] packet) throws IOException {
        if (packet.length < 8) {
            return false;
        }

        final int HEADER_SIZE = 8;
        var isHeaderEmpty = true;
        for (int it = 0; it < HEADER_SIZE; ++it) {
            if (packet[it] != 0) {
                isHeaderEmpty = false;
                break;
            }
        }
        if (isHeaderEmpty) {
            return true;
        }

        outputStream.writeBytes(packet);
        return false;
    }

    byte[] flush() throws IOException {
        var res = outputStream.toByteArray();
        outputStream.flush();
        outputStream.reset();
        return res;
    }

    void close() throws IOException {
        outputStream.close();
    }
}
