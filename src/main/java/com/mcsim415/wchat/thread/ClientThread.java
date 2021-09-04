package com.mcsim415.wchat.thread;

import com.mcsim415.wchat.crypto.DHExchange;
import com.mcsim415.wchat.crypto.RSA;
import com.mcsim415.wchat.gui.GuiChat;
import com.mcsim415.wchat.gui.WChatGui;
import com.mcsim415.wchat.socketHandler.ClientSocketHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientThread extends Thread {
    private String ip, port;
    private GuiChat chatGui;
    public chatSendThread chatSendThread;

    public void setParams(String _ip, String _port, GuiChat _chatGui) {
        ip = _ip;
        port = _port;
        chatGui = _chatGui;
    }

    @Override
    public void run() {
        super.run();

        JLabel firstLabel = chatGui.firstLabel;
        DHExchange dhe = new DHExchange();

        ClientSocketHandler sockHandler = new ClientSocketHandler(ip, port);
        if (sockHandler.connect()) {
            firstLabel.setText("<html>Connected to Server!<br>Prepare Encrypt...</html>");
        } else {
            firstLabel.setText("<html><span style='color: red;'>Failed to Connect Server.</span></html>");
            return;
        }

        Boolean res;
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
