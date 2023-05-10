package com.armemius.lab6.network;

import com.armemius.lab6.io.OutputHandler;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class NetworkHandler {

    private NetworkHandler() {}

    private final static Logger logger = Logger.getLogger(NetworkHandler.class);
    private static DatagramChannel channel;
    private static PacketBuilder packetBuilder;
    private static OutputHandler outputHandler;
    private static String serverAddress;
    private static int serverPort;
    private static final int BUFFER_CAPACITY = 8192;
    private static final int TIMEOUT_MILLIS = 5000;
    private static volatile long lastRequestTime = 0;
    private static volatile boolean hasRequested = false;
    private static volatile boolean hasReceived = false;
    private static volatile boolean isRunning = false;

    private static volatile StatusCodes lastRequestCode = null;

    public static void start(OutputHandler outputHandler) throws IOException, ClassNotFoundException {
        if (isRunning) {
            logger.warn("Trying to start already started NetworkHandler");
            return;
        }
        isRunning = true;
        NetworkHandler.packetBuilder = new PacketBuilder();

        String serverAddressValue = System.getenv("LAB6_SERVER_ADDRESS");
        String serverPortValue = System.getenv("LAB6_SERVER_PORT");
        String clientPortValue = System.getenv("LAB6_CLIENT_PORT");

        final int DEFAULT_SERVER_PORT_VALUE = 2077;
        final int PORT_RANGE = 1000;
        final int DEFAULT_CLIENT_PORT_VALUE = 6000 + (int)(PORT_RANGE * Math.random());
        final int MIN_PORT_VALUE = 0;
        final int MAX_PORT_VALUE = 65536;

        String serverAddress = serverAddressValue == null ? "localhost" : serverAddressValue;

        int serverPort;
        try {
            if (serverPortValue == null) {
                serverPort = DEFAULT_SERVER_PORT_VALUE;
            } else {
                serverPort = Integer.parseInt(serverPortValue);
                if (serverPort < MIN_PORT_VALUE || serverPort > MAX_PORT_VALUE) {
                    logger.warn("Invalid port value: " + serverPortValue + ", using default: " + DEFAULT_SERVER_PORT_VALUE);
                    serverPort = DEFAULT_SERVER_PORT_VALUE;
                }
            }
        } catch (NumberFormatException ex) {
            logger.warn("Invalid port value: " + serverPortValue + ", using default: " + DEFAULT_SERVER_PORT_VALUE);
            serverPort = DEFAULT_SERVER_PORT_VALUE;
        }

        int clientPort;
        try {
            if (serverPortValue == null) {
                clientPort = DEFAULT_CLIENT_PORT_VALUE;
            } else {
                clientPort = Integer.parseInt(clientPortValue);
                if (clientPort < MIN_PORT_VALUE || clientPort > MAX_PORT_VALUE) {
                    logger.warn("Invalid port value: " + clientPortValue + ", using default: " + DEFAULT_CLIENT_PORT_VALUE);
                    clientPort = DEFAULT_CLIENT_PORT_VALUE;
                }
            }
        } catch (NumberFormatException ex) {
            logger.warn("Invalid port value: " + clientPortValue + ", using default: " + DEFAULT_CLIENT_PORT_VALUE);
            clientPort = DEFAULT_CLIENT_PORT_VALUE;
        }

        outputHandler.println("Client will start on port " + clientPort);
        outputHandler.println("Server address is " + serverAddress + ":" + serverPort);

        NetworkHandler.serverAddress = serverAddress;
        NetworkHandler.serverPort = serverPort;
        NetworkHandler.outputHandler = outputHandler;

        channel = DatagramChannel.open().bind(new InetSocketAddress(clientPort));
        channel.configureBlocking(false);
        logger.info("DatagramChannel is working in " + (channel.isBlocking() ? "blocking" : "non-blocking") + " mode");

        Thread listeningThread = new Thread(NetworkHandler::listen);
        listeningThread.start();
    }

    public static void stop() throws IOException {
        if (!isRunning) {
            logger.warn("Trying to stop not running NetworkHandler");
            return;
        }
        isRunning = false;
        channel.close();
        packetBuilder.close();
    }

    public static C2SPayload genPayload(PayloadTypes type, TaskContextData data) {
        return new C2SPayload(type, data);
    }

    public static void send(C2SPayload payload) {
        lastRequestCode = null;
        hasReceived = false;
        if (!isRunning) {
            logger.warn("Trying to sent payload when NetworkHandler is not running");
            return;
        }
        if (hasRequested) {
            logger.debug("Waiting for the last response to be finished");
            while (hasRequested) {
                Thread.onSpinWait();
            }
        }
        ByteBuffer buffer;
        try {
            var data = PayloadHandler.serialize(payload);
            logger.debug("Sending payload with size " + data.length);
            for (int i = 0; i < data.length; i += BUFFER_CAPACITY) {
                var dataFragment = Arrays.copyOfRange(data, i, Math.min(i + BUFFER_CAPACITY, data.length));
                logger.debug("Sending data chunk №" + (i / BUFFER_CAPACITY + 1) + " to " + serverAddress + ":" + serverPort + " size: " + dataFragment.length);
                buffer = ByteBuffer.wrap(dataFragment);
                channel.send(buffer, new InetSocketAddress(serverAddress, serverPort));
            }
            buffer = ByteBuffer.wrap(new byte[BUFFER_CAPACITY]);
            channel.send(buffer, new InetSocketAddress(serverAddress, serverPort));
            lastRequestTime = System.currentTimeMillis();
            logger.debug("Last request time: " + lastRequestTime);
            hasRequested = true;
            hasReceived = false;
        } catch (IOException ex) {
            outputHandler.println("Error while sending payload: " + ex.getMessage());
        }
    }

    public static StatusCodes wait4response() {
        while(lastRequestCode == null) {
            Thread.onSpinWait();
        }
        return lastRequestCode;
    }

    public static boolean isTimeout() {
        return (lastRequestTime + TIMEOUT_MILLIS < System.currentTimeMillis()) && hasRequested;
    }

    private static void listen() {
        while (isRunning) {
            try {
                if (!hasRequested) {
                    continue;
                }
                boolean receivedAny = false;
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
                do {
                    buffer.clear();
                    while (channel.receive(buffer) == null && !isTimeout()) {
                        Thread.onSpinWait();
                    }
                    if (isTimeout()) {
                        hasReceived = false;
                        break;
                    }
                    logger.debug("Received package part");
                    receivedAny = true;
                    hasReceived = true;
                    buffer.flip();
                } while (!packetBuilder.append(buffer.array()));

                if (hasReceived) {
                    Object obj = PayloadHandler.deserialize(packetBuilder.flush());
                    if (!(obj instanceof S2CPayload payload)) {
                        logger.error("Received payload with incorrect signature: " + obj.getClass());
                        continue;
                    }
                    processIncomingPayload(payload);
                } else {
                    if (receivedAny) {
                        outputHandler.println("Didn't receive end of transmission packet, it seems like some packets were lost");
                    } else {
                        lastRequestCode = StatusCodes.TIMEOUT;
                        outputHandler.println("Didn't receive response from server, perhaps server is offline or incorrect address was specified");
                    }
                    packetBuilder.flush();
                }
                hasRequested = false;

            } catch (ClassNotFoundException ex) {
                logger.error("Received damaged payload: " + ex.getMessage());
            } catch (IOException ex) {
                logger.error("Error while processing input package: " + ex.getMessage());
            }
        }
    }

    private static void processIncomingPayload(S2CPayload payload) {
        logger.debug("Processing " + payload);
        StatusCodes status = payload.status();
        lastRequestCode = status;
        switch (status) {
            case OK -> {}
            case ACCEPTED -> logger.info("Server accepted connection, can continue");
            case BAD_REQUEST -> outputHandler.println("Incorrect data was provided to server");
            case AUTH_ERROR -> outputHandler.println("Authentication error");
            case PERMISSION_DENIED -> outputHandler.println("Permission denied");
            case TEAPOT -> outputHandler.println("Server says that he is a teapot");
            case OVERLOAD -> outputHandler.println("Server overloaded");
            case ERROR -> outputHandler.println("Server error");
            default -> outputHandler.println("Response status code: " + status);
        }
        if (payload.message() != null) {
            outputHandler.print(payload.message());
        }
    }
}
