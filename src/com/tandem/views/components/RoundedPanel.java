package com.tandem.views.components;

import java.awt.*;
import javax.swing.*;

public class RoundedPanel extends JPanel {

    private final Color bg;
    private final Color border;
    private final float borderW;

    public RoundedPanel(Color bg) {
        this(bg, null, 0f);
    }

    public RoundedPanel(Color bg, Color border) {
        this(bg, border, 1.5f);
    }

    public RoundedPanel(Color bg, Color border, float borderW) {
        this.bg = bg;
        this.border = border;
        this.borderW = borderW;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UITheme.R, UITheme.R);
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(borderW));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UITheme.R, UITheme.R);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
