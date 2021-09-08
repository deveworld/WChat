package com.mcsim415.wchat.gui;

import javax.swing.*;
import java.awt.*;

public class WChatGui extends JFrame {
    private static class LazyHolder {
        private static final WChatGui instance = new WChatGui();
    }

    public static WChatGui getInstance() {
        return LazyHolder.instance;
    }

    private WChatGui() {
        super("WChat");
        setUndecorated(true);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        setMinimumSize(new Dimension(480, 750));
        setMaximumSize(new Dimension(600, 900));
        setSize(480, 750);
        setLocationRelativeTo(null);

        JPanel panel = new GuiMain().MainPanel;
        setContentPane(panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        GuiChat panel = new GuiChat("Server Open at - Local", port);
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
