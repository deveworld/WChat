package com.mcsim415.wchat.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class GuiField {
    public JPanel FieldPanel;
    public final String panelText, labelS, buttonS, wrongLabelS, regex, destFunction;
    public final Object[] destParams;
    public final Object obj;
    public final Font font;

    public static final String text = "FIELD TEXT";

    public GuiField(
            String panelText,
            String labelS,
            String wrongLabelS,
            String buttonS,
            String regex,
            String destFunction,
            Font font,
            String[] destParams,
            Object obj
    ) {
        this.panelText = panelText;
        this.labelS = labelS;
        this.buttonS = buttonS;
        this.font = font;
        this.wrongLabelS = wrongLabelS;
        this.regex = regex;
        this.destFunction = destFunction;
        this.destParams = destParams;
        this.obj = obj;
        setupUI();
    }

    private void checkValid(JTextField field, JLabel label) {
        if (!field.getText().isBlank() && field.getText().matches(regex)) {
            try {
                Class<?> cls = Class.forName(obj.getClass().getName());
                Method m;
                if (destParams.length == 0) {
                    m = cls.getDeclaredMethod(destFunction);
                } else if (destParams.length == 1) {
                    m = cls.getDeclaredMethod(destFunction, String.class);
                } else if (destParams.length == 2) {
                    m = cls.getDeclaredMethod(destFunction, String.class, String.class);
                } else {
                    m = cls.getDeclaredMethod(destFunction, String.class, String.class, String.class);
                }
                int i = 0;
                for (Object param : destParams) {
                    if (param instanceof String) {
                        if (param.equals(text)) {
                            destParams[i] = field.getText();
                        }
                    }
                    i++;
                }
                m.setAccessible(true);
                Objects.requireNonNull(m).invoke(obj, destParams);
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFoundException from Class.forName()");
            } catch (NoSuchMethodException e) {
                System.out.println("NoSuchMethodException from getDeclaredMethod()");
            } catch (InvocationTargetException e) {
                System.out.println("InvocationTargetException from invoke()");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.out.println("IllegalAccessException from invoke()");
            }
        } else {
            label.setText(wrongLabelS);
        }
    }

    private void setupUI() {
        FieldPanel = new JPanel();
        FieldPanel.setLayout(new BoxLayout(FieldPanel, BoxLayout.Y_AXIS));

        Box xBox1 = new GuiNavigation(panelText, false).NavigationBox;
        Box xBox2 = Box.createHorizontalBox();
        Box xBox3 = Box.createHorizontalBox();
        Box xBox4 = Box.createHorizontalBox();

        Box yBox1 = Box.createVerticalBox();

        JLabel label = new JLabel(labelS);
        label.setFont(font);

        JTextField field = new JTextField();
        field.setFont(font);
        field.setPreferredSize(new Dimension(500, 30));
        field.setMaximumSize(field.getPreferredSize());
        int condition = JComponent.WHEN_FOCUSED;
        InputMap inputMap = field.getInputMap(condition);
        ActionMap actionMap = field.getActionMap();

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        inputMap.put(enterKey, enterKey.toString());
        actionMap.put(enterKey.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkValid(field, label);
                field.setText("");
            }
        });

        JButton connectButton = new JButton(buttonS);
        connectButton.setFont(font);
        connectButton.addActionListener(e -> checkValid(field, label));
        connectButton.setBackground(new Color(255, 255, 255));

        xBox2.add(Box.createHorizontalGlue());
        xBox2.add(label);
        xBox2.add(Box.createHorizontalGlue());

        xBox3.add(Box.createHorizontalGlue());
        xBox3.add(field);
        xBox3.add(Box.createHorizontalGlue());

        xBox4.add(Box.createHorizontalGlue());
        xBox4.add(connectButton);
        xBox4.add(Box.createHorizontalGlue());

        yBox1.add(Box.createVerticalGlue());
        yBox1.add(xBox2);
        yBox1.add(Box.createVerticalStrut(30));
        yBox1.add(xBox3);
        yBox1.add(Box.createVerticalStrut(30));
        yBox1.add(xBox4);
        yBox1.add(Box.createVerticalGlue());

        FieldPanel.add(xBox1);
        FieldPanel.add(new JSeparator());
        FieldPanel.add(Box.createVerticalGlue());
        FieldPanel.add(yBox1);
        FieldPanel.add(Box.createVerticalGlue());
    }
}
