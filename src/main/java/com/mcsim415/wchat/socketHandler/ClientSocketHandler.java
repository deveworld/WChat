package com.mcsim415.wchat.socketHandler;

import com.mcsim415.wchat.Main;
import com.mcsim415.wchat.crypto.DHExchange;
import com.mcsim415.wchat.crypto.SHA256;
import com.mcsim415.wchat.gui.GuiPassword;

import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

public class ClientSocketHandler extends SocketHandler {
    private final String ip, port;
    public Socket ClientSocket;
    public String passwordOrg;
    public SocketIO socketIO;

    public ClientSocketHandler(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public Boolean connect() {
        ClientSocket = new Socket();
        InetSocketAddress isd = new InetSocketAddress(ip, Integer.parseInt(port));
        try {
            ClientSocket.connect(isd);
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    public Boolean prepareChat(JLabel firstLabel, DHExchange dhe) throws IOException {
        // Client
        socketIO = new SocketIO(ClientSocket);
        Boolean res = socketIO.setupStream();
        if (!res) {
            return false;
        }

        socketIO.send(Main.ProtocolVersion);
        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>Version Not Matched.</span></html>");
            socketIO.close();
            return false;
        }

        String received = socketIO.receive();
        try {
            dhe.setP(new BigInteger(received));
        } catch (NumberFormatException e) {
            firstLabel.setText("<html><span style='color: red;'>Prime is not Valid Value.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }
        socketIO.send("100");

        received = socketIO.receive();
        try {
            dhe.setG(new BigInteger(received));
        } catch (NumberFormatException e) {
            firstLabel.setText("<html><span style='color: red;'>G is not Valid Value.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }
        socketIO.send("100");

        dhe.makePrivateKey();

        passwordOrg = new GuiPassword().getText();
        while (Objects.equals(passwordOrg, "")) {
            passwordOrg = new GuiPassword().getText();
        }
        SHA256 sha256 = new SHA256();
        String password = sha256.encrypt(
                sha256.encrypt(sha256.encrypt(Double.toString(Math.pow(Integer.parseInt(port), 2))) + passwordOrg)
        );
        socketIO.send(password);
        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>Password Not Matched.</span></html>");
            socketIO.close();
            return false;
        }

        BigInteger common = dhe.getPrivateKey();
        socketIO.send(common.toString());
        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>PrivateKey Not Approved.</span></html>");
            socketIO.close();
            return false;
        }

        received = socketIO.receive();
        if (!received.matches("^[0-9]+$")) {
            firstLabel.setText("<html><span style='color: red;'>OtherPrivateKey is not Valid Value.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }
        socketIO.send("100");

        BigInteger key = null;
        boolean keyResult;
        try {
            dhe.setFinalCommonKey(new BigInteger(received));
            key = dhe.getFinalCommonKey();
            keyResult = true;
        } catch (Throwable e) {
            keyResult = false;
        }

        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>Server Failed to Make Key.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }

        if (keyResult) {
            socketIO.send("100");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Failed to Make Key.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }

        String keyHash = sha256.encrypt(key.toString());
        socketIO.send(keyHash);
        if (!Objects.equals(socketIO.receive(), "100")) {
            firstLabel.setText("<html><span style='color: red;'>MITM attack suspected.</span></html>");
            socketIO.send("300");
            socketIO.close();
            return false;
        }

        if (Objects.equals(socketIO.receive(), "100")) {
            socketIO.send("100");
            firstLabel.setText("Make RSA Keys...");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Unknown Error</span></html>");
            socketIO.close();
            return false;
        }

        return true;
    }
}
