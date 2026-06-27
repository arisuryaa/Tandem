# Feature Design: Member Competition Load & Competition Lock

**Date:** 2026-06-27  
**Project:** Tandem (Java Swing Team-Matching Desktop App)  
**Scope:** 2 new features for team leader

---

## Overview

Two complementary features to improve team leader decision-making and protect team commitment:

1. **View Member Competition Load** — Leaders can see what other competitions each pending/accepted member is participating in
2. **Lock Competition Details After Members Join** — Competition name and schedule become immutable once a member joins, protecting data integrity

---

## Feature #1: View Member Competition Load

### Purpose
When recruiting members or evaluating join requests, team leaders need visibility into applicants' schedule commitments. This helps answer: "*Is this person available to focus on our team, or are they overcommitted to other competitions?*"

### Location
**Edit Team Panel** — new expandable sections added below existing anggota tim section

### Data Displayed

For each pending applicant and accepted member, show:
- **Name** (User name)
- **All ACCEPTED Competitions** (across entire system, not just current competition)
  - Format: `📅 Kompetisi Name (Start Date - End Date)`
  - Only ACCEPTED/confirmed teams shown, not pending or rejected

### UI Structure - Expandable Sections

#### Section 1: Pending Applicants (if any exist)
```
▼ Pending Applicants (N)
  ├─ John Doe
  │  📅 Kompetisi A (15 Mei - 30 Mei)
  │  📅 Kompetisi B (1 Juni - 10 Juni)
  └─ Jane Smith
     📅 Kompetisi C (20 Mei - 25 Mei)
```

- Section header shows count of pending applicants
- Collapse/expand toggle (default: expanded if data exists, collapsed if empty)
- If no pending applicants: section hidden or collapsed by default
- List items show user name + accepted competitions with date ranges

#### Section 2: Accepted Members (if any exist beyond leader)
```
▼ Accepted Members (N)
  ├─ Alice
  │  📅 Kompetisi D (10 Juni - 20 Juni)
  ├─ Bob
  │  📅 Kompetisi A (15 Mei - 30 Mei)
  │  📅 Kompetisi E (5 Juli - 15 Juli)
  └─ Charlie
     (No other competitions)
```

- Section header shows count of accepted members (excluding leader)
- If member has no other accepted competitions: show text "(No other competitions)"
- If no accepted members exist (only leader): section hidden or collapsed by default

### Technical Details

**Data Source:**
- Query: For each pending request / accepted member, fetch all teams where `user.isMember(team) == true` AND team status is not the current team
- Competition schedule: `team.getCompetition().getEventStartDate()` and `getEventEndDate()`
- Filter: Only include competitions with ACCEPTED join status

**Scrollability:**
- New sections integrate into existing FitPanel scrollable structure
- Expandable sections don't break scroll behavior
- No horizontal overflow (follows existing 450px width constraint)

**Persistence:**
- Read-only display — no data modification needed
- Data sourced from existing models at view time

---

## Feature #2: Lock Competition Details After Members Join

### Purpose
Once a member accepts and joins a team, changing the competition name or schedule becomes risky — existing members may not agree to the change. This feature prevents accidental/intentional modifications that could break member contracts.

### Lock Trigger
**Condition:** Team has 1 or more accepted members (beyond the leader)

```java
if (team.getMembers().size() > 1) {
  // Lock is active
}
```

### Locked Fields
- ❌ **Nama Kompetisi** (Competition Name)
- ❌ **Jadwal Lomba** (Event Start Date + Event End Date)

### Editable Fields
- ✅ Nama Tim
- ✅ Deskripsi Tim
- ✅ Deadline Pendaftaran (Registration Deadline)
- ✅ Open Slots (add/remove)
- ✅ Keluarkan Member (kick member)

### UX Behavior

#### Visual Presentation
- Locked fields remain visible in their normal positions
- Fields appear **readonly** (not disabled/grayed) — can be focused but not edited
- No visual indication (no lock icon) — behavior revealed on interaction

#### Warning Dialog
When leader attempts to modify a locked field:

