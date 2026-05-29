package com.tandem;

import com.tandem.controllers.AuthController;
import com.tandem.controllers.TeamController;
import com.tandem.models.*;
import com.tandem.views.LoginForm;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class TandemApp {

    public static void main(String[] args) {
        seedDemoData();
        SwingUtilities.invokeLater(LoginForm::new);
    }

    private static void seedDemoData() {
        AuthController auth = new AuthController();
        TeamController tc = new TeamController();

        // Demo users
        User hacker = auth.register("Budi Santoso", "2505551001", "budi@uni.edu", "password123",
                "Hacker", "Teknik Informatika", "Ilmu Komputer", "081234567890");
        User hipster = auth.register("Sari Dewi", "2505551002", "sari@uni.edu", "password123",
                "Hipster", "Seni Rupa", "DKV", "081234567891");
        User hustler = auth.register("Adi Pratama", "2505551003", "adi@uni.edu", "password123",
                "Hustler", "Ekonomi", "Manajemen", "081234567892");

        if (hacker instanceof Hacker) {
            ((Hacker) hacker).addTechStack("Android");
            ((Hacker) hacker).addTechStack("Java");
            ((Hacker) hacker).addProgrammingLanguage("Java");
        }
        if (hipster instanceof Hipster) {
            ((Hipster) hipster).addDesignTool("Figma");
            ((Hipster) hipster).addDesignTool("Adobe XD");
        }
        if (hustler instanceof Hustler) {
            ((Hustler) hustler).addBusinessSkill("Pitching");
            ((Hustler) hustler).addBusinessSkill("Market Research");
        }

        // Demo team 1 (led by hacker, needs Hipster + Hustler)
        if (hacker != null) {
            Competition c1 = new Competition("C001", "Hackathon Nasional 2025", "Hackathon", "2025-08-15", 3);
            ArrayList<String> slots1 = new ArrayList<>();
            slots1.add("Hipster");
            slots1.add("Hustler");
            tc.createTeam(hacker, "Team Inovasi", "Membangun platform edukasi berbasis AI untuk desa terpencil.", c1, slots1);
        }

        // Demo team 2 (led by hustler, needs Hacker)
        if (hustler != null) {
            Competition c2 = new Competition("C002", "Global Student Fintech 2025", "Business", "2025-09-20", 4);
            ArrayList<String> slots2 = new ArrayList<>();
            slots2.add("Hacker");
            slots2.add("Hipster");
            tc.createTeam(hustler, "FinTech Squad", "Membangun aplikasi micro-lending untuk UMKM lokal.", c2, slots2);
        }
    }
}
