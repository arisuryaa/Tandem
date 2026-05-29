package com.tandem.services;

import com.tandem.models.JoinRequest;
import com.tandem.models.Team;
import com.tandem.models.User;
import java.io.*;
import java.util.ArrayList;

public class DataStore {

    private static DataStore instance;

    private ArrayList<User> users;
    private ArrayList<Team> teams;
    private ArrayList<JoinRequest> joinRequests;

    private static final String DATA_FILE = "tandem_data.ser";

    private DataStore() {
        users = new ArrayList<>();
        teams = new ArrayList<>();
        joinRequests = new ArrayList<>();
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

    public void addJoinRequest(JoinRequest jr)           { joinRequests.add(jr); }
    public ArrayList<JoinRequest> getAllJoinRequests()   { return new ArrayList<>(joinRequests); }

    public void persistToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(teams);
            oos.writeObject(joinRequests);
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users        = (ArrayList<User>)        ois.readObject();
            teams        = (ArrayList<Team>)        ois.readObject();
            joinRequests = (ArrayList<JoinRequest>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load data: " + e.getMessage());
        }
    }
}
