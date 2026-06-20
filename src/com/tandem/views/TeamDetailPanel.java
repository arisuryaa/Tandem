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

public class TeamDetailPanel extends JPanel {

    private final MainFrame frame;
    private final Team team;
    private final TeamController tc = new TeamController();
    private final RequestController rc = new RequestController();

    public TeamDetailPanel(MainFrame frame, Team team) {
        this.frame = frame;
        this.team  = team;
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
        User me = Session.getCurrentUser();
        boolean isLeader = team.getLeader().getUserId().equals(me.getUserId());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 24, UITheme.PAD));

        // ── Back button ───────────────────────────────────────────────────────
        JLabel back = new JLabel("← Back");
        back.setFont(UITheme.F_LABEL);
        back.setForeground(UITheme.GRAY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { frame.showDashboard(); }
        });

        // ── Project ID badge ──────────────────────────────────────────────────
        JLabel idBadge = new JLabel("PROJECT ID: #" + team.getTeamId());
        idBadge.setFont(UITheme.F_LABEL);
        idBadge.setForeground(UITheme.GRAY);
        idBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Team name ─────────────────────────────────────────────────────────
        JLabel teamName = new JLabel(team.getTeamName());
        teamName.setFont(UITheme.F_TITLE);
        teamName.setForeground(UITheme.TEXT);
        teamName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Info card ─────────────────────────────────────────────────────────
        p.add(back);
        p.add(Box.createVerticalStrut(16));
        p.add(idBadge);
        p.add(Box.createVerticalStrut(6));
        p.add(teamName);
        p.add(Box.createVerticalStrut(16));
        p.add(buildInfoCard());
        p.add(Box.createVerticalStrut(28));

        // ── Team Structure ────────────────────────────────────────────────────
        int memberCount = team.getMembers().size();
        int total = memberCount + team.getOpenSlots().size();
        JPanel structHeader = new JPanel(new BorderLayout());
        structHeader.setOpaque(false);
        structHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        structHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel structTitle = new JLabel("Team Structure");
        structTitle.setFont(UITheme.F_HEAD);
        structTitle.setForeground(UITheme.TEXT);
        JLabel countLbl = new JLabel(memberCount + "/" + total + " Members");
        countLbl.setFont(UITheme.F_SMALL);
        countLbl.setForeground(UITheme.GRAY);
        structHeader.add(structTitle, BorderLayout.WEST);
        structHeader.add(countLbl, BorderLayout.EAST);

        p.add(structHeader);
        p.add(Box.createVerticalStrut(12));

        // Members
        for (User member : team.getMembers()) {
            boolean isThisLeader = member.getUserId().equals(team.getLeader().getUserId());
            String label = isThisLeader ? "Team Lead" : member.getMajor();
            p.add(buildMemberRow(member, label));
            p.add(Box.createVerticalStrut(8));
        }

        // Vacant slots
        for (String slot : team.getOpenSlots()) {
            p.add(buildVacantRow(slot));
            p.add(Box.createVerticalStrut(8));
        }

        // ── Join Request button (for non-members) ─────────────────────────────
        if (!isLeader && !team.isMember(me) && !team.getOpenSlots().isEmpty()) {
            p.add(Box.createVerticalStrut(16));
            RoundedButton joinBtn;
            if (isDeadlinePassed(team.getRegistrationDeadline())) {
                joinBtn = new RoundedButton("Pendaftaran Ditutup", UITheme.BADGE, UITheme.GRAY);
                joinBtn.setEnabled(false);
            } else if (rc.hasPendingRequest(me, team)) {
                joinBtn = new RoundedButton("Request Sent ✓", UITheme.BADGE, UITheme.GRAY);
                joinBtn.setEnabled(false);
            } else {
                joinBtn = new RoundedButton("Request to Join", UITheme.DARK, Color.WHITE);
                joinBtn.addActionListener(e -> sendJoinRequest(me));
            }
            p.add(joinBtn);
        }

        // ── Incoming Requests (for leader) ────────────────────────────────────
        if (isLeader) {
            ArrayList<JoinRequest> pending = rc.getPendingRequestsForTeam(team);
            if (!pending.isEmpty()) {
                p.add(Box.createVerticalStrut(28));
                JLabel reqTitle = new JLabel("Incoming Requests");
                reqTitle.setFont(UITheme.F_HEAD);
                reqTitle.setForeground(UITheme.TEXT);
                reqTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                p.add(reqTitle);
                p.add(Box.createVerticalStrut(12));
                for (JoinRequest jr : pending) {
                    p.add(buildRequestCard(jr));
                    p.add(Box.createVerticalStrut(12));
                }
            }
        }

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel buildInfoCard() {
        RoundedPanel card = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel conceptLabel = new JLabel("THE CONCEPT");
        conceptLabel.setFont(UITheme.F_LABEL);
        conceptLabel.setForeground(UITheme.GRAY);
        conceptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String desc = team.getDescription().isEmpty() ? "No description provided." : team.getDescription();
        JLabel descLabel = new JLabel("<html><body style='width:350px'>" + desc + "</body></html>");
        descLabel.setFont(UITheme.F_BODY);
        descLabel.setForeground(UITheme.TEXT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Row 1: Competition + Submission deadline
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow.setOpaque(false);
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        infoRow.add(infoBlock("KOMPETISI", team.getCompetition().getName()));
        infoRow.add(infoBlock("DEADLINE SUBMISSION", team.getCompetition().getSubmissionDeadline()));

        // Row 2: Event schedule + registration deadline
        JPanel infoRow2 = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow2.setOpaque(false);
        infoRow2.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoRow2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        String jadwal = team.getCompetition().getEventStartDate()
                + " s/d " + team.getCompetition().getEventEndDate();
        infoRow2.add(infoBlock("JADWAL LOMBA", jadwal));
        String regDead = team.getRegistrationDeadline();
        infoRow2.add(infoBlock("DEADLINE PENDAFTARAN TIM",
                regDead.isEmpty() ? "Tidak ditentukan" : regDead));

        // Tags row
        JPanel tagsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tagsRow.setOpaque(false);
        tagsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String tag : team.getCompetition().getTags()) {
            JLabel tagPill = new JLabel(tag);
            tagPill.setFont(UITheme.F_SMALL);
            tagPill.setForeground(UITheme.GRAY);
            tagPill.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
            tagsRow.add(tagPill);
        }

        card.add(conceptLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(sep);
        card.add(Box.createVerticalStrut(12));
        card.add(infoRow);
        card.add(Box.createVerticalStrut(10));
        card.add(infoRow2);
        if (!team.getCompetition().getTags().isEmpty()) {
            card.add(Box.createVerticalStrut(10));
            card.add(tagsRow);
        }

        return card;
    }

    private JPanel infoBlock(String label, String value) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.GRAY);
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 13));
        val.setForeground(UITheme.TEXT);
        block.add(lbl);
        block.add(Box.createVerticalStrut(4));
        block.add(val);
        return block;
    }

    private JPanel buildMemberRow(User member, String roleLabel) {
        RoundedPanel row = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar circle
        JPanel avatar = makeAvatar(member.getName().substring(0, 1).toUpperCase(), UITheme.DARK, Color.WHITE);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel name = new JLabel(member.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 14));
        name.setForeground(UITheme.TEXT);
        JLabel role = new JLabel(roleLabel);
        role.setFont(UITheme.F_SMALL);
        role.setForeground(UITheme.GRAY);
        info.add(name);
        info.add(role);

        JLabel check = new JLabel("✓");
        check.setFont(UITheme.F_HEAD);
        check.setForeground(UITheme.TEXT);

        row.add(avatar, BorderLayout.WEST);
        row.add(info,   BorderLayout.CENTER);
        row.add(check,  BorderLayout.EAST);
        return row;
    }

    private JPanel buildVacantRow(String slot) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createDashedBorder(UITheme.BORDER, 4, 4),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Placeholder avatar
        JPanel avatar = makeAvatar("+", UITheme.BADGE, UITheme.GRAY);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel v = new JLabel("Vacant Position");
        v.setFont(new Font("SansSerif", Font.BOLD, 14));
        v.setForeground(UITheme.GRAY);
        JLabel r = new JLabel(slot);
        r.setFont(UITheme.F_SMALL);
        r.setForeground(UITheme.HINT);
        info.add(v); info.add(r);

        JPanel badge = makeOpenBadge();

        row.add(avatar, BorderLayout.WEST);
        row.add(info,   BorderLayout.CENTER);
        row.add(badge,  BorderLayout.EAST);
        return row;
    }

    private JPanel makeOpenBadge() {
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BADGE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(UITheme.GRAY);
                g2.setFont(UITheme.F_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("OPEN", (getWidth() - fm.stringWidth("OPEN")) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(52, 28));
        badge.setMaximumSize(new Dimension(52, 28));
        badge.setOpaque(false);
        return badge;
    }

    private JPanel buildRequestCard(JoinRequest jr) {
        RoundedPanel card = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header row: avatar + name + time
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JPanel avatar = makeAvatar(jr.getRequester().getName().substring(0, 1).toUpperCase(), UITheme.DARK, Color.WHITE);
        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);
        JLabel rName = new JLabel(jr.getRequester().getName());
        rName.setFont(new Font("SansSerif", Font.BOLD, 14));
        rName.setForeground(UITheme.TEXT);
        JLabel rRole = new JLabel(jr.getRequester().getFaculty() + " · " + jr.getRequester().getMajor());
        rRole.setFont(UITheme.F_SMALL);
        rRole.setForeground(UITheme.GRAY);
        nameBlock.add(rName); nameBlock.add(rRole);
        JLabel time = new JLabel(jr.getCreatedAt());
        time.setFont(UITheme.F_SMALL);
        time.setForeground(UITheme.HINT);
        header.add(avatar,    BorderLayout.WEST);
        header.add(nameBlock, BorderLayout.CENTER);
        header.add(time,      BorderLayout.EAST);

        // Message
        JLabel msg = new JLabel("<html><i>\"" + jr.getMessage() + "\"</i></html>");
        msg.setFont(UITheme.F_BODY);
        msg.setForeground(UITheme.GRAY);
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action buttons
        RoundedButton acceptBtn  = new RoundedButton("Accept",  UITheme.DARK, Color.WHITE);
        RoundedButton declineBtn = new RoundedButton("Decline", UITheme.BADGE, UITheme.TEXT);
        acceptBtn.setPreferredSize(new Dimension(120, 44));
        acceptBtn.setMaximumSize(new Dimension(120, 44));
        declineBtn.setPreferredSize(new Dimension(120, 44));
        declineBtn.setMaximumSize(new Dimension(120, 44));

        acceptBtn.addActionListener(e -> {
            tc.approveRequest(jr);
            JOptionPane.showMessageDialog(this, jr.getRequester().getName() + " berhasil bergabung ke tim!");
            rebuildPanel();
        });
        declineBtn.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(this,
                    "Masukkan alasan penolakan (opsional, bisa dikosongkan):",
                    "Tolak Request", JOptionPane.PLAIN_MESSAGE);
            if (reason == null) return;
            tc.rejectRequest(jr, reason);
            rebuildPanel();
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(acceptBtn);
        btnRow.add(declineBtn);

        card.add(header);
        card.add(Box.createVerticalStrut(12));
        card.add(msg);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRow);

        return card;
    }

    private void sendJoinRequest(User me) {
        String msg = JOptionPane.showInputDialog(this,
                "Tulis pesan singkat untuk team leader:", "Join Request", JOptionPane.PLAIN_MESSAGE);
        if (msg == null) return;
        JoinRequest jr = rc.sendJoinRequest(me, team, msg.isEmpty() ? "Saya ingin bergabung!" : msg);
        if (jr != null) {
            JOptionPane.showMessageDialog(this, "Request berhasil dikirim!");
            rebuildPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Tidak bisa mengirim request (sudah terkirim atau slot tidak sesuai).",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rebuildPanel() {
        mainScrollPane.setViewportView(buildContent());
        revalidate();
        repaint();
    }

    private static boolean isDeadlinePassed(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return false;
        try {
            return java.time.LocalDate.now().isAfter(java.time.LocalDate.parse(dateStr));
        } catch (Exception ex) {
            return false;
        }
    }

    private JPanel makeAvatar(String letter, Color bg, Color fg) {
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(fg);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter, (getWidth() - fm.stringWidth(letter)) / 2,
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
}