1. User clicks or starts typing in locked field
2. Modal dialog appears:
   ```
   ⚠️ Tidak Bisa Diubah
   
   "Field ini tidak bisa diubah karena sudah ada 
   anggota tim yang bergabung. Jika ingin mengubah, 
   silakan diskusikan dengan anggota tim terlebih dahulu."
   
   [OK]
   ```
3. Dialog dismisses when user clicks OK
4. Field remains unfocused and unchanged

#### Implementation Protection
- Set field `.setEditable(false)` to prevent input
- Add `FocusListener` to locked field:
  - On focus gained → show warning dialog
  - Return focus to previous component
- Prevent programmatic updates to locked fields in save logic

### Technical Details

**Lock Detection:**
```java
boolean isLocked = team.getMembers().size() > 1;
```

**Field Protection (EditTeamPanel):**
- Apply `.setEditable(false)` to competition name and date fields when lock is active
- Add FocusListener → trigger warning dialog on interaction
- Save button validation: skip/block attempts to modify locked fields

**State Persistence:**
- Lock state is **computed at runtime** based on member count, not stored
- On app restart: lock state re-evaluated automatically based on current members

---

## Data Model Changes

**No model changes required.** Both features use existing fields:
- `Team.members` — to check if members exist
- `Team.getCompetition()` → `getEventStartDate()`, `getEventEndDate()`, `getName()`
- `JoinRequest.getStatus()` → filter by APPROVED status
- `TeamController.getTeamsByMember(User)` — retrieve all teams for a member

---

## UI/UX Changes

### EditTeamPanel
- Add 2 new expandable sections: "Pending Applicants" + "Accepted Members"
- Integrate into existing scroll layout (FitPanel)
- Add FocusListener logic to competition name + date fields
- Show warning dialog when locked fields are accessed
- Maintain all existing fields and functionality

### No changes to:
- TeamDetailPanel
- CreateTeamPanel
- BrowseTeamsPanel
- DashboardPanel
- Other panels

---

## Testing Scenarios

### Feature #1: View Member Competition Load
1. **No applicants/members:** Sections hidden or collapsed
2. **With pending applicants:** Show names + their accepted competitions with dates
3. **With accepted members:** Show names + their accepted competitions with dates
4. **Mixed state:** Both sections visible, accurate data for each
5. **Scroll behavior:** New sections scroll properly within FitPanel

### Feature #2: Lock Competition Details
1. **No members (only leader):** All fields editable, no lock
2. **After 1st member joins:** Lock activates, warning dialog shows on field focus
3. **Attempt edit locked field:** Dialog appears, field unchanged
4. **Edit other fields:** Name/description/deadline/slots work normally
5. **Remove last non-leader member:** Lock should deactivate on next view
6. **App restart:** Lock state re-evaluated, correct state applied

---

## Edge Cases & Assumptions

1. **Member with no other competitions:** Shown as "(No other competitions)"
2. **Pending applicant who gets rejected:** Still shows in pending section until UI refresh
3. **Member leaves team:** Lock remains until all non-leader members removed (realistic UX)
4. **Lock deactivation:** Only computed at view load time; if member is kicked, lock lifts on next visit to Edit Team
5. **Concurrent members:** If multiple members join simultaneously, lock applies after any 1st join
6. **Rejected/PENDING competitions:** Only ACCEPTED competitions shown (members committed to those teams)

---

## Deployment & Rollback

- **No database migration needed** — uses existing data
- **No dependency changes needed** — Java 17, Swing, existing libraries
- **Backward compatible** — old team data (without members) works fine
- **Simple rollback:** Remove new sections from EditTeamPanel, remove FocusListener logic

---

## Success Criteria

- ✅ Leaders can see all accepted competitions for each applicant/member
- ✅ Competition name and schedule are immutable after 1st member joins
- ✅ Warning dialog clearly explains why field is locked
- ✅ Other team fields remain editable (name, desc, deadline, slots)
- ✅ No UI breakage or scroll issues
- ✅ Lock state correctly computed based on member count
