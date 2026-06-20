# Deadline Pendaftaran, Jadwal Lomba & Pesan Penolakan — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Tambahkan deadline pendaftaran tim, jadwal event kompetisi (rentang tanggal), dan pesan penolakan join request ke aplikasi Tandem.

**Architecture:** Perubahan dimulai dari model (Competition, Team, JoinRequest), lalu merambat ke controller (TeamController) dan caller konstruktor (DataStore, TandemApp, Main), kemudian setiap view diperbarui satu per satu. Tidak ada perubahan arsitektur — hanya penambahan field dan update UI yang menggunakannya.

**Tech Stack:** Java 17, Swing, NetBeans Ant project, Java Serialization (`tandem_data.ser`)

## Global Constraints

- Semua model yang implement `Serializable` HARUS ditambah `private static final long serialVersionUID = 1L;`
- Format tanggal selalu String `YYYY-MM-DD` — konsisten dengan kode yang ada
- File `tandem_data.ser` HARUS dihapus sebelum run pertama setelah model berubah
- Compile dengan: `javac -cp "build/classes" -d "build/classes" src/com/tandem/**/*.java src/com/tandem/*.java` dari root project
- Run dengan: `java -cp "build/classes" com.tandem.TandemApp` dari root project
- `registrationDeadline` di `Team` bersifat opsional — boleh kosong string `""`
- Jika leader cancel dialog penolakan, request TIDAK jadi ditolak

---

## File Map

| File | Aksi |
|---|---|
| `src/com/tandem/models/Competition.java` | Modify — rename `deadline`, tambah 2 field date |
| `src/com/tandem/models/Team.java` | Modify — tambah `registrationDeadline` |
| `src/com/tandem/models/JoinRequest.java` | Modify — tambah `rejectionMessage`, ubah `reject()` |
| `src/com/tandem/services/DataStore.java` | Modify — update `seedCompetitions()` untuk constructor baru |
| `src/com/tandem/TandemApp.java` | Modify — update constructor Competition di `seedDemoData()` |
| `src/com/tandem/Main.java` | Modify — update constructor Competition |
| `src/com/tandem/controllers/TeamController.java` | Modify — ubah signature `rejectRequest()` |
| `src/com/tandem/views/CreateTeamPanel.java` | Modify — tambah field input deadline & jadwal |
| `src/com/tandem/views/TeamDetailPanel.java` | Modify — tampilkan jadwal & deadline, dialog reject |
| `src/com/tandem/views/AlertsPanel.java` | Modify — dialog reject + tampilkan pesan penolakan |
| `src/com/tandem/views/BrowseTeamsPanel.java` | Modify — tampilkan deadline di card |
| `src/com/tandem/views/DashboardPanel.java` | Modify — tampilkan deadline di card |

---

### Task 1: Update Model Classes

**Files:**
- Modify: `src/com/tandem/models/Competition.java`
- Modify: `src/com/tandem/models/Team.java`
- Modify: `src/com/tandem/models/JoinRequest.java`

**Interfaces:**
- Produces:
  - `Competition(String id, String name, String category, String submissionDeadline, String eventStartDate, String eventEndDate, int maxTeamSize, ArrayList<String> tags)`
  - `Competition(String id, String name, String category, String submissionDeadline, String eventStartDate, String eventEndDate, int maxTeamSize)` — no-tags overload
  - `competition.getSubmissionDeadline()` → `String`
  - `competition.getEventStartDate()` → `String`
  - `competition.getEventEndDate()` → `String`
  - `team.getRegistrationDeadline()` → `String`
  - `team.setRegistrationDeadline(String)` → `void`
  - `joinRequest.reject(String reason)` → `void`
  - `joinRequest.getRejectionMessage()` → `String`

- [ ] **Step 1: Ganti isi `Competition.java` selengkapnya**

