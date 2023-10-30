package com.armemius.lab7.network;

import java.io.*;

public class PayloadHandler {
    public static byte[] serialize(Serializable obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException {
        var byteArrayInputStream = new ByteArrayInputStream(buffer);
        var objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }
}
