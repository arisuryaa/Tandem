# Tandem Redesign — Design Spec
**Date:** 2026-06-01  
**Status:** Approved  

---

## Overview

Tandem adalah platform desktop Java Swing untuk membantu mahasiswa menemukan dan membentuk tim kompetisi. Redesign ini menghapus sistem role kaku (Hacker/Hipster/Hustler) dan menggantinya dengan sistem rekomendasi berbasis jurusan/fakultas, mendukung semua jenis kompetisi mahasiswa (Hackathon, PKM, UIUX, Business Plan, dll).

---

## Scope Perubahan

1. Hapus model role-based (`Hacker`, `Hipster`, `Hustler`) → satu class `User` konkret
2. Tambah tags pada `Competition` untuk pemetaan jurusan
3. Redesign `DashboardPanel` dengan rekomendasi berbasis jurusan
4. Update semua view yang terdampak (Register, Browse, Profile, CreateTeam)
5. Sederhanakan controller dari role-based ke jurusan-based
6. Tambah seed data kompetisi awal

---

## 1. Model Changes

### 1.1 User (konkret, tidak abstract)

Hapus `Hacker.java`, `Hipster.java`, `Hustler.java`. `User` menjadi class konkret biasa.

**Field yang dihapus:**
- `getRole()` (abstract)
- `getSkillSummary()` (abstract)
- Tech stack, programming languages (dari Hacker)
- Design tools, portfolio link (dari Hipster)
- Business skills, LinkedIn profile (dari Hustler)

**Field baru:**
```java
private String bio;           // deskripsi singkat diri, boleh kosong
private String cvLink;        // link eksternal (Google Drive, dll)
private String portfolioLink; // link eksternal (GitHub, Behance, dll)
```

**Method yang dihapus:**
- Tidak ada lagi `verifyPassword` yang bergantung role

**Method baru:**
- Getter/setter untuk `bio`, `cvLink`, `portfolioLink`

### 1.2 Competition — tambah tags

```java
private ArrayList<String> tags; // contoh: ["Manajemen", "Ekonomi", "Akuntansi"]
```

Tags berisi nama jurusan atau fakultas yang relevan dengan kompetisi tersebut. Nilai `"Semua"` berarti kompetisi terbuka untuk semua jurusan.

**Constructor baru:**
```java
public Competition(String id, String name, String category, 
                   String deadline, int maxTeamSize, ArrayList<String> tags)
```

### 1.3 Team — slots tetap, semantik berubah

`openSlots` tetap `ArrayList<String>`, tapi isinya kini bebas ditulis leader (contoh: `"Desainer UI"`, `"Analis Bisnis"`, `"Programmer Backend"`). Tidak ada lagi validasi role saat join request.

### 1.4 JoinRequest — tidak berubah

Struktur tetap sama.

### 1.5 Enums

- `TeamStatus` — tetap (`OPEN`, `FULL`)
- `RequestStatus` — tetap (`PENDING`, `APPROVED`, `REJECTED`)

---

## 2. Controller & Service Changes

### 2.1 AuthController

Hapus `switch (role)`. Registrasi langsung buat `User` konkret:

```java
public User register(String name, String nim, String email, String rawPassword,
                     String faculty, String major, String contactNumber) {
    // validasi, hash password, buat User, tambah ke store
}
```

Parameter `role` dihapus dari signature.

### 2.2 TeamController

**Hapus:** `getTeamsForRole(String role)`

**Tambah:**
```java
public ArrayList<Team> getRecommendedTeams(User user) {
    // ambil semua tim OPEN
    // filter: competition.tags contains user.getMajor() 
    //         ATAU competition.tags contains user.getFaculty()
    //         ATAU competition.tags contains "Semua"
    // exclude: tim yang user sudah jadi member
}
```

**Filter di BrowseTeams berubah:** dari filter by role → filter by `competition.getCategory()`.

```java
public ArrayList<Team> getTeamsByCategory(String category) {
    // filter tim OPEN berdasarkan kategori kompetisi
    // "Semua" → kembalikan semua tim OPEN
}
```

### 2.3 RequestController

**Hapus validasi:**
```java
// DIHAPUS:
if (!team.getOpenSlots().contains(requester.getRole())) return null;
```

**Validasi yang tersisa:**
1. User belum menjadi member tim
2. Tim belum full
3. Belum ada pending request dari user yang sama ke tim yang sama

### 2.4 DataStore — seed data

Seed dijalankan di `loadFromFile()` saat file `.ser` belum ada:

```
Kompetisi seed:
┌─────────────────────────────┬──────────────┬─────────────────────────────────────────┐
│ Nama                        │ Kategori     │ Tags                                    │
├─────────────────────────────┼──────────────┼─────────────────────────────────────────┤
│ Hackathon Nasional 2025     │ Hackathon    │ Informatika, Teknik Komputer, Sistem Info│
│ UIUX Competition 2025       │ Design       │ Desain Komunikasi Visual, Informatika,   │
│                             │              │ Seni Rupa                               │
│ PKM-K Kewirausahaan 2025    │ PKM          │ Manajemen, Ekonomi, Akuntansi,          │
│                             │              │ Informatika                             │
│ Business Plan Competition   │ Business     │ Manajemen, Ekonomi, Akuntansi           │
│ Data Science Challenge 2025 │ Data Science │ Statistika, Informatika, Matematika     │
│ PKM-PM Pengabdian Masyarakat│ PKM          │ Semua                                   │
└─────────────────────────────┴──────────────┴─────────────────────────────────────────┘
```

