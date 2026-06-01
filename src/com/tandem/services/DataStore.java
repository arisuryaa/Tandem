package com.tandem.services;

import com.tandem.models.Competition;
import com.tandem.models.JoinRequest;
import com.tandem.models.Team;
import com.tandem.models.User;
import com.tandem.utils.IDGenerator;
import java.io.*;
import java.util.ArrayList;

public class DataStore {

    private static DataStore instance;

    private ArrayList<User> users;
    private ArrayList<Team> teams;
    private ArrayList<JoinRequest> joinRequests;
    private ArrayList<Competition> competitions;

    private static final String DATA_FILE = "tandem_data.ser";

    private DataStore() {
        users = new ArrayList<>();
        teams = new ArrayList<>();
        joinRequests = new ArrayList<>();
        competitions = new ArrayList<>();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void addUser(User user)       { users.add(user); }
    public ArrayList<User> getAllUsers() { return new ArrayList<>(users); }

    public User findUserByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    public boolean emailExists(String email) {
        return findUserByEmail(email) != null;
    }

    public void addTeam(Team team)       { teams.add(team); }
    public ArrayList<Team> getAllTeams() { return new ArrayList<>(teams); }

    public Team findTeamById(String teamId) {
        for (Team t : teams) {
            if (t.getTeamId().equals(teamId)) return t;
        }
        return null;
    }

    public void addJoinRequest(JoinRequest jr)         { joinRequests.add(jr); }
    public ArrayList<JoinRequest> getAllJoinRequests() { return new ArrayList<>(joinRequests); }

    public void addCompetition(Competition comp) {
        for (Competition c : competitions) {
            if (c.getCompetitionId().equals(comp.getCompetitionId())) return;
        }
        competitions.add(comp);
    }
    public ArrayList<Competition> getAllCompetitions()   { return new ArrayList<>(competitions); }

    public void persistToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(teams);
            oos.writeObject(joinRequests);
            oos.writeObject(competitions);
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            seedDefaultData();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users        = (ArrayList<User>)        ois.readObject();
            teams        = (ArrayList<Team>)        ois.readObject();
            joinRequests = (ArrayList<JoinRequest>) ois.readObject();
            try {
                competitions = (ArrayList<Competition>) ois.readObject();
            } catch (Exception e) {
                competitions = new ArrayList<>();
                seedCompetitions();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load data: " + e.getMessage());
            seedDefaultData();
        }
    }

    private void seedDefaultData() {
        seedCompetitions();
    }

    private void seedCompetitions() {
        ArrayList<String> tagsHackathon = new ArrayList<>();
        tagsHackathon.add("Informatika"); tagsHackathon.add("Teknik Komputer");
        tagsHackathon.add("Sistem Informasi"); tagsHackathon.add("Ilmu Komputer");
        competitions.add(new Competition(IDGenerator.generateId(),
                "Hackathon Nasional 2025", "Hackathon", "2025-08-15", 4, tagsHackathon));

        ArrayList<String> tagsDesign = new ArrayList<>();
        tagsDesign.add("Desain Komunikasi Visual"); tagsDesign.add("Informatika");
        tagsDesign.add("Seni Rupa"); tagsDesign.add("DKV");
        competitions.add(new Competition(IDGenerator.generateId(),
                "UIUX Competition 2025", "Design", "2025-07-30", 3, tagsDesign));

        ArrayList<String> tagsPkmK = new ArrayList<>();
        tagsPkmK.add("Manajemen"); tagsPkmK.add("Ekonomi");
        tagsPkmK.add("Akuntansi"); tagsPkmK.add("Informatika");
        competitions.add(new Competition(IDGenerator.generateId(),
                "PKM-K Kewirausahaan 2025", "PKM", "2025-09-01", 5, tagsPkmK));

        ArrayList<String> tagsBusiness = new ArrayList<>();
        tagsBusiness.add("Manajemen"); tagsBusiness.add("Ekonomi"); tagsBusiness.add("Akuntansi");
        competitions.add(new Competition(IDGenerator.generateId(),
                "Business Plan Competition 2025", "Business", "2025-10-15", 4, tagsBusiness));

        ArrayList<String> tagsData = new ArrayList<>();
        tagsData.add("Statistika"); tagsData.add("Informatika"); tagsData.add("Matematika");
        competitions.add(new Competition(IDGenerator.generateId(),
                "Data Science Challenge 2025", "Data Science", "2025-08-20", 3, tagsData));

        ArrayList<String> tagsPkmPm = new ArrayList<>();
        tagsPkmPm.add("Semua");
        competitions.add(new Competition(IDGenerator.generateId(),
                "PKM-PM Pengabdian Masyarakat", "PKM", "2025-09-15", 5, tagsPkmPm));
    }
}
