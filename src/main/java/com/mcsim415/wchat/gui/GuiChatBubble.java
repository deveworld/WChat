package com.mcsim415.wchat.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.Serial;

public class GuiChatBubble extends JPanel {
    @Serial
    private static final long serialVersionUID = -5389178141802153305L;
    private final int align;
    public static int LEFT = 0;
    public static int RIGHT = 1;

    public GuiChatBubble(int align) {
        this.align = align;
        this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
    }

    @Override
    protected void paintComponent(final Graphics g) {
        int radius = 10;
        int arrowSize = 12;
        int strokeThickness = 3;
        int padding = strokeThickness / 2;

        final Graphics2D g2d = (Graphics2D) g;
        if (align == LEFT) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.ORANGE);
        }
        int bottomLineY = getHeight() - strokeThickness;
        int width = getWidth() - arrowSize - (strokeThickness * 2);
        RoundRectangle2D.Double rect;
        if (align == LEFT) {
            int x = padding + strokeThickness + arrowSize;
            g2d.fillRect(x, padding, width, bottomLineY);
            rect = new RoundRectangle2D.Double(x, padding, width, bottomLineY, radius, radius);
        } else {
            g2d.fillRect(padding, padding, width, bottomLineY);
            rect = new RoundRectangle2D.Double(padding, padding, width, bottomLineY, radius, radius);
        }
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setStroke(new BasicStroke(strokeThickness));
        Polygon arrow = new Polygon();
        if (align == LEFT) {
            arrow.addPoint(20, 8);
            arrow.addPoint(0, 3);
            arrow.addPoint(20, 12);
        } else {
            arrow.addPoint(width, 8);
            arrow.addPoint(width + arrowSize, 3);
            arrow.addPoint(width, 12);
        }
        Area area = new Area(rect);
        area.add(new Area(arrow));
        g2d.draw(area);
    }
}
