package com.mcsim415.wchat.thread;

import com.mcsim415.wchat.crypto.RSA;
import com.mcsim415.wchat.gui.GuiChatBubble;
import com.mcsim415.wchat.gui.GuiChat;
import com.mcsim415.wchat.gui.WChatGui;
import com.mcsim415.wchat.socketHandler.SocketIO;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class chatSendThread extends Thread {
    private RSA rsa;
    private GuiChat chatGui;
    private SocketIO socketIO;
    private volatile boolean running = true;

    public void setParams(SocketIO _sockHandler, RSA _rsa, GuiChat _chatGui) {
        socketIO = _sockHandler;
        rsa = _rsa;
        chatGui = _chatGui;
    }

    public void sendChat(String chat) {
        int i = 1;
        String[] sendChats = rsa.encrypt(chat);
        for (String sendChat:sendChats) {
            try {
                if (i == 1) {
                    socketIO.send("|||"+sendChat); // FIST
                } else if (i == sendChats.length) {
                    socketIO.send("|\\|"+sendChat); // END
                } else {
                    socketIO.send("|/|"+sendChat); // MIDDLE
                }
            } catch (IOException e) {
                WChatGui.getInstance().home();
                JPanel dialogPanel = new JPanel();
                JOptionPane.showMessageDialog(dialogPanel, "Connection Ended.");
                socketIO.close();
                running = false;
                e.printStackTrace();
                return;
            }
            i++;
        }
        final int size = 380;
        final int maximumSize = size-150;

        GuiChatBubble chatBubble = new GuiChatBubble(GuiChatBubble.RIGHT);
        chatBubble.setMaximumSize(new Dimension(size,size));

        JLabel chatL = new JLabel();//"<html><body style='width:" + (size-150) + "px; padding:15px;display:block;'>" + textWithSeparators + "</body></html>");
        chatL.setMaximumSize(new Dimension(size-50,size-50));
        chatL.setFont(chatL.getFont().deriveFont(20f));
        chatL.setOpaque(false);

        FontMetrics metrics = chatL.getFontMetrics(chatL.getFont());

        StringBuilder textWithSeparators = new StringBuilder();
        final StringTokenizer textTokenizer
                = new StringTokenizer(chat, " \t\n\r", true);

        while (textTokenizer.hasMoreTokens()) {
            final String part = textTokenizer.nextToken();
            int chunk = 0, separate = 1;
            for (i = 0; i<part.length(); i++) {
                if (chunk / maximumSize == separate) {
                    textWithSeparators.append("<br>");
                    separate++;
                }
                if (part.charAt(i) == '\n') {
                    textWithSeparators.append("<br>");
                } else if (part.charAt(i) == '\t') {
                    textWithSeparators.append("<span class='tab' style='white-space: pre;'>&#9;</span>");
                } else {
                    textWithSeparators.append(part.charAt(i));
                }
                chunk += metrics.stringWidth(part.substring(i, i+1));
            }
        }
        chatL.setText("<html><body style='width:" + (size-150) + "px; padding:15px;display:block;'>" + textWithSeparators + "</body></html>");

        chatBubble.add(chatL, BorderLayout.NORTH);
        Box xBox = Box.createHorizontalBox();
        xBox.add(Box.createHorizontalGlue());
        xBox.add(chatBubble);

        chatGui.add(xBox);
        chatGui.add(Box.createRigidArea(new Dimension(0,5)));
    }

    @Override
    public void run() {
        super.run();

        while (running) {
            Thread.onSpinWait();
        }
    }
}
