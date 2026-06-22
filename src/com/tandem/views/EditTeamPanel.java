package com.tandem.views;

import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.services.DataStore;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class EditTeamPanel extends JPanel {

    private final MainFrame frame;
    private final Team team;
    private final TeamController tc = new TeamController();

    private JTextField teamNameField, descField;
    private JCheckBox regDeadlineCheck;
    private JComboBox<String> regDayBox, regMonthBox, regYearBox;
    private JPanel regDateRow;
    private JPanel slotsContainer;
    private final ArrayList<String> currentSlots;

    public EditTeamPanel(MainFrame frame, Team team) {
        this.frame = frame;
        this.team  = team;
        this.currentSlots = new ArrayList<>(team.getOpenSlots());

        setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(UITheme.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        FitPanel p = new FitPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, UITheme.PAD, 32, UITheme.PAD));

        // Back
        JLabel back = new JLabel("← Back");
        back.setFont(UITheme.F_LABEL);
        back.setForeground(UITheme.GRAY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { frame.showTeamDetail(team); }
        });

        JLabel title = new JLabel("Edit Tim");
        title.setFont(UITheme.F_TITLE);
        title.setForeground(UITheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Ubah detail tim, slot, dan anggota.");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Fields ────────────────────────────────────────────────────────────
        teamNameField = styledField();
        teamNameField.setText(team.getTeamName());

        descField = styledField();
        descField.setText(team.getDescription());

        // ── Registration deadline ─────────────────────────────────────────────
        regDeadlineCheck = new JCheckBox("Tentukan batas pendaftaran anggota");
        regDeadlineCheck.setFont(UITheme.F_BODY);
        regDeadlineCheck.setBackground(UITheme.BG);
        regDeadlineCheck.setForeground(UITheme.TEXT);
        regDeadlineCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        regDeadlineCheck.setFocusPainted(false);

        String[] days = new String[31];
        for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1);
        regDayBox = new JComboBox<>(days);
        regDayBox.setFont(UITheme.F_BODY);
        regDayBox.setBackground(UITheme.CARD);

        regMonthBox = new JComboBox<>(new String[]{
            "Januari","Februari","Maret","April","Mei","Juni",
            "Juli","Agustus","September","Oktober","November","Desember"});
        regMonthBox.setFont(UITheme.F_BODY);
        regMonthBox.setBackground(UITheme.CARD);

        regYearBox = new JComboBox<>(new String[]{"2025","2026","2027","2028","2029","2030"});
        regYearBox.setFont(UITheme.F_BODY);
        regYearBox.setBackground(UITheme.CARD);

        String existingDeadline = team.getRegistrationDeadline();
        if (!existingDeadline.isEmpty()) {
            regDeadlineCheck.setSelected(true);
            try {
                String[] parts = existingDeadline.split("-");
                regYearBox.setSelectedItem(parts[0]);
                regMonthBox.setSelectedIndex(Integer.parseInt(parts[1]) - 1);
                regDayBox.setSelectedIndex(Integer.parseInt(parts[2]) - 1);
            } catch (Exception ignored) {}
        } else {
            regYearBox.setSelectedItem("2026");
        }

        regDateRow = new JPanel(new GridLayout(1, 3, 8, 0));
        regDateRow.setOpaque(false);
        regDateRow.setPreferredSize(new Dimension(0, 44));
        regDateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        regDateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        regDateRow.add(regDayBox);
        regDateRow.add(regMonthBox);
        regDateRow.add(regYearBox);
        regDateRow.setVisible(regDeadlineCheck.isSelected());

        regDeadlineCheck.addActionListener(e -> {
            regDateRow.setVisible(regDeadlineCheck.isSelected());
            revalidate(); repaint();
        });

        // ── Slots ─────────────────────────────────────────────────────────────
        slotsContainer = new JPanel();
        slotsContainer.setLayout(new BoxLayout(slotsContainer, BoxLayout.Y_AXIS));
        slotsContainer.setOpaque(false);
        slotsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        rebuildSlots();

        RoundedButton addSlotBtn = new RoundedButton("+ Tambah Slot", UITheme.BADGE, UITheme.TEXT);
        addSlotBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addSlotBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this,
                    "Nama slot baru:", "Tambah Slot", JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.trim().isEmpty()) return;
            currentSlots.add(name.trim());
            rebuildSlots();
            revalidate(); repaint();
        });

        // ── Members (non-leader) ──────────────────────────────────────────────
        JPanel membersContainer = new JPanel();
        membersContainer.setLayout(new BoxLayout(membersContainer, BoxLayout.Y_AXIS));
        membersContainer.setOpaque(false);
        membersContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean hasNonLeader = false;
        for (User member : team.getMembers()) {
            if (member.getUserId().equals(team.getLeader().getUserId())) continue;
            hasNonLeader = true;
            membersContainer.add(buildMemberRow(member));
            membersContainer.add(Box.createVerticalStrut(8));
        }

        // ── Save button ───────────────────────────────────────────────────────
        RoundedButton saveBtn = new RoundedButton("Simpan Perubahan", UITheme.DARK, Color.WHITE);
        saveBtn.addActionListener(e -> doSave());

        // ── Assemble ──────────────────────────────────────────────────────────
        p.add(back);
        p.add(Box.createVerticalStrut(12));
        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(24));

        p.add(sectionLabel("Nama Tim"));
        p.add(Box.createVerticalStrut(8));
        p.add(teamNameField);
        p.add(Box.createVerticalStrut(16));

        p.add(sectionLabel("Deskripsi Tim"));
        p.add(Box.createVerticalStrut(8));
        p.add(descField);
        p.add(Box.createVerticalStrut(16));

        p.add(sectionLabel("Deadline Pendaftaran Tim"));
        p.add(Box.createVerticalStrut(4));
        p.add(smallGray("Batas tanggal orang bisa bergabung ke tim ini (opsional)."));
        p.add(Box.createVerticalStrut(8));
        p.add(regDeadlineCheck);
        p.add(Box.createVerticalStrut(8));
        p.add(regDateRow);
        p.add(Box.createVerticalStrut(24));

        p.add(sectionLabel("Open Slots"));
        p.add(Box.createVerticalStrut(4));
        p.add(smallGray("Slot posisi yang tersedia untuk anggota baru."));
        p.add(Box.createVerticalStrut(12));
        p.add(slotsContainer);
        p.add(Box.createVerticalStrut(8));
        p.add(addSlotBtn);

        if (hasNonLeader) {
            p.add(Box.createVerticalStrut(24));
            p.add(sectionLabel("Anggota Tim"));
            p.add(Box.createVerticalStrut(4));
            p.add(smallGray("Keluarkan anggota dari tim."));
            p.add(Box.createVerticalStrut(12));
            p.add(membersContainer);
        }

        p.add(Box.createVerticalStrut(32));
        p.add(saveBtn);

        return p;
    }

    private void rebuildSlots() {
        slotsContainer.removeAll();
        if (currentSlots.isEmpty()) {
            JLabel empty = new JLabel("Belum ada open slot.");
            empty.setFont(UITheme.F_SMALL);
            empty.setForeground(UITheme.HINT);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            slotsContainer.add(empty);
            slotsContainer.add(Box.createVerticalStrut(8));
        } else {
            for (int i = 0; i < currentSlots.size(); i++) {
                final int idx = i;
                JPanel row = buildSlotRow(currentSlots.get(i), () -> {
                    currentSlots.remove(idx);
                    rebuildSlots();
                    revalidate(); repaint();
                });
                slotsContainer.add(row);
                slotsContainer.add(Box.createVerticalStrut(8));
            }
        }
        slotsContainer.revalidate();
        slotsContainer.repaint();
    }

    private JPanel buildSlotRow(String slotName, Runnable onRemove) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setPreferredSize(new Dimension(0, 44));

        JLabel lbl = new JLabel(slotName);
        lbl.setFont(UITheme.F_BODY);
        lbl.setForeground(UITheme.TEXT);
        lbl.setBackground(UITheme.CARD);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));

        JButton removeBtn = new JButton("×");
        removeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        removeBtn.setForeground(UITheme.GRAY);
        removeBtn.setBackground(UITheme.BADGE);
        removeBtn.setPreferredSize(new Dimension(44, 44));
        removeBtn.setFocusPainted(false);
        removeBtn.setBorderPainted(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> onRemove.run());

        row.add(lbl, BorderLayout.CENTER);
        row.add(removeBtn, BorderLayout.EAST);
        return row;
    }

    private JPanel buildMemberRow(User member) {
        RoundedPanel row = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatar = makeAvatar(member.getName().substring(0, 1).toUpperCase());

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel name = new JLabel(member.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 14));
        name.setForeground(UITheme.TEXT);
        JLabel major = new JLabel(member.getMajor());
        major.setFont(UITheme.F_SMALL);
        major.setForeground(UITheme.GRAY);
        info.add(name);
        info.add(major);

        final Color RED = new Color(220, 50, 50);
        JButton kickBtn = new JButton("Keluarkan") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        kickBtn.setOpaque(false);
        kickBtn.setContentAreaFilled(false);
        kickBtn.setBorderPainted(false);
        kickBtn.setFocusPainted(false);
        kickBtn.setFont(UITheme.F_SMALL);
        kickBtn.setForeground(RED);
        kickBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        kickBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Keluarkan " + member.getName() + " dari tim?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tc.kickMember(team, member);
                JOptionPane.showMessageDialog(this,
                        member.getName() + " telah dikeluarkan dari tim.");
                frame.showEditTeam(team);
            }
        });

        row.add(avatar, BorderLayout.WEST);
        row.add(info,   BorderLayout.CENTER);
        row.add(kickBtn, BorderLayout.EAST);
        return row;
    }

    private void doSave() {
        String newName = teamNameField.getText().trim();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tim tidak boleh kosong!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        team.setTeamName(newName);
        team.setDescription(descField.getText().trim());

        if (regDeadlineCheck.isSelected()) {
            int day   = regDayBox.getSelectedIndex() + 1;
            int month = regMonthBox.getSelectedIndex() + 1;
            String year = (String) regYearBox.getSelectedItem();
            team.setRegistrationDeadline(String.format("%s-%02d-%02d", year, month, day));
        } else {
            team.setRegistrationDeadline("");
        }

        tc.updateSlots(team, currentSlots);
        DataStore.getInstance().persistToFile();

        JOptionPane.showMessageDialog(this, "Tim berhasil diperbarui!");
        frame.showTeamDetail(team);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JPanel makeAvatar(String letter) {
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.DARK);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter,
                        (getWidth()  - fm.stringWidth(letter)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        av.setPreferredSize(new Dimension(36, 36));
        av.setMaximumSize(new Dimension(36, 36));
        av.setOpaque(false);
        return av;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(UITheme.F_BODY);
        f.setBackground(UITheme.CARD);
        f.setPreferredSize(new Dimension(0, 44));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return f;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_LABEL);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel smallGray(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SMALL);
        l.setForeground(UITheme.GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static class FitPanel extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d) { return 20; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) { return r.height; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
