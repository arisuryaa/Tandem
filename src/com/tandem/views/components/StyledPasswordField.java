package com.tandem.views.components;

import java.awt.*;
import javax.swing.*;

public class StyledPasswordField extends JPanel {

    private final JPasswordField field;

    public StyledPasswordField() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        setPreferredSize(new Dimension(0, 52));
        setAlignmentX(LEFT_ALIGNMENT);

        field = new JPasswordField();
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        field.setFont(UITheme.F_BODY);

        add(field, BorderLayout.CENTER);
    }

    public String getPasswordText() {
        return new String(field.getPassword());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UITheme.CARD);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UITheme.R, UITheme.R);
        g2.setColor(UITheme.BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UITheme.R, UITheme.R);
        g2.dispose();
    }
}
