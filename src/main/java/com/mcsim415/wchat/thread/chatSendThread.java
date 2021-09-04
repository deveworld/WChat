package com.mcsim415.wchat.thread;

import com.mcsim415.wchat.crypto.RSA;
import com.mcsim415.wchat.gui.GuiChatBubble;
import com.mcsim415.wchat.gui.GuiChat;
import com.mcsim415.wchat.gui.WChatGui;
import com.mcsim415.wchat.socketHandler.SocketIO;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
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
                if (sendChats.length == 1) {
                    socketIO.send(sendChat);
                } else {
                    if (i == 1) {
                        socketIO.send("|||"+sendChat); // FIST
                    } else if (i == sendChats.length) {
                        socketIO.send("|\\|"+sendChat); // END
                    } else {
                        socketIO.send("|/|"+sendChat); // MIDDLE
                    }
                }
            } catch (IOException e) {
                WChatGui.getInstance().home();
                JPanel dialogPanel = new JPanel();
                JOptionPane.showMessageDialog(dialogPanel, "Connection Ended.");
                running = false;
            }
            i++;
        }
        final int maximumSize = 25;
        StringBuilder textWithSeparators = new StringBuilder();
        final StringTokenizer textTokenizer
                = new StringTokenizer(chat, " \t\n\r", true);

        while (textTokenizer.hasMoreTokens()) {
            final String part = textTokenizer.nextToken();
            for (int beginIndex = 0; beginIndex < part.length();
                 beginIndex += maximumSize)
                textWithSeparators.append(beginIndex == 0 ? "" : " ").append(part, beginIndex, Math.min(part.length(),
                        beginIndex + maximumSize));
        }

        final int size = 380;
        GuiChatBubble chatBorder = new GuiChatBubble(GuiChatBubble.RIGHT);
        chatBorder.setMaximumSize(new Dimension(size,size));

        JLabel chatL = new JLabel("<html><body style='width:" + (size-150) + "px; padding:15px;display:block;'>" + textWithSeparators + "</body></html>");
        chatL.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        chatL.setMaximumSize(new Dimension(size-50,size-50));
        chatL.setOpaque(false);

        chatBorder.add(chatL, BorderLayout.NORTH);
        chatGui.add(chatBorder);
        chatGui.add(Box.createRigidArea(new Dimension(0,5)));
    }

    @Override
    public void run() {
        super.run();

        try {
            while (running) {
                Thread.onSpinWait();
            }
        } catch (Throwable e) {
            try {
                socketIO.close();
            } catch (IOException ignored) {}
        }
    }
}
