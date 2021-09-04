package com.mcsim415.wchat.socketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public record ServerSocketHandler(String port) {

    public void start() {
        ServerSocket socket;
        try {
            socket = new ServerSocket();
        } catch (IOException e) {
            return;
        }
        InetSocketAddress isd = new InetSocketAddress(Integer.parseInt(port));
        try {
            socket.bind(isd);
        } catch (IOException e) {
            return;
        }


    }
}
