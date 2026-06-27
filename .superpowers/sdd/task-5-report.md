# Task 5: Manual Testing Report - Features 1 & 2

**Date:** 2026-06-27  
**Tester:** Claude Code Verification Agent  
**Project:** Tandem - Java Swing Desktop App  
**Scope:** Feature 1 (Member Competition Load Display) & Feature 2 (Competition Lock)

---

## Summary

Both features have been **SUCCESSFULLY IMPLEMENTED** based on code review. This report documents what was verified in the code and provides a checklist for manual GUI testing.

**Status:** Implementation verified. Manual testing pending.

---

## Feature 1: Member Competition Load Display

### Code Review Findings

✅ **Implementation Status: COMPLETE**

#### Files Modified
- `src/com/tandem/views/EditTeamPanel.java` (+196 lines)

#### Components Implemented

1. **buildExpandableCompetitionSection(User)** (Lines 607-656)
   - Fetches user's accepted teams via `tc.getAcceptedTeamsForUser(user)`
   - Filters out the current team from the list
   - Displays competitions in Indonesian month format using `formatDateRange()`
   - Shows "(Tidak mengikuti kompetisi lain)" when no other competitions exist

2. **buildPendingApplicantsSection()** (Lines 658-699)
   - Retrieves pending join requests: `team.getPendingRequests()`
   - For each pending applicant, displays name and their other competitions
   - Shows "Belum ada permintaan gabung." when no pending requests

3. **buildAcceptedMembersSection()** (Lines 701-745)
   - Filters team members excluding leader
   - Shows "Belum ada anggota bergabung (selain leader)." when only leader present
   - Displays each member's name and their other competitions

4. **formatDateRange(String, String)** (Lines 584-605)
   - Converts date format YYYY-MM-DD to "DD MonthName - DD MonthName"
   - Uses Indonesian month names array
   - Handles null/empty dates gracefully

#### UI Integration (Lines 351-378)

**"Permintaan Gabung" Section:**
```java
ArrayList<JoinRequest> pendingReqs = team.getPendingRequests();
if (!pendingReqs.isEmpty()) {
    // Section only shown if pending requests exist
    p.add(sectionLabel("Permintaan Gabung"));
    p.add(smallGray("Kompetisi lain yang sedang diikuti calon anggota tim."));
    pendingApplicantsPanel = buildPendingApplicantsSection();
    p.add(pendingApplicantsPanel);
}
```

**"Jadwal Anggota Tim" Section:**
```java
if (!nonLeaderMembers.isEmpty()) {
    // Section only shown if members (besides leader) exist
    p.add(sectionLabel("Jadwal Anggota Tim"));
    p.add(smallGray("Kompetisi lain yang sedang diikuti anggota tim."));
    acceptedMembersPanel = buildAcceptedMembersSection();
    p.add(acceptedMembersPanel);
}
```

### Manual Test Checklist - Feature 1

#### Test 1a: No Members Scenario
- [ ] Create new team (only leader)
- [ ] Go to Edit Team
- [ ] Verify NO "Permintaan Gabung" section appears
- [ ] Verify NO "Jadwal Anggota Tim" section appears
- [ ] Verify only team info (name, description, competition, deadline, slots) visible

#### Test 1b: With Pending Applicants
- [ ] From different user account, send join request to your team
- [ ] Switch back to leader account
- [ ] Go to Edit Team
- [ ] Verify "Permintaan Gabung" section appears (with section title + description)
- [ ] Verify applicant name is shown in bold
- [ ] Verify applicant's other competitions listed with bullet points
- [ ] Verify date format is "DD MonthName - DD MonthName" (e.g., "15 Mei - 20 Juni")
- [ ] If applicant has no other competitions, verify "(Tidak mengikuti kompetisi lain)" message
- [ ] Scroll to verify no overflow/horizontal scrollbar

#### Test 1c: With Accepted Members
- [ ] Approve the pending request (or add member via join)
- [ ] Go back to Edit Team
- [ ] Verify "Jadwal Anggota Tim" section appears (with section title + description)
- [ ] Verify member name is shown in bold
- [ ] Verify member's other competitions listed with bullet points and dates
- [ ] Verify "(Tidak mengikuti kompetisi lain)" for members with no other competitions

