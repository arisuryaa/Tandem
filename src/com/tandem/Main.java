package com.tandem;

import com.tandem.models.*;
import com.tandem.services.DataStore;
import com.tandem.utils.IDGenerator;
import com.tandem.utils.PasswordUtils;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        DataStore store = DataStore.getInstance();

        String hashed = PasswordUtils.hash("password123");

        User userA = new User(IDGenerator.generateId(), "Budi Santoso", "2505551001",
                "budi@email.com", hashed, "Teknik", "Informatika", "081234567890");
        userA.setBio("Suka ngoding dan bikin aplikasi");
        userA.setCvLink("https://drive.google.com/budi-cv");

        User userB = new User(IDGenerator.generateId(), "Sari Dewi", "2505551002",
                "sari@email.com", hashed, "Seni dan Desain", "Desain Komunikasi Visual", "081234567891");
        userB.setBio("Designer dengan 2 tahun pengalaman Figma");
        userB.setPortfolioLink("https://behance.net/sari");

        User userC = new User(IDGenerator.generateId(), "Adi Pratama", "2505551003",
                "adi@email.com", hashed, "Ekonomi dan Bisnis", "Manajemen", "081234567892");
        userC.setBio("Tertarik di entrepreneurship dan strategi bisnis");
        userC.setCvLink("https://linkedin.com/in/adi-pratama");

        store.addUser(userA);
        store.addUser(userB);
        store.addUser(userC);

        System.out.println("=== USERS REGISTERED ===");
        for (User u : store.getAllUsers()) {
            System.out.println(u);
            System.out.println("  Bio: " + u.getBio());
        }

        // Create competition with tags
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Informatika"); tags.add("Sistem Informasi");
        Competition comp = new Competition(IDGenerator.generateId(),
                "Hackathon Nasional 2025", "Hackathon",
                "2025-08-10", "2025-08-15", "2025-08-17", 3, tags);

        ArrayList<String> slots = new ArrayList<>();
        slots.add("Desainer UI");
        slots.add("Analis Bisnis");

        Team team = new Team(IDGenerator.generateId(), "Team Inovasi", comp, userA, slots);
        store.addTeam(team);

        System.out.println("\n=== TEAM CREATED ===");
        System.out.println(team);
        System.out.println("Open Slots : " + team.getOpenSlots());
        System.out.println("Tags       : " + comp.getTags());

        // Relevance check
        System.out.println("\n=== RELEVANCE CHECK ===");
        System.out.println("Budi (Informatika) relevant? " + comp.isRelevantFor(userA.getMajor()));
        System.out.println("Sari (DKV) relevant?         " + comp.isRelevantFor(userB.getMajor()));
        System.out.println("Adi (Manajemen) relevant?    " + comp.isRelevantFor(userC.getMajor()));

        // Send join request
        JoinRequest req = new JoinRequest(IDGenerator.generateId(), userB, team,
                "Saya ingin bergabung sebagai desainer!", "2025-05-25");
        team.addPendingRequest(req);
        store.addJoinRequest(req);

        System.out.println("\n=== JOIN REQUEST ===");
        System.out.println(req);

        // Approve
        req.approve();
        team.addMember(userB);
        team.removePendingRequest(req);

        System.out.println("\n=== AFTER APPROVAL ===");
        System.out.println("Status         : " + req.getStatus());
        System.out.println("Members:");
        for (User u : team.getMembers()) {
            System.out.println("  - " + u.getName() + " (" + u.getMajor() + ")");
        }
        System.out.println("Remaining slots: " + team.getOpenSlots());
        System.out.println("Team full?     : " + team.isFull());
        System.out.println("Team status    : " + team.getStatus());
    }
}
