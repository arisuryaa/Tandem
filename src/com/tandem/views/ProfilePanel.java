package com.tandem.views;

import com.tandem.models.*;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProfilePanel extends JPanel {

    private final MainFrame frame;

    public ProfilePanel(MainFrame frame) {
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
        p.setBorder(BorderFactory.createEmptyBorder(28, UITheme.PAD, 32, UITheme.PAD));

        // ── Avatar + Name ──────────────────────────────────────────────────────
        JPanel avatar = makeAvatar(user.getName().substring(0, 1).toUpperCase());
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(user.getName(), SwingConstants.CENTER);
        name.setFont(UITheme.F_HEAD);
        name.setForeground(UITheme.TEXT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel majorBadge = makePill(user.getMajor(), UITheme.DARK, Color.WHITE);
        majorBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Academic Info ──────────────────────────────────────────────────────
        RoundedPanel infoCard = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(infoRow("NIM",      user.getNim()));
        infoCard.add(divider());
        infoCard.add(infoRow("Fakultas", user.getFaculty()));
        infoCard.add(divider());
        infoCard.add(infoRow("Jurusan",  user.getMajor()));
        infoCard.add(divider());
        infoCard.add(infoRow("Kontak",   user.getContactNumber().isEmpty() ? "—" : user.getContactNumber()));

        // ── Portfolio & CV ─────────────────────────────────────────────────────
        RoundedPanel profileCard = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String bio = user.getBio().isEmpty() ? "—" : user.getBio();
        profileCard.add(bioRow("Bio", bio));
        profileCard.add(divider());
        profileCard.add(linkRow("CV", user.getCvLink()));
        profileCard.add(divider());
        profileCard.add(linkRow("Portfolio", user.getPortfolioLink()));

        // ── Edit Profile button ────────────────────────────────────────────────
        RoundedButton editBtn = new RoundedButton("Edit Profil", UITheme.DARK, Color.WHITE);
        editBtn.addActionListener(e -> frame.showEditProfile());

        // ── Logout — outline style ─────────────────────────────────────────────
        final Color RED = new Color(220, 50, 50);
        JButton logoutBtn = new JButton("Logout") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, UITheme.R, UITheme.R);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(UITheme.F_BTN);
        logoutBtn.setForeground(RED);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            Session.clear();
            new LoginForm();
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        // ── Button row: Edit | Logout side by side ─────────────────────────────
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setPreferredSize(new Dimension(0, 54));
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(editBtn);
        btnRow.add(logoutBtn);

        // ── Assemble ───────────────────────────────────────────────────────────
        p.add(avatar);
        p.add(Box.createVerticalStrut(12));
        p.add(name);
        p.add(Box.createVerticalStrut(8));
        p.add(majorBadge);
        p.add(Box.createVerticalStrut(28));
        p.add(sectionHead("Informasi Akademik"));
        p.add(Box.createVerticalStrut(10));
        p.add(infoCard);
        p.add(Box.createVerticalStrut(24));
        p.add(sectionHead("Profil & Portofolio"));
        p.add(Box.createVerticalStrut(10));
        p.add(profileCard);
        p.add(Box.createVerticalStrut(16));
        p.add(btnRow);
        p.add(Box.createVerticalGlue());

        return p;
    }

    // ── Sub-components ────────────────────────────────────────────────────────

    private JPanel makeAvatar(String letter) {
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.DARK);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter,
                        (getWidth() - fm.stringWidth(letter)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        av.setPreferredSize(new Dimension(80, 80));
        av.setMaximumSize(new Dimension(80, 80));
        av.setOpaque(false);
        return av;
    }

    private JPanel makePill(String text, Color bg, Color fg) {
        JPanel pill = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.setColor(fg);
                g2.setFont(UITheme.F_LABEL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text,
                        (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        FontMetrics fm = getFontMetrics(UITheme.F_LABEL);
        int w = fm.stringWidth(text) + 28;
        pill.setPreferredSize(new Dimension(w, 30));
        pill.setMaximumSize(new Dimension(w, 30));
        pill.setOpaque(false);
        return pill;
    }

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
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

    private JPanel bioRow(String label, String value) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel val = new JLabel("<html><body style='width:360px'>" + value + "</body></html>");
        val.setFont(UITheme.F_BODY);
        val.setForeground(UITheme.TEXT);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(lbl);
        row.add(Box.createVerticalStrut(4));
        row.add(val);
        return row;
    }

    private JPanel linkRow(String label, String url) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val;
        if (url == null || url.isEmpty()) {
            val = new JLabel("—");
            val.setFont(UITheme.F_BODY);
            val.setForeground(UITheme.HINT);
        } else {
            val = new JLabel("<html><u>" + url + "</u></html>");
            val.setFont(UITheme.F_SMALL);
            val.setForeground(new Color(0, 100, 200));
            val.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final String link = url;
            val.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    try { Desktop.getDesktop().browse(new java.net.URI(link)); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Tidak bisa membuka: " + link);
                    }
                }
            });
        }
        val.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(lbl);
        row.add(Box.createVerticalStrut(4));
        row.add(val);
        return row;
    }

    private JPanel divider() {
        JPanel d = new JPanel();
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        d.setPreferredSize(new Dimension(0, 12));
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        d.setLayout(new BorderLayout());
        d.add(sep, BorderLayout.CENTER);
        return d;
    }

    private JLabel sectionHead(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SUB);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}
