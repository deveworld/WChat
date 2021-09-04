package com.mcsim415.wchat.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class GuiNavigation {
    public Box NavigationBox;
    private final String panelText;

    public GuiNavigation(String panelText, Boolean isHome) {
        this.panelText = panelText;
        setupBox(isHome);
    }

    private void setupBox(Boolean isHome) {
        NavigationBox = Box.createHorizontalBox();

        JLabel panelLabel = new JLabel(panelText);
        panelLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

        NavigationBox.add(Box.createHorizontalGlue());
        NavigationBox.add(panelLabel);
        NavigationBox.add(Box.createHorizontalGlue());
        if (!isHome) {
            JButton backButton;
            try {
                Image img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("back.png"))).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                backButton = new JButton(new ImageIcon(img));
                backButton.setBackground(new Color(255, 255, 255));
            } catch (IOException e) {
                backButton = new JButton("Back");
                backButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            }
            backButton.addActionListener(e -> WChatGui.getInstance().home());
            NavigationBox.add(backButton);
        }
    }
}
