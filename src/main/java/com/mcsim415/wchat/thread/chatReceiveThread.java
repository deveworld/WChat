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
        try {
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
                    final int maximumSize = 25;
                    StringBuilder textWithSeparators = new StringBuilder();
                    final StringTokenizer textTokenizer
                            = new StringTokenizer(receive, " \t\n\r", true);

                    while (textTokenizer.hasMoreTokens()) {
                        final String part = textTokenizer.nextToken();
                        for (int beginIndex = 0; beginIndex < part.length();
                             beginIndex += maximumSize)
                            textWithSeparators.append(beginIndex == 0 ? "" : " ").append(part, beginIndex, Math.min(part.length(),
                                    beginIndex + maximumSize));
                    }

                    final int size = 380;
                    GuiChatBubble chatBorder = new GuiChatBubble(GuiChatBubble.LEFT);
                    chatBorder.setMaximumSize(new Dimension(size,size));

                    JLabel chatL = new JLabel("<html><body style='width:" + (size-150) + "px; padding:15px;display:block;'>" + textWithSeparators + "</body></html>");
                    chatL.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
                    chatL.setMaximumSize(new Dimension(size-50,size-50));
                    chatL.setOpaque(false);

                    chatBorder.add(chatL, BorderLayout.NORTH);
                    chatGui.add(chatBorder);
                    chatGui.add(Box.createRigidArea(new Dimension(0,5)));
                } catch (IOException e) {
                    WChatGui.getInstance().home();
                    JPanel dialogPanel = new JPanel();
                    JOptionPane.showMessageDialog(dialogPanel, "Connection Ended.");
                    break;
                }
            }
        } catch (Throwable e) {
            try {
                socketIO.close();
            } catch (IOException ignored) {}
        }
    }
}
