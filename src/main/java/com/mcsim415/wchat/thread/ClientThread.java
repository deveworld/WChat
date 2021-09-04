package com.mcsim415.wchat.thread;

import com.mcsim415.wchat.socketHandler.ClientSocketHandler;

import javax.swing.*;
import java.io.IOException;

public class ClientThread extends Thread {
    private String ip, port;
    private JLabel firstLabel;
    private JPanel ChatContentPanel;

    public void setParams(String _ip, String _port, JLabel _firstLabel, JPanel _ChatContentPanel) {
        ip = _ip;
        port = _port;
        firstLabel = _firstLabel;
        ChatContentPanel = _ChatContentPanel;
    }

    @Override
    public void run() {
        super.run();

        ClientSocketHandler sockHandler = new ClientSocketHandler(ip, port);
        if (!sockHandler.connect()) {
            firstLabel.setText("<html><span style='color: red;'>Failed.</span></html>");
            return;
        }
        firstLabel.setText("Connected! Setting Encrypt!");
        try {
            sockHandler.keyExchange();
        } catch (IOException e) {
            firstLabel.setText("<html><span style='color: red;'>Failed To Set Encrypt.</span></html>");
            return;
        }
        ChatContentPanel.remove(0);
        Box yBox1 = Box.createVerticalBox();

        firstLabel.setText("-----Start Chat-----");

        yBox1.add(firstLabel);
        yBox1.add(Box.createVerticalGlue());

        ChatContentPanel.add(yBox1);
    }
}
