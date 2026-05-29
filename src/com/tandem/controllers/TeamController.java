package com.tandem.controllers;

import com.tandem.models.*;
import com.tandem.models.enums.TeamStatus;
import com.tandem.services.DataStore;
import com.tandem.utils.IDGenerator;
import java.util.ArrayList;

public class TeamController {

    private DataStore store = DataStore.getInstance();

    public Team createTeam(User leader, String teamName, String description,
                           Competition competition, ArrayList<String> openSlots) {
        Team team = new Team(IDGenerator.generateId(), teamName, competition, leader, openSlots);
        team.setDescription(description);
        store.addTeam(team);
        return team;
    }

    public ArrayList<Team> getAllOpenTeams() {
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : store.getAllTeams()) {
            if (t.getStatus() == TeamStatus.OPEN) result.add(t);
        }
        return result;
    }

    public ArrayList<Team> getTeamsForRole(String role) {
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : getAllOpenTeams()) {
            if (t.getOpenSlots().contains(role)) result.add(t);
        }
        return result;
    }

    public ArrayList<Team> getTeamsByLeader(User leader) {
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : store.getAllTeams()) {
            if (t.getLeader().getUserId().equals(leader.getUserId())) result.add(t);
        }
        return result;
    }

    public ArrayList<Team> getTeamsByMember(User user) {
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : store.getAllTeams()) {
            if (t.isMember(user)) result.add(t);
        }
        return result;
    }

    public void approveRequest(JoinRequest request) {
        request.approve();
        request.getTargetTeam().addMember(request.getRequester());
        request.getTargetTeam().removePendingRequest(request);
    }

    public void rejectRequest(JoinRequest request) {
        request.reject();
        request.getTargetTeam().removePendingRequest(request);
    }
}
