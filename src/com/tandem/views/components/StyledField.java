package com.tandem.views.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StyledField extends JPanel {

    private final JTextField field;
    private final String placeholder;

    public StyledField(String placeholder) {
        this.placeholder = placeholder;
        setLayout(new BorderLayout());
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        setPreferredSize(new Dimension(0, 52));
        setAlignmentX(LEFT_ALIGNMENT);

        field = new JTextField();
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        field.setFont(UITheme.F_BODY);
        field.setForeground(UITheme.HINT);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(StyledField.this.placeholder)) {
                    field.setText("");
                    field.setForeground(UITheme.TEXT);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(StyledField.this.placeholder);
                    field.setForeground(UITheme.HINT);
                }
            }
        });

        add(field, BorderLayout.CENTER);
    }

    public String getText() {
        String t = field.getText();
        return t.equals(placeholder) ? "" : t;
    }

    public void setText(String text) {
        field.setText(text);
        field.setForeground(UITheme.TEXT);
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
