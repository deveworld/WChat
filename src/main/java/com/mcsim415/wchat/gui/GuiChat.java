package com.mcsim415.wchat.gui;

import com.mcsim415.wchat.thread.ClientThread;
import com.mcsim415.wchat.thread.ServerThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

public class GuiChat {
    private final String ip, port;
    private JScrollPane scrollPane;
    private ServerThread sThread;
    private ClientThread cThread;
    public JPanel ChatPanel, ChatContentPanel;
    public JLabel firstLabel, chatName;
    public Box yBox1;

    public GuiChat(String ip, String port) {
        this.ip = ip;
        this.port = port;
        setupUI();
    }

    private void setupUI() {
        Font customFont = new Font("Comic Sans MS", Font.PLAIN, 20);
        ChatPanel = new JPanel();
        ChatPanel.setLayout(new BoxLayout(ChatPanel, BoxLayout.Y_AXIS));

        ChatContentPanel = new JPanel();
        ChatContentPanel.setLayout(new BoxLayout(ChatContentPanel, BoxLayout.Y_AXIS));

        Box xBox1 = Box.createHorizontalBox();
        Box xBox2 = Box.createHorizontalBox();

        yBox1 = Box.createVerticalBox();

        chatName = new JLabel(ip+":"+port);
        chatName.setFont(customFont);

        JButton exitButton, sendButton;

        try {
            Image img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("exit.png"))).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            exitButton = new JButton(new ImageIcon(img));
            exitButton.setBackground(new Color(255, 255, 255));
        } catch (IOException e) {
            exitButton = new JButton("Exit");
            exitButton.setFont(customFont);
        }
        exitButton.addActionListener(e -> {
            if (sThread != null) {
                sThread.interrupt();
            }
            if (cThread != null) {
                cThread.interrupt();
            }
            WChatGui.getInstance().home();
        });

        xBox1.add(Box.createHorizontalGlue());
        xBox1.add(chatName);
        xBox1.add(Box.createHorizontalGlue());
        xBox1.add(exitButton);

        JTextArea sendTextArea = new JTextArea();
        sendTextArea.setFont(sendTextArea.getFont().deriveFont(20f));
        sendTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    KeyboardFocusManager.
                            getCurrentKeyboardFocusManager().focusNextComponent();
                } else if (e.getKeyCode() == KeyEvent.VK_TAB
                        &&  e.isShiftDown()) {
                    e.consume();
                    KeyboardFocusManager.
                            getCurrentKeyboardFocusManager().focusPreviousComponent();
                }
            }
        });
        int condition = JComponent.WHEN_FOCUSED;
        InputMap inputMap = sendTextArea.getInputMap(condition);
        ActionMap actionMap = sendTextArea.getActionMap();

        UndoManager undoManager = new UndoManager();
        Document doc = sendTextArea.getDocument();
        doc.addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "Undo");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), "Redo");

        actionMap.put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        });
        actionMap.put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        });

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        inputMap.put(enterKey, enterKey.toString());
        actionMap.put(enterKey.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChat(sendTextArea.getText());
                sendTextArea.setText("");
            }
        });

        KeyStroke ctrlEnterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(ctrlEnterKey, ctrlEnterKey.toString());
        actionMap.put(ctrlEnterKey.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendTextArea.append("\n");
            }
        });

        JScrollPane sendText = new JScrollPane(sendTextArea);
        sendText.setPreferredSize(new Dimension(700, 70));
        sendText.setMaximumSize(new Dimension(700, 70));
        sendText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sendText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendText.getVerticalScrollBar().setUnitIncrement(1);

        try {
            Image img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("send.png"))).getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            sendButton = new JButton(new ImageIcon(img));
            sendButton.setBackground(new Color(255, 255, 255));
        } catch (IOException e) {
            sendButton = new JButton("Send");
            sendButton.setFont(customFont);
        }
        sendButton.addActionListener(e -> { sendChat(sendTextArea.getText()); sendTextArea.setText(""); });

        xBox2.add(sendText);
        xBox2.add(sendButton);

        if (Objects.equals(ip, "Server Open at - Local")) {
            firstLabel = new JLabel("Starting server at ':"+port+"'...");
        } else {
            firstLabel = new JLabel("<html>Attempt to connect to server, <br>'"+ip+":"+port+"'...</html>");
        }
        firstLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));

        yBox1.add(Box.createVerticalGlue());
        yBox1.add(firstLabel);
        yBox1.add(Box.createVerticalGlue());

        ChatContentPanel.add(yBox1);

        scrollPane = new JScrollPane(ChatContentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(6);

        ChatPanel.add(xBox1);
        ChatPanel.add(scrollPane);
        ChatPanel.add(xBox2);
    }

    public void sendChat(String text) {
        if (!Objects.equals(text, "")) {
            if (Objects.equals(ip, "Server Open at - Local")) {
                if (sThread.chatSendThread != null) {
                    sThread.chatSendThread.sendChat(text);
                }
            } else {
                if (cThread.chatSendThread != null) {
                    cThread.chatSendThread.sendChat(text);
                }
            }
        }
    }

    public void startServer() {
        sThread = new ServerThread();
        sThread.setParams(port, this);
        sThread.start();
    }

    public void connect() {
        cThread = new ClientThread();
        cThread.setParams(ip, port, this);
        cThread.start();
    }

    synchronized public void remove(Component comp) {
        ChatContentPanel.remove(comp);
        ChatContentPanel.revalidate();
        ChatContentPanel.repaint();
        goLastScroll();
    }

    synchronized public void add(Component comp) {
        ChatContentPanel.add(comp);
        ChatContentPanel.revalidate();
        ChatContentPanel.repaint();
        goLastScroll();
    }

    public void goLastScroll() {
        scrollPane.validate();
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
}