```java
package com.tandem.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    private String competitionId;
    private String name;
    private String category;
    private String submissionDeadline;
    private String eventStartDate;
    private String eventEndDate;
    private int maxTeamSize;
    private ArrayList<String> tags;

    public Competition(String competitionId, String name, String category,
                       String submissionDeadline, String eventStartDate, String eventEndDate,
                       int maxTeamSize, ArrayList<String> tags) {
        this.competitionId     = competitionId;
        this.name              = name;
        this.category          = category;
        this.submissionDeadline = submissionDeadline;
        this.eventStartDate    = eventStartDate;
        this.eventEndDate      = eventEndDate;
        this.maxTeamSize       = maxTeamSize;
        this.tags              = new ArrayList<>(tags);
    }

    public Competition(String competitionId, String name, String category,
                       String submissionDeadline, String eventStartDate, String eventEndDate,
                       int maxTeamSize) {
        this(competitionId, name, category, submissionDeadline, eventStartDate, eventEndDate,
             maxTeamSize, new ArrayList<>());
    }

    public boolean isRelevantFor(String facultyOrMajor) {
        if (tags.contains("Semua")) return true;
        for (String tag : tags) {
            if (tag.equalsIgnoreCase(facultyOrMajor)) return true;
        }
        return false;
    }

    public String getCompetitionId()     { return competitionId; }
    public String getName()              { return name; }
    public String getCategory()          { return category; }
    public String getSubmissionDeadline(){ return submissionDeadline; }
    public String getEventStartDate()    { return eventStartDate; }
    public String getEventEndDate()      { return eventEndDate; }
    public int getMaxTeamSize()          { return maxTeamSize; }
    public ArrayList<String> getTags()   { return new ArrayList<>(tags); }

    public void setName(String name)                       { this.name = name; }
    public void setCategory(String category)               { this.category = category; }
    public void setSubmissionDeadline(String deadline)     { this.submissionDeadline = deadline; }
    public void setEventStartDate(String date)             { this.eventStartDate = date; }
    public void setEventEndDate(String date)               { this.eventEndDate = date; }
    public void setMaxTeamSize(int size)                   { this.maxTeamSize = size; }
    public void addTag(String tag)                         { tags.add(tag); }
    public void setTags(ArrayList<String> tags)            { this.tags = new ArrayList<>(tags); }

    @Override
    public String toString() {
        return name + " [" + category + "] - " + eventStartDate + " s/d " + eventEndDate;
    }
}
```

- [ ] **Step 2: Ganti isi `Team.java` selengkapnya**

```java
package com.tandem.models;

import com.tandem.models.enums.TeamStatus;
import java.io.Serializable;
import java.util.ArrayList;

public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    private String teamId;
    private String teamName;
    private String description;
    private String registrationDeadline;
    private Competition competition;
    private User leader;
    private ArrayList<User> members;
    private ArrayList<String> openSlots;
    private ArrayList<JoinRequest> pendingRequests;
    private TeamStatus status;

    public Team(String teamId, String teamName, Competition competition,
                User leader, ArrayList<String> openSlots) {
        this.teamId               = teamId;
        this.teamName             = teamName;
        this.description          = "";
        this.registrationDeadline = "";
        this.competition          = competition;
        this.leader               = leader;
        this.members              = new ArrayList<>();
        this.members.add(leader);
        this.openSlots            = new ArrayList<>(openSlots);
        this.pendingRequests      = new ArrayList<>();
        this.status               = TeamStatus.OPEN;
    }

    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            if (!openSlots.isEmpty()) openSlots.remove(0);
        }
        if (openSlots.isEmpty()) {
            status = TeamStatus.FULL;
        }
    }

    public boolean isMember(User user)  { return members.contains(user); }
    public boolean isFull()             { return openSlots.isEmpty(); }

    public void addPendingRequest(JoinRequest request)    { pendingRequests.add(request); }
    public void removePendingRequest(JoinRequest request) { pendingRequests.remove(request); }

    public String getTeamId()                           { return teamId; }
    public String getTeamName()                         { return teamName; }
    public String getDescription()                      { return description; }
    public String getRegistrationDeadline()             { return registrationDeadline; }
    public Competition getCompetition()                 { return competition; }
    public User getLeader()                             { return leader; }
    public TeamStatus getStatus()                       { return status; }
    public ArrayList<User> getMembers()                 { return new ArrayList<>(members); }
    public ArrayList<String> getOpenSlots()             { return new ArrayList<>(openSlots); }
    public ArrayList<JoinRequest> getPendingRequests()  { return new ArrayList<>(pendingRequests); }

    public void setTeamName(String teamName)                   { this.teamName = teamName; }
    public void setDescription(String description)             { this.description = description; }
    public void setRegistrationDeadline(String deadline)       { this.registrationDeadline = deadline; }
    public void setStatus(TeamStatus status)                   { this.status = status; }

    @Override
    public String toString() {
        return teamName + " | " + competition.getName() + " | Open: " + openSlots;
    }
}
```

