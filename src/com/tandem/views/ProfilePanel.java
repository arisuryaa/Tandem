package com.tandem.views;

import com.tandem.controllers.RequestController;
import com.tandem.models.*;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ProfilePanel extends JPanel {

    private final MainFrame frame;
    private final RequestController rc = new RequestController();

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
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JScrollPane mainScrollPane;
    // End of variables declaration

    private JPanel buildContent() {
        User user = Session.getCurrentUser();

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(28, UITheme.PAD, 28, UITheme.PAD));

        // ── Avatar ─────────────────────────────────────────────────────────────
        JPanel avatar = makeAvatar(user.getName().substring(0, 1).toUpperCase());
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Name + role ────────────────────────────────────────────────────────
        JLabel name = centered(user.getName(), UITheme.F_HEAD, UITheme.TEXT);
        JPanel roleBadge = makeRoleBadge(user.getRole());
        roleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Info rows ──────────────────────────────────────────────────────────
        RoundedPanel infoCard = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        infoCard.add(infoRow("NIM", user.getNim()));
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(infoRow("Faculty", user.getFaculty()));
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(infoRow("Major", user.getMajor()));
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(infoRow("Contact", user.getContactNumber().isEmpty() ? "-" : user.getContactNumber()));

        // ── Skills ─────────────────────────────────────────────────────────────
        JLabel skillsTitle = sectionHead("Skills");
        JPanel skillsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        skillsRow.setOpaque(false);
        skillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        ArrayList<String> skills = getUserSkills(user);
        if (skills.isEmpty()) {
            JLabel noSkills = new JLabel("Belum ada skill yang ditambahkan.");
            noSkills.setFont(UITheme.F_SMALL);
            noSkills.setForeground(UITheme.HINT);
            skillsRow.add(noSkills);
        } else {
            for (String skill : skills) {
                skillsRow.add(makeSkillChip(skill));
            }
        }

        // ── My Applications ────────────────────────────────────────────────────
        JLabel appsTitle = sectionHead("My Applications");
        ArrayList<JoinRequest> myRequests = rc.getRequestsByUser(user);

        JPanel appsPanel = new JPanel();
        appsPanel.setLayout(new BoxLayout(appsPanel, BoxLayout.Y_AXIS));
        appsPanel.setOpaque(false);
        appsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (myRequests.isEmpty()) {
            JLabel none = new JLabel("Kamu belum pernah mengirim join request.");
            none.setFont(UITheme.F_BODY);
            none.setForeground(UITheme.GRAY);
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            appsPanel.add(none);
        } else {
            for (JoinRequest jr : myRequests) {
                appsPanel.add(makeRequestRow(jr));
                appsPanel.add(Box.createVerticalStrut(8));
            }
        }

        // ── Logout button ──────────────────────────────────────────────────────
        RoundedButton logoutBtn = new RoundedButton("Logout", new Color(220, 50, 50), Color.WHITE);
        logoutBtn.addActionListener(e -> {
            Session.clear();
            new LoginForm();
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        // ── Assemble ───────────────────────────────────────────────────────────
        p.add(avatar);
        p.add(Box.createVerticalStrut(12));
        p.add(name);
        p.add(Box.createVerticalStrut(8));
        p.add(roleBadge);
        p.add(Box.createVerticalStrut(24));
        p.add(infoCard);
        p.add(Box.createVerticalStrut(24));
        p.add(skillsTitle);
        p.add(Box.createVerticalStrut(8));
        p.add(skillsRow);
        p.add(Box.createVerticalStrut(24));
        p.add(appsTitle);
        p.add(Box.createVerticalStrut(8));
        p.add(appsPanel);
        p.add(Box.createVerticalStrut(32));
        p.add(logoutBtn);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private ArrayList<String> getUserSkills(User user) {
        ArrayList<String> skills = new ArrayList<>();
        if (user instanceof Hacker) {
            skills.addAll(((Hacker) user).getTechStack());
            skills.addAll(((Hacker) user).getProgrammingLanguages());
        } else if (user instanceof Hipster) {
            skills.addAll(((Hipster) user).getDesignTools());
        } else if (user instanceof Hustler) {
            skills.addAll(((Hustler) user).getBusinessSkills());
        }
        return skills;
    }

    private JPanel makeRequestRow(JoinRequest jr) {
        RoundedPanel row = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel teamName = new JLabel(jr.getTargetTeam().getTeamName());
        teamName.setFont(new Font("SansSerif", Font.BOLD, 13));
        teamName.setForeground(UITheme.TEXT);
        JLabel compName = new JLabel(jr.getTargetTeam().getCompetition().getName());
        compName.setFont(UITheme.F_SMALL);
        compName.setForeground(UITheme.GRAY);
        info.add(teamName); info.add(compName);

        JLabel status = new JLabel(jr.getStatus().toString());
        status.setFont(UITheme.F_LABEL);
        Color statusColor = jr.getStatus().toString().equals("APPROVED") ? new Color(34, 139, 34)
                : jr.getStatus().toString().equals("REJECTED") ? new Color(200, 50, 50)
                : UITheme.GRAY;
        status.setForeground(statusColor);

        row.add(info,   BorderLayout.CENTER);
        row.add(status, BorderLayout.EAST);
        return row;
    }

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
                g2.drawString(letter, (getWidth() - fm.stringWidth(letter)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        av.setPreferredSize(new Dimension(80, 80));
        av.setMaximumSize(new Dimension(80, 80));
        av.setOpaque(false);
        return av;
    }

    private JPanel makeRoleBadge(String role) {
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.DARK);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.setColor(Color.WHITE);
                g2.setFont(UITheme.F_LABEL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(role, (getWidth() - fm.stringWidth(role)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        FontMetrics fm = getFontMetrics(UITheme.F_LABEL);
        badge.setPreferredSize(new Dimension(fm.stringWidth(role) + 28, 30));
        badge.setMaximumSize(new Dimension(fm.stringWidth(role) + 28, 30));
        badge.setOpaque(false);
        return badge;
    }

    private JPanel makeSkillChip(String skill) {
        JPanel chip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BADGE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.setColor(UITheme.TEXT);
                g2.setFont(UITheme.F_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(skill, (getWidth() - fm.stringWidth(skill)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        FontMetrics fm = getFontMetrics(UITheme.F_SMALL);
        chip.setPreferredSize(new Dimension(fm.stringWidth(skill) + 20, 28));
        chip.setOpaque(false);
        return chip;
    }

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    private JLabel centered(String text, Font font, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel sectionHead(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_HEAD);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}
