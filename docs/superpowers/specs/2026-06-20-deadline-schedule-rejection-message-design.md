# Design: Deadline Pendaftaran, Jadwal Lomba, dan Pesan Penolakan

**Tanggal:** 2026-06-20  
**Status:** Approved  

---

## Latar Belakang

Dari hasil asistensi dan uji coba kepada dosen dan teman-teman, muncul dua kebutuhan utama:

1. **Informasi waktu** — pengguna yang ingin join tim perlu tahu kapan batas waktu pendaftaran tim ditutup dan kapan jadwal lombanya berlangsung, agar bisa mempertimbangkan ketersediaan waktu mereka.
2. **Pesan penolakan** — ketika leader menolak join request, pemohon harus bisa mengetahui alasannya agar tidak kebingungan.

---

## Fitur yang Dirancang

### Fitur 1: Deadline Pendaftaran Tim & Jadwal Lomba

**Deadline pendaftaran tim** diatur oleh team leader (bukan per-kompetisi), karena setiap tim bisa punya kebijakan berbeda meski lomba yang diikuti sama (banyak cabang lomba).

**Jadwal perlombaan** disimpan sebagai rentang tanggal (tanggal mulai dan selesai) di level kompetisi, karena jadwal event biasanya sudah ditentukan panitia dan berlaku untuk semua tim di lomba tersebut.

### Fitur 2: Pesan Penolakan Join Request

Ketika leader menolak join request, leader diminta (opsional) mengisi alasan penolakan. Pemohon bisa melihat status penolakannya beserta pesan alasan di tab Alerts → section "Lamaranku".

---

## Perubahan Model

### `Competition.java`
- Rename field `deadline` → `submissionDeadline` *(deadline pengumpulan/submission karya)*
- Tambah field `eventStartDate` (String, format `YYYY-MM-DD`) — tanggal mulai lomba
- Tambah field `eventEndDate` (String, format `YYYY-MM-DD`) — tanggal selesai lomba
- Update constructor, getters, setters
- Tambah `private static final long serialVersionUID`

### `Team.java`
- Tambah field `registrationDeadline` (String, format `YYYY-MM-DD`) — batas waktu pendaftaran anggota, opsional (boleh kosong)
- Tambah getter `getRegistrationDeadline()` dan setter `setRegistrationDeadline(String)`
- Tambah `private static final long serialVersionUID`

### `JoinRequest.java`
- Tambah field `rejectionMessage` (String, default `""`)
- Ubah method `reject()` → `reject(String reason)` — menyimpan alasan ke `rejectionMessage`
- Tambah getter `getRejectionMessage()`
- Tambah `private static final long serialVersionUID`

> **Catatan:** Karena ada rename dan penambahan field pada model Serializable, file `tandem_data.ser` lama harus dihapus sebelum menjalankan aplikasi setelah perubahan ini. Data akan di-seed ulang otomatis.

---

## Perubahan Controller

### `TeamController.java`
- Ubah signature `rejectRequest(JoinRequest request)` → `rejectRequest(JoinRequest request, String reason)`
- Teruskan `reason` ke `request.reject(reason)`

### `DataStore.java`
- Update `seedCompetitions()`: semua kompetisi seed diperbarui dengan field `eventStartDate`, `eventEndDate`, dan `submissionDeadline` (rename dari `deadline`)
- Contoh seed: Hackathon Nasional → `eventStartDate="2025-08-15"`, `eventEndDate="2025-08-17"`, `submissionDeadline="2025-08-14"`

---

## Perubahan Views

### `CreateTeamPanel.java`
**Section info tim:**
- Tambah field input **"Deadline Pendaftaran Tim"** (opsional, placeholder `YYYY-MM-DD`, hint teks "Kosongkan jika tidak ada batas")
- Di `doCreate()`: baca field ini, panggil `team.setRegistrationDeadline(value)`

**Sub-panel "Buat Kompetisi Baru":**
- Rename label "Deadline" → **"Deadline Submission"**
- Tambah field **"Tanggal Mulai Event"** (placeholder `YYYY-MM-DD`)
- Tambah field **"Tanggal Selesai Event"** (placeholder `YYYY-MM-DD`)
- Di `doCreate()`: baca kedua field ini, set ke `Competition`

### `TeamDetailPanel.java`
- Tambahkan baris info **"Deadline Pendaftaran:"** — tampil hanya jika `registrationDeadline` tidak kosong, warna oranye/merah jika sudah dekat/lewat
- Tambahkan baris info **"Jadwal Lomba: [eventStartDate] s/d [eventEndDate]"** — tampil selalu jika terisi

### `AlertsPanel.java`
**Sisi leader — tombol "Tolak":**
- Ganti `tc.rejectRequest(jr)` langsung dengan dialog input:
  ```
  JOptionPane.showInputDialog("Masukkan alasan penolakan (opsional):")
  ```
- Jika user cancel dialog → batalkan penolakan (tidak jadi reject)
- Jika user OK (dengan atau tanpa teks) → panggil `tc.rejectRequest(jr, alasan)`

**Sisi pemohon — `buildMyApplicationRow()`:**
- Jika status `REJECTED` dan `rejectionMessage` tidak kosong: tampilkan baris tambahan di bawah nama tim dengan teks *"Alasan: [pesan]"* berwarna merah muted (`new Color(200, 50, 50)`)
- Tinggi card row disesuaikan (dari fixed `72px` menjadi dinamis jika ada pesan)

### `BrowseTeamsPanel.java` & `DashboardPanel.java`
- Di card tim: tambah baris kecil **"Tutup: [registrationDeadline]"** (font `F_SMALL`, warna `HINT`) jika field tidak kosong

---

## Alur Lengkap Fitur 2 (Pesan Penolakan)

```
Leader klik "Tolak"
  → Dialog muncul: "Masukkan alasan penolakan (opsional)"
  → Leader isi / kosongkan → klik OK
  → tc.rejectRequest(jr, alasan) dipanggil
  → jr.status = REJECTED, jr.rejectionMessage = alasan
  → Panel di-refresh

Pemohon buka tab Alerts → "Lamaranku"
  → Row tim tampil dengan status "REJECTED"
  → Jika ada rejectionMessage: tampil "Alasan: [pesan]" di bawahnya
```

---

## Yang Tidak Berubah

- `AuthController`, `RequestController`, `LoginForm`, `RegisterForm`, `ProfilePanel`, `EditProfilePanel` — tidak ada perubahan
- Logika rekomendasi tim, filter kategori, join request sending — tidak berubah
- Format date tetap String (`YYYY-MM-DD`) — tidak pakai `LocalDate` untuk menjaga konsistensi dengan kode yang ada

---

## Urutan Implementasi yang Disarankan

1. Update model (`Competition`, `Team`, `JoinRequest`) + tambah `serialVersionUID`
2. Update `DataStore.seedCompetitions()`
3. Update `TeamController.rejectRequest()`
4. Hapus `tandem_data.ser` lama
5. Update `CreateTeamPanel` (form input)
6. Update `TeamDetailPanel` (tampilkan info)
7. Update `AlertsPanel` (dialog reject + tampilkan pesan di lamaranku)
8. Update `BrowseTeamsPanel` + `DashboardPanel` (deadline di card)
9. Compile & uji coba end-to-end
