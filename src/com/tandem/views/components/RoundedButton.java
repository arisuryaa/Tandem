package com.tandem.views.components;

import java.awt.*;
import javax.swing.*;

public class RoundedButton extends JButton {

    private final Color bg;

    public RoundedButton(String text, Color bg, Color fg) {
        super(text);
        this.bg = bg;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(UITheme.F_BTN);
        setForeground(fg);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        setPreferredSize(new Dimension(300, 54));
        setAlignmentX(LEFT_ALIGNMENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isPressed() ? bg.darker() : bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.R, UITheme.R);
        g2.dispose();
        super.paintComponent(g);
    }
}
