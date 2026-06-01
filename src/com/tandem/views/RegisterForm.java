package com.tandem.views;

import com.tandem.controllers.AuthController;
import com.tandem.models.User;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class RegisterForm extends JFrame {

    // Step 1 fields
    private StyledField nameField, nimField;
    private JComboBox<String> facultyBox, majorBox;

    // Step 2 fields
    private StyledField emailField, contactField, bioField, cvField, portfolioField;
    private StyledPasswordField passwordField;

    private int currentStep = 1;
    private JPanel[] bars;
    private CardLayout stepLayout;
    private JPanel stepPanel;

    private final AuthController auth = new AuthController();

    public RegisterForm() {
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        root.add(stepPanel, BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel hdr = new JPanel();
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.setBackground(UITheme.BG);
        hdr.setBorder(BorderFactory.createEmptyBorder(24, UITheme.PAD, 0, UITheme.PAD));

        JLabel brand = new JLabel("Tandem");
        brand.setFont(UITheme.F_SUB);
        brand.setForeground(UITheme.TEXT);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Join the Team");
        title.setFont(UITheme.F_TITLE);
        title.setForeground(UITheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Lengkapi profil akademikmu untuk mulai matching.");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        hdr.add(brand);
        hdr.add(Box.createVerticalStrut(10));
        hdr.add(title);
        hdr.add(Box.createVerticalStrut(4));
        hdr.add(sub);
        hdr.add(Box.createVerticalStrut(16));
        hdr.add(buildProgressBar());
        hdr.add(Box.createVerticalStrut(4));
        return hdr;
    }

    private JPanel buildProgressBar() {
        JPanel row = new JPanel(new GridLayout(1, 2, 8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        row.setPreferredSize(new Dimension(0, 5));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        bars = new JPanel[2];
        for (int i = 0; i < 2; i++) {
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

    private void refreshBars() { for (JPanel b : bars) b.repaint(); }

    // ── Step 1: Academic Data ─────────────────────────────────────────────────

    private JScrollPane buildStep1() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 32, UITheme.PAD));

        nameField = new StyledField("e.g. Alex Rivera");
        nimField  = new StyledField("e.g. 2505551107");

        String[] faculties = {"Teknik", "Ekonomi dan Bisnis", "Seni dan Desain",
                              "Sains dan Matematika", "Hukum", "Kedokteran", "FISIP", "Pertanian"};
        String[] majors    = {"Informatika", "Ilmu Komputer", "Sistem Informasi",
                              "Teknik Komputer", "Desain Komunikasi Visual",
                              "Manajemen", "Akuntansi", "Ekonomi",
                              "Statistika", "Matematika"};
        facultyBox = new JComboBox<>(faculties);
        majorBox   = new JComboBox<>(majors);
        styleCombo(facultyBox);
        styleCombo(majorBox);

        RoundedButton next = new RoundedButton("Lanjut  →", UITheme.DARK, Color.WHITE);
        next.addActionListener(e -> {
            if (nameField.getText().isEmpty() || nimField.getText().isEmpty()) {
                warn("Isi Nama dan NIM terlebih dahulu!"); return;
            }
            currentStep = 2; refreshBars();
            stepLayout.show(stepPanel, "2");
        });

        RoundedButton toLogin = new RoundedButton("Sudah punya akun? Login", UITheme.BADGE, UITheme.TEXT);
        toLogin.addActionListener(e -> { new LoginForm(); dispose(); });

        p.add(field("Data Akademik", "SansSerif", Font.BOLD, 20));
        p.add(Box.createVerticalStrut(20));
        p.add(lbl("Nama Lengkap"));       p.add(Box.createVerticalStrut(8));  p.add(nameField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("NIM"));                p.add(Box.createVerticalStrut(8));  p.add(nimField);
        p.add(Box.createVerticalStrut(20));
        p.add(lbl("Fakultas"));           p.add(Box.createVerticalStrut(8));  p.add(wrap(facultyBox));
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Jurusan"));            p.add(Box.createVerticalStrut(8));  p.add(wrap(majorBox));
        p.add(Box.createVerticalStrut(32));
        p.add(next);
        p.add(Box.createVerticalStrut(10));
        p.add(toLogin);
        p.add(Box.createVerticalGlue());

        return scrollOf(p);
    }

    // ── Step 2: Account & Profile ─────────────────────────────────────────────

    private JScrollPane buildStep2() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 32, UITheme.PAD));

        emailField     = new StyledField("email@universitas.ac.id");
        passwordField  = new StyledPasswordField();
        contactField   = new StyledField("e.g. 081234567890 (opsional)");
        bioField       = new StyledField("Deskripsi singkat dirimu (opsional)");
        cvField        = new StyledField("Link CV / Google Drive (opsional)");
        portfolioField = new StyledField("Link Portfolio / GitHub / Behance (opsional)");

        RoundedButton submit = new RoundedButton("Selesaikan Registrasi", UITheme.DARK, Color.WHITE);
        submit.addActionListener(e -> doRegister());

        RoundedButton back = new RoundedButton("← Kembali", UITheme.BADGE, UITheme.TEXT);
        back.addActionListener(e -> { currentStep = 1; refreshBars(); stepLayout.show(stepPanel, "1"); });

        p.add(field("Akun & Profil", "SansSerif", Font.BOLD, 20));
        p.add(Box.createVerticalStrut(4));
        p.add(lbl2("CV & porto bisa dilengkapi nanti di halaman profil."));
        p.add(Box.createVerticalStrut(20));
        p.add(lbl("Email Institusi *"));  p.add(Box.createVerticalStrut(8)); p.add(emailField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Password *"));         p.add(Box.createVerticalStrut(8)); p.add(passwordField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Nomor HP"));           p.add(Box.createVerticalStrut(8)); p.add(contactField);
        p.add(Box.createVerticalStrut(24));

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(sep);
        p.add(Box.createVerticalStrut(20));

        p.add(lbl("Bio"));                p.add(Box.createVerticalStrut(8)); p.add(bioField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("CV Link"));            p.add(Box.createVerticalStrut(8)); p.add(cvField);
        p.add(Box.createVerticalStrut(16));
        p.add(lbl("Portfolio Link"));     p.add(Box.createVerticalStrut(8)); p.add(portfolioField);
        p.add(Box.createVerticalStrut(32));
        p.add(submit);
        p.add(Box.createVerticalStrut(10));
        p.add(back);
        p.add(Box.createVerticalGlue());

        return scrollOf(p);
    }

    // ── Logic ─────────────────────────────────────────────────────────────────

    private void doRegister() {
        String email   = emailField.getText().trim();
        String pw      = passwordField.getPasswordText();

        if (email.isEmpty() || pw.isEmpty()) {
            warn("Email dan password wajib diisi!"); return;
        }
        if (pw.length() < 6) {
            warn("Password minimal 6 karakter!"); return;
        }
        if (auth.isEmailTaken(email)) {
            warn("Email sudah terdaftar!"); return;
        }

        String faculty = (String) facultyBox.getSelectedItem();
        String major   = (String) majorBox.getSelectedItem();
        String contact = contactField.getText().trim();

        User user = auth.register(
                nameField.getText().trim(), nimField.getText().trim(),
                email, pw, faculty, major, contact);
        if (user == null) {
            warn("Registrasi gagal. Periksa kembali data kamu."); return;
        }

        user.setBio(bioField.getText().trim());
        user.setCvLink(cvField.getText().trim());
        user.setPortfolioLink(portfolioField.getText().trim());

        Session.setCurrentUser(user);
        JOptionPane.showMessageDialog(this,
                "Selamat datang, " + user.getName() + "! Registrasi berhasil.");
        new MainFrame();
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tandem - Join the Team");
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 732, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    // ── Utilities ─────────────────────────────────────────────────────────────

    private JLabel field(String text, String family, int style, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(family, style, size));
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

    private JLabel lbl2(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SMALL);
        l.setForeground(UITheme.GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(UITheme.F_BODY);
        cb.setBackground(UITheme.CARD);
        cb.setForeground(UITheme.TEXT);
        cb.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
    }

    private JPanel wrap(JComboBox<String> cb) {
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        w.setPreferredSize(new Dimension(0, 44));
        w.setAlignmentX(Component.LEFT_ALIGNMENT);
        w.add(cb, BorderLayout.CENTER);
        return w;
    }

    private JScrollPane scrollOf(JPanel p) {
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getViewport().setBackground(UITheme.BG);
        return sp;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
