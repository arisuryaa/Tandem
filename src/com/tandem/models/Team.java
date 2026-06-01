package com.tandem.models;

import com.tandem.models.enums.TeamStatus;
import java.io.Serializable;
import java.util.ArrayList;

public class Team implements Serializable {

    private String teamId;
    private String teamName;
    private String description;
    private Competition competition;
    private User leader;
    private ArrayList<User> members;
    private ArrayList<String> openSlots;
    private ArrayList<JoinRequest> pendingRequests;
    private TeamStatus status;

    public Team(String teamId, String teamName, Competition competition,
                User leader, ArrayList<String> openSlots) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.description = "";
        this.competition = competition;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.members.add(leader);
        this.openSlots = new ArrayList<>(openSlots);
        this.pendingRequests = new ArrayList<>();
        this.status = TeamStatus.OPEN;
    }

    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            if (!openSlots.isEmpty()) openSlots.remove(0);
        }
        if (openSlots.isEmpty()) {
            status = TeamStatus.FULL;
        }
    }

    public boolean isMember(User user) {
        return members.contains(user);
    }

    public boolean isFull() {
        return openSlots.isEmpty();
    }

    public void addPendingRequest(JoinRequest request) {
        pendingRequests.add(request);
    }

    public void removePendingRequest(JoinRequest request) {
        pendingRequests.remove(request);
    }

    public String getTeamId()                          { return teamId; }
    public String getTeamName()                        { return teamName; }
    public Competition getCompetition()                { return competition; }
    public User getLeader()                            { return leader; }
    public TeamStatus getStatus()                      { return status; }
    public ArrayList<User> getMembers()                { return new ArrayList<>(members); }
    public ArrayList<String> getOpenSlots()            { return new ArrayList<>(openSlots); }
    public ArrayList<JoinRequest> getPendingRequests() { return new ArrayList<>(pendingRequests); }

    public void setTeamName(String teamName)       { this.teamName = teamName; }
    public void setDescription(String description) { this.description = description; }
    public String getDescription()                 { return description; }
    public void setStatus(TeamStatus status)       { this.status = status; }

    @Override
    public String toString() {
        return teamName + " | " + competition.getName() + " | Open: " + openSlots;
    }
}
