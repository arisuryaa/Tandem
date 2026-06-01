package com.tandem.views;

import com.tandem.models.User;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import javax.swing.*;

public class EditProfilePanel extends JPanel {

    private final MainFrame frame;

    private StyledField bioField, cvField, portfolioField, contactField;

    public EditProfilePanel(MainFrame frame) {
        this.frame = frame;
        initComponents();

        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        mainScrollPane.setBorder(null);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getViewport().setBackground(UITheme.BG);
        mainScrollPane.setViewportView(buildContent());
        add(mainScrollPane, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        mainScrollPane = new javax.swing.JScrollPane();
        mainScrollPane.setBorder(null);
        mainScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE));
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JScrollPane mainScrollPane;
    // End of variables declaration

    private JPanel buildContent() {
        User user = Session.getCurrentUser();

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 32, UITheme.PAD));

        // Back
        JLabel back = new JLabel("← Kembali ke Profil");
        back.setFont(UITheme.F_LABEL);
        back.setForeground(UITheme.GRAY);
        back.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.showProfile();
            }
        });

        // Title
        JLabel title = new JLabel("Edit Profil");
        title.setFont(UITheme.F_TITLE);
        title.setForeground(UITheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Perbarui informasi profil dan portofoliomu.");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Info fields (read-only display)
        RoundedPanel readOnlyCard = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        readOnlyCard.setLayout(new BoxLayout(readOnlyCard, BoxLayout.Y_AXIS));
        readOnlyCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        readOnlyCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        readOnlyCard.add(infoRow("Nama",     user.getName()));
        readOnlyCard.add(spacer());
        readOnlyCard.add(infoRow("NIM",      user.getNim()));
        readOnlyCard.add(spacer());
        readOnlyCard.add(infoRow("Fakultas", user.getFaculty()));
        readOnlyCard.add(spacer());
        readOnlyCard.add(infoRow("Jurusan",  user.getMajor()));

        JLabel readOnlyNote = new JLabel("* Nama, NIM, Fakultas dan Jurusan tidak dapat diubah.");
        readOnlyNote.setFont(UITheme.F_SMALL);
        readOnlyNote.setForeground(UITheme.HINT);
        readOnlyNote.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Editable fields — pre-filled with current values
        contactField   = new StyledField("Nomor HP");
        bioField       = new StyledField("Deskripsi singkat dirimu");
        cvField        = new StyledField("Link CV / Google Drive");
        portfolioField = new StyledField("Link Portfolio / GitHub / Behance");

        if (!user.getContactNumber().isEmpty()) contactField.setText(user.getContactNumber());
        if (!user.getBio().isEmpty())           bioField.setText(user.getBio());
        if (!user.getCvLink().isEmpty())        cvField.setText(user.getCvLink());
        if (!user.getPortfolioLink().isEmpty()) portfolioField.setText(user.getPortfolioLink());

        // Save button
        RoundedButton saveBtn = new RoundedButton("Simpan Perubahan", UITheme.DARK, Color.WHITE);
        saveBtn.addActionListener(e -> doSave(user));

        RoundedButton cancelBtn = new RoundedButton("Batal", UITheme.BADGE, UITheme.TEXT);
        cancelBtn.addActionListener(e -> frame.showProfile());

        // Assemble
        p.add(back);
        p.add(Box.createVerticalStrut(16));
        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(24));

        p.add(sectionLabel("Informasi Akademik (Read-only)"));
        p.add(Box.createVerticalStrut(10));
        p.add(readOnlyCard);
        p.add(Box.createVerticalStrut(6));
        p.add(readOnlyNote);
        p.add(Box.createVerticalStrut(28));

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(sep);
        p.add(Box.createVerticalStrut(24));

        p.add(sectionLabel("Informasi Kontak"));
        p.add(Box.createVerticalStrut(10));
        p.add(lbl("Nomor HP"));    p.add(Box.createVerticalStrut(8)); p.add(contactField);
        p.add(Box.createVerticalStrut(28));

        p.add(sectionLabel("Profil & Portofolio"));
        p.add(Box.createVerticalStrut(10));
        p.add(lbl("Bio"));         p.add(Box.createVerticalStrut(8)); p.add(bioField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("CV Link"));     p.add(Box.createVerticalStrut(8)); p.add(cvField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Portfolio Link")); p.add(Box.createVerticalStrut(8)); p.add(portfolioField);
        p.add(Box.createVerticalStrut(36));
        p.add(saveBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(cancelBtn);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private void doSave(User user) {
        user.setContactNumber(contactField.getText().trim());
        user.setBio(bioField.getText().trim());
        user.setCvLink(cvField.getText().trim());
        user.setPortfolioLink(portfolioField.getText().trim());

        JOptionPane.showMessageDialog(this, "Profil berhasil diperbarui!");
        frame.showProfile();
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.GRAY);
        JLabel val = new JLabel(value);
        val.setFont(UITheme.F_BODY);
        val.setForeground(UITheme.TEXT);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel spacer() {
        JPanel d = new JPanel(new BorderLayout());
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        d.add(sep, BorderLayout.CENTER);
        return d;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SUB);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_LABEL);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}
