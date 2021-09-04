package com.mcsim415.wchat.socketHandler;

import com.mcsim415.wchat.crypto.DHExchange;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public abstract class SocketHandler {
    public Socket ClientSocket;
    public String passwordOrg;
    public SocketIO socketIO;

    public abstract Boolean prepareChat(JLabel firstLabel, DHExchange dhe) throws IOException;
}
