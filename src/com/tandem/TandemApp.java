package com.tandem;

import com.tandem.controllers.AuthController;
import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.services.DataStore;
import com.tandem.views.LoginForm;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class TandemApp {

    public static void main(String[] args) {
        DataStore.getInstance().loadFromFile();
        seedDemoData();
        SwingUtilities.invokeLater(LoginForm::new);
    }

    private static void seedDemoData() {
        AuthController auth = new AuthController();
        TeamController tc = new TeamController();
        DataStore store = DataStore.getInstance();

        // Demo users — only seed if not already exist
        if (auth.isEmailTaken("budi@uni.edu")) return;

        User userA = auth.register("Budi Santoso", "2505551001", "budi@uni.edu", "password123",
                "Teknik", "Informatika", "081234567890");
        if (userA != null) {
            userA.setBio("Suka ngoding dan bikin aplikasi mobile");
            userA.setCvLink("https://github.com/budi-santoso");
        }

        User userB = auth.register("Sari Dewi", "2505551002", "sari@uni.edu", "password123",
                "Seni dan Desain", "Desain Komunikasi Visual", "081234567891");
        if (userB != null) {
            userB.setBio("UI/UX designer dengan passion di product design");
            userB.setPortfolioLink("https://behance.net/saridewi");
        }

        User userC = auth.register("Adi Pratama", "2505551003", "adi@uni.edu", "password123",
                "Ekonomi dan Bisnis", "Manajemen", "081234567892");
        if (userC != null) {
            userC.setBio("Tertarik di kewirausahaan dan strategi bisnis");
            userC.setCvLink("https://linkedin.com/in/adi-pratama");
        }

        // Demo team 1 — Budi buat tim hackathon (butuh desainer + analis)
        if (userA != null) {
            ArrayList<String> tags1 = new ArrayList<>();
            tags1.add("Informatika"); tags1.add("Teknik Komputer");
            Competition c1 = new Competition("C001", "Hackathon Nasional 2025", "Hackathon", "2025-08-15", 3, tags1);
            ArrayList<String> slots1 = new ArrayList<>();
            slots1.add("Desainer UI/UX");
            slots1.add("Analis Bisnis");
            tc.createTeam(userA, "Team Inovasi",
                    "Membangun platform edukasi berbasis AI untuk desa terpencil.", c1, slots1);
        }

        // Demo team 2 — Adi buat tim business plan (butuh programmer + desainer)
        if (userC != null) {
            ArrayList<String> tags2 = new ArrayList<>();
            tags2.add("Manajemen"); tags2.add("Ekonomi"); tags2.add("Akuntansi");
            Competition c2 = new Competition("C002", "Business Plan Competition 2025", "Business", "2025-09-20", 4, tags2);
            ArrayList<String> slots2 = new ArrayList<>();
            slots2.add("Programmer");
            slots2.add("Desainer Presentasi");
            tc.createTeam(userC, "FinTech Squad",
                    "Membangun aplikasi micro-lending untuk UMKM lokal.", c2, slots2);
        }
    }
}
