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
        store.addCompetition(competition);
        return team;
    }

    public ArrayList<Team> getAllOpenTeams() {
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : store.getAllTeams()) {
            if (t.getStatus() == TeamStatus.OPEN) result.add(t);
        }
        return result;
    }

    public ArrayList<Team> getRecommendedTeams(User user) {
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : getAllOpenTeams()) {
            if (t.isMember(user)) continue;
            Competition comp = t.getCompetition();
            if (comp.isRelevantFor(user.getMajor()) || comp.isRelevantFor(user.getFaculty())) {
                result.add(t);
            }
        }
        return result;
    }

    public ArrayList<Team> getTeamsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("Semua")) {
            return getAllOpenTeams();
        }
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : getAllOpenTeams()) {
            if (t.getCompetition().getCategory().equalsIgnoreCase(category)) result.add(t);
        }
        return result;
    }

    public ArrayList<String> getAvailableCategories() {
        ArrayList<String> cats = new ArrayList<>();
        cats.add("Semua");
        for (Team t : store.getAllTeams()) {
            String cat = t.getCompetition().getCategory();
            if (!cats.contains(cat)) cats.add(cat);
        }
        return cats;
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

    public ArrayList<Team> getAcceptedTeamsForUser(User user) {
        ArrayList<Team> result = new ArrayList<>();
        for (Team t : store.getAllTeams()) {
            if (t.isMember(user)) {
                result.add(t);
            }
        }
        return result;
    }

    public void approveRequest(JoinRequest request) {
        request.approve();
        request.getTargetTeam().addMember(request.getRequester());
        request.getTargetTeam().removePendingRequest(request);
    }

    public void rejectRequest(JoinRequest request, String reason) {
        request.reject(reason);
        request.getTargetTeam().removePendingRequest(request);
    }

    public void kickMember(Team team, User member) {
        team.removeMember(member);
        store.persistToFile();
    }

    public void updateSlots(Team team, ArrayList<String> newSlots) {
        team.setOpenSlots(newSlots);
    }
}
