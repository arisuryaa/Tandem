# Member Load & Competition Lock Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add two features to EditTeamPanel: (1) display pending applicants and accepted members with their accepted competitions, (2) lock competition name/schedule fields after first member joins.

**Architecture:** 
- **Feature 1 (Member Competition Load):** Extend EditTeamPanel with two new expandable sections below existing member list. Each section displays a list of users (pending applicants or accepted members) with collapsible details showing their accepted competitions and event dates. Data fetched via TeamController helper method.
- **Feature 2 (Lock Competition Details):** Add competition name and schedule display/edit fields to EditTeamPanel (new section above slots). Detect lock condition (members.size() > 1) and set fields to read-only when true. Add FocusListener to show warning dialog if user tries to interact with locked fields.

**Tech Stack:** Java 17, Swing, MVC architecture, existing Team/JoinRequest/User models

## Global Constraints

- Java 17 target
- Swing UI framework, 450px fixed width (FitPanel scrollable)
- Date format: YYYY-MM-DD for storage, displayed as "DD Bulan YYYY - DD Bulan YYYY" (e.g., "15 Mei - 30 Mei")
- No database migration (uses existing persisted data)
- UITheme constants for colors, fonts, dimensions
- Follow existing code patterns: component names (teamNameField, regDateRow), builder methods (sectionLabel, smallGray, styledField), error dialogs (JOptionPane)

---

## File Structure

**Modified:**
- `src/com/tandem/controllers/TeamController.java` — add helper method `getAcceptedTeamsForUser(User user)`
- `src/com/tandem/views/EditTeamPanel.java` — add competition fields section, expandable member load sections, lock logic + warning dialog

**No new files created.**

---

## Task 1: Add Helper Method to TeamController

**Files:**
- Modify: `src/com/tandem/controllers/TeamController.java:90-98`

**Interfaces:**
- Consumes: `DataStore.getInstance().getAllTeams()` — existing data access
- Produces: `getAcceptedTeamsForUser(User user) → ArrayList<Team>` — returns all teams where user is ACCEPTED member

**Purpose:** Support Feature 1 by fetching all accepted competitions for a given user.

- [ ] **Step 1: Add helper method to TeamController**

Add this method to TeamController after the existing `getTeamsByMember()` method:

```java
public ArrayList<Team> getAcceptedTeamsForUser(User user) {
    ArrayList<Team> result = new ArrayList<>();
    for (Team t : store.getAllTeams()) {
        if (t.isMember(user)) {
            result.add(t);
        }
    }
    return result;
}
```

- [ ] **Step 2: Verify compilation**

Run: `cd C:\Users\Dewa\Desktop\Tandem && find src -name "*.java" | xargs javac -encoding UTF-8 -d build/classes 2>&1 | head -20`

Expected: No errors related to TeamController

- [ ] **Step 3: Commit**

```bash
cd C:\Users\Dewa\Desktop\Tandem
git add src/com/tandem/controllers/TeamController.java
git commit -m "feat: add getAcceptedTeamsForUser helper method for member load feature"
```

---

## Task 2: Add Competition Section to EditTeamPanel

**Files:**
- Modify: `src/com/tandem/views/EditTeamPanel.java:12-25` (fields), `src/com/tandem/views/EditTeamPanel.java:40-210` (buildContent), `src/com/tandem/views/EditTeamPanel.java:325-349` (doSave)

**Interfaces:**
- Consumes: `Team.getCompetition()`, `Competition.getName()`, `getEventStartDate()`, `getEventEndDate()`
- Produces: Read-only display of competition name and event schedule; fields will be used as editable fields in later task (Task 3)

**Purpose:** Display (and prepare for editing/locking) competition name and event schedule.

- [ ] **Step 1: Add competition display fields to EditTeamPanel class**

Add these field declarations after line 23 (after `private final ArrayList<String> currentSlots;`):

```java
private JLabel compNameLabel;
private JLabel compDateLabel;
private JLabel compStatusLabel;
```

- [ ] **Step 2: Build competition section in buildContent()**

Find line 179 (`p.add(sectionLabel("Deadline Pendaftaran Tim"));`) and add this BEFORE that section (insert before line 179):

