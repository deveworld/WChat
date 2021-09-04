package com.mcsim415.wchat.socketHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class SocketIO {
    private final Socket socket;
    private InputStream receiver;
    private OutputStream sender;

    public SocketIO(Socket socket) {
        this.socket = socket;
    }

    public Boolean setupStream() {
        try {
            receiver = socket.getInputStream();
            sender = socket.getOutputStream();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String receive() throws IOException {
        try {
            byte[] data = new byte[4];
            receiver.read(data, 0, 4);
            ByteBuffer b = ByteBuffer.wrap(data);
            b.order(ByteOrder.LITTLE_ENDIAN); // little_endian
            int length = b.getInt();
            data = new byte[length];
            receiver.read(data, 0, length);
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            return "";
        }
    }

    public void send(String msg) throws IOException {
        byte[] data = msg.getBytes();
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(data.length);
        sender.write(b.array(), 0, 4);
        sender.write(data);
    }

    public void close() throws IOException {
        socket.close();
    }
}