- [ ] **Step 3: Ganti isi `JoinRequest.java` selengkapnya**

```java
package com.tandem.models;

import com.tandem.models.enums.RequestStatus;
import java.io.Serializable;

public class JoinRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;
    private User requester;
    private Team targetTeam;
    private String message;
    private String rejectionMessage;
    private RequestStatus status;
    private String createdAt;

    public JoinRequest(String requestId, User requester, Team targetTeam,
                       String message, String createdAt) {
        this.requestId        = requestId;
        this.requester        = requester;
        this.targetTeam       = targetTeam;
        this.message          = message;
        this.rejectionMessage = "";
        this.status           = RequestStatus.PENDING;
        this.createdAt        = createdAt;
    }

    public void approve() { this.status = RequestStatus.APPROVED; }

    public void reject(String reason) {
        this.status           = RequestStatus.REJECTED;
        this.rejectionMessage = (reason != null && !reason.trim().isEmpty()) ? reason.trim() : "";
    }

    public String getRequestId()         { return requestId; }
    public User getRequester()           { return requester; }
    public Team getTargetTeam()          { return targetTeam; }
    public String getMessage()           { return message; }
    public String getRejectionMessage()  { return rejectionMessage; }
    public RequestStatus getStatus()     { return status; }
    public String getCreatedAt()         { return createdAt; }

    @Override
    public String toString() {
        return requester.getName() + " → " + targetTeam.getTeamName() + " [" + status + "]";
    }
}
```

- [ ] **Step 4: Tambah `serialVersionUID` ke `User.java`** (agar semua Serializable class konsisten)

Di `src/com/tandem/models/User.java`, tambahkan baris ini setelah `public class User implements Serializable {`:

```java
    private static final long serialVersionUID = 1L;
```

---

### Task 2: Update Semua Caller Konstruktor & Method

**Files:**
- Modify: `src/com/tandem/services/DataStore.java`
- Modify: `src/com/tandem/TandemApp.java`
- Modify: `src/com/tandem/Main.java`
- Modify: `src/com/tandem/controllers/TeamController.java`

**Interfaces:**
- Consumes: constructor `Competition(id, name, category, submissionDeadline, eventStartDate, eventEndDate, maxTeamSize, tags)` dari Task 1
- Consumes: `joinRequest.reject(String reason)` dari Task 1
- Produces: `TeamController.rejectRequest(JoinRequest request, String reason)`

- [ ] **Step 1: Ganti method `seedCompetitions()` di `DataStore.java`**

Ganti seluruh method `seedCompetitions()` (baris 109–142) dengan:

```java
    private void seedCompetitions() {
        ArrayList<String> tagsHackathon = new ArrayList<>();
        tagsHackathon.add("Informatika"); tagsHackathon.add("Teknik Komputer");
        tagsHackathon.add("Sistem Informasi"); tagsHackathon.add("Ilmu Komputer");
        competitions.add(new Competition(IDGenerator.generateId(),
                "Hackathon Nasional 2025", "Hackathon",
                "2025-08-10", "2025-08-15", "2025-08-17", 4, tagsHackathon));

        ArrayList<String> tagsDesign = new ArrayList<>();
        tagsDesign.add("Desain Komunikasi Visual"); tagsDesign.add("Informatika");
        tagsDesign.add("Seni Rupa"); tagsDesign.add("DKV");
        competitions.add(new Competition(IDGenerator.generateId(),
                "UIUX Competition 2025", "Design",
                "2025-07-25", "2025-07-30", "2025-07-31", 3, tagsDesign));

        ArrayList<String> tagsPkmK = new ArrayList<>();
        tagsPkmK.add("Manajemen"); tagsPkmK.add("Ekonomi");
        tagsPkmK.add("Akuntansi"); tagsPkmK.add("Informatika");
        competitions.add(new Competition(IDGenerator.generateId(),
                "PKM-K Kewirausahaan 2025", "PKM",
                "2025-08-25", "2025-09-01", "2025-09-03", 5, tagsPkmK));

        ArrayList<String> tagsBusiness = new ArrayList<>();
        tagsBusiness.add("Manajemen"); tagsBusiness.add("Ekonomi"); tagsBusiness.add("Akuntansi");
        competitions.add(new Competition(IDGenerator.generateId(),
                "Business Plan Competition 2025", "Business",
                "2025-10-10", "2025-10-15", "2025-10-16", 4, tagsBusiness));

        ArrayList<String> tagsData = new ArrayList<>();
        tagsData.add("Statistika"); tagsData.add("Informatika"); tagsData.add("Matematika");
        competitions.add(new Competition(IDGenerator.generateId(),
                "Data Science Challenge 2025", "Data Science",
                "2025-08-15", "2025-08-20", "2025-08-22", 3, tagsData));

        ArrayList<String> tagsPkmPm = new ArrayList<>();
        tagsPkmPm.add("Semua");
        competitions.add(new Competition(IDGenerator.generateId(),
                "PKM-PM Pengabdian Masyarakat", "PKM",
                "2025-09-10", "2025-09-15", "2025-09-20", 5, tagsPkmPm));
    }
```

