package com.mcsim415.wchat.thread;

import com.mcsim415.wchat.crypto.DHExchange;
import com.mcsim415.wchat.crypto.RSA;
import com.mcsim415.wchat.gui.GuiChat;
import com.mcsim415.wchat.gui.WChatGui;
import com.mcsim415.wchat.socketHandler.ServerSocketHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ServerThread extends Thread {
    private String port;
    private GuiChat chatGui;
    public chatSendThread chatSendThread;
    public ServerSocketHandler sockHandler;

    public void setParams(String _port, GuiChat _chatGui) {
        port = _port;
        chatGui = _chatGui;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            if (sockHandler.ServerSocket != null) {
                sockHandler.ServerSocket.close();
            }
            if (sockHandler.ClientSocket != null) {
                sockHandler.ClientSocket.close();
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void run() {
        super.run();

        JLabel firstLabel = chatGui.firstLabel;

        firstLabel.setText("<html>Make Keys...</html>");
        DHExchange dhe = new DHExchange();
        dhe.makeCommonKeys();
        firstLabel.setText("<html>Done!<br>Starting Server...</html>");

        sockHandler = new ServerSocketHandler(port);
        Boolean res = sockHandler.start();

        if (res) {
            firstLabel.setText("<html>Server Started!<br>Waiting Client Connecting...</html>");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Failed to Server Started.</span></html>");
            return;
        }

        res = sockHandler.accept();
        if (Thread.interrupted()) {
            return;
        }
        if (res) {
            chatGui.chatName.setText(sockHandler.getClientAddress());
            firstLabel.setText("<html>Client Connected!<br>Prepare Encrypt...</html>");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Failed to Client Connect.</span></html>");
            return;
        }

        try {
            res = sockHandler.prepareChat(firstLabel, dhe);
        } catch (IOException e) {
            WChatGui.getInstance().home();
            JPanel dialogPanel = new JPanel();
            JOptionPane.showMessageDialog(dialogPanel, "Connection Ended.");
            return;
        }
        if (!res) {
            return;
        }
        RSA rsa = new RSA(dhe.getFinalCommonKey(), sockHandler.passwordOrg);

        sockHandler.passwordOrg = null;
        dhe = null;
        System.gc();

        chatGui.remove(chatGui.yBox1);

        chatGui.add(Box.createVerticalGlue());

        chatSendThread = new chatSendThread();
        chatReceiveThread chatReceiveThread = new chatReceiveThread();

        chatSendThread.setParams(sockHandler.socketIO, rsa, chatGui);
        chatReceiveThread.setParams(sockHandler.socketIO, rsa, chatGui);

        chatSendThread.start();
        chatReceiveThread.start();
    }
}
