package com.tandem.views;

import com.tandem.controllers.RequestController;
import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class AlertsPanel extends JPanel {

    private final MainFrame frame;
    private final TeamController tc = new TeamController();
    private final RequestController rc = new RequestController();

    public AlertsPanel(MainFrame frame) {
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
        p.setBorder(BorderFactory.createEmptyBorder(24, UITheme.PAD, 32, UITheme.PAD));

        // Header
        JLabel title = new JLabel("Alerts");
        title.setFont(UITheme.F_HEAD);
        title.setForeground(UITheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Notifikasi masuk dan status lamaran timmu.");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(28));

        // ── Section: Incoming Requests (as leader) ─────────────────────────────
        ArrayList<Team> myTeams = tc.getTeamsByLeader(user);
        ArrayList<JoinRequest> allIncoming = new ArrayList<>();
        for (Team t : myTeams) {
            allIncoming.addAll(rc.getPendingRequestsForTeam(t));
        }

        p.add(sectionHead("Permintaan Bergabung"));
        p.add(Box.createVerticalStrut(4));
        p.add(sectionSub("Tim yang kamu pimpin · " + allIncoming.size() + " pending"));
        p.add(Box.createVerticalStrut(12));

        if (allIncoming.isEmpty()) {
            p.add(emptyBox("Tidak ada permintaan masuk saat ini."));
        } else {
            for (JoinRequest jr : allIncoming) {
                p.add(buildRequestCard(jr));
                p.add(Box.createVerticalStrut(12));
            }
        }

        p.add(Box.createVerticalStrut(28));

        // ── Section: My Applications ───────────────────────────────────────────
        ArrayList<JoinRequest> myApps = rc.getRequestsByUser(user);

        p.add(sectionHead("Lamaranku"));
        p.add(Box.createVerticalStrut(4));
        p.add(sectionSub("Status semua join request yang kamu kirim"));
        p.add(Box.createVerticalStrut(12));

        if (myApps.isEmpty()) {
            p.add(emptyBox("Kamu belum mengirim join request ke tim manapun."));
        } else {
            for (JoinRequest jr : myApps) {
                p.add(buildMyApplicationRow(jr));
                p.add(Box.createVerticalStrut(8));
            }
        }

        p.add(Box.createVerticalGlue());
        return p;
    }

    // ── Request Card (for leader) ─────────────────────────────────────────────

    private JPanel buildRequestCard(JoinRequest jr) {
        RoundedPanel card = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        User applicant = jr.getRequester();

        // ── Header row: avatar + name + date ──────────────────────────────────
        JPanel headerRow = new JPanel(new BorderLayout(12, 0));
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatar = makeAvatar(applicant.getName().substring(0, 1).toUpperCase());

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);

        JLabel nameLabel = new JLabel(applicant.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(UITheme.TEXT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel acadLabel = new JLabel(applicant.getFaculty() + "  ·  " + applicant.getMajor());
        acadLabel.setFont(UITheme.F_SMALL);
        acadLabel.setForeground(UITheme.GRAY);
        acadLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameBlock.add(nameLabel);
        nameBlock.add(acadLabel);

        JLabel dateLabel = new JLabel(jr.getCreatedAt());
        dateLabel.setFont(UITheme.F_SMALL);
        dateLabel.setForeground(UITheme.HINT);

        headerRow.add(avatar,    BorderLayout.WEST);
        headerRow.add(nameBlock, BorderLayout.CENTER);
        headerRow.add(dateLabel, BorderLayout.EAST);

        // ── Team label ─────────────────────────────────────────────────────────
        JLabel teamLabel = new JLabel("Tim: " + jr.getTargetTeam().getTeamName()
                + "  ·  " + jr.getTargetTeam().getCompetition().getName());
        teamLabel.setFont(UITheme.F_SMALL);
        teamLabel.setForeground(UITheme.GRAY);
        teamLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Applicant bio ──────────────────────────────────────────────────────
        String bio = applicant.getBio().isEmpty() ? "(tidak ada bio)" : applicant.getBio();
        JLabel bioLabel = new JLabel("<html><body style='width:340px'><i>\"" + bio + "\"</i></body></html>");
        bioLabel.setFont(UITheme.F_BODY);
        bioLabel.setForeground(UITheme.GRAY);
        bioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Message ────────────────────────────────────────────────────────────
        JLabel msgLabel = new JLabel("<html><body style='width:340px'>" + jr.getMessage() + "</body></html>");
        msgLabel.setFont(UITheme.F_BODY);
        msgLabel.setForeground(UITheme.TEXT);
        msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── CV & Portfolio links ───────────────────────────────────────────────
        JPanel linksRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        linksRow.setOpaque(false);
        linksRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!applicant.getCvLink().isEmpty()) {
            linksRow.add(linkChip("CV", applicant.getCvLink()));
        }
        if (!applicant.getPortfolioLink().isEmpty()) {
            linksRow.add(linkChip("Portfolio", applicant.getPortfolioLink()));
        }

        // ── Action buttons ─────────────────────────────────────────────────────
        RoundedButton acceptBtn  = new RoundedButton("Terima",  UITheme.DARK, Color.WHITE);
        RoundedButton declineBtn = new RoundedButton("Tolak", new Color(220, 50, 50), Color.WHITE);
        acceptBtn.setMaximumSize(new Dimension(140, 44));
        acceptBtn.setPreferredSize(new Dimension(140, 44));
        declineBtn.setMaximumSize(new Dimension(140, 44));
        declineBtn.setPreferredSize(new Dimension(140, 44));

        acceptBtn.addActionListener(e -> {
            tc.approveRequest(jr);
            JOptionPane.showMessageDialog(this,
                    applicant.getName() + " berhasil bergabung ke " + jr.getTargetTeam().getTeamName() + "!");
            refreshPanel();
        });
        declineBtn.addActionListener(e -> {
            tc.rejectRequest(jr);
            refreshPanel();
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(acceptBtn);
        btnRow.add(declineBtn);

        // ── Separator ──────────────────────────────────────────────────────────
        JSeparator sep1 = makeSep();
        JSeparator sep2 = makeSep();

        card.add(headerRow);
        card.add(Box.createVerticalStrut(10));
        card.add(teamLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(sep1);
        card.add(Box.createVerticalStrut(10));
        card.add(bioLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(msgLabel);
        if (!applicant.getCvLink().isEmpty() || !applicant.getPortfolioLink().isEmpty()) {
            card.add(Box.createVerticalStrut(10));
            card.add(sep2);
            card.add(Box.createVerticalStrut(10));
            card.add(linksRow);
        }
        card.add(Box.createVerticalStrut(12));
        card.add(btnRow);

        return card;
    }

    // ── My Application Row ────────────────────────────────────────────────────

    private JPanel buildMyApplicationRow(JoinRequest jr) {
        RoundedPanel row = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel teamName = new JLabel(jr.getTargetTeam().getTeamName());
        teamName.setFont(new Font("SansSerif", Font.BOLD, 13));
        teamName.setForeground(UITheme.TEXT);
        teamName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel compName = new JLabel(jr.getTargetTeam().getCompetition().getName()
                + "  ·  " + jr.getCreatedAt());
        compName.setFont(UITheme.F_SMALL);
        compName.setForeground(UITheme.GRAY);
        compName.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(teamName);
        info.add(Box.createVerticalStrut(2));
        info.add(compName);

        String statusStr = jr.getStatus().toString();
        JLabel statusLabel = new JLabel(statusStr);
        statusLabel.setFont(UITheme.F_LABEL);
        statusLabel.setForeground(
                statusStr.equals("APPROVED") ? new Color(34, 139, 34)
              : statusStr.equals("REJECTED")  ? new Color(200, 50, 50)
              : UITheme.GRAY);

        row.add(info,        BorderLayout.CENTER);
        row.add(statusLabel, BorderLayout.EAST);
        return row;
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private JPanel makeAvatar(String letter) {
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.DARK);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter,
                        (getWidth() - fm.stringWidth(letter)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        av.setPreferredSize(new Dimension(40, 40));
        av.setMinimumSize(new Dimension(40, 40));
        av.setMaximumSize(new Dimension(40, 40));
        av.setOpaque(false);
        return av;
    }

    private JPanel linkChip(String label, String url) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel("↗");
        icon.setFont(UITheme.F_SMALL);
        icon.setForeground(new Color(0, 100, 200));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(new Color(0, 100, 200));

        chip.add(icon); chip.add(lbl);

        chip.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                try { Desktop.getDesktop().browse(new java.net.URI(url)); }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Tidak bisa membuka: " + url);
                }
            }
            @Override public void mouseEntered(MouseEvent e) {
                chip.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 100, 200)),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)));
            }
            @Override public void mouseExited(MouseEvent e) {
                chip.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)));
            }
        });
        return chip;
    }

    private JSeparator makeSep() {
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JPanel emptyBox(String msg) {
        RoundedPanel box = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(msg, SwingConstants.CENTER);
        l.setFont(UITheme.F_BODY);
        l.setForeground(UITheme.GRAY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(l);
        return box;
    }

    private JLabel sectionHead(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SUB);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel sectionSub(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SMALL);
        l.setForeground(UITheme.GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void refreshPanel() {
        mainScrollPane.setViewportView(buildContent());
        revalidate();
        repaint();
    }
}
