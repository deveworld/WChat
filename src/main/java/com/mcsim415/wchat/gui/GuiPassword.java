package com.mcsim415.wchat.gui;

import javax.swing.*;

public class GuiPassword extends JDialog {
    public String text;
    private JPanel dialogPanel;
    private JTextField textField;

    { setupUI(); }

    private void setupUI() {
        dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));

        dialogPanel.add(new JLabel("Enter Password."));
        textField = new JTextField(10);
        dialogPanel.add(textField);
    }

    public String getText() {
        Object[] options = {
                "Ok"
        };
        JOptionPane.showOptionDialog(null, dialogPanel, "Enter the Password.",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, null);
        return textField.getText();
    }
}
