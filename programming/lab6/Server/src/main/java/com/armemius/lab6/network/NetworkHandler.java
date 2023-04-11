package com.armemius.lab6.network;

import com.armemius.lab6.commands.exceptions.CommandArgumentException;
import com.armemius.lab6.io.BufferOutputHandler;
import com.armemius.lab6.tasks.RemoteExecutable;
import com.armemius.lab6.tasks.Task;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

public class NetworkHandler {

    private NetworkHandler() {}

    private final static Logger logger = Logger.getLogger(NetworkHandler.class);
    private static DatagramSocket socket;
    private static PacketBuilder packetBuilder;
    private static final int BUFFER_CAPACITY = 8192;
    private static final byte[] buffer = new byte[BUFFER_CAPACITY];
    private static volatile boolean isRunning = false;

    public static void start() throws IOException {
        if (isRunning) {
            logger.warn("Trying to start already started NetworkHandler");
            return;
        }
        isRunning = true;
        NetworkHandler.packetBuilder = new PacketBuilder();

        String portValue = System.getenv("LAB6_SERVER_PORT");
        int serverPort;
        final int DEFAULT_PORT_VALUE = 777;
        final int MIN_PORT_VALUE = 0;
        final int MAX_PORT_VALUE = 65536;

        try {
            if (portValue == null) {
                serverPort = DEFAULT_PORT_VALUE;
            } else {
                serverPort = Integer.parseInt(portValue);
                if (serverPort < MIN_PORT_VALUE || serverPort > MAX_PORT_VALUE) {
                    logger.warn("Invalid port value: " + portValue + ", using default: " + DEFAULT_PORT_VALUE);
                    serverPort = 777;
                }
            }
        } catch (NumberFormatException ex) {
            logger.warn("Invalid port value: " + portValue + ", using default: " + DEFAULT_PORT_VALUE);
            serverPort = DEFAULT_PORT_VALUE;
        }

        socket = new DatagramSocket(serverPort);
        Thread listeningThread = new Thread(NetworkHandler::listen);
        listeningThread.start();
    }

    public static void stop() throws IOException {
        if (!isRunning) {
            logger.warn("Trying to stop not running NetworkHandler");
            return;
        }
        isRunning = false;
        socket.close();
        packetBuilder.close();
    }

    private static void send(S2CPayload payload, InetAddress clientAddress, int clientPort) throws IOException {
        if (!isRunning) {
            logger.warn("Trying to sent payload when NetworkHandler is not running");
            return;
        }
        var data = PayloadHandler.serialize(payload);
        logger.debug("Sending " + payload + " to " + clientAddress + ":" + clientPort + " size: " + data.length + "B");
        var pkg = new DatagramPacket(data, data.length, clientAddress, clientPort);
        socket.send(pkg);
    }


    private static void listen() {
        logger.info("Starting to listen for incoming packages");
        while (isRunning) {
            try {
                var packetPart = new DatagramPacket(buffer, buffer.length);
                do {
                    socket.receive(packetPart);
                    logger.debug("Received packet from " + packetPart.getAddress());
                } while (!packetBuilder.append(buffer));
                var packet = packetBuilder.flush();

                Object obj = PayloadHandler.deserialize(packet);
                if (!(obj instanceof C2SPayload payload)) {
                    logger.error("Received payload with incorrect signature: " + obj.getClass());
                    continue;
                }
                processIncomingPayload(payload, packetPart.getAddress(), packetPart.getPort());
            } catch (ClassNotFoundException ex) {
                logger.error("Received damaged payload: " + ex.getMessage());
            } catch (IOException ex) {
                logger.error("Error while processing input package: " + ex.getMessage());
            }
        }
    }

    private static void processIncomingPayload(C2SPayload payload, InetAddress address, int port) throws IOException {
        logger.debug("Processing " + payload);
        final var type = payload.type();

        if (type == PayloadTypes.HANDSHAKE) {
            send(new S2CPayload(PayloadTypes.HANDSHAKE, StatusCodes.ACCEPTED, null), address, port);
        } else if (type == PayloadTypes.EXECUTE_REQUEST) {
            processTask(payload, address, port);
        }
    }

    private static void processTask(C2SPayload payload, InetAddress address, int port) throws IOException {
        final var contextData = payload.data();
        final var buffer = new StringBuffer();
        final var bufferOutputHandler = new BufferOutputHandler(buffer);
        try {
            Constructor<? extends Task> constructor = contextData.task().getConstructor();
            var task = constructor.newInstance();
            if (!(task instanceof RemoteExecutable executor)) {
                throw new ClassCastException();
            }
            executor.validate(contextData.operationId(), contextData.data());
            executor.execute(contextData.operationId(), contextData.data(), bufferOutputHandler);
            logger.debug("Output buffer size: " + buffer.length());
            final int HEADER_BYTES_PRESERVED = 512;
            final int MAX_STRING_LENGTH = BUFFER_CAPACITY - HEADER_BYTES_PRESERVED;
            var it = 0;
            do {
                var part = buffer.substring(MAX_STRING_LENGTH * it,
                        Math.min(buffer.length(), MAX_STRING_LENGTH * ++it));
                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.OK, part), address, port);
            } while (it < (buffer.length() - 1) / MAX_STRING_LENGTH + 1);
            send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.OK, "\0"), address, port);
        } catch (IllegalArgumentException | CommandArgumentException ex) {
            send(new S2CPayload(PayloadTypes.RESPONSE,
                        StatusCodes.BAD_REQUEST,
                        "Invalid parameters: " + ex.getMessage() + "\n\r"),
                        address,
                        port);
        } catch (InvocationTargetException
                 | InstantiationException
                 | IllegalAccessException
                 | NoSuchMethodException
                 | ClassCastException ex) {
            logger.error("Unable to process payload from client due to class incompatibility");
            send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.ERROR, "Check client version\n\r"), address, port);
        }
    }
}
