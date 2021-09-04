package com.mcsim415.wchat.gui;

import javax.swing.*;
import java.awt.*;

public class GuiClient {
    public JPanel ClientPanel;

    { setupUI(); }

    private void setupUI() {
        String[] params = {
                GuiField.text
        };
        ClientPanel = new GuiField(
                "Client",
                "<html>Please enter the IP(ipv4) of the server to connect to: <br>" +
                        "<span style='font-size:17;'>(Ip address must be a valid ipv4 address.)</span></html>",
                "<html>Please enter the IP(ipv4) of the server to connect to: <br>" +
                        "<span style='color: red; font-size:17;'>(Ip address must be a valid ipv4 address.)</span></html>",
                "Confirm",
                "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
                "checkPort",
                new Font("Comic Sans MS", Font.PLAIN, 20),
                params,
                this
        ).FieldPanel;
    }

    private void checkPort(String ip) {
        String[] params = {
                ip,
                GuiField.text
        };
        ClientPanel = new GuiField(
                "Client",
                "<html>Please enter the port of the server to connect to: <br>" +
                        "<span style='font-size:17;'>(Port number must be an integer between 0 and 65535)</span></html>",
                "<html>Please enter the port of the server to connect to: <br>" +
                        "<span style='color: red; font-size:17;'>(Port number must be an integer between 0 and 65535)</span></html>",
                "Connect",
                "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$",
                "connect",
                new Font("Comic Sans MS", Font.PLAIN, 20),
                params,
                this
        ).FieldPanel;
        WChatGui.getInstance().setContentPane(ClientPanel);
        WChatGui.getInstance().setVisible(true);
    }

    public void connect(String ip, String port) {
        WChatGui.getInstance().connect(ip, port);
    }
}