```java
// ── Competition Details ───────────────────────────────────────────────
Competition comp = team.getCompetition();
compStatusLabel = new JLabel();
compStatusLabel.setFont(UITheme.F_SMALL);
compStatusLabel.setForeground(UITheme.HINT);
compStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

compNameLabel = new JLabel(comp.getName());
compNameLabel.setFont(UITheme.F_BODY);
compNameLabel.setForeground(UITheme.TEXT);
compNameLabel.setOpaque(true);
compNameLabel.setBackground(UITheme.CARD);
compNameLabel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UITheme.BORDER),
        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
compNameLabel.setPreferredSize(new Dimension(0, 44));
compNameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
compNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

String startDate = comp.getEventStartDate();
String endDate = comp.getEventEndDate();
String dateStr = formatDateRange(startDate, endDate);
compDateLabel = new JLabel(dateStr);
compDateLabel.setFont(UITheme.F_BODY);
compDateLabel.setForeground(UITheme.TEXT);
compDateLabel.setOpaque(true);
compDateLabel.setBackground(UITheme.CARD);
compDateLabel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UITheme.BORDER),
        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
compDateLabel.setPreferredSize(new Dimension(0, 44));
compDateLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
compDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

p.add(sectionLabel("Kompetisi"));
p.add(Box.createVerticalStrut(4));
p.add(compStatusLabel);
p.add(Box.createVerticalStrut(8));
p.add(new JLabel("Nama:"));
p.add(new JLabel("Nama Kompetisi").getFont());  // dummy
compNameLabel.setFont(UITheme.F_SMALL);
compNameLabel.setForeground(UITheme.GRAY);
compNameLabel.setText("Nama Kompetisi");
p.add(Box.createVerticalStrut(4));
p.add(compNameLabel);
p.add(Box.createVerticalStrut(8));
p.add(new JLabel("Jadwal Lomba:"));
JLabel schedLabel = new JLabel("Jadwal Lomba");
schedLabel.setFont(UITheme.F_SMALL);
schedLabel.setForeground(UITheme.GRAY);
p.add(Box.createVerticalStrut(4));
p.add(compDateLabel);
p.add(Box.createVerticalStrut(24));
```

Wait, let me simplify this. Looking at the existing pattern, the UI should be simpler. Let me rewrite:

Actually, looking at the existing code more carefully, I see there's a pattern for displaying read-only info. But competition details are currently only shown in TeamDetailPanel. For EditTeamPanel, we should follow the same pattern of having labels and displays, but make them subject to lock.

Let me reconsider the approach. The spec says locked fields should be "read-only" but not disabled/grayed. This suggests they should be JTextFields set to read-only. Let me revise:

- [ ] **Step 2 (REVISED): Add competition fields and initialize them**

Add these field declarations after line 23:

```java
private JTextField compNameField;
private JComboBox<String> compEventStartDayBox, compEventStartMonthBox, compEventStartYearBox;
private JComboBox<String> compEventEndDayBox, compEventEndMonthBox, compEventEndYearBox;
private JPanel compStartDateRow, compEndDateRow;
```

Then in buildContent(), AFTER the initialization of `regDateRow` (around line 123), add this code to initialize competition date fields:

