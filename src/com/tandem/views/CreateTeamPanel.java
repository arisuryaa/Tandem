package com.tandem.views;

import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.services.DataStore;
import com.tandem.services.Session;
import com.tandem.views.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class CreateTeamPanel extends JPanel {

    private final MainFrame frame;
    private final TeamController tc = new TeamController();
    private final DataStore store = DataStore.getInstance();

    // Competition mode
    private boolean createNewComp = false;
    private JPanel compSelectPanel;
    private JPanel compCreatePanel;
    private JComboBox<String> existingCompBox;
    private JTextField compNameField, compDeadlineField;
    private JComboBox<String> compCategoryBox;
    private JTextField compTagsField;

    // Team fields
    private JTextField teamNameField, descField;
    private JPanel slotsContainer;
    private final ArrayList<JTextField> slotFields = new ArrayList<>();

    public CreateTeamPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(UITheme.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel p = new JPanel();
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
            @Override public void mouseClicked(MouseEvent e) { frame.showDashboard(); }
        });

        JLabel title = new JLabel("Create Team");
        title.setFont(UITheme.F_TITLE);
        title.setForeground(UITheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Set up your competition team and open positions.");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Team name
        teamNameField = styledField();
        // Description
        descField = styledField();

        // Competition selection
        JLabel compLabel = sectionLabel("Kompetisi");
        JLabel compSub = smallGray("Pilih kompetisi yang sudah ada atau buat baru.");

        // Toggle buttons
        JPanel modeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        modeRow.setOpaque(false);
        modeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JToggleButton btnExisting = new JToggleButton("Pilih yang ada");
        JToggleButton btnNew = new JToggleButton("Buat baru");
        styleToggle(btnExisting, true);
        styleToggle(btnNew, false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(btnExisting); bg.add(btnNew);
        btnExisting.setSelected(true);

        modeRow.add(btnExisting);
        modeRow.add(btnNew);

        // Panel: pilih kompetisi existing
        compSelectPanel = buildExistingCompPanel();
        compSelectPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel: buat kompetisi baru
        compCreatePanel = buildNewCompPanel();
        compCreatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        compCreatePanel.setVisible(false);

        btnExisting.addActionListener(e -> {
            createNewComp = false;
            compSelectPanel.setVisible(true);
            compCreatePanel.setVisible(false);
            styleToggle(btnExisting, true);
            styleToggle(btnNew, false);
        });
        btnNew.addActionListener(e -> {
            createNewComp = true;
            compSelectPanel.setVisible(false);
            compCreatePanel.setVisible(true);
            styleToggle(btnExisting, false);
            styleToggle(btnNew, true);
        });

        // Open slots
        JLabel slotsLabel = sectionLabel("Open Slots yang Dibutuhkan");
        JLabel slotsSub = smallGray("Deskripsikan posisi yang kamu butuhkan dari anggota.");

        slotsContainer = new JPanel();
        slotsContainer.setLayout(new BoxLayout(slotsContainer, BoxLayout.Y_AXIS));
        slotsContainer.setOpaque(false);
        slotsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        addSlotField();

        RoundedButton addSlotBtn = new RoundedButton("+ Tambah Slot", UITheme.BADGE, UITheme.TEXT);
        addSlotBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addSlotBtn.addActionListener(e -> {
            addSlotField();
            revalidate(); repaint();
        });

        // Create button
        RoundedButton createBtn = new RoundedButton("Buat Tim", UITheme.DARK, Color.WHITE);
        createBtn.addActionListener(e -> doCreate());

        p.add(back);
        p.add(Box.createVerticalStrut(12));
        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(24));
        p.add(sectionLabel("Nama Tim"));      p.add(Box.createVerticalStrut(8)); p.add(teamNameField);
        p.add(Box.createVerticalStrut(16));
        p.add(sectionLabel("Deskripsi Tim")); p.add(Box.createVerticalStrut(8)); p.add(descField);
        p.add(Box.createVerticalStrut(24));
        p.add(compLabel);
        p.add(Box.createVerticalStrut(4));
        p.add(compSub);
        p.add(Box.createVerticalStrut(12));
        p.add(modeRow);
        p.add(Box.createVerticalStrut(12));
        p.add(compSelectPanel);
        p.add(compCreatePanel);
        p.add(Box.createVerticalStrut(24));
        p.add(slotsLabel);
        p.add(Box.createVerticalStrut(4));
        p.add(slotsSub);
        p.add(Box.createVerticalStrut(12));
        p.add(slotsContainer);
        p.add(Box.createVerticalStrut(8));
        p.add(addSlotBtn);
        p.add(Box.createVerticalStrut(32));
        p.add(createBtn);

        return p;
    }

    private JPanel buildExistingCompPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        ArrayList<Competition> comps = store.getAllCompetitions();
        String[] names = new String[comps.size()];
        for (int i = 0; i < comps.size(); i++) {
            names[i] = comps.get(i).getName() + " [" + comps.get(i).getCategory() + "]";
        }
        existingCompBox = new JComboBox<>(names.length > 0 ? names : new String[]{"Belum ada kompetisi"});
        existingCompBox.setFont(UITheme.F_BODY);
        existingCompBox.setBackground(UITheme.CARD);

        panel.add(existingCompBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildNewCompPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        compNameField     = styledField();
        compDeadlineField = styledField();
        compDeadlineField.setText("YYYY-MM-DD");

        String[] categories = {"Hackathon", "Design", "PKM", "Business", "Data Science", "Other"};
        compCategoryBox = new JComboBox<>(categories);
        compCategoryBox.setFont(UITheme.F_BODY);
        compCategoryBox.setBackground(UITheme.CARD);
        compCategoryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        compCategoryBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        compTagsField = styledField();
        compTagsField.setText("e.g. Informatika, Manajemen, DKV");

        panel.add(smallGray("Nama Kompetisi")); panel.add(Box.createVerticalStrut(6)); panel.add(compNameField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(smallGray("Kategori")); panel.add(Box.createVerticalStrut(6)); panel.add(compCategoryBox);
        panel.add(Box.createVerticalStrut(12));
        panel.add(smallGray("Deadline")); panel.add(Box.createVerticalStrut(6)); panel.add(compDeadlineField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(smallGray("Tags Jurusan (pisah dengan koma)")); panel.add(Box.createVerticalStrut(6)); panel.add(compTagsField);

        return panel;
    }

    private void addSlotField() {
        JTextField field = styledField();
        field.setText("e.g. Desainer UI");
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        slotFields.add(field);
        slotsContainer.add(field);
        slotsContainer.add(Box.createVerticalStrut(8));
    }

    private void doCreate() {
        User leader = Session.getCurrentUser();
        String tName = teamNameField.getText().trim();
        String desc  = descField.getText().trim();

        if (tName.isEmpty()) {
            warn("Nama tim wajib diisi!"); return;
        }

        // Build slots
        ArrayList<String> slots = new ArrayList<>();
        for (JTextField sf : slotFields) {
            String slot = sf.getText().trim();
            if (!slot.isEmpty() && !slot.equals("e.g. Desainer UI")) slots.add(slot);
        }
        if (slots.isEmpty()) {
            warn("Tambahkan minimal satu open slot!"); return;
        }

        // Build competition
        Competition comp;
        ArrayList<Competition> allComps = store.getAllCompetitions();

        if (!createNewComp) {
            if (allComps.isEmpty()) {
                warn("Tidak ada kompetisi tersedia. Buat kompetisi baru."); return;
            }
            int idx = existingCompBox.getSelectedIndex();
            comp = allComps.get(idx);
        } else {
            String cName = compNameField.getText().trim();
            String cDead = compDeadlineField.getText().trim();
            String cCat  = (String) compCategoryBox.getSelectedItem();

            if (cName.isEmpty() || cDead.isEmpty()) {
                warn("Nama dan deadline kompetisi wajib diisi!"); return;
            }

            ArrayList<String> tags = new ArrayList<>();
            for (String tag : compTagsField.getText().split(",")) {
                String t = tag.trim();
                if (!t.isEmpty()) tags.add(t);
            }
            if (tags.isEmpty()) tags.add("Semua");

            comp = new Competition(
                    java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    cName, cCat, cDead, slots.size() + 1, tags);
        }

        tc.createTeam(leader, tName, desc, comp, slots);
        JOptionPane.showMessageDialog(this, "Tim \"" + tName + "\" berhasil dibuat!");
        frame.showDashboard();
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

    private void styleToggle(JToggleButton btn, boolean active) {
        btn.setFont(UITheme.F_SMALL);
        btn.setBackground(active ? UITheme.DARK : UITheme.CARD);
        btn.setForeground(active ? Color.WHITE : UITheme.TEXT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(active ? UITheme.DARK : UITheme.BORDER),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
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

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validasi", JOptionPane.WARNING_MESSAGE);
    }
}
