package com.armemius.lab7.network;

import com.armemius.lab7.collection.CollectionManager;
import com.armemius.lab7.collection.DatabaseManager;
import com.armemius.lab7.commands.exceptions.CommandArgumentException;
import com.armemius.lab7.commands.exceptions.CommandRuntimeException;
import com.armemius.lab7.io.BufferOutputHandler;
import com.armemius.lab7.tasks.RemoteExecutable;
import com.armemius.lab7.tasks.Task;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class NetworkHandler {

    private NetworkHandler() {}

    private final static Logger logger = Logger.getLogger(NetworkHandler.class);
    private static DatagramSocket socket;
    private static PacketBuilder packetBuilder;

    public static final ForkJoinPool forkJoinPool = new ForkJoinPool(6);
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
        final int DEFAULT_PORT_VALUE = 2077;
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

        new Thread(() -> {
            try {
                for (var it : splitByteBuffer(data)) {
                    var pkg = new DatagramPacket(it, it.length, clientAddress, clientPort);
                    socket.send(pkg);
                }
                socket.send(new DatagramPacket(new byte[BUFFER_CAPACITY], BUFFER_CAPACITY, clientAddress, clientPort));
            } catch (IOException ex) {
                logger.error("Error while sending package " + ex.getMessage());
            }

        }).start();
    }

    public static List<byte[]> splitByteBuffer(byte[] source) {

        List<byte[]> result = new ArrayList<>();
        int start = 0;
        while (start < source.length) {
            int end = Math.min(source.length, start + BUFFER_CAPACITY);
            result.add(Arrays.copyOfRange(source, start, end));
            start += BUFFER_CAPACITY;
        }

        return result;
    }


    private static void listen() {
        logger.info("Starting to listen for incoming packages");
        InetAddress lastAddress = null;
        int lastPort = 0;

        while (isRunning) {
            try {
                var packetPart = new DatagramPacket(buffer, buffer.length);
                do {
                    socket.receive(packetPart);
                    logger.debug("Received packet from " + packetPart.getAddress());
                } while (!packetBuilder.append(buffer));
                var packet = packetBuilder.flush();
                lastAddress = packetPart.getAddress();
                lastPort = packetPart.getPort();

                Object obj = PayloadHandler.deserialize(packet);
                if (!(obj instanceof C2SPayload payload)) {
                    logger.error("Received payload with incorrect signature: " + obj.getClass());
                    continue;
                }
                processIncomingPayload(payload, packetPart.getAddress(), packetPart.getPort());
            } catch (ClassNotFoundException ex) {
                logger.error("Received damaged payload: " + ex.getMessage());
                try {
                    send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.ERROR, "Check client version\n\r"), lastAddress, lastPort);
                } catch (IOException ignore) {}
            } catch (IOException ex) {
                if (isRunning) {
                    logger.error("Error while processing input package: " + ex.getMessage());
                }
            }
        }
    }

    private static void processIncomingPayload(C2SPayload payload, InetAddress address, int port) throws IOException {
        logger.debug("Processing " + payload);
        final var type = payload.type();

        if (type == PayloadTypes.HANDSHAKE) {
            send(new S2CPayload(PayloadTypes.HANDSHAKE, StatusCodes.ACCEPTED, null), address, port);
        } else if (type == PayloadTypes.EXECUTE_REQUEST) {
            if (payload.login() == null || payload.password() == null) {
                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.AUTH_REQUIRED, null), address, port);
                return;
            }
            processTask(payload, address, port);
        } else if (type == PayloadTypes.REGISTER_REQUEST) {
            logger.info("New registration request: " + payload.login() + " " + payload.password());
            if (DatabaseManager.register(payload.login(), payload.password())) {
                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.OK, "Successfully registered\n\r"), address, port);
            } else {
                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.AUTH_ERROR, "Login is already in use\n\r"), address, port);
            }
        } else if (type == PayloadTypes.AUTH_REQUEST) {
            logger.info("New auth request: " + payload.login() + " " + payload.password());
            if (DatabaseManager.auth(payload.login(), payload.password())) {
                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.OK, "Successfully authenticated\n\r"), address, port);
            } else {
                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.AUTH_ERROR, "Invalid data provided\n\r"), address, port);
            }
        }
    }

    private static void processTask(C2SPayload payload, InetAddress address, int port) throws IOException {
        final var contextData = payload.data();
        final var buffer = new StringBuffer();
        final var bufferOutputHandler = new BufferOutputHandler(buffer);
        try {
            if (contextData.task() == null) {
                if (contextData.operationId() == 0 && contextData.data().length == 1) {
                    if (contextData.data()[0] instanceof Integer id) {
                        var present = CollectionManager.checkId(id) ;
                        if (present) {
                            if (CollectionManager.checkOwnership(id, payload.login())) {
                                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.OK, null), address, port);
                            } else {
                                send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.BAD_REQUEST, "Permission denied\n\r"), address, port);
                            }
                        } else {
                            send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.BAD_REQUEST, "No element with such id\n\r"), address, port);
                        }
                    }
                }
                return;
            }
            Constructor<? extends Task> constructor = contextData.task().getConstructor();
            var task = constructor.newInstance();
            if (!(task instanceof RemoteExecutable executor)) {
                throw new ClassCastException();
            }
            executor.validate(contextData.operationId(), contextData.data());
            executor.execute(contextData.operationId(), contextData.data(), bufferOutputHandler, payload.login());
            logger.debug("Output buffer size: " + buffer.length());
            send(new S2CPayload(PayloadTypes.RESPONSE, StatusCodes.OK, buffer.toString()), address, port);

        } catch (IllegalArgumentException | CommandRuntimeException ex) {
            send(new S2CPayload(PayloadTypes.RESPONSE,
                            StatusCodes.BAD_REQUEST,
                            ex.getMessage() + "\n\r"),
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