```java
// ── Competition Details ───────────────────────────────────────────────
compNameField = styledField();
compNameField.setText(team.getCompetition().getName());
compNameField.setEditable(false);

String[] days = new String[31];
for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1);

String[] months = new String[]{
    "Januari","Februari","Maret","April","Mei","Juni",
    "Juli","Agustus","September","Oktober","November","Desember"};

String[] years = new String[]{"2025","2026","2027","2028","2029","2030"};

// Event start date
compEventStartDayBox = new JComboBox<>(days);
compEventStartDayBox.setFont(UITheme.F_BODY);
compEventStartDayBox.setBackground(UITheme.CARD);
compEventStartDayBox.setEnabled(false);

compEventStartMonthBox = new JComboBox<>(months);
compEventStartMonthBox.setFont(UITheme.F_BODY);
compEventStartMonthBox.setBackground(UITheme.CARD);
compEventStartMonthBox.setEnabled(false);

compEventStartYearBox = new JComboBox<>(years);
compEventStartYearBox.setFont(UITheme.F_BODY);
compEventStartYearBox.setBackground(UITheme.CARD);
compEventStartYearBox.setEnabled(false);

String eventStart = team.getCompetition().getEventStartDate();
if (eventStart != null && !eventStart.isEmpty()) {
    try {
        String[] parts = eventStart.split("-");
        compEventStartYearBox.setSelectedItem(parts[0]);
        compEventStartMonthBox.setSelectedIndex(Integer.parseInt(parts[1]) - 1);
        compEventStartDayBox.setSelectedIndex(Integer.parseInt(parts[2]) - 1);
    } catch (Exception ignored) {}
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
compEventEndDayBox = new JComboBox<>(days);
compEventEndDayBox.setFont(UITheme.F_BODY);
compEventEndDayBox.setBackground(UITheme.CARD);
compEventEndDayBox.setEnabled(false);

compEventEndMonthBox = new JComboBox<>(months);
compEventEndMonthBox.setFont(UITheme.F_BODY);
compEventEndMonthBox.setBackground(UITheme.CARD);
compEventEndMonthBox.setEnabled(false);

compEventEndYearBox = new JComboBox<>(years);
compEventEndYearBox.setFont(UITheme.F_BODY);
compEventEndYearBox.setBackground(UITheme.CARD);
compEventEndYearBox.setEnabled(false);

String eventEnd = team.getCompetition().getEventEndDate();
if (eventEnd != null && !eventEnd.isEmpty()) {
    try {
        String[] parts = eventEnd.split("-");
        compEventEndYearBox.setSelectedItem(parts[0]);
        compEventEndMonthBox.setSelectedIndex(Integer.parseInt(parts[1]) - 1);
        compEventEndDayBox.setSelectedIndex(Integer.parseInt(parts[2]) - 1);
    } catch (Exception ignored) {}
}

compEndDateRow = new JPanel(new GridLayout(1, 3, 8, 0));
compEndDateRow.setOpaque(false);
compEndDateRow.setPreferredSize(new Dimension(0, 44));
compEndDateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
compEndDateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
compEndDateRow.add(compEventEndDayBox);
compEndDateRow.add(compEventEndMonthBox);
compEndDateRow.add(compEventEndYearBox);
```

- [ ] **Step 3: Add competition section to panel assembly**

In the "Assemble" section (around line 161-207), add this BEFORE the registration deadline section (before line 179):

```java
p.add(sectionLabel("Kompetisi"));
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
```

- [ ] **Step 4: Verify compilation**

Run: `cd C:\Users\Dewa\Desktop\Tandem && find src -name "*.java" | xargs javac -encoding UTF-8 -d build/classes 2>&1 | head -30`

Expected: No errors in EditTeamPanel

- [ ] **Step 5: Commit**

```bash
cd C:\Users\Dewa\Desktop\Tandem
git add src/com/tandem/views/EditTeamPanel.java
git commit -m "feat: add competition display section to edit team panel"
```

---

## Task 3: Add Lock Logic to Competition Fields

**Files:**
- Modify: `src/com/tandem/views/EditTeamPanel.java:25-37` (constructor), `src/com/tandem/views/EditTeamPanel.java:40-210` (buildContent after competition section init)

**Interfaces:**
- Consumes: `team.getMembers().size()` — count of members to detect lock
- Produces: Competition name and date fields become read-only when lock active; FocusListener added to trigger warning dialog

**Purpose:** Implement Feature 2 — lock competition details when members exist.

- [ ] **Step 1: Add lock detection and field preparation in constructor**

After line 28 (`this.currentSlots = new ArrayList<>(team.getOpenSlots());`), add:

```java
boolean isLocked = team.getMembers().size() > 1;
```

Then keep this variable in mind for the next steps.

Actually, better approach: make lock detection a method call so it's done after fields are initialized. Let me revise:

- [ ] **Step 1 (REVISED): Add method to check lock state**

Add this method at the end of EditTeamPanel class (before the inner FitPanel class):

```java
private boolean isCompetitionLocked() {
    return team.getMembers().size() > 1;
}
```

- [ ] **Step 2: Apply lock state after competition fields are initialized**

After the competition date field initialization (after `compEndDateRow` is built in buildContent), add:

