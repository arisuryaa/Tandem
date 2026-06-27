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

    private JTextField compNameField;
    private JComboBox<String> compEventStartDayBox, compEventStartMonthBox, compEventStartYearBox;
    private JComboBox<String> compEventEndDayBox, compEventEndMonthBox, compEventEndYearBox;
    private JPanel compStartDateRow, compEndDateRow;

    private JPanel pendingApplicantsPanel, acceptedMembersPanel;
    private boolean pendingExpanded = true, membersExpanded = true;

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

        // ── Competition Details ───────────────────────────────────────────────
        compNameField = styledField();
        Competition competition = team.getCompetition();
        if (competition != null) {
            compNameField.setText(competition.getName());
        } else {
            compNameField.setText("(No competition)");
        }
        compNameField.setEditable(false);

        String[] compDays = new String[31];
        for (int i = 0; i < 31; i++) compDays[i] = String.format("%02d", i + 1);

        String[] compMonths = new String[]{
            "Januari","Februari","Maret","April","Mei","Juni",
            "Juli","Agustus","September","Oktober","November","Desember"};

        String[] compYears = new String[]{"2025","2026","2027","2028","2029","2030"};

        // Event start date
        compEventStartDayBox = new JComboBox<>(compDays);
        compEventStartDayBox.setFont(UITheme.F_BODY);
        compEventStartDayBox.setBackground(UITheme.CARD);

        compEventStartMonthBox = new JComboBox<>(compMonths);
        compEventStartMonthBox.setFont(UITheme.F_BODY);
        compEventStartMonthBox.setBackground(UITheme.CARD);

        compEventStartYearBox = new JComboBox<>(compYears);
        compEventStartYearBox.setFont(UITheme.F_BODY);
        compEventStartYearBox.setBackground(UITheme.CARD);

        String eventStart = team.getCompetition().getEventStartDate();
        if (eventStart != null && !eventStart.isEmpty()) {
            String[] parts = eventStart.split("-");
            if (parts.length == 3) {
                try {
                    compEventStartYearBox.setSelectedItem(parts[0]);
                    compEventStartMonthBox.setSelectedIndex(Integer.parseInt(parts[1]) - 1);
                    compEventStartDayBox.setSelectedIndex(Integer.parseInt(parts[2]) - 1);
                } catch (Exception e) {
                    // Silently default to first option if format is invalid
                }
            }
        }

        compStartDateRow = new JPanel(new GridLayout(1, 3, 8, 0));
        compStartDateRow.setOpaque(false);
        compStartDateRow.setPreferredSize(new Dimension(0, 44));
        compStartDateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        compStartDateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        compStartDateRow.add(compEventStartDayBox);
        compStartDateRow.add(compEventStartMonthBox);
        compStartDateRow.add(compEventStartYearBox);

        // Event end date
        compEventEndDayBox = new JComboBox<>(compDays);
        compEventEndDayBox.setFont(UITheme.F_BODY);
        compEventEndDayBox.setBackground(UITheme.CARD);

        compEventEndMonthBox = new JComboBox<>(compMonths);
        compEventEndMonthBox.setFont(UITheme.F_BODY);
        compEventEndMonthBox.setBackground(UITheme.CARD);

        compEventEndYearBox = new JComboBox<>(compYears);
        compEventEndYearBox.setFont(UITheme.F_BODY);
        compEventEndYearBox.setBackground(UITheme.CARD);

        String eventEnd = team.getCompetition().getEventEndDate();
        if (eventEnd != null && !eventEnd.isEmpty()) {
            String[] parts = eventEnd.split("-");
            if (parts.length == 3) {
                try {
                    compEventEndYearBox.setSelectedItem(parts[0]);
                    compEventEndMonthBox.setSelectedIndex(Integer.parseInt(parts[1]) - 1);
                    compEventEndDayBox.setSelectedIndex(Integer.parseInt(parts[2]) - 1);
                } catch (Exception e) {
                    // Silently default to first option if format is invalid
                }
            }
        }

        compEndDateRow = new JPanel(new GridLayout(1, 3, 8, 0));
        compEndDateRow.setOpaque(false);
        compEndDateRow.setPreferredSize(new Dimension(0, 44));
        compEndDateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        compEndDateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        compEndDateRow.add(compEventEndDayBox);
        compEndDateRow.add(compEventEndMonthBox);
        compEndDateRow.add(compEventEndYearBox);

        // Apply lock if team has members
        if (isCompetitionLocked()) {
            compNameField.setEditable(false);

            // Add focus listener for text field (which can receive focus when not editable)
            FocusListener lockWarningTextField = new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    String msg = "Field ini tidak bisa diubah karena sudah ada anggota tim yang bergabung. " +
                                 "Jika ingin mengubah, silakan diskusikan dengan anggota tim terlebih dahulu.";
                    JOptionPane.showMessageDialog(EditTeamPanel.this, msg, "Tidak Bisa Diubah", JOptionPane.INFORMATION_MESSAGE);
                    e.getComponent().transferFocus();
                }
            };
            compNameField.addFocusListener(lockWarningTextField);
        }

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

        p.add(sectionLabel("Kompetisi"));
        if (isCompetitionLocked()) {
            JLabel lockLabel = new JLabel("🔒 Terkunci (anggota sudah bergabung)");
            lockLabel.setFont(UITheme.F_SMALL);
            lockLabel.setForeground(new Color(220, 50, 50));
            lockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(lockLabel);
            p.add(Box.createVerticalStrut(4));
        }
        p.add(Box.createVerticalStrut(4));
        p.add(smallGray("Nama dan jadwal kompetisi (tidak bisa diubah jika sudah ada anggota)."));
        p.add(Box.createVerticalStrut(8));
        p.add(smallGray("Nama Kompetisi:"));
        p.add(Box.createVerticalStrut(4));
        p.add(compNameField);
        p.add(Box.createVerticalStrut(8));
        p.add(smallGray("Jadwal Lomba - Mulai:"));
        p.add(Box.createVerticalStrut(4));
        p.add(compStartDateRow);
        p.add(Box.createVerticalStrut(8));
        p.add(smallGray("Jadwal Lomba - Selesai:"));
        p.add(Box.createVerticalStrut(4));
        p.add(compEndDateRow);
        p.add(Box.createVerticalStrut(24));

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

        // ── Member Competition Load ───────────────────────────────────────
        ArrayList<JoinRequest> pendingReqs = team.getPendingRequests();
        if (!pendingReqs.isEmpty()) {
            p.add(Box.createVerticalStrut(24));
            p.add(sectionLabel("Permintaan Gabung"));
            p.add(Box.createVerticalStrut(4));
            p.add(smallGray("Kompetisi lain yang sedang diikuti calon anggota tim."));
            p.add(Box.createVerticalStrut(12));
            pendingApplicantsPanel = buildPendingApplicantsSection();
            p.add(pendingApplicantsPanel);
        }

        ArrayList<User> nonLeaderMembers = new ArrayList<>();
        for (User m : team.getMembers()) {
            if (!m.getUserId().equals(team.getLeader().getUserId())) {
                nonLeaderMembers.add(m);
            }
        }

        if (!nonLeaderMembers.isEmpty()) {
            p.add(Box.createVerticalStrut(24));
            p.add(sectionLabel("Jadwal Anggota Tim"));
            p.add(Box.createVerticalStrut(4));
            p.add(smallGray("Kompetisi lain yang sedang diikuti anggota tim."));
            p.add(Box.createVerticalStrut(12));
            acceptedMembersPanel = buildAcceptedMembersSection();
            p.add(acceptedMembersPanel);
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

    private boolean isCompetitionLocked() {
        return team.getMembers().size() > 1;
    }

    private String formatDateRange(String startDate, String endDate) {
        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
            return "";
        }
        try {
            String[] startParts = startDate.split("-");
            String[] endParts = endDate.split("-");
            int startDay = Integer.parseInt(startParts[2]);
            int startMonth = Integer.parseInt(startParts[1]);
            int endDay = Integer.parseInt(endParts[2]);
            int endMonth = Integer.parseInt(endParts[1]);

            String[] monthNames = {"Januari","Februari","Maret","April","Mei","Juni",
                                  "Juli","Agustus","September","Oktober","November","Desember"};

            return String.format("%d %s - %d %s",
                startDay, monthNames[startMonth - 1],
                endDay, monthNames[endMonth - 1]);
        } catch (Exception e) {
            return startDate + " - " + endDate;
        }
    }

    private JPanel buildExpandableCompetitionSection(User user) {
        if (user == null) {
            JPanel emptySection = new JPanel();
            emptySection.setOpaque(false);
            JLabel empty = new JLabel("(User data unavailable)");
            empty.setFont(UITheme.F_SMALL);
            empty.setForeground(UITheme.HINT);
            emptySection.add(empty);
            return emptySection;
        }

        ArrayList<Team> userTeams = tc.getAcceptedTeamsForUser(user);
        ArrayList<Team> otherTeams = new ArrayList<>();
        for (Team t : userTeams) {
            if (!t.getTeamId().equals(team.getTeamId())) {
                otherTeams.add(t);
            }
        }

        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (otherTeams.isEmpty()) {
            JLabel noCompetitions = new JLabel("(Tidak mengikuti kompetisi lain)");
            noCompetitions.setFont(UITheme.F_SMALL);
            noCompetitions.setForeground(UITheme.HINT);
            noCompetitions.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.add(noCompetitions);
        } else {
            for (Team t : otherTeams) {
                Competition comp = t.getCompetition();
                String dateRange = formatDateRange(comp.getEventStartDate(), comp.getEventEndDate());

                JPanel compRow = new JPanel();
                compRow.setLayout(new BoxLayout(compRow, BoxLayout.Y_AXIS));
                compRow.setOpaque(false);
                compRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                compRow.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 0));

                JLabel compName = new JLabel("• " + comp.getName());
                compName.setFont(UITheme.F_SMALL);
                compName.setForeground(UITheme.TEXT);
                compName.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel compDate = new JLabel("  📅 " + dateRange);
                compDate.setFont(UITheme.F_SMALL);
                compDate.setForeground(UITheme.GRAY);
                compDate.setAlignmentX(Component.LEFT_ALIGNMENT);

                compRow.add(compName);
                compRow.add(compDate);
                section.add(compRow);
                section.add(Box.createVerticalStrut(4));
            }
        }

        return section;
    }

    private JPanel buildPendingApplicantsSection() {
        ArrayList<JoinRequest> pending = team.getPendingRequests();

        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (pending.isEmpty()) {
            JLabel empty = new JLabel("Belum ada permintaan gabung.");
            empty.setFont(UITheme.F_SMALL);
            empty.setForeground(UITheme.HINT);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.add(empty);
        } else {
            for (JoinRequest req : pending) {
                User requester = req.getRequester();

                JPanel userRow = new JPanel();
                userRow.setLayout(new BoxLayout(userRow, BoxLayout.Y_AXIS));
                userRow.setOpaque(false);
                userRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                userRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

                JLabel userName = new JLabel(requester.getName());
                userName.setFont(new Font("SansSerif", Font.BOLD, 12));
                userName.setForeground(UITheme.TEXT);
                userName.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel compSection = buildExpandableCompetitionSection(requester);

                userRow.add(userName);
                userRow.add(Box.createVerticalStrut(4));
                userRow.add(compSection);

                section.add(userRow);
                section.add(Box.createVerticalStrut(12));
            }
        }

        return section;
    }

    private JPanel buildAcceptedMembersSection() {
        ArrayList<User> members = team.getMembers();
        ArrayList<User> nonLeaderMembers = new ArrayList<>();
        for (User m : members) {
            if (!m.getUserId().equals(team.getLeader().getUserId())) {
                nonLeaderMembers.add(m);
            }
        }

        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (nonLeaderMembers.isEmpty()) {
            JLabel empty = new JLabel("Belum ada anggota bergabung (selain leader).");
            empty.setFont(UITheme.F_SMALL);
            empty.setForeground(UITheme.HINT);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.add(empty);
        } else {
            for (User member : nonLeaderMembers) {
                JPanel userRow = new JPanel();
                userRow.setLayout(new BoxLayout(userRow, BoxLayout.Y_AXIS));
                userRow.setOpaque(false);
                userRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                userRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

                JLabel userName = new JLabel(member.getName());
                userName.setFont(new Font("SansSerif", Font.BOLD, 12));
                userName.setForeground(UITheme.TEXT);
                userName.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel compSection = buildExpandableCompetitionSection(member);

                userRow.add(userName);
                userRow.add(Box.createVerticalStrut(4));
                userRow.add(compSection);

                section.add(userRow);
                section.add(Box.createVerticalStrut(12));
            }
        }

        return section;
    }

    private static class FitPanel extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d) { return 20; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) { return r.height; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
