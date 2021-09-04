package com.mcsim415.wchat.gui;

import com.mcsim415.wchat.crypto.DHExchange;

import javax.swing.*;
import java.awt.*;

public class WChatGui extends JFrame {
    private static class LazyHolder {
        private static final WChatGui instance = new WChatGui();
    }

    public static WChatGui getInstance() {
        System.out.print("a");
        return LazyHolder.instance;
    }

    private WChatGui() {
        super("WChat");
        JPanel panel = new GuiMain().MainPanel;
        setContentPane(panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 750);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void home() {
        JPanel panel = new GuiMain().MainPanel;
        setContentPane(panel);
        setVisible(true);
    }

    public void server() {
        JPanel panel = new GuiServer().ServerPanel;
        setContentPane(panel);
        setVisible(true);
    }

    public void client() {
        JPanel panel = new GuiClient().ClientPanel;
        setContentPane(panel);
        setVisible(true);
    }

    public void startServer(String port) {
        GuiChat panel = new GuiChat("Local", port);
        setContentPane(panel.ChatPanel);
        setVisible(true);
        panel.startServer();
    }

    public void connect(String ip, String port) {
        GuiChat panel = new GuiChat(ip, port);
        setContentPane(panel.ChatPanel);
        setVisible(true);
        panel.connect();
    }
}
