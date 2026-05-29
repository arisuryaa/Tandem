package com.tandem;

import com.tandem.models.*;
import com.tandem.services.DataStore;
import com.tandem.utils.IDGenerator;
import com.tandem.utils.PasswordUtils;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        DataStore store = DataStore.getInstance();

        // 1. Create 3 users (one per role)
        String hashed = PasswordUtils.hash("password123");

        Hacker hacker = new Hacker(IDGenerator.generateId(), "Budi Santoso", "2505551001",
                "budi@email.com", hashed, "Teknik Informatika", "Ilmu Komputer", "081234567890");
        hacker.addTechStack("Android");
        hacker.addTechStack("Web");
        hacker.addProgrammingLanguage("Java");

        Hipster hipster = new Hipster(IDGenerator.generateId(), "Sari Dewi", "2505551002",
                "sari@email.com", hashed, "Seni Rupa", "Desain Komunikasi Visual", "081234567891");
        hipster.addDesignTool("Figma");
        hipster.addDesignTool("Adobe XD");

        Hustler hustler = new Hustler(IDGenerator.generateId(), "Adi Pratama", "2505551003",
                "adi@email.com", hashed, "Ekonomi", "Manajemen", "081234567892");
        hustler.addBusinessSkill("Market Research");
        hustler.addBusinessSkill("Pitching");

        store.addUser(hacker);
        store.addUser(hipster);
        store.addUser(hustler);

        System.out.println("=== USERS REGISTERED ===");
        for (User u : store.getAllUsers()) {
            System.out.println(u);
            System.out.println("  Role   : " + u.getRole());
            System.out.println("  Skills : " + u.getSkillSummary());
        }

        // 2. Create team
        Competition comp = new Competition(IDGenerator.generateId(),
                "Hackathon Nasional 2025", "Hackathon", "2025-08-15", 3);

        ArrayList<String> slots = new ArrayList<>();
        slots.add("Hipster");
        slots.add("Hustler");

        Team team = new Team(IDGenerator.generateId(), "Team Inovasi", comp, hacker, slots);
        store.addTeam(team);

        System.out.println("\n=== TEAM CREATED ===");
        System.out.println(team);
        System.out.println("Open Slots : " + team.getOpenSlots());

        // 3. Send join request
        JoinRequest req = new JoinRequest(IDGenerator.generateId(), hipster, team,
                "I want to join as UI/UX designer!", "2025-05-25");
        team.addPendingRequest(req);
        store.addJoinRequest(req);

        System.out.println("\n=== JOIN REQUEST SENT ===");
        System.out.println(req);

        // 4. Password check
        System.out.println("\n=== PASSWORD VERIFICATION ===");
        System.out.println("Correct : " + hacker.verifyPassword("password123"));
        System.out.println("Wrong   : " + hacker.verifyPassword("wrongpass"));

        // 5. Approve request
        req.approve();
        team.addMember(hipster);
        team.removePendingRequest(req);

        System.out.println("\n=== AFTER APPROVAL ===");
        System.out.println("Status         : " + req.getStatus());
        System.out.println("Members:");
        for (User u : team.getMembers()) {
            System.out.println("  - " + u.getName() + " (" + u.getRole() + ")");
        }
        System.out.println("Remaining slots: " + team.getOpenSlots());
        System.out.println("Team full?     : " + team.isFull());
        System.out.println("Team status    : " + team.getStatus());
    }
}