- [ ] **Step 2: Update konstruktor Competition di `TandemApp.java`**

Di `TandemApp.java`, ganti dua baris pembuatan Competition (baris 52 dan 63):

Ganti:
```java
            Competition c1 = new Competition("C001", "Hackathon Nasional 2025", "Hackathon", "2025-08-15", 3, tags1);
```
Dengan:
```java
            Competition c1 = new Competition("C001", "Hackathon Nasional 2025", "Hackathon",
                    "2025-08-10", "2025-08-15", "2025-08-17", 3, tags1);
```

Ganti:
```java
            Competition c2 = new Competition("C002", "Business Plan Competition 2025", "Business", "2025-09-20", 4, tags2);
```
Dengan:
```java
            Competition c2 = new Competition("C002", "Business Plan Competition 2025", "Business",
                    "2025-09-15", "2025-09-20", "2025-09-21", 4, tags2);
```

- [ ] **Step 3: Update konstruktor Competition di `Main.java`**

Di `Main.java` baris 44, ganti:
```java
        Competition comp = new Competition(IDGenerator.generateId(),
                "Hackathon Nasional 2025", "Hackathon", "2025-08-15", 3, tags);
```
Dengan:
```java
        Competition comp = new Competition(IDGenerator.generateId(),
                "Hackathon Nasional 2025", "Hackathon",
                "2025-08-10", "2025-08-15", "2025-08-17", 3, tags);
```

- [ ] **Step 4: Update `TeamController.rejectRequest()`**

Di `TeamController.java`, ganti method `rejectRequest` (baris 83–86):

Ganti:
```java
    public void rejectRequest(JoinRequest request) {
        request.reject();
        request.getTargetTeam().removePendingRequest(request);
    }
```
Dengan:
```java
    public void rejectRequest(JoinRequest request, String reason) {
        request.reject(reason);
        request.getTargetTeam().removePendingRequest(request);
    }
```

---

### Task 3: Compile Verification + Reset Data

**Files:** tidak ada file baru — hanya compile dan delete

- [ ] **Step 1: Hapus file data lama**