```java
// Apply lock if team has members
if (isCompetitionLocked()) {
    compNameField.setEditable(false);
    compEventStartDayBox.setEnabled(false);
    compEventStartMonthBox.setEnabled(false);
    compEventStartYearBox.setEnabled(false);
    compEventEndDayBox.setEnabled(false);
    compEventEndMonthBox.setEnabled(false);
    compEventEndYearBox.setEnabled(false);
    
    // Add focus listeners to show warning
    FocusListener lockWarning = new FocusAdapter() {
        @Override public void focusGained(FocusEvent e) {
            String msg = "Field ini tidak bisa diubah karena sudah ada anggota tim yang bergabung. " +
                         "Jika ingin mengubah, silakan diskusikan dengan anggota tim terlebih dahulu.";
            JOptionPane.showMessageDialog(EditTeamPanel.this, msg, "Tidak Bisa Diubah", JOptionPane.INFORMATION_MESSAGE);
            e.getComponent().transferFocus();
        }
    };
    
    compNameField.addFocusListener(lockWarning);
    compEventStartDayBox.addFocusListener(lockWarning);
    compEventStartMonthBox.addFocusListener(lockWarning);
    compEventStartYearBox.addFocusListener(lockWarning);
    compEventEndDayBox.addFocusListener(lockWarning);
    compEventEndMonthBox.addFocusListener(lockWarning);
    compEventEndYearBox.addFocusListener(lockWarning);
}
```

- [ ] **Step 3: Add status label to competition section**

Modify the competition section assembly (from Task 2, Step 3) to add a status indicator. Update it to include this line right after `p.add(sectionLabel("Kompetisi"));`:

```java
if (isCompetitionLocked()) {
    JLabel lockLabel = new JLabel("🔒 Terkunci (anggota sudah bergabung)");
    lockLabel.setFont(UITheme.F_SMALL);
    lockLabel.setForeground(new Color(220, 50, 50));
    lockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    p.add(lockLabel);
    p.add(Box.createVerticalStrut(4));
}
```

- [ ] **Step 4: Verify lock works manually**

Compile and run the app:
```bash
cd C:\Users\Dewa\Desktop\Tandem
find src -name "*.java" | xargs javac -encoding UTF-8 -d build/classes
```

Expected: No compile errors

- [ ] **Step 5: Commit**

```bash
cd C:\Users\Dewa\Desktop\Tandem
git add src/com/tandem/views/EditTeamPanel.java
git commit -m "feat: add lock logic for competition fields after members join"
```

---

## Task 4: Add Expandable Member Competition Load Sections to EditTeamPanel

**Files:**
- Modify: `src/com/tandem/views/EditTeamPanel.java:12-25` (fields), `src/com/tandem/views/EditTeamPanel.java:40-210` (buildContent), add helper methods

**Interfaces:**
- Consumes: `team.getPendingRequests()`, `team.getMembers()`, `tc.getAcceptedTeamsForUser(user)` (from Task 1)
- Produces: Two new expandable sections in EditTeamPanel displaying pending applicants and accepted members with their accepted competitions

**Purpose:** Implement Feature 1 — show leader what competitions pending/accepted members are participating in.

- [ ] **Step 1: Add fields for expandable sections**

Add these field declarations after the competition field declarations (after `compEndDateRow`):

```java
private JPanel pendingApplicantsPanel, acceptedMembersPanel;
private boolean pendingExpanded = true, membersExpanded = true;
```

- [ ] **Step 2: Add helper method to format date range**

Add this method at the end of EditTeamPanel class (before the FitPanel inner class):

```java
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
```

- [ ] **Step 3: Add helper method to build expandable member load sections**

Add this method at the end of EditTeamPanel class:

```java
private JPanel buildExpandableCompetitionSection(User user) {
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
```

- [ ] **Step 4: Add member competition load sections to panel assembly**

In the buildContent() method, after the `hasNonLeader` block (after line 203, where members section ends), add:

```java
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

if (!nonLeaderMembers.isEmpty()) {
    p.add(Box.createVerticalStrut(24));
    p.add(sectionLabel("Jadwal Anggota Tim"));
    p.add(Box.createVerticalStrut(4));
    p.add(smallGray("Kompetisi lain yang sedang diikuti anggota tim."));
    p.add(Box.createVerticalStrut(12));
    acceptedMembersPanel = buildAcceptedMembersSection();
    p.add(acceptedMembersPanel);
}
```