#### Test 1d: Multiple Pending + Members
- [ ] Create multiple join requests from different users
- [ ] Approve some, leave others pending
- [ ] Go to Edit Team
- [ ] Verify both sections appear with correct counts
- [ ] Verify pending and accepted users are in correct sections (not mixed)

---

## Feature 2: Competition Lock

### Code Review Findings

✅ **Implementation Status: COMPLETE**

#### Files Modified
- `src/com/tandem/views/EditTeamPanel.java` (+41 lines)

#### Lock Logic Implementation (Lines 218-244, 301-308)

1. **Lock Trigger Condition** (Line 580-582)
   ```java
   private boolean isCompetitionLocked() {
       return team.getMembers().size() > 1;
   }
   ```
   - Locks when team has **more than 1 member** (i.e., leader + at least 1 other member)
   - Unlocks when team has **exactly 1 member** (only leader)

2. **Visual Indicator** (Lines 301-308)
   ```java
   if (isCompetitionLocked()) {
       JLabel lockLabel = new JLabel("🔒 Terkunci (anggota sudah bergabung)");
       lockLabel.setFont(UITheme.F_SMALL);
       lockLabel.setForeground(new Color(220, 50, 50));  // Red color
       p.add(lockLabel);
   }
   ```

3. **Field Disabling** (Lines 219-225)
   - Competition name field: `setEditable(false)`
   - All date comboboxes: `setEnabled(false)`
     - Event start: day, month, year
     - Event end: day, month, year

4. **Warning Dialog on Focus** (Lines 227-243)
   ```java
   FocusListener lockWarning = new FocusAdapter() {
       @Override public void focusGained(FocusEvent e) {
           String msg = "Field ini tidak bisa diubah karena sudah ada anggota tim yang bergabung. " +
                        "Jika ingin mengubah, silakan diskusikan dengan anggota tim terlebih dahulu.";
           JOptionPane.showMessageDialog(EditTeamPanel.this, msg, "Tidak Bisa Diubah", JOptionPane.INFORMATION_MESSAGE);
           e.getComponent().transferFocus();
       }
   };
   ```
   - Applied to all locked fields
   - Moves focus away after showing dialog

### Manual Test Checklist - Feature 2

#### Test 2a: No Lock (No Members)
- [ ] Create new team with only yourself
- [ ] Go to Edit Team
- [ ] Verify "Kompetisi" section visible
- [ ] Verify NO red lock label appears (no "🔒 Terkunci...")
- [ ] Verify competition name field is ENABLED (white background, editable)
- [ ] Verify all date comboboxes are ENABLED (not grayed out)
- [ ] Try clicking on competition name field - should NOT show warning
- [ ] Try selecting date comboboxes - should NOT show warning

#### Test 2b: Lock Active (With Members)
- [ ] Ensure your team has at least 1 member besides you (leader)
- [ ] Go to Edit Team
- [ ] Verify red lock label appears: "🔒 Terkunci (anggota sudah bergabung)"
- [ ] Verify competition name field appears DISABLED/grayed
- [ ] Verify all 6 date comboboxes appear DISABLED/grayed
- [ ] **Click on competition name field** → Verify warning dialog appears with message:
  - "Field ini tidak bisa diubah karena sudah ada anggota tim yang bergabung..."
- [ ] **Click OK on dialog** → Verify focus moves away (not staying on field)
- [ ] **Try clicking each date combobox** → Verify same warning appears for each
- [ ] Verify other fields are still EDITABLE:
  - Team name field should be enabled
  - Description field should be enabled
  - Deadline checkboxes/dates should be enabled
  - Open slots add/remove buttons should work
  - Kick member button should work

#### Test 2c: Lock Persists After Edit
- [ ] With lock active, edit team name to new value
- [ ] Click "Simpan Perubahan"
- [ ] Wait for success message
- [ ] Go back to Edit Team
- [ ] Verify lock is STILL ACTIVE (label still shows)
- [ ] Verify new team name is saved
- [ ] Verify fields are still disabled

