package com.mcsim415.wchat.gui;

import com.mcsim415.wchat.thread.ClientThread;
import com.mcsim415.wchat.thread.ServerThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class GuiChat {
    public String ip, port;
    public JPanel ChatPanel, ChatContentPanel;
    public JLabel firstLabel;

    public GuiChat(String ip, String port) {
        this.ip = ip;
        this.port = port;
        setupUI();
    }

    private void setupUI() {
        Font customFont = new Font("Comic Sans MS", Font.PLAIN, 20);
        ChatPanel = new JPanel();
        ChatPanel.setLayout(new BoxLayout(ChatPanel, BoxLayout.Y_AXIS));

        ChatContentPanel = new JPanel();
        ChatContentPanel.setLayout(new BoxLayout(ChatContentPanel, BoxLayout.Y_AXIS));

        Box xBox1 = Box.createHorizontalBox();
        Box xBox2 = Box.createHorizontalBox();

        Box yBox1 = Box.createVerticalBox();

        JLabel chatName = new JLabel(ip+":"+port);
        chatName.setFont(customFont);

        JButton exitButton, sendButton;

        try {
            Image img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("exit.png"))).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            exitButton = new JButton(new ImageIcon(img));
            exitButton.setBackground(new Color(255, 255, 255));
        } catch (IOException e) {
            exitButton = new JButton("Exit");
            exitButton.setFont(customFont);
        }
        exitButton.addActionListener(e -> WChatGui.getInstance().home());
        // TODO: socket(Thread) Close Together.

        xBox1.add(Box.createHorizontalGlue());
        xBox1.add(chatName);
        xBox1.add(Box.createHorizontalGlue());
        xBox1.add(exitButton);

        JTextField sendText = new JTextField();
        sendText.setFont(customFont);
        sendText.setPreferredSize(new Dimension(700, 35));
        sendText.setMaximumSize(sendText.getPreferredSize());

        try {
            Image img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("send.png"))).getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            sendButton = new JButton(new ImageIcon(img));
            sendButton.setBackground(new Color(255, 255, 255));
        } catch (IOException e) {
            sendButton = new JButton("Send");
            sendButton.setFont(customFont);
        }

        xBox2.add(sendText);
        xBox2.add(sendButton);

        if (Objects.equals(ip, "Local")) {
            firstLabel = new JLabel("Start server at ':"+port+"'...");
        } else {
            firstLabel = new JLabel("<html>Attempt to connect to server, <br>'"+ip+":"+port+"'...</html>");
        }
        firstLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));

        yBox1.add(Box.createVerticalGlue());
        yBox1.add(firstLabel);
        yBox1.add(Box.createVerticalGlue());

        ChatContentPanel.add(yBox1);

        JScrollPane scrollPane = new JScrollPane(ChatContentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(50, 30, 300, 50);

        JLabel test = new JLabel("Test!");
        test.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
        for (int i=0; i<10; i++) {
            ChatContentPanel.add(test);
        }
        // TODO: make scroll

        ChatPanel.add(xBox1);
        ChatPanel.add(scrollPane);
        ChatPanel.add(xBox2);
    }

    public void startServer() {
        ServerThread sThread = new ServerThread();
        sThread.setParams(port);
        sThread.start();
    }

    public void connect() {
        ClientThread cThread = new ClientThread();
        cThread.setParams(ip, port, firstLabel, ChatContentPanel);
        cThread.start();
    }
}