- [ ] **Step 5: Verify compilation**

Run: `cd C:\Users\Dewa\Desktop\Tandem && find src -name "*.java" | xargs javac -encoding UTF-8 -d build/classes 2>&1 | head -30`

Expected: No errors in EditTeamPanel

- [ ] **Step 6: Commit**

```bash
cd C:\Users\Dewa\Desktop\Tandem
git add src/com/tandem/views/EditTeamPanel.java
git commit -m "feat: add member competition load sections to edit team panel"
```

---

## Task 5: Manual Testing - Feature 1 & Feature 2

**Files:**
- Test: Application UI (visual/manual testing)

**Purpose:** Verify both features work correctly before finalizing.

- [ ] **Step 1: Start the app**

```bash
cd C:\Users\Dewa\Desktop\Tandem
java -cp build/classes com.tandem.Main
```

Expected: App starts normally

- [ ] **Step 2: Test Feature 1 - No Pending/Members Scenario**

1. Create a new team with 1 person (just yourself)
2. Navigate to Edit Team
3. Verify: "Kompetisi" section shows with competition name and schedule
4. Scroll down and verify no "Permintaan Gabung" or "Jadwal Anggota Tim" sections appear (empty)

Expected: Only leader visible, no member sections shown

- [ ] **Step 3: Test Feature 1 - With Pending Applicants**

1. From another user account, send a join request to your team
2. Go back to Edit Team (as leader)
3. Scroll down and verify "Permintaan Gabung" section appears
4. Verify pending requester's name is shown
5. Verify their accepted competitions are listed (if they have any)
6. If they have no other competitions, verify "(Tidak mengikuti kompetisi lain)" message appears

Expected: Pending section shows with applicant(s) and their competitions

- [ ] **Step 4: Test Feature 1 - With Accepted Members**

1. Approve a pending join request (from Edit Team, approve button on pending request, or via Alerts)
2. Go back to Edit Team
3. Scroll down and verify "Jadwal Anggota Tim" section now appears
4. Verify accepted member's name is shown
5. Verify their accepted competitions are listed

Expected: Members section shows with accepted member(s) and their competitions

- [ ] **Step 5: Test Feature 2 - Lock Inactivity (No Members)**

1. Open Edit Team for a team with NO accepted members (only leader)
2. Verify "Kompetisi" section shows both name and date fields ENABLED
3. Verify no lock status label appears
4. Try to edit competition name — should work normally

Expected: No lock, fields are editable

- [ ] **Step 6: Test Feature 2 - Lock Activation (With Members)**

1. Ensure team has at least 1 accepted member (not leader)
2. Open Edit Team for that team
3. Verify "Kompetisi" section shows lock status: "🔒 Terkunci (anggota sudah bergabung)"
4. Verify competition name field is disabled (grayed)
5. Verify date comboboxes are disabled (grayed)
6. Try to click on competition name field → warning dialog should appear: "Field ini tidak bisa diubah..."
7. Try to click on date comboboxes → warning dialog should appear
8. Verify dialog dismisses and focus moves away from field

Expected: Lock works, warning dialog shows on interaction

- [ ] **Step 7: Test Feature 2 - Lock Persists Across Edits**

1. With locked team open, edit other fields (Nama Tim, Deskripsi, Deadline Pendaftaran, Slots)
2. Verify these edits work normally and save successfully
3. Go back to Edit Team and verify competition fields are still locked

Expected: Lock persists, other fields work normally

- [ ] **Step 8: Test Member Removal Unlocks Fields**

1. From Edit Team, kick the last non-leader member out of the team
2. Go back to Edit Team (or refresh)
3. Verify lock status label disappears
4. Verify competition fields are now ENABLED
5. Try to edit competition name — should work (or attempt to, depending on implementation)

Expected: Lock deactivates when no members remain

- [ ] **Step 9: Visual Inspection - No Overflow**

