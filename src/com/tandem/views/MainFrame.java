package com.tandem.views;

import com.tandem.models.Team;
import com.tandem.views.components.UITheme;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private DashboardPanel  dashboardPanel;
    private BrowseTeamsPanel browsePanel;
    private AlertsPanel      alertsPanel;
    private ProfilePanel     profilePanel;

    private JPanel navPanel;

    private static final String DASH       = "dashboard";
    private static final String BROWSE     = "browse";
    private static final String ALERTS     = "alerts";
    private static final String PROF       = "profile";
    private static final String TEAM       = "teamDetail";
    private static final String CREATE     = "createTeam";
    private static final String EDIT_PROF  = "editProfile";

    private static final String[] NAV_LABELS = {"Dashboard", "Find Teams", "Alerts", "Profile"};

    public MainFrame() {
        initComponents();
        setSize(UITheme.W, UITheme.H);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        add(buildHeader(), BorderLayout.NORTH);

        cards.setBackground(UITheme.BG);
        dashboardPanel = new DashboardPanel(this);
        browsePanel    = new BrowseTeamsPanel(this);
        alertsPanel    = new AlertsPanel(this);
        profilePanel   = new ProfilePanel(this);

        cards.add(dashboardPanel,       DASH);
        cards.add(browsePanel,          BROWSE);
        cards.add(alertsPanel,          ALERTS);
        cards.add(profilePanel,         PROF);
        cards.add(new CreateTeamPanel(this), CREATE);

        add(cards, BorderLayout.CENTER);

        navPanel = buildBottomNav(0);
        add(navPanel, BorderLayout.SOUTH);

        cardLayout.show(cards, DASH);
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tandem");
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    // ── Navigation ────────────────────────────────────────────────────────────

    public void showDashboard() {
        cards.remove(dashboardPanel);
        dashboardPanel = new DashboardPanel(this);
        cards.add(dashboardPanel, DASH);
        cardLayout.show(cards, DASH);
        switchNav(0);
    }

    public void showBrowse() {
        cards.remove(browsePanel);
        browsePanel = new BrowseTeamsPanel(this);
        cards.add(browsePanel, BROWSE);
        cardLayout.show(cards, BROWSE);
        switchNav(1);
    }

    public void showAlerts() {
        cards.remove(alertsPanel);
        alertsPanel = new AlertsPanel(this);
        cards.add(alertsPanel, ALERTS);
        cardLayout.show(cards, ALERTS);
        switchNav(2);
    }

    public void showProfile() {
        cards.remove(profilePanel);
        profilePanel = new ProfilePanel(this);
        cards.add(profilePanel, PROF);
        cardLayout.show(cards, PROF);
        switchNav(3);
    }

    public void showTeamDetail(Team team) {
        TeamDetailPanel tdp = new TeamDetailPanel(this, team);
        cards.add(tdp, TEAM);
        cardLayout.show(cards, TEAM);
    }

    public void showCreateTeam() {
        CreateTeamPanel ctp = new CreateTeamPanel(this);
        cards.add(ctp, CREATE);
        cardLayout.show(cards, CREATE);
    }

    public void showEditProfile() {
        EditProfilePanel epp = new EditProfilePanel(this);
        cards.add(epp, EDIT_PROF);
        cardLayout.show(cards, EDIT_PROF);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(UITheme.CARD);
        hdr.setPreferredSize(new Dimension(UITheme.W, 56));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, UITheme.PAD, 0));
        logoArea.setOpaque(false);

        JPanel logoIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.TEXT);
                g2.fillOval(0, 3, 18, 18);
                g2.fillOval(11, 3, 18, 18);
                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(30, 24));
        logoIcon.setOpaque(false);

        JLabel brandName = new JLabel("Tandem");
        brandName.setFont(UITheme.F_SUB);
        brandName.setForeground(UITheme.TEXT);

        logoArea.add(logoIcon);
        logoArea.add(brandName);
        hdr.add(logoArea, BorderLayout.WEST);
        return hdr;
    }

    // ── Bottom Nav ────────────────────────────────────────────────────────────

    private JPanel buildBottomNav(int activeIdx) {
        JPanel nav = new JPanel(new GridLayout(1, 4));
        nav.setBackground(UITheme.CARD);
        nav.setPreferredSize(new Dimension(UITheme.W, 68));
        nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            JPanel item = makeNavItem(NAV_LABELS[i], i, i == activeIdx);
            item.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    switch (idx) {
                        case 0: showDashboard(); break;
                        case 1: showBrowse();    break;
                        case 2: showAlerts();    break;
                        case 3: showProfile();   break;
                    }
                }
            });
            nav.add(item);
        }
        return nav;
    }

    private void switchNav(int activeIdx) {
        remove(navPanel);
        navPanel = buildBottomNav(activeIdx);
        add(navPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel makeNavItem(String label, int iconType, boolean active) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(UITheme.CARD);
        item.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel icon = makeNavIcon(iconType, active);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(UITheme.F_SMALL);
        lbl.setForeground(active ? UITheme.TEXT : UITheme.HINT);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        item.add(Box.createVerticalGlue());
        item.add(icon);
        item.add(Box.createVerticalStrut(4));
        item.add(lbl);
        item.add(Box.createVerticalGlue());
        return item;
    }

    private JPanel makeNavIcon(int type, boolean active) {
        Color c = active ? UITheme.TEXT : UITheme.HINT;
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.setStroke(new BasicStroke(1.5f));
                switch (type) {
                    case 0: // Grid
                        int s = 5, gap = 3;
                        g2.fillRect(0, 0, s, s); g2.fillRect(s+gap, 0, s, s);
                        g2.fillRect(0, s+gap, s, s); g2.fillRect(s+gap, s+gap, s, s);
                        break;
                    case 1: // Person+
                        g2.fillOval(1, 0, 9, 9);
                        g2.fillArc(0, 8, 11, 7, 0, 180);
                        g2.drawLine(13, 3, 13, 9);
                        g2.drawLine(10, 6, 16, 6);
                        break;
                    case 2: // Bell
                        g2.fillArc(2, 0, 10, 9, 0, 180);
                        g2.fillRect(2, 4, 10, 6);
                        g2.fillOval(5, 10, 4, 3);
                        break;
                    case 3: // Person
                        g2.fillOval(3, 0, 10, 10);
                        g2.fillArc(1, 9, 14, 7, 0, 180);
                        break;
                }
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(18, 16));
        icon.setMaximumSize(new Dimension(18, 16));
        icon.setOpaque(false);
        return icon;
    }
}
