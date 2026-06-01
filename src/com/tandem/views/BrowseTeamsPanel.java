package com.tandem.views;

import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class BrowseTeamsPanel extends JPanel {

    private final MainFrame frame;
    private final TeamController tc = new TeamController();
    private String activeFilter = "Semua";
    private JPanel listPanel;
    private JPanel filterRow;

    public BrowseTeamsPanel(MainFrame frame) {
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
    }

    private javax.swing.JScrollPane mainScrollPane;

    private JPanel buildContent() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, UITheme.PAD, 24, UITheme.PAD));

        JLabel title = new JLabel("Find Teams");
        title.setFont(UITheme.F_HEAD);
        title.setForeground(UITheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Browse open teams looking for members.");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buildFilterChips();

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        populateList();

        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(20));
        p.add(filterRow);
        p.add(Box.createVerticalStrut(16));

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(sep);
        p.add(Box.createVerticalStrut(16));
        p.add(listPanel);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private void buildFilterChips() {
        filterRow.removeAll();
        ArrayList<String> categories = tc.getAvailableCategories();
        for (String cat : categories) {
            filterRow.add(makeFilterChip(cat));
        }
        filterRow.revalidate();
        filterRow.repaint();
    }

    private void populateList() {
        listPanel.removeAll();
        User user = Session.getCurrentUser();
        ArrayList<Team> teams = tc.getTeamsByCategory(activeFilter);

        if (teams.isEmpty()) {
            JLabel empty = new JLabel("Tidak ada tim yang tersedia untuk kategori ini.");
            empty.setFont(UITheme.F_BODY);
            empty.setForeground(UITheme.GRAY);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(empty);
        } else {
            for (Team t : teams) {
                if (!t.isMember(user)) {
                    listPanel.add(makeTeamCard(t));
                    listPanel.add(Box.createVerticalStrut(12));
                }
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel makeFilterChip(String label) {
        final boolean[] active = {label.equals(activeFilter)};
        JPanel chip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(active[0] ? UITheme.DARK : UITheme.CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                if (!active[0]) {
                    g2.setColor(UITheme.BORDER);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                }
                g2.setColor(active[0] ? Color.WHITE : UITheme.TEXT);
                g2.setFont(UITheme.F_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        FontMetrics fm = getFontMetrics(UITheme.F_SMALL);
        chip.setPreferredSize(new Dimension(fm.stringWidth(label) + 24, 32));
        chip.setOpaque(false);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                activeFilter = label;
                active[0] = true;
                buildFilterChips();
                populateList();
            }
        });
        return chip;
    }

    private JPanel makeTeamCard(Team team) {
        RoundedPanel card = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel catLabel = smallGray(team.getCompetition().getCategory().toUpperCase());
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel(team.getTeamName());
        name.setFont(UITheme.F_SUB);
        name.setForeground(UITheme.TEXT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel compName = smallGray(team.getCompetition().getName());
        compName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel slotsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        slotsRow.setOpaque(false);
        slotsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String slot : team.getOpenSlots()) {
            slotsRow.add(slotPill(slot));
        }

        card.add(catLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(name);
        card.add(Box.createVerticalStrut(2));
        card.add(compName);
        card.add(Box.createVerticalStrut(10));
        card.add(slotsRow);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { frame.showTeamDetail(team); }
        });

        return card;
    }

    private JLabel smallGray(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SMALL);
        l.setForeground(UITheme.GRAY);
        return l;
    }

    private JPanel slotPill(String role) {
        JPanel pill = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BADGE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.setColor(UITheme.GRAY);
                g2.setFont(UITheme.F_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(role, (getWidth() - fm.stringWidth(role)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        FontMetrics fm = getFontMetrics(UITheme.F_SMALL);
        pill.setPreferredSize(new Dimension(fm.stringWidth(role) + 20, 26));
        pill.setOpaque(false);
        return pill;
    }
}