1. Open Edit Team for a team with many members and pending requests
2. Scroll through the entire panel
3. Verify all text, fields, and sections fit within 450px width
4. Verify no horizontal scrollbar appears
5. Verify vertical scrolling works smoothly

Expected: No overflow, clean layout

- [ ] **Step 10: Summary Report**

Document any issues found:
- [ ] No issues — all features work as designed
- [ ] Minor issues (list and fix in follow-up commit)
- [ ] Major issues (need investigation)

---

## Task 6: Final Code Review & Commit

**Files:**
- Verify: `src/com/tandem/controllers/TeamController.java`, `src/com/tandem/views/EditTeamPanel.java`

**Purpose:** Ensure code quality before finalizing.

- [ ] **Step 1: Review TeamController changes**

Verify:
- `getAcceptedTeamsForUser()` method exists and returns correct type
- No typos or logic errors
- Method is called correctly in EditTeamPanel

- [ ] **Step 2: Review EditTeamPanel competition section**

Verify:
- Fields are initialized correctly with team's competition data
- Lock detection logic is correct: `team.getMembers().size() > 1`
- Date formatting works for all valid dates
- Warning dialog message is clear

- [ ] **Step 3: Review EditTeamPanel member competition load sections**

Verify:
- Helper methods build correct UI structure
- Data fetching filters correctly (only accepted competitions for each user)
- Empty states handled (no pending/members, no other competitions)
- Date range formatting works

- [ ] **Step 4: Verify no unintended side effects**

Test scenarios:
- Create new team (Feature 2 lock should not trigger with no members) ✓
- Edit team with only leader (no member sections, no lock) ✓
- Edit team with pending requests (show pending section, no lock) ✓
- Edit team with accepted members (show members section, lock active) ✓

- [ ] **Step 5: Create final summary commit**

If any fixes were needed from testing, create a commit:

```bash
cd C:\Users\Dewa\Desktop\Tandem
git add src/com/tandem/controllers/TeamController.java src/com/tandem/views/EditTeamPanel.java
git commit -m "fix: refinements to member load and competition lock features"
```

If no fixes needed, the previous commits are sufficient.

- [ ] **Step 6: View commit history**

```bash
git log --oneline -10
```

Expected output should show:
- "feat: add member competition load sections to edit team panel"
- "feat: add lock logic for competition fields after members join"
- "feat: add competition display section to edit team panel"
- "feat: add getAcceptedTeamsForUser helper method for member load feature"

---

## Verification Checklist

**Feature 1 - Member Competition Load:**
- ✅ Pending applicants section shows when join requests exist
- ✅ Accepted members section shows when members exist (non-leader)
- ✅ Each person's name is displayed with their accepted competitions
- ✅ Competition name and date range displayed correctly
- ✅ "(Tidak mengikuti kompetisi lain)" shown when person has no other competitions
- ✅ Empty state handled when no pending requests or members

**Feature 2 - Competition Lock:**
- ✅ Competition name and schedule fields visible in Edit Team
- ✅ Fields disabled (setEditable/setEnabled(false)) when members.size() > 1
- ✅ Warning dialog shows with correct message when trying to interact
- ✅ Dialog dismisses and focus transfers away
- ✅ Lock status label visible ("🔒 Terkunci...") when active
- ✅ Other team fields (name, desc, deadline, slots) still editable
- ✅ Lock persists after edit/save cycle
- ✅ Lock deactivates when last member is removed

**Code Quality:**
- ✅ No compilation errors
- ✅ No horizontal overflow (450px constraint maintained)
- ✅ Follows existing code patterns (field naming, builder methods, error dialogs)
- ✅ Helper methods reusable and well-structured
- ✅ No model changes required (uses existing fields)
- ✅ FitPanel scrolling works smoothly

---

## Success Criteria

All tasks completed:
1. ✅ Helper method added to TeamController
2. ✅ Competition section added to EditTeamPanel
3. ✅ Lock logic implemented with warning dialog
4. ✅ Member competition load sections added
5. ✅ Manual testing passed
6. ✅ All commits clean and descriptive

Application behavior:
- ✅ Leaders see pending applicants' and members' competition commitments
- ✅ Competition details locked after first member joins
- ✅ Warning dialog prevents accidental changes
- ✅ Other team details remain editable
- ✅ No UI/UX regressions