#### Test 2d: Lock Removal (Remove Last Member)
- [ ] With team that has lock active, go to Edit Team
- [ ] Find "Keluarkan" button for the non-leader member
- [ ] Click "Keluarkan" → Confirm removal
- [ ] After member is removed, team should refresh to Edit Team
- [ ] Verify red lock label DISAPPEARS
- [ ] Verify competition name field is now ENABLED (white background)
- [ ] Verify all date comboboxes are now ENABLED (not grayed)
- [ ] Try clicking fields - NO warning dialog should appear

#### Test 2e: Lock Reactivation
- [ ] With lock removed, add a new member to team
- [ ] Go back to Edit Team
- [ ] Verify red lock label REAPPEARS
- [ ] Verify fields are disabled again

---

## Visual/Layout Tests

### Test 3a: No Horizontal Overflow

**Setup:** Team with 3+ members and 5+ pending requests

- [ ] Open Edit Team
- [ ] Scroll through entire panel from top to bottom
- [ ] Verify NO horizontal scrollbar appears at bottom
- [ ] Verify all content fits within fixed width
- [ ] Verify text wraps properly (member names, competition names)
- [ ] Check "Permintaan Gabung" section - no overflow
- [ ] Check "Jadwal Anggota Tim" section - no overflow
- [ ] All sections should be cleanly aligned left

### Test 3b: Date Formatting

**Setup:** Team with competition dates: Start 2026-05-15, End 2026-06-20

- [ ] Go to Edit Team
- [ ] Look at "Jadwal Anggota Tim" or "Permintaan Gabung" sections
- [ ] Verify date displays as "15 Mei - 20 Juni" (NOT "2026-05-15 - 2026-06-20")
- [ ] Verify month names are in Indonesian
- [ ] Verify calendar icon (📅) displays before dates
- [ ] Test with different month combinations (e.g., same month, different years)

### Test 3c: Empty States

**Test with no other competitions:**
- [ ] Go to Edit Team for user with no other competitions
- [ ] Verify "(Tidak mengikuti kompetisi lain)" message appears
- [ ] Verify message color is HINT/gray (subtle)
- [ ] Verify message is indented under user name

**Test with no pending requests:**
- [ ] Create team, don't send any requests
- [ ] Verify "Belum ada permintaan gabung." does NOT appear (section should not exist)

**Test with no members (except leader):**
- [ ] Create team and don't add any members
- [ ] Verify "Belum ada anggota bergabung (selain leader)." does NOT appear (section should not exist)
- [ ] After removing all members: Section disappears

---

## Code Quality Assessment

### Feature 1 Code Review

| Aspect | Status | Notes |
|--------|--------|-------|
| Null safety | ✅ Pass | Checks for null/empty dates before parsing |
| Data accuracy | ✅ Pass | Correctly filters current team from other teams |
| Formatting | ✅ Pass | Indonesian month names, proper date format |
| UI layout | ✅ Pass | Proper spacing, alignment using Box.createVerticalStrut |
| Empty state handling | ✅ Pass | Shows appropriate messages when no competitions |

### Feature 2 Code Review

| Aspect | Status | Notes |
|--------|--------|-------|
| Lock trigger | ✅ Pass | Correctly triggers when members.size() > 1 |
| Disabling mechanism | ✅ Pass | Uses `setEnabled(false)` and `setEditable(false)` |
| Visual indicator | ✅ Pass | Clear red lock label with emoji |
| User feedback | ✅ Pass | Dialog message is clear and informative |
| Focus handling | ✅ Pass | `transferFocus()` moves focus away after warning |
| Other fields accessible | ✅ Pass | Only competition fields are locked |

---

## Integration Points Verified

### Data Flow - Feature 1

```
Team.getPendingRequests() 
  → JoinRequest.getRequester() 
  → buildExpandableCompetitionSection(requester) 
  → TeamController.getAcceptedTeamsForUser(user) 
  → For each team: Competition.getEventStartDate/EndDate() 
  → formatDateRange() 
  → Display
```

✅ All methods exist and are called correctly

### Data Flow - Feature 2

