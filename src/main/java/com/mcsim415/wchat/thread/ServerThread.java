package com.mcsim415.wchat.thread;

import com.mcsim415.wchat.socketHandler.ServerSocketHandler;

public class ServerThread extends Thread {
    private String port;

    public void setParams(String _port) {
        port = _port;
    }

    @Override
    public void run() {
        super.run();

        ServerSocketHandler sockHandler = new ServerSocketHandler(port);
        sockHandler.start();
    }
}
