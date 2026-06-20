# Design: Profile Page Button Cleanup

**Tanggal:** 2026-06-20  
**Status:** Approved  

---

## Latar Belakang

Halaman Profile terlihat tidak rapi karena tombol "Edit Profil" berwarna abu-abu penuh (badge) sehingga terlihat seperti card, dan tombol "Logout" berwarna merah solid mendominasi halaman. Keduanya ditumpuk vertikal, memakan ruang dan tidak memiliki hierarki visual yang jelas.

## Perubahan

Hanya menyentuh `ProfilePanel.java` — tidak ada model/controller yang berubah.

### 1. Layout tombol
Ganti dua tombol yang ditumpuk vertikal menjadi **satu baris horizontal** menggunakan `JPanel` dengan `GridLayout(1, 2, 10, 0)`:
- Kolom kiri: tombol "Edit Profil"
- Kolom kanan: tombol "Logout"

### 2. Tombol "Edit Profil"
- Background: `UITheme.DARK` (hitam `#1A1A1A`)
- Foreground: `Color.WHITE`
- Tetap menggunakan `RoundedButton`

### 3. Tombol "Logout" — Outline Style
- Background: transparan (tidak ada fill)
- Border: 1.5px solid merah `new Color(220, 50, 50)`
- Teks: merah `new Color(220, 50, 50)`
- Dibuat sebagai inner anonymous `JButton` dengan custom `paintComponent` (bukan `RoundedButton` karena `RoundedButton` tidak support outline)
- Tinggi: 54px (sama dengan `RoundedButton`)
- Corner radius: `UITheme.R` (12px)

### 4. Spacing
- Kurangi `Box.createVerticalStrut` sebelum button row dari 24 → 16

## Yang Tidak Berubah
- Avatar, nama, badge jurusan
- Kartu "Informasi Akademik" dan "Profil & Portofolio"
- Logika logout (`Session.clear()`, dispose window)
- Logika navigasi ke edit profile