```bash
del "tandem_data.ser"
```
(Atau hapus manual via file explorer jika file ada di root project `C:\Users\Dewa\Desktop\Tandem\`)

- [ ] **Step 2: Compile seluruh project**

```bash
cd "C:\Users\Dewa\Desktop\Tandem"
javac -cp "build/classes" -d "build/classes" src/com/tandem/models/enums/*.java src/com/tandem/models/*.java src/com/tandem/utils/*.java src/com/tandem/services/*.java src/com/tandem/controllers/*.java src/com/tandem/views/components/*.java src/com/tandem/views/*.java src/com/tandem/*.java
```

Expected: tidak ada error. Jika ada error, perbaiki sebelum lanjut ke task berikutnya.

- [ ] **Step 3: Commit**

```bash
git add src/com/tandem/models/Competition.java src/com/tandem/models/Team.java src/com/tandem/models/JoinRequest.java src/com/tandem/models/User.java src/com/tandem/services/DataStore.java src/com/tandem/TandemApp.java src/com/tandem/Main.java src/com/tandem/controllers/TeamController.java
git commit -m "feat: update models with deadline, event schedule, and rejection message fields"
```

---

### Task 4: Update CreateTeamPanel

**Files:**
- Modify: `src/com/tandem/views/CreateTeamPanel.java`

**Interfaces:**
- Consumes: `Competition(id, name, category, submissionDeadline, eventStartDate, eventEndDate, maxTeamSize, tags)` — Task 1
- Consumes: `team.setRegistrationDeadline(String)` — Task 1

- [ ] **Step 1: Tambah field baru di bagian deklarasi field (atas class)**

Di `CreateTeamPanel.java`, bagian deklarasi field (sekitar baris 23–30), tambahkan 3 field baru:

```java
    // Team fields — tambah di bawah baris "private JTextField teamNameField, descField;"
    private JTextField teamRegDeadlineField;

    // Competition fields — tambah di bawah "private JTextField compNameField, compDeadlineField;"
    private JTextField compEventStartField, compEventEndField;
```

Sehingga bagian deklarasi menjadi:
```java
    // Competition mode
    private boolean createNewComp = false;
    private JPanel compSelectPanel;
    private JPanel compCreatePanel;
    private JComboBox<String> existingCompBox;
    private JTextField compNameField, compDeadlineField;
    private JTextField compEventStartField, compEventEndField;
    private JComboBox<String> compCategoryBox;
    private JTextField compTagsField;

    // Team fields
    private JTextField teamNameField, descField;
    private JTextField teamRegDeadlineField;
    private JPanel slotsContainer;
    private final ArrayList<JTextField> slotFields = new ArrayList<>();
```

- [ ] **Step 2: Tambah field "Deadline Pendaftaran Tim" di `buildContent()`**

Di `buildContent()`, cari blok ini (sekitar baris 147–149):
```java
        p.add(sectionLabel("Nama Tim"));      p.add(Box.createVerticalStrut(8)); p.add(teamNameField);
        p.add(Box.createVerticalStrut(16));
        p.add(sectionLabel("Deskripsi Tim")); p.add(Box.createVerticalStrut(8)); p.add(descField);
```

Tambahkan inisialisasi dan label setelah `descField`:
```java
        teamRegDeadlineField = styledField();
        teamRegDeadlineField.setText("YYYY-MM-DD (opsional)");
```

Dan tambahkan ke panel `p` setelah `descField`:
```java
        p.add(sectionLabel("Nama Tim"));      p.add(Box.createVerticalStrut(8)); p.add(teamNameField);
        p.add(Box.createVerticalStrut(16));
        p.add(sectionLabel("Deskripsi Tim")); p.add(Box.createVerticalStrut(8)); p.add(descField);
        p.add(Box.createVerticalStrut(16));
        p.add(sectionLabel("Deadline Pendaftaran Tim"));
        p.add(Box.createVerticalStrut(4));
        p.add(smallGray("Batas tanggal orang bisa bergabung ke tim ini. Kosongkan jika tidak ada."));
        p.add(Box.createVerticalStrut(8));
        p.add(teamRegDeadlineField);
```

Catatan: inisialisasi `teamNameField = styledField();` dan `descField = styledField();` sudah ada di `buildContent()`. Lakukan hal yang sama untuk `teamRegDeadlineField` di baris yang sama (sebelum dipakai di `p.add`).

- [ ] **Step 3: Update `buildNewCompPanel()` — rename + tambah 2 field tanggal event**

Ganti seluruh method `buildNewCompPanel()` dengan:

```java
    private JPanel buildNewCompPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        compNameField     = styledField();
        compDeadlineField = styledField();
        compDeadlineField.setText("YYYY-MM-DD");

        compEventStartField = styledField();
        compEventStartField.setText("YYYY-MM-DD");

        compEventEndField = styledField();
        compEventEndField.setText("YYYY-MM-DD");

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
        panel.add(smallGray("Tanggal Mulai Event")); panel.add(Box.createVerticalStrut(6)); panel.add(compEventStartField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(smallGray("Tanggal Selesai Event")); panel.add(Box.createVerticalStrut(6)); panel.add(compEventEndField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(smallGray("Deadline Submission")); panel.add(Box.createVerticalStrut(6)); panel.add(compDeadlineField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(smallGray("Tags Jurusan (pisah dengan koma)")); panel.add(Box.createVerticalStrut(6)); panel.add(compTagsField);

        return panel;
    }
```

- [ ] **Step 4: Update `doCreate()` — baca field baru**

Di method `doCreate()`, ganti blok pembuatan `Competition` baru (bagian `else` dari `if (!createNewComp)`):

Ganti:
```java
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
```

Dengan:
```java
        } else {
            String cName       = compNameField.getText().trim();
            String cDead       = compDeadlineField.getText().trim();
            String cEventStart = compEventStartField.getText().trim();
            String cEventEnd   = compEventEndField.getText().trim();
            String cCat        = (String) compCategoryBox.getSelectedItem();

            if (cName.isEmpty()) {
                warn("Nama kompetisi wajib diisi!"); return;
            }
            if (cEventStart.isEmpty() || cEventStart.equals("YYYY-MM-DD")) {
                warn("Tanggal mulai event wajib diisi!"); return;
            }
            if (cEventEnd.isEmpty() || cEventEnd.equals("YYYY-MM-DD")) {
                warn("Tanggal selesai event wajib diisi!"); return;
            }

            ArrayList<String> tags = new ArrayList<>();
            for (String tag : compTagsField.getText().split(",")) {
                String t = tag.trim();
                if (!t.isEmpty() && !t.startsWith("e.g")) tags.add(t);
            }
            if (tags.isEmpty()) tags.add("Semua");

            String submissionDeadline = (cDead.isEmpty() || cDead.equals("YYYY-MM-DD")) ? cEventStart : cDead;

            comp = new Competition(
                    java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    cName, cCat, submissionDeadline, cEventStart, cEventEnd, slots.size() + 1, tags);
        }
```

Kemudian, setelah `tc.createTeam(...)`, set registrationDeadline. Ganti baris:
```java
        tc.createTeam(leader, tName, desc, comp, slots);
```
Dengan:
```java
        Team newTeam = tc.createTeam(leader, tName, desc, comp, slots);
        String regDeadline = teamRegDeadlineField.getText().trim();
        if (!regDeadline.isEmpty() && !regDeadline.equals("YYYY-MM-DD (opsional)")) {
            newTeam.setRegistrationDeadline(regDeadline);
        }
```

- [ ] **Step 5: Compile dan verify**

```bash
cd "C:\Users\Dewa\Desktop\Tandem"
javac -cp "build/classes" -d "build/classes" src/com/tandem/views/CreateTeamPanel.java
```

Expected: tidak ada error.

- [ ] **Step 6: Commit**

```bash
git add src/com/tandem/views/CreateTeamPanel.java
git commit -m "feat: add registration deadline and event schedule fields to CreateTeamPanel"
```

---

### Task 5: Update TeamDetailPanel

**Files:**
- Modify: `src/com/tandem/views/TeamDetailPanel.java`

**Interfaces:**
- Consumes: `competition.getSubmissionDeadline()`, `competition.getEventStartDate()`, `competition.getEventEndDate()` — Task 1
- Consumes: `team.getRegistrationDeadline()` — Task 1
- Consumes: `tc.rejectRequest(JoinRequest, String)` — Task 2

- [ ] **Step 1: Update `buildInfoCard()` — tampilkan jadwal event dan deadline**

Di `buildInfoCard()`, ganti seluruh `infoRow` (GridLayout 1x2) yang sekarang menampilkan COMPETITION + TIMELINE:

Ganti:
```java
        // Competition + Timeline row
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow.setOpaque(false);
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        infoRow.add(infoBlock("COMPETITION", team.getCompetition().getName()));
        infoRow.add(infoBlock("TIMELINE", "Ends " + team.getCompetition().getDeadline()));
```

Dengan:
```java
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
```

Kemudian tambahkan `infoRow2` ke card setelah `infoRow`. Ganti bagian yang menambah `infoRow` ke card:
```java
        card.add(infoRow);
        if (!team.getCompetition().getTags().isEmpty()) {
            card.add(Box.createVerticalStrut(10));
            card.add(tagsRow);
        }
```
Dengan:
```java
        card.add(infoRow);
        card.add(Box.createVerticalStrut(10));
        card.add(infoRow2);
        if (!team.getCompetition().getTags().isEmpty()) {
            card.add(Box.createVerticalStrut(10));
            card.add(tagsRow);
        }
```

- [ ] **Step 2: Update tombol "Decline" di `buildRequestCard()` — tambah dialog**

Di `TeamDetailPanel.buildRequestCard()`, ganti listener tombol `declineBtn`:

Ganti:
```java
        declineBtn.addActionListener(e -> {
            tc.rejectRequest(jr);
            rebuildPanel();
        });
```

Dengan:
```java
        declineBtn.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(this,
                    "Masukkan alasan penolakan (opsional, bisa dikosongkan):",
                    "Tolak Request", JOptionPane.PLAIN_MESSAGE);
            if (reason == null) return; // user klik Cancel — tidak jadi reject
            tc.rejectRequest(jr, reason);
            rebuildPanel();
        });
```

- [ ] **Step 3: Compile dan verify**

```bash
javac -cp "build/classes" -d "build/classes" src/com/tandem/views/TeamDetailPanel.java
```

Expected: tidak ada error.

- [ ] **Step 4: Commit**

```bash
git add src/com/tandem/views/TeamDetailPanel.java
git commit -m "feat: show event schedule and deadline in TeamDetailPanel, add rejection reason dialog"
```

---

### Task 6: Update AlertsPanel

**Files:**
- Modify: `src/com/tandem/views/AlertsPanel.java`

**Interfaces:**
- Consumes: `tc.rejectRequest(JoinRequest, String)` — Task 2
- Consumes: `joinRequest.getRejectionMessage()` — Task 1

- [ ] **Step 1: Update tombol "Tolak" di `AlertsPanel.buildRequestCard()` — tambah dialog**

Di `AlertsPanel.java`, method `buildRequestCard()`, ganti listener `declineBtn`:

Ganti:
```java
        declineBtn.addActionListener(e -> {
            tc.rejectRequest(jr);
            refreshPanel();
        });
```

Dengan:
```java
        declineBtn.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(this,
                    "Masukkan alasan penolakan (opsional, bisa dikosongkan):",
                    "Tolak Request", JOptionPane.PLAIN_MESSAGE);
            if (reason == null) return; // user klik Cancel — tidak jadi reject
            tc.rejectRequest(jr, reason);
            refreshPanel();
        });
```

- [ ] **Step 2: Update `buildMyApplicationRow()` — tampilkan pesan penolakan**

Ganti seluruh method `buildMyApplicationRow()` dengan:

```java
    private JPanel buildMyApplicationRow(JoinRequest jr) {
        boolean isRejectedWithMsg = jr.getStatus() == com.tandem.models.enums.RequestStatus.REJECTED
                && !jr.getRejectionMessage().isEmpty();

        RoundedPanel row = new RoundedPanel(UITheme.CARD, UITheme.BORDER);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, isRejectedWithMsg ? 100 : 72));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel teamName = new JLabel(jr.getTargetTeam().getTeamName());
        teamName.setFont(new Font("SansSerif", Font.BOLD, 13));
        teamName.setForeground(UITheme.TEXT);
        teamName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel compName = new JLabel(jr.getTargetTeam().getCompetition().getName()
                + "  ·  " + jr.getCreatedAt());
        compName.setFont(UITheme.F_SMALL);
        compName.setForeground(UITheme.GRAY);
        compName.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(teamName);
        info.add(Box.createVerticalStrut(2));
        info.add(compName);

        if (isRejectedWithMsg) {
            JLabel reasonLabel = new JLabel("<html><i>Alasan: " + jr.getRejectionMessage() + "</i></html>");
            reasonLabel.setFont(UITheme.F_SMALL);
            reasonLabel.setForeground(new Color(200, 50, 50));
            reasonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            info.add(Box.createVerticalStrut(4));
            info.add(reasonLabel);
        }

        String statusStr = jr.getStatus().toString();
        JLabel statusLabel = new JLabel(statusStr);
        statusLabel.setFont(UITheme.F_LABEL);
        statusLabel.setForeground(
                statusStr.equals("APPROVED") ? new Color(34, 139, 34)
              : statusStr.equals("REJECTED")  ? new Color(200, 50, 50)
              : UITheme.GRAY);

        row.add(info,        BorderLayout.CENTER);
        row.add(statusLabel, BorderLayout.EAST);
        return row;
    }
```

- [ ] **Step 3: Compile dan verify**

```bash
javac -cp "build/classes" -d "build/classes" src/com/tandem/views/AlertsPanel.java
```

Expected: tidak ada error.

- [ ] **Step 4: Commit**

```bash
git add src/com/tandem/views/AlertsPanel.java
git commit -m "feat: add rejection reason dialog and show rejection message in AlertsPanel"
```

---

### Task 7: Update BrowseTeamsPanel & DashboardPanel

**Files:**
- Modify: `src/com/tandem/views/BrowseTeamsPanel.java`
- Modify: `src/com/tandem/views/DashboardPanel.java`

**Interfaces:**
- Consumes: `team.getRegistrationDeadline()` — Task 1

- [ ] **Step 1: Update `BrowseTeamsPanel.makeTeamCard()` — tambah baris deadline**

Di `BrowseTeamsPanel.makeTeamCard()`, setelah baris `card.add(slotsRow);` di akhir method, tambahkan:

```java
        String regDeadline = team.getRegistrationDeadline();
        if (!regDeadline.isEmpty()) {
            card.add(Box.createVerticalStrut(8));
            JLabel deadlineLabel = new JLabel("Tutup pendaftaran: " + regDeadline);
            deadlineLabel.setFont(UITheme.F_SMALL);
            deadlineLabel.setForeground(UITheme.HINT);
            deadlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(deadlineLabel);
        }
```

Juga update `maxSize` card dari `160` menjadi `190` untuk memberi ruang baris deadline:
```java
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
```

- [ ] **Step 2: Update `DashboardPanel.makeTeamCard()` — tambah baris deadline**

Di `DashboardPanel.makeTeamCard()`, setelah baris `card.add(members);` (baris terakhir sebelum mouse listener), tambahkan:

```java
        String regDeadline = team.getRegistrationDeadline();
        if (!regDeadline.isEmpty()) {
            JLabel deadlineLbl = new JLabel("Tutup: " + regDeadline);
            deadlineLbl.setFont(UITheme.F_SMALL);
            deadlineLbl.setForeground(UITheme.HINT);
            deadlineLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(Box.createVerticalStrut(4));
            card.add(deadlineLbl);
        }
```

Update juga `maxSize` card dari `150` dan `140` menjadi `170` dan `160` (keduanya):
```java
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, showCategory ? 170 : 160));
```

- [ ] **Step 3: Compile dan verify**

```bash
javac -cp "build/classes" -d "build/classes" src/com/tandem/views/BrowseTeamsPanel.java src/com/tandem/views/DashboardPanel.java
```

Expected: tidak ada error.

- [ ] **Step 4: Commit**

```bash
git add src/com/tandem/views/BrowseTeamsPanel.java src/com/tandem/views/DashboardPanel.java
git commit -m "feat: show registration deadline on team cards in Browse and Dashboard"
```

---

### Task 8: Final Compile + End-to-End Test

**Files:** tidak ada perubahan kode

- [ ] **Step 1: Full compile**

```bash
cd "C:\Users\Dewa\Desktop\Tandem"
javac -cp "build/classes" -d "build/classes" src/com/tandem/models/enums/*.java src/com/tandem/models/*.java src/com/tandem/utils/*.java src/com/tandem/services/*.java src/com/tandem/controllers/*.java src/com/tandem/views/components/*.java src/com/tandem/views/*.java src/com/tandem/*.java
```

Expected: zero errors, zero warnings (selain deprecation yang tidak relevan).

- [ ] **Step 2: Pastikan `tandem_data.ser` sudah terhapus**, lalu jalankan aplikasi

```bash
java -cp "build/classes" com.tandem.TandemApp
```

- [ ] **Step 3: Test skenario — fitur jadwal & deadline**

  - Login sebagai `budi@uni.edu` / `password123`
  - Buka **"Buat Tim Baru"**, pilih mode "Buat baru" untuk kompetisi
  - Isi Tanggal Mulai Event = `2025-10-01`, Tanggal Selesai = `2025-10-03`, Deadline Submission = `2025-09-30`
  - Isi Deadline Pendaftaran Tim = `2025-09-15`
  - Buat tim, lalu buka detail tim → pastikan **JADWAL LOMBA** dan **DEADLINE PENDAFTARAN TIM** muncul di info card
  - Buka tab **Find Teams** → pastikan card tim menampilkan "Tutup pendaftaran: 2025-09-15"
  - Buka **Dashboard** → pastikan card tim di "Tim Saya" menampilkan deadline

- [ ] **Step 4: Test skenario — fitur pesan penolakan**

  - Login sebagai `sari@uni.edu` / `password123`
  - Buka tim milik Budi, klik **"Request to Join"**, isi pesan
  - Logout, login sebagai `budi@uni.edu`
  - Buka tab **Alerts** → section "Permintaan Bergabung"
  - Klik **"Tolak"** → dialog muncul → isi alasan, klik OK
  - Logout, login sebagai `sari@uni.edu`
  - Buka tab **Alerts** → section "Lamaranku" → pastikan status **REJECTED** dan pesan alasan tampil di bawahnya dengan warna merah
  - Ulangi test: klik "Tolak" tapi klik **Cancel** di dialog → request TIDAK berubah jadi ditolak

- [ ] **Step 5: Final commit**

```bash
git add -A
git commit -m "feat: complete deadline, event schedule, and rejection message implementation"
```
