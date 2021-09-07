package com.mcsim415.wchat.thread;

import com.mcsim415.wchat.crypto.RSA;
import com.mcsim415.wchat.gui.GuiChatBubble;
import com.mcsim415.wchat.gui.GuiChat;
import com.mcsim415.wchat.gui.WChatGui;
import com.mcsim415.wchat.socketHandler.SocketIO;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.StringTokenizer;

public class chatReceiveThread extends Thread {
    private RSA rsa;
    private GuiChat chatGui;
    private SocketIO socketIO;

    public void setParams(SocketIO _socketIO, RSA _rsa, GuiChat _chatGui) {
        socketIO = _socketIO;
        chatGui = _chatGui;
        rsa = _rsa;
    }

    @Override
    public void run() {
        super.run();

        StringBuilder data = null;
        while (true) {
            try {
                String receive = socketIO.receive();
                if (receive.contains("|||") || receive.contains("|/|") || receive.contains("|\\|")) {
                    if (receive.contains("|||")) {
                        receive = receive.replace("|||", "");
                        receive = rsa.decrypt(receive);
                        data = new StringBuilder(receive);
                        continue;
                    } else if (receive.contains("|/|")) {
                        receive = receive.replace("|/|", "");
                        receive = rsa.decrypt(receive);
                        Objects.requireNonNull(data).append(receive);
                        continue;
                    } else {
                        receive = receive.replace("|\\|", "");
                        receive = rsa.decrypt(receive);
                        Objects.requireNonNull(data).append(receive);
                        receive = Objects.requireNonNull(data).toString();
                        data = null;
                    }
                } else {
                    receive = rsa.decrypt(receive);
                }
                final int size = 380;
                final int maximumSize = size-150;

                GuiChatBubble chatBubble = new GuiChatBubble(GuiChatBubble.LEFT);
                chatBubble.setMaximumSize(new Dimension(size,size));

                JLabel chatL = new JLabel();//"<html><body style='width:" + (size-150) + "px; padding:15px;display:block;'>" + textWithSeparators + "</body></html>");
                chatL.setMaximumSize(new Dimension(size-50,size-50));
                chatL.setFont(chatL.getFont().deriveFont(20f));
                chatL.setOpaque(false);

                FontMetrics metrics = chatL.getFontMetrics(chatL.getFont());

                StringBuilder textWithSeparators = new StringBuilder();
                final StringTokenizer textTokenizer
                        = new StringTokenizer(receive, " \t\n\r", true);

                while (textTokenizer.hasMoreTokens()) {
                    final String part = textTokenizer.nextToken();
                    int chunk = 0, separate = 1;
                    for (int i = 0; i<part.length(); i++) {
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
                xBox.add(chatBubble);
                xBox.add(Box.createHorizontalGlue());

                chatGui.add(xBox);
                chatGui.add(Box.createRigidArea(new Dimension(0,5)));
            } catch (IOException e) {
                WChatGui.getInstance().home();
                JPanel dialogPanel = new JPanel();
                JOptionPane.showMessageDialog(dialogPanel, "Connection Ended.");
                socketIO.close();
                break;
            }
        }
    }
}