---

## 3. View Changes

### 3.1 RegisterForm

**Dihapus:** dropdown pemilihan role (Hacker/Hipster/Hustler)

**Ditambah (semua opsional, bisa diisi nanti di profil):**
- Field `Bio` — `StyledField` atau textarea singkat
- Field `CV Link` — `StyledField` (URL)
- Field `Portfolio Link` — `StyledField` (URL)

Form tetap menggunakan `StyledField` dan `StyledPasswordField` yang sudah ada.

### 3.2 DashboardPanel — redesign utama

Layout baru (scroll vertical):

```
┌─────────────────────────────────┐
│  Good day, [Nama]!              │
│  [Fakultas] · [Jurusan]         │  ← ganti role badge dengan info akademik
│                                 │
│  ── Rekomendasi untuk Kamu ──   │  ← BARU: tampil pertama
│  [TeamCard] [TeamCard] ...      │
│  (kosong jika tidak ada match)  │
│                                 │
│  ── Tim Saya ──                 │  ← pindah ke bawah
│  [TeamCard] ...                 │
│  (kosong state jika belum ada)  │
│                                 │
│  [+ Buat Tim Baru]              │
└─────────────────────────────────┘
```

`TeamCard` di Rekomendasi: tampilkan nama tim, kompetisi, kategori, open slots.  
Klik card → `showTeamDetail(team)`.

### 3.3 BrowseTeamsPanel

**Filter chips lama:** `[All][Hacker][Hipster][Hustler]`  
**Filter chips baru:** `[Semua][Hackathon][PKM][Design][Business][Data Science]`

Kategori filter diambil dinamis dari `DataStore.getAllTeams()` → extract unique `competition.getCategory()`, sehingga otomatis berkembang jika ada kategori baru.

Card tetap sama tampilannya, slot pills menampilkan teks bebas dari leader.

### 3.4 ProfilePanel

**Dihapus:** Skill chips berbasis role (tech stack, design tools, business skills)

**Ditambah:**
- Row `Bio` — teks deskripsi diri
- Row `CV` — label + link yang bisa diklik (open browser)
- Row `Portfolio` — label + link yang bisa diklik (open browser)

Info akademik (NIM, Fakultas, Jurusan, Kontak) tetap ada.  
Riwayat join request tetap ada.  
Tombol Logout tetap ada.

### 3.5 CreateTeamPanel

Dua mode untuk memilih kompetisi:

**Mode A — Pilih kompetisi yang sudah ada:**
- Dropdown/list kompetisi dari DataStore
- Tampilkan nama, kategori, deadline, tags

**Mode B — Buat kompetisi baru inline:**
- Field: Nama Kompetisi, Kategori (dropdown), Deadline, Max Tim Size
- Multi-select tags jurusan (pilih dari daftar jurusan umum atau ketik bebas)

**Slots:** tombol `+ Tambah Slot`, user ketik deskripsi bebas (misal: "Desainer UI"). Bisa tambah hingga max team size - 1 (karena leader sudah 1 slot).

### 3.6 TeamDetailPanel

- Tampilkan tags kompetisi sebagai pill/badge
- Slot ditampilkan sebagai teks bebas (bukan role label)
- Tombol "Kirim Join Request" tetap ada dengan modal isi pesan

---

## 4. Komponen UI yang Tidak Berubah

- `UITheme.java` — warna, font, konstanta tetap
- `RoundedPanel.java` — tidak berubah
- `RoundedButton.java` — tidak berubah
- `StyledField.java` — tidak berubah
- `StyledPasswordField.java` — tidak berubah
- `LoginForm.java` — tidak berubah
- `MainFrame.java` — struktur navigasi tetap, tidak ada perubahan signifikan
- `Session.java` — tidak berubah
- `IDGenerator.java` — tidak berubah
- `PasswordUtils.java` — tidak berubah
- `Validator.java` — tidak berubah

---

## 5. File yang Dihapus

- `src/com/tandem/models/Hacker.java`
- `src/com/tandem/models/Hipster.java`
- `src/com/tandem/models/Hustler.java`

---

## 6. Urutan Implementasi (Prioritas)

1. **Model** — ubah `User`, ubah `Competition` (tambah tags), hapus subclass
2. **DataStore** — tambah seed data
3. **Controllers** — update `AuthController`, `TeamController`, `RequestController`
4. **Views** — `RegisterForm` → `DashboardPanel` → `BrowseTeamsPanel` → `ProfilePanel` → `CreateTeamPanel` → `TeamDetailPanel`
5. **Smoke test** — jalankan `Main.java` verifikasi alur dasar

---

## 7. Hal yang Bisa Dikembangkan Nanti (Out of Scope)

- Panel khusus "Manage Competitions" terpisah
- Notifikasi/Alerts panel (tombol sudah ada di nav, belum diimplementasi)
- Auto-save berkala ke file `.ser`
- Search teks bebas untuk cari tim/kompetisi
