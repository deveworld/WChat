package com.mcsim415.wchat.socketHandler;

import com.mcsim415.wchat.Main;
import com.mcsim415.wchat.crypto.DHExchange;
import com.mcsim415.wchat.crypto.SHA256;
import com.mcsim415.wchat.gui.GuiPassword;

import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ServerSocketHandler extends SocketHandler {
    private final String port;
    public ServerSocket ServerSocket;
    public Socket ClientSocket;
    public String passwordOrg;
    public SocketIO socketIO;

    public ServerSocketHandler(String port) {
        this.port = port;
    }

    public Boolean start() {
        try {
            ServerSocket = new ServerSocket();
            InetSocketAddress isd = new InetSocketAddress(Integer.parseInt(port));
            ServerSocket.bind(isd);
        } catch (Throwable e) {
            return false;
        }
        return ServerSocket != null;
    }

    public String getClientAddress() {
        if (ClientSocket != null) {
            return ClientSocket.getInetAddress().toString()+":"+ClientSocket.getPort();
        } else {
            return "Not Connected";
        }
    }

    public Boolean accept() {
        try {
            ClientSocket = ServerSocket.accept();
        } catch (Throwable e) {
            return false;
        }
        return ClientSocket != null;
    }

    public Boolean prepareChat(JLabel firstLabel, DHExchange dhe) throws IOException {
        // Server
        socketIO = new SocketIO(ClientSocket);
        Boolean res = socketIO.setupStream();
        if (!res) {
            return false;
        }

        if (Objects.equals(socketIO.receive(), Main.ProtocolVersion)) {
            socketIO.send("100");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Version Not Matched.</span></html>");
            socketIO.send("200");
            socketIO.close();
            return false;
        }

        socketIO.send(dhe.getP().toString());
        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>DHExchange Prime Not Approved.</span></html>");
            socketIO.close();
            return false;
        }

        socketIO.send(dhe.getG().toString());
        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>DHExchange G Not Approved.</span></html>");
            socketIO.close();
            return false;
        }

        dhe.makePrivateKey();

        passwordOrg = new GuiPassword().getText();
        while (Objects.equals(passwordOrg, "")) {
            passwordOrg = new GuiPassword().getText();
        }
        SHA256 sha256 = new SHA256();
        String password = sha256.encrypt(
                sha256.encrypt(sha256.encrypt(Double.toString(Math.pow(Integer.parseInt(port), 2))) + passwordOrg)
        );
        if (Objects.equals(socketIO.receive(), password)) {
            socketIO.send("100");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Password Not Matched.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }

        String received = socketIO.receive();
        if (!received.matches("^[0-9]+$")) {
            firstLabel.setText("<html><span style='color: red;'>OtherPrivateKey is not Valid Value.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }
        socketIO.send("100");

        BigInteger common = dhe.getPrivateKey();
        socketIO.send(common.toString());
        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>PrivateKey Not Approved.</span></html>");
            socketIO.close();
            return false;
        }

        BigInteger key;
        try {
            dhe.setFinalCommonKey(new BigInteger(received));
            key = dhe.getFinalCommonKey();
        } catch (Throwable e) {
            firstLabel.setText("<html><span style='color: red;'>Failed to Make Key.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }
        socketIO.send("100");

        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>Client Failed to Make Key.</span></html>");
            socketIO.close();
            return false;
        }

        String keyHash = sha256.encrypt(key.toString());
        if (Objects.equals(socketIO.receive(), keyHash)) {
            socketIO.send("100");
        } else {
            firstLabel.setText("<html><span style='color: red;'>MITM attack suspected.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }

        socketIO.send("100");
        if (Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("Make RSA Keys...");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Unknown Error.</span></html>");
            socketIO.close();
            return false;
        }

        return true;
    }
}
