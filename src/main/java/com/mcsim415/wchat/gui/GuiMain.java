package com.mcsim415.wchat.gui;

import javax.swing.*;
import java.awt.*;

public class GuiMain {
    public JPanel MainPanel;

    { setupUI(); }

    private void setupUI() {
        Font buttonFont = new Font("Comic Sans MS", Font.PLAIN, 35);
        MainPanel = new JPanel();
        MainPanel.setLayout(new BoxLayout(MainPanel, BoxLayout.Y_AXIS));

        Box xBox1 = new GuiNavigation("Main", true).NavigationBox;
        Box xBox2 = Box.createHorizontalBox();
        Box xBox3 = Box.createHorizontalBox();

        Box yBox1 = Box.createVerticalBox();

        JLabel wchatLabel = new JLabel("WChat");
        wchatLabel.setFont(new Font("Impact", Font.PLAIN, 90));

        xBox2.add(Box.createHorizontalGlue());
        xBox2.add(wchatLabel);
        xBox2.add(Box.createHorizontalGlue());

        JButton serverButton = new JButton("Server");
        serverButton.setFont(buttonFont);
        serverButton.addActionListener(e -> WChatGui.getInstance().server());
        serverButton.setBackground(new Color(255, 255, 255));

        JButton clientButton = new JButton("Client");
        clientButton.setFont(buttonFont);
        clientButton.addActionListener(e -> WChatGui.getInstance().client());
        clientButton.setBackground(new Color(255, 255, 255));

        yBox1.add(Box.createVerticalGlue());
        yBox1.add(serverButton);
        yBox1.add(Box.createVerticalGlue());
        yBox1.add(clientButton);
        yBox1.add(Box.createVerticalGlue());

        xBox3.add(Box.createHorizontalGlue());
        xBox3.add(yBox1);
        xBox3.add(Box.createHorizontalGlue());

        MainPanel.add(xBox1);
        MainPanel.add(new JSeparator());
        MainPanel.add(Box.createVerticalGlue());
        MainPanel.add(xBox2);
        MainPanel.add(Box.createVerticalGlue());
        MainPanel.add(xBox3);
        MainPanel.add(Box.createVerticalGlue());
    }

}
