package com.tandem.views;

import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.services.Session;
import com.tandem.views.components.UITheme;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class CreateTeamPanel extends javax.swing.JPanel {

    private final MainFrame frame;
    private final TeamController tc = new TeamController();
    private JComboBox<String> categoryBox, maxSizeBox;

    public CreateTeamPanel(MainFrame frame) {
        this.frame = frame;
        initComponents();

        // Style form fields
        styleField(teamNameField);
        styleField(descField);
        styleField(compNameField);
        styleField(deadlineField);

        // Category + MaxSize rows added programmatically below checkboxes area
        String[] categories = {"Hackathon", "Business", "Scientific", "Design", "Other"};
        String[] sizes      = {"2", "3", "4", "5", "6"};
        categoryBox = new JComboBox<>(categories);
        maxSizeBox  = new JComboBox<>(sizes);
        categoryBox.setFont(UITheme.F_BODY);
        maxSizeBox.setFont(UITheme.F_BODY);
        maxSizeBox.setSelectedIndex(1);

        // Disable user's own role
        User me = Session.getCurrentUser();
        if (me.getRole().equals("Hacker"))  { cbHacker.setEnabled(false); cbHacker.setSelected(false); }
        if (me.getRole().equals("Hipster")) { cbHipster.setEnabled(false); cbHipster.setSelected(false); }
        if (me.getRole().equals("Hustler")) { cbHustler.setEnabled(false); cbHustler.setSelected(false); }

        backLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { frame.showDashboard(); }
        });

        createButton.addActionListener(e -> doCreate(me));
    }

    private void styleField(JTextField f) {
        f.setBackground(UITheme.CARD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
    }

    private void doCreate(User leader) {
        String tName = teamNameField.getText().trim();
        String desc  = descField.getText().trim();
        String cName = compNameField.getText().trim();
        String dead  = deadlineField.getText().trim();

        if (tName.isEmpty() || cName.isEmpty() || dead.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Team name, competition name, dan deadline wajib diisi!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<String> slots = new ArrayList<>();
        if (cbHacker.isSelected()  && cbHacker.isEnabled())  slots.add("Hacker");
        if (cbHipster.isSelected() && cbHipster.isEnabled()) slots.add("Hipster");
        if (cbHustler.isSelected() && cbHustler.isEnabled()) slots.add("Hustler");

        if (slots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu open slot!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cat  = (String) categoryBox.getSelectedItem();
        int maxSize = Integer.parseInt((String) maxSizeBox.getSelectedItem());
        Competition comp = new Competition(
                java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                cName, cat, dead, maxSize);

        tc.createTeam(leader, tName, desc, comp, slots);
        JOptionPane.showMessageDialog(this, "Tim \"" + tName + "\" berhasil dibuat!");
        frame.showDashboard();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        backLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        subLabel = new javax.swing.JLabel();
        teamNameLabel = new javax.swing.JLabel();
        teamNameField = new javax.swing.JTextField();
        descLabel = new javax.swing.JLabel();
        descField = new javax.swing.JTextField();
        compNameLabel = new javax.swing.JLabel();
        compNameField = new javax.swing.JTextField();
        deadlineLabel = new javax.swing.JLabel();
        deadlineField = new javax.swing.JTextField();
        cbHacker = new javax.swing.JCheckBox();
        cbHipster = new javax.swing.JCheckBox();
        cbHustler = new javax.swing.JCheckBox();
        createButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(247, 247, 247));

        backLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        backLabel.setForeground(new java.awt.Color(107, 107, 107));
        backLabel.setText("← Back");
        backLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 22));
        titleLabel.setForeground(new java.awt.Color(26, 26, 26));
        titleLabel.setText("Create Team");

        subLabel.setFont(new java.awt.Font("SansSerif", 0, 13));
        subLabel.setForeground(new java.awt.Color(107, 107, 107));
        subLabel.setText("Set up your competition team and open skill slots.");

        teamNameLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        teamNameLabel.setText("Team Name");

        teamNameField.setFont(new java.awt.Font("SansSerif", 0, 14));
        teamNameField.setPreferredSize(new java.awt.Dimension(394, 44));

        descLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        descLabel.setText("Team Description / Concept");

        descField.setFont(new java.awt.Font("SansSerif", 0, 14));
        descField.setPreferredSize(new java.awt.Dimension(394, 44));

        compNameLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        compNameLabel.setText("Competition Name");

        compNameField.setFont(new java.awt.Font("SansSerif", 0, 14));
        compNameField.setPreferredSize(new java.awt.Dimension(394, 44));

        deadlineLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        deadlineLabel.setText("Deadline (YYYY-MM-DD)");

        deadlineField.setFont(new java.awt.Font("SansSerif", 0, 14));
        deadlineField.setPreferredSize(new java.awt.Dimension(394, 44));

        cbHacker.setBackground(new java.awt.Color(247, 247, 247));
        cbHacker.setFont(new java.awt.Font("SansSerif", 0, 14));
        cbHacker.setText("Hacker  (Technical/IT)");

        cbHipster.setBackground(new java.awt.Color(247, 247, 247));
        cbHipster.setFont(new java.awt.Font("SansSerif", 0, 14));
        cbHipster.setText("Hipster (UI/UX Design)");

        cbHustler.setBackground(new java.awt.Color(247, 247, 247));
        cbHustler.setFont(new java.awt.Font("SansSerif", 0, 14));
        cbHustler.setText("Hustler (Business/Marketing)");

        createButton.setBackground(new java.awt.Color(26, 26, 26));
        createButton.setFont(new java.awt.Font("SansSerif", 1, 15));
        createButton.setForeground(java.awt.Color.white);
        createButton.setText("Create Team");
        createButton.setPreferredSize(new java.awt.Dimension(394, 54));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(backLabel)
                    .addComponent(titleLabel)
                    .addComponent(subLabel)
                    .addComponent(teamNameLabel)
                    .addComponent(teamNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(descLabel)
                    .addComponent(descField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(compNameLabel)
                    .addComponent(compNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(deadlineLabel)
                    .addComponent(deadlineField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(cbHacker)
                    .addComponent(cbHipster)
                    .addComponent(cbHustler)
                    .addComponent(createButton, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(backLabel)
                .addGap(12, 12, 12)
                .addComponent(titleLabel)
                .addGap(4, 4, 4)
                .addComponent(subLabel)
                .addGap(20, 20, 20)
                .addComponent(teamNameLabel)
                .addGap(8, 8, 8)
                .addComponent(teamNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(descLabel)
                .addGap(8, 8, 8)
                .addComponent(descField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(compNameLabel)
                .addGap(8, 8, 8)
                .addComponent(compNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(deadlineLabel)
                .addGap(8, 8, 8)
                .addComponent(deadlineField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(cbHacker)
                .addGap(8, 8, 8)
                .addComponent(cbHipster)
                .addGap(8, 8, 8)
                .addComponent(cbHustler)
                .addGap(24, 24, 24)
                .addComponent(createButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JLabel backLabel;
    private javax.swing.JCheckBox cbHacker;
    private javax.swing.JCheckBox cbHipster;
    private javax.swing.JCheckBox cbHustler;
    private javax.swing.JTextField compNameField;
    private javax.swing.JLabel compNameLabel;
    private javax.swing.JButton createButton;
    private javax.swing.JTextField deadlineField;
    private javax.swing.JLabel deadlineLabel;
    private javax.swing.JTextField descField;
    private javax.swing.JLabel descLabel;
    private javax.swing.JLabel subLabel;
    private javax.swing.JTextField teamNameField;
    private javax.swing.JLabel teamNameLabel;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration
}
