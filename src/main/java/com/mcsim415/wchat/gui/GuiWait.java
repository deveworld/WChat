package com.mcsim415.wchat.gui;

import javax.swing.*;
import java.awt.*;

public class GuiWait {
    public JPanel WaitPanel;
    public JLabel label;
    public String msg;
    public Font font;

    public GuiWait(String msg, Font font) {
        this.msg = msg;
        this.font = font;
        setupUI();
    }

    private void setupUI() {
        WaitPanel = new JPanel();
        WaitPanel.setLayout(new BoxLayout(WaitPanel, BoxLayout.Y_AXIS));

        Box xBox1 = Box.createHorizontalBox();

        label = new JLabel(msg);
        label.setFont(font);

        xBox1.add(Box.createHorizontalGlue());
        xBox1.add(label);
        xBox1.add(Box.createHorizontalGlue());

        WaitPanel.add(Box.createVerticalGlue());
        WaitPanel.add(xBox1);
        WaitPanel.add(Box.createVerticalGlue());
    }
}
