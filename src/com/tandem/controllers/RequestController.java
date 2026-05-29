package com.tandem.controllers;

import com.tandem.models.*;
import com.tandem.models.enums.RequestStatus;
import com.tandem.services.DataStore;
import com.tandem.utils.IDGenerator;
import java.time.LocalDate;
import java.util.ArrayList;

public class RequestController {

    private DataStore store = DataStore.getInstance();

    public JoinRequest sendJoinRequest(User requester, Team team, String message) {
        if (team.isMember(requester) || team.isFull()) return null;
        if (!team.getOpenSlots().contains(requester.getRole())) return null;
        if (hasPendingRequest(requester, team)) return null;

        JoinRequest jr = new JoinRequest(IDGenerator.generateId(), requester, team,
                message, LocalDate.now().toString());
        team.addPendingRequest(jr);
        store.addJoinRequest(jr);
        return jr;
    }

    public boolean hasPendingRequest(User user, Team team) {
        for (JoinRequest jr : store.getAllJoinRequests()) {
            if (jr.getRequester().getUserId().equals(user.getUserId())
                    && jr.getTargetTeam().getTeamId().equals(team.getTeamId())
                    && jr.getStatus() == RequestStatus.PENDING) return true;
        }
        return false;
    }

    public ArrayList<JoinRequest> getPendingRequestsForTeam(Team team) {
        ArrayList<JoinRequest> result = new ArrayList<>();
        for (JoinRequest jr : store.getAllJoinRequests()) {
            if (jr.getTargetTeam().getTeamId().equals(team.getTeamId())
                    && jr.getStatus() == RequestStatus.PENDING) result.add(jr);
        }
        return result;
    }

    public ArrayList<JoinRequest> getRequestsByUser(User user) {
        ArrayList<JoinRequest> result = new ArrayList<>();
        for (JoinRequest jr : store.getAllJoinRequests()) {
            if (jr.getRequester().getUserId().equals(user.getUserId())) result.add(jr);
        }
        return result;
    }
}
