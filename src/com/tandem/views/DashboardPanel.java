package com.tandem.views;

import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class DashboardPanel extends JPanel {

    private final MainFrame frame;
    private final TeamController tc = new TeamController();

    public DashboardPanel(MainFrame frame) {
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
        p.setBorder(BorderFactory.createEmptyBorder(24, UITheme.PAD, 24, UITheme.PAD));

        // Greeting
        JLabel greet = new JLabel("Good day, " + user.getName().split(" ")[0] + "!");
        greet.setFont(UITheme.F_HEAD);
        greet.setForeground(UITheme.TEXT);
        greet.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Ready to build your dream team?");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Role badge
        JPanel roleBadge = makeBadge(user.getRole());
        roleBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create team button
        RoundedButton createBtn = new RoundedButton("+ Create New Team", UITheme.DARK, Color.WHITE);
        createBtn.addActionListener(e -> frame.showCreateTeam());

        p.add(greet);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(12));
        p.add(roleBadge);
        p.add(Box.createVerticalStrut(28));

        // Section: My Teams
        JLabel myTeamsLabel = sectionHead("My Teams");
        p.add(myTeamsLabel);
        p.add(Box.createVerticalStrut(12));

        ArrayList<Team> myTeams = tc.getTeamsByMember(user);
        if (myTeams.isEmpty()) {
            p.add(emptyState("Kamu belum bergabung di tim manapun.", "Cari tim atau buat tim baru!"));
        } else {
            for (Team t : myTeams) {
                p.add(makeTeamCard(t));
                p.add(Box.createVerticalStrut(12));
            }
        }

        p.add(Box.createVerticalStrut(24));
        p.add(createBtn);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private JPanel makeTeamCard(Team team) {
        RoundedPanel card = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Competition badge
        JPanel compBadge = makeBadge(team.getCompetition().getCategory().toUpperCase());
        compBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel(team.getTeamName());
        name.setFont(UITheme.F_SUB);
        name.setForeground(UITheme.TEXT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel comp = new JLabel(team.getCompetition().getName());
        comp.setFont(UITheme.F_SMALL);
        comp.setForeground(UITheme.GRAY);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Members info
        int memberCount = team.getMembers().size();
        int total = memberCount + team.getOpenSlots().size();
        JLabel members = new JLabel(memberCount + "/" + total + " Members · " +
                (team.getOpenSlots().isEmpty() ? "Full" : team.getOpenSlots().size() + " open slot(s)"));
        members.setFont(UITheme.F_SMALL);
        members.setForeground(UITheme.GRAY);
        members.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(compBadge);
        card.add(Box.createVerticalStrut(8));
        card.add(name);
        card.add(Box.createVerticalStrut(2));
        card.add(comp);
        card.add(Box.createVerticalStrut(8));
        card.add(members);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { frame.showTeamDetail(team); }
        });

        return card;
    }

    private JPanel makeBadge(String text) {
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BADGE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.F_SMALL);
        lbl.setForeground(UITheme.GRAY);
        badge.add(lbl);

        FontMetrics fm = getFontMetrics(UITheme.F_SMALL);
        badge.setPreferredSize(new Dimension(fm.stringWidth(text) + 24, 26));
        badge.setMaximumSize(new Dimension(fm.stringWidth(text) + 24, 26));
        return badge;
    }

    private JLabel sectionHead(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_HEAD);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel emptyState(String line1, String line2) {
        RoundedPanel box = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l1 = new JLabel(line1, SwingConstants.CENTER);
        l1.setFont(UITheme.F_BODY); l1.setForeground(UITheme.GRAY); l1.setAlignmentX(CENTER_ALIGNMENT);
        JLabel l2 = new JLabel(line2, SwingConstants.CENTER);
        l2.setFont(UITheme.F_SMALL); l2.setForeground(UITheme.HINT); l2.setAlignmentX(CENTER_ALIGNMENT);
        box.add(l1); box.add(Box.createVerticalStrut(4)); box.add(l2);
        return box;
    }
}
