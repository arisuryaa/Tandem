package com.tandem.views;

import com.tandem.controllers.AuthController;
import com.tandem.models.*;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class RegisterForm extends JFrame {

    // ── Step 1 fields ────────────────────────────────────────────────────────
    private StyledField nameField, nimField;
    private JComboBox<String> facultyBox, majorBox;

    // ── Step 2 state ─────────────────────────────────────────────────────────
    private String selectedRole = "Hacker";
    private final ArrayList<String> selectedSkills = new ArrayList<>();
    private JPanel skillsRow;
    private JPanel[] roleCards;

    // ── Step 3 fields ────────────────────────────────────────────────────────
    private StyledField emailField, contactField;
    private StyledPasswordField passwordField;

    private int currentStep = 1;
    private JPanel[] bars;
    private CardLayout stepLayout;
    private JPanel stepPanel;

    private final AuthController auth = new AuthController();

    public RegisterForm() {
        initComponents();
        getContentPane().removeAll();  // Remove design-time placeholder components
        getContentPane().setLayout(new java.awt.BorderLayout());
        setSize(UITheme.W, UITheme.H);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(buildHeader(), BorderLayout.NORTH);

        stepLayout = new CardLayout();
        stepPanel  = new JPanel(stepLayout);
        stepPanel.setBackground(UITheme.BG);
        stepPanel.add(buildStep1(), "1");
        stepPanel.add(buildStep2(), "2");
        stepPanel.add(buildStep3(), "3");
        root.add(stepPanel, BorderLayout.CENTER);

        add(root);
        setVisible(true);
    }

    // ── Header (shared across steps) ─────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel hdr = new JPanel();
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.setBackground(UITheme.BG);
        hdr.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 12, UITheme.PAD));

        JLabel brand = new JLabel("Tandem");
        brand.setFont(UITheme.F_SUB);
        brand.setForeground(UITheme.TEXT);

        JLabel title = new JLabel("Join the Team");
        title.setFont(UITheme.F_TITLE);
        title.setForeground(UITheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Complete your academic profile to start matching.");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        hdr.add(brand);
        hdr.add(Box.createVerticalStrut(12));
        hdr.add(title);
        hdr.add(Box.createVerticalStrut(4));
        hdr.add(sub);
        hdr.add(Box.createVerticalStrut(16));
        hdr.add(buildProgressBar());
        return hdr;
    }

    private JPanel buildProgressBar() {
        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        row.setPreferredSize(new Dimension(0, 5));
        bars = new JPanel[3];
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            bars[i] = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(idx < currentStep ? UITheme.DARK : UITheme.BORDER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 3, 3);
                    g2.dispose();
                }
            };
            bars[i].setOpaque(false);
            row.add(bars[i]);
        }
        return row;
    }

    private void refreshBars() {
        for (JPanel b : bars) b.repaint();
    }

    // ── Step 1: Academic Data ─────────────────────────────────────────────────

    private JScrollPane buildStep1() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 20, UITheme.PAD));

        JLabel sec = new JLabel("Academic Data");
        sec.setFont(UITheme.F_HEAD);
        sec.setForeground(UITheme.TEXT);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = new StyledField("e.g. Alex Rivera");
        nimField  = new StyledField("e.g. 2505551107");

        String[] faculties = {"Engineering", "Economics", "Arts & Design", "Science", "Law", "Medicine"};
        String[] majors    = {"Computer Science", "Informatics", "Info Systems", "DKV", "Management", "Accounting"};
        facultyBox = styledCombo(faculties);
        majorBox   = styledCombo(majors);

        JPanel dropRow = new JPanel(new GridLayout(1, 2, 12, 0));
        dropRow.setOpaque(false);
        dropRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dropRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        dropRow.add(comboWrap("Faculty", facultyBox));
        dropRow.add(comboWrap("Major", majorBox));

        RoundedButton next = new RoundedButton("Next  →", UITheme.DARK, Color.WHITE);
        next.addActionListener(e -> {
            if (nameField.getText().isEmpty() || nimField.getText().isEmpty()) {
                warn("Isi Full Name dan Student ID terlebih dahulu!");
                return;
            }
            currentStep = 2; refreshBars();
            stepLayout.show(stepPanel, "2");
        });

        p.add(sec);
        p.add(Box.createVerticalStrut(20));
        p.add(lbl("Full Name"));         p.add(Box.createVerticalStrut(8));  p.add(nameField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Student ID (NIM)")); p.add(Box.createVerticalStrut(8));  p.add(nimField);
        p.add(Box.createVerticalStrut(16));
        p.add(dropRow);
        p.add(Box.createVerticalStrut(32));
        p.add(next);
        p.add(Box.createVerticalGlue());

        return scroll(p);
    }

    // ── Step 2: Competency + Skills ───────────────────────────────────────────

    private JScrollPane buildStep2() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 20, UITheme.PAD));

        JLabel sec = new JLabel("Competency Category");
        sec.setFont(UITheme.F_HEAD);
        sec.setForeground(UITheme.TEXT);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel secSub = new JLabel("Select your primary role in a team environment.");
        secSub.setFont(UITheme.F_BODY);
        secSub.setForeground(UITheme.GRAY);
        secSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Role cards
        String[][] roles = {
            {"Hacker",  "Technical/IT focus. Building infrastructure and handling logic."},
            {"Hipster", "UI/UX Design. Creating beautiful, user-centric experiences."},
            {"Hustler", "Business/Marketing. Strategic growth and project management."}
        };
        roleCards = new JPanel[3];
        JPanel rolesPanel = new JPanel();
        rolesPanel.setLayout(new BoxLayout(rolesPanel, BoxLayout.Y_AXIS));
        rolesPanel.setOpaque(false);
        rolesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < roles.length; i++) {
            final String role = roles[i][0];
            final String desc = roles[i][1];
            final int idx = i;
            roleCards[i] = buildRoleCard(role, desc, i == 0);
            roleCards[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selectedRole = role;
                    for (int j = 0; j < roleCards.length; j++) {
                        refreshRoleCard(roleCards[j], j == idx);
                    }
                    selectedSkills.clear();
                    refreshSkillChips();
                }
            });
            rolesPanel.add(roleCards[i]);
            rolesPanel.add(Box.createVerticalStrut(10));
        }

        // Skills section
        JLabel skillsLabel = new JLabel("SELECT YOUR SKILLS");
        skillsLabel.setFont(UITheme.F_LABEL);
        skillsLabel.setForeground(UITheme.GRAY);
        skillsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        skillsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        skillsRow.setOpaque(false);
        skillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshSkillChips();

        RoundedButton next = new RoundedButton("Next  →", UITheme.DARK, Color.WHITE);
        next.addActionListener(e -> {
            currentStep = 3; refreshBars();
            stepLayout.show(stepPanel, "3");
        });

        RoundedButton back = new RoundedButton("← Back", UITheme.BADGE, UITheme.TEXT);
        back.addActionListener(e -> {
            currentStep = 1; refreshBars();
            stepLayout.show(stepPanel, "1");
        });

        p.add(sec);    p.add(Box.createVerticalStrut(4));
        p.add(secSub); p.add(Box.createVerticalStrut(20));
        p.add(rolesPanel);
        p.add(Box.createVerticalStrut(20));
        p.add(skillsLabel); p.add(Box.createVerticalStrut(4));
        p.add(skillsRow);
        p.add(Box.createVerticalStrut(24));
        p.add(next); p.add(Box.createVerticalStrut(8)); p.add(back);
        p.add(Box.createVerticalGlue());

        return scroll(p);
    }

    private JPanel buildRoleCard(String role, String desc, boolean selected) {
        RoundedPanel card = new RoundedPanel(UITheme.CARD, selected ? UITheme.DARK : UITheme.BORDER, selected ? 2f : 1.5f);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel icon = makeRoleIcon(role, selected);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        JLabel name = new JLabel(role);
        name.setFont(UITheme.F_SUB);
        name.setForeground(UITheme.TEXT);
        JLabel d = new JLabel("<html><body style='width:200px'>" + desc + "</body></html>");
        d.setFont(UITheme.F_SMALL);
        d.setForeground(UITheme.GRAY);
        text.add(name); text.add(d);

        JLabel check = new JLabel(selected ? "✓" : "");
        check.setFont(UITheme.F_HEAD);
        check.setForeground(UITheme.TEXT);

        card.add(icon, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        card.add(check, BorderLayout.EAST);
        return card;
    }

    private void refreshRoleCard(JPanel card, boolean selected) {
        if (card instanceof RoundedPanel) {
            // Re-draw by rebuilding — find role from child labels
            Component[] comps = card.getComponents();
            // The EAST label is the checkmark
            for (Component c : comps) {
                if (c instanceof JLabel) {
                    ((JLabel) c).setText(selected ? "✓" : "");
                }
            }
        }
        card.repaint();
    }

    private JPanel makeRoleIcon(String role, boolean selected) {
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(selected ? UITheme.DARK : UITheme.BADGE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(selected ? Color.WHITE : UITheme.GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                String sym = role.equals("Hacker") ? "{ }" : role.equals("Hipster") ? "◐" : "↗";
                g2.drawString(sym, (getWidth() - fm.stringWidth(sym)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(44, 44));
        icon.setMinimumSize(new Dimension(44, 44));
        icon.setMaximumSize(new Dimension(44, 44));
        icon.setOpaque(false);
        return icon;
    }

    private void refreshSkillChips() {
        skillsRow.removeAll();
        String[] skills = selectedRole.equals("Hacker")  ? UITheme.HACKER_SKILLS
                        : selectedRole.equals("Hipster") ? UITheme.HIPSTER_SKILLS
                        : UITheme.HUSTLER_SKILLS;
        for (String skill : skills) {
            skillsRow.add(makeChip(skill));
        }
        skillsRow.revalidate();
        skillsRow.repaint();
    }

    private JPanel makeChip(String skill) {
        final boolean[] on = {selectedSkills.contains(skill)};
        JPanel chip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(on[0] ? UITheme.DARK : UITheme.CARD);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                if (!on[0]) {
                    g2.setColor(UITheme.BORDER);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
                g2.setColor(on[0] ? Color.WHITE : UITheme.TEXT);
                g2.setFont(UITheme.F_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(skill, (getWidth() - fm.stringWidth(skill)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        Dimension d = new Dimension(getFontMetrics(UITheme.F_SMALL).stringWidth(skill) + 24, 34);
        chip.setPreferredSize(d);
        chip.setOpaque(false);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                on[0] = !on[0];
                if (on[0]) selectedSkills.add(skill);
                else selectedSkills.remove(skill);
                chip.repaint();
            }
        });
        return chip;
    }

    // ── Step 3: Account Info ──────────────────────────────────────────────────

    private JScrollPane buildStep3() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 20, UITheme.PAD));

        JLabel sec = new JLabel("Account Information");
        sec.setFont(UITheme.F_HEAD);
        sec.setForeground(UITheme.TEXT);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField    = new StyledField("yourname@university.edu");
        passwordField = new StyledPasswordField();
        contactField  = new StyledField("e.g. 081234567890");

        RoundedButton submit = new RoundedButton("Complete Registration", UITheme.DARK, Color.WHITE);
        submit.addActionListener(e -> doRegister());

        RoundedButton back = new RoundedButton("← Back", UITheme.BADGE, UITheme.TEXT);
        back.addActionListener(e -> {
            currentStep = 2; refreshBars();
            stepLayout.show(stepPanel, "2");
        });

        p.add(sec); p.add(Box.createVerticalStrut(20));
        p.add(lbl("Institutional Email")); p.add(Box.createVerticalStrut(8)); p.add(emailField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Password")); p.add(Box.createVerticalStrut(8)); p.add(passwordField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Contact Number")); p.add(Box.createVerticalStrut(8)); p.add(contactField);
        p.add(Box.createVerticalStrut(32));
        p.add(submit); p.add(Box.createVerticalStrut(8)); p.add(back);
        p.add(Box.createVerticalGlue());

        return scroll(p);
    }

    // ── Registration logic ────────────────────────────────────────────────────

    private void doRegister() {
        String email   = emailField.getText().trim();
        String pw      = passwordField.getPasswordText();
        String contact = contactField.getText().trim();

        if (email.isEmpty() || pw.isEmpty()) {
            warn("Email dan password wajib diisi!"); return;
        }
        if (auth.isEmailTaken(email)) {
            warn("Email sudah terdaftar!"); return;
        }

        String faculty = (String) facultyBox.getSelectedItem();
        String major   = (String) majorBox.getSelectedItem();

        User user = auth.register(nameField.getText(), nimField.getText(), email, pw,
                selectedRole, faculty, major, contact);
        if (user == null) {
            warn("Registrasi gagal. Periksa kembali data kamu."); return;
        }

        // Add selected skills to user
        for (String skill : selectedSkills) {
            if (user instanceof Hacker)       ((Hacker) user).addTechStack(skill);
            else if (user instanceof Hipster) ((Hipster) user).addDesignTool(skill);
            else if (user instanceof Hustler) ((Hustler) user).addBusinessSkill(skill);
        }

        Session.setCurrentUser(user);
        JOptionPane.showMessageDialog(this, "Registrasi berhasil! Selamat datang, " + user.getName() + "!");
        new MainFrame();
        dispose();
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_LABEL);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(UITheme.F_BODY);
        cb.setBackground(UITheme.CARD);
        return cb;
    }

    private JPanel comboWrap(String label, JComboBox<String> cb) {
        JPanel wrap = new JPanel(new BorderLayout(0, 6));
        wrap.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.TEXT);
        wrap.add(lbl, BorderLayout.NORTH);
        wrap.add(cb, BorderLayout.CENTER);
        return wrap;
    }

    private JScrollPane scroll(JPanel p) {
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getViewport().setBackground(UITheme.BG);
        return sp;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        rfBrandLabel    = new javax.swing.JLabel();
        rfTitleLabel    = new javax.swing.JLabel();
        rfSubLabel      = new javax.swing.JLabel();
        rfNameLabel     = new javax.swing.JLabel();
        rfNameField     = new javax.swing.JTextField();
        rfNimLabel      = new javax.swing.JLabel();
        rfNimField      = new javax.swing.JTextField();
        rfEmailLabel    = new javax.swing.JLabel();
        rfEmailField    = new javax.swing.JTextField();
        rfPasswordLabel = new javax.swing.JLabel();
        rfPasswordField = new javax.swing.JPasswordField();
        rfNextButton    = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tandem - Join the Team");
        setResizable(false);

        rfBrandLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
        rfBrandLabel.setText("Tandem");

        rfTitleLabel.setFont(new java.awt.Font("SansSerif", 1, 24));
        rfTitleLabel.setText("Join the Team");

        rfSubLabel.setFont(new java.awt.Font("SansSerif", 0, 13));
        rfSubLabel.setText("Complete your academic profile to start matching.");

        rfNameLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        rfNameLabel.setText("Full Name");

        rfNameField.setFont(new java.awt.Font("SansSerif", 0, 14));
        rfNameField.setPreferredSize(new java.awt.Dimension(394, 44));

        rfNimLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        rfNimLabel.setText("Student ID (NIM)");

        rfNimField.setFont(new java.awt.Font("SansSerif", 0, 14));
        rfNimField.setPreferredSize(new java.awt.Dimension(394, 44));

        rfEmailLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        rfEmailLabel.setText("Institutional Email");

        rfEmailField.setFont(new java.awt.Font("SansSerif", 0, 14));
        rfEmailField.setPreferredSize(new java.awt.Dimension(394, 44));

        rfPasswordLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        rfPasswordLabel.setText("Password");

        rfPasswordField.setFont(new java.awt.Font("SansSerif", 0, 14));
        rfPasswordField.setPreferredSize(new java.awt.Dimension(394, 44));

        rfNextButton.setBackground(new java.awt.Color(26, 26, 26));
        rfNextButton.setFont(new java.awt.Font("SansSerif", 1, 15));
        rfNextButton.setForeground(java.awt.Color.white);
        rfNextButton.setText("Complete Registration");
        rfNextButton.setPreferredSize(new java.awt.Dimension(394, 54));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rfBrandLabel)
                    .addComponent(rfTitleLabel)
                    .addComponent(rfSubLabel)
                    .addComponent(rfNameLabel)
                    .addComponent(rfNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(rfNimLabel)
                    .addComponent(rfNimField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(rfEmailLabel)
                    .addComponent(rfEmailField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(rfPasswordLabel)
                    .addComponent(rfPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(rfNextButton, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(rfBrandLabel)
                .addGap(12, 12, 12)
                .addComponent(rfTitleLabel)
                .addGap(4, 4, 4)
                .addComponent(rfSubLabel)
                .addGap(24, 24, 24)
                .addComponent(rfNameLabel)
                .addGap(8, 8, 8)
                .addComponent(rfNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(rfNimLabel)
                .addGap(8, 8, 8)
                .addComponent(rfNimField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(rfEmailLabel)
                .addGap(8, 8, 8)
                .addComponent(rfEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(rfPasswordLabel)
                .addGap(8, 8, 8)
                .addComponent(rfPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(rfNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JTextField rfEmailField;
    private javax.swing.JLabel rfEmailLabel;
    private javax.swing.JTextField rfNameField;
    private javax.swing.JLabel rfNameLabel;
    private javax.swing.JButton rfNextButton;
    private javax.swing.JTextField rfNimField;
    private javax.swing.JLabel rfNimLabel;
    private javax.swing.JPasswordField rfPasswordField;
    private javax.swing.JLabel rfPasswordLabel;
    private javax.swing.JLabel rfBrandLabel;
    private javax.swing.JLabel rfSubLabel;
    private javax.swing.JLabel rfTitleLabel;
    // End of variables declaration
}