```
isCompetitionLocked() checks: team.getMembers().size() > 1
  → Disable competition fields
  → Add lock label (red, 🔒)
  → Add FocusListeners to show dialog
  → Dialog triggers: "Field ini tidak bisa diubah..."
  → transferFocus() moves focus away
```

✅ All components implemented and connected

---

## Known Behavior (Not Issues)

1. **Lock applies on panel creation** - If you're already viewing Edit Team and a member joins elsewhere, the panel won't update. You need to navigate away and back to see the lock applied. *(This is expected for current architecture)*

2. **Warning dialog appears on every focus** - If user tries multiple fields, they'll see the warning multiple times. *(This is the intended behavior to remind user)*

3. **Section visibility is static** - If member is removed while viewing panel, sections don't disappear until panel is refreshed. *(Expected - panels aren't real-time observers)*

---

## Test Execution Notes

### Prerequisites
1. Application compiles successfully: ✅ Verified
2. Demo accounts created on startup: ✅ Verified
3. Database persistence works: ✅ Verified
4. Edit Team panel loads: ✅ Expected (not tested in headless)

### How to Run Manual Tests

1. **Compile:** `cd C:\Users\Dewa\Desktop\Tandem && find src -name "*.java" | xargs javac -encoding UTF-8 -d build/classes`

2. **Run:** `java -cp build/classes com.tandem.Main`

3. **Login:** Use demo account (budi@uni.edu / password123) or create new

4. **Navigate:** Dashboard → Find Teams → [Create/Select Team] → [Team Detail] → "Edit Tim" button

5. **Execute tests in order:** 1a → 1b → 1c → 2a → 2b → 2c → 2d → 3a → 3b → 3c

### Screenshot Guidance

When testing, capture screenshots for:
- Feature 1: Section headers, applicant/member names, competition dates, empty states
- Feature 2: Lock label (red), disabled fields appearance, warning dialog
- Layout: Full panel (top to bottom), horizontal extent, scrollbars

---

## Summary Checklist

### Feature 1: Member Competition Load Display
- [x] Code implementation reviewed: 196 lines added
- [x] All methods exist and connected properly
- [x] Empty state messages implemented
- [x] Date formatting in Indonesian
- [ ] Manual testing: Pending user execution

### Feature 2: Competition Lock
- [x] Code implementation reviewed: 41 lines added
- [x] Lock condition verified: team.getMembers().size() > 1
- [x] Visual indicator implemented: red lock label with emoji
- [x] Field disabling implemented correctly
- [x] Warning dialog message implemented
- [x] Focus transfer implemented
- [x] Other fields remain editable
- [ ] Manual testing: Pending user execution

---

## Next Steps

1. **Run the application** using the command above
2. **Follow the manual test checklist** for both features
3. **Document any issues** found (UI glitches, unexpected behavior, etc.)
4. **Take screenshots** of key test scenarios
5. **Update this report** with test results

---

## Test Results (To Be Filled By Tester)

### Feature 1 Test Results
- Test 1a (No members): [ ] PASS [ ] FAIL
- Test 1b (Pending applicants): [ ] PASS [ ] FAIL
- Test 1c (Accepted members): [ ] PASS [ ] FAIL
- Test 1d (Multiple pending + members): [ ] PASS [ ] FAIL

### Feature 2 Test Results
- Test 2a (No lock): [ ] PASS [ ] FAIL
- Test 2b (Lock active): [ ] PASS [ ] FAIL
- Test 2c (Lock persists): [ ] PASS [ ] FAIL
- Test 2d (Lock removal): [ ] PASS [ ] FAIL
- Test 2e (Lock reactivation): [ ] PASS [ ] FAIL

### Layout/Visual Test Results
- Test 3a (No overflow): [ ] PASS [ ] FAIL
- Test 3b (Date formatting): [ ] PASS [ ] FAIL
- Test 3c (Empty states): [ ] PASS [ ] FAIL

### Overall Status
**FEATURES READY FOR MANUAL TESTING**

---

*Report Generated: 2026-06-27*  
*Code Review: Complete*  
*Manual Testing: Awaiting Execution*
