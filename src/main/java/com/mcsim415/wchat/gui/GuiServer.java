package com.mcsim415.wchat.gui;

import javax.swing.*;
import java.awt.*;

public class GuiServer {
    public JPanel ServerPanel;

    { setupUI(); }

    private void setupUI() {
        String[] params = {
                GuiField.text
        };
        ServerPanel = new GuiField(
                "Server",
                "<html>Please enter the port to wait for the connection:<br>" +
                        "<span style='font-size:17;'>(Port number must be an integer between 0 and 65535)</span></html>",
                "<html>Please enter the port to wait for the connection:<br>" +
                        "<span style='color: red; font-size:17;'>(Port number must be an integer between 0 and 65535)</span></html>",
                "Connect",
                "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$",
                "startServer",
                new Font("Comic Sans MS", Font.PLAIN, 20),
                params,
                this
        ).FieldPanel;
    }

    public void startServer(String text) {
        WChatGui.getInstance().startServer(text);
    }
}
