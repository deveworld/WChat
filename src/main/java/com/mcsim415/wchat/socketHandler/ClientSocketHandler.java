package com.mcsim415.wchat.socketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public record ClientSocketHandler(String ip, String port) {

    public Boolean connect() {
        Socket socket = new Socket();
        InetSocketAddress isd = new InetSocketAddress(ip, Integer.parseInt(port));
        try {
            socket.connect(isd);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void keyExchange() throws IOException {
    }
}
