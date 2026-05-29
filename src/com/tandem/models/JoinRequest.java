package com.tandem.models;

import com.tandem.models.enums.RequestStatus;
import java.io.Serializable;

public class JoinRequest implements Serializable {

    private String requestId;
    private User requester;
    private Team targetTeam;
    private String message;
    private RequestStatus status;
    private String createdAt;

    public JoinRequest(String requestId, User requester, Team targetTeam,
                       String message, String createdAt) {
        this.requestId = requestId;
        this.requester = requester;
        this.targetTeam = targetTeam;
        this.message = message;
        this.status = RequestStatus.PENDING;
        this.createdAt = createdAt;
    }

    public void approve() { this.status = RequestStatus.APPROVED; }
    public void reject()  { this.status = RequestStatus.REJECTED; }

    public String getRequestId()     { return requestId; }
    public User getRequester()       { return requester; }
    public Team getTargetTeam()      { return targetTeam; }
    public String getMessage()       { return message; }
    public RequestStatus getStatus() { return status; }
    public String getCreatedAt()     { return createdAt; }

    @Override
    public String toString() {
        return requester.getName() + " → " + targetTeam.getTeamName() + " [" + status + "]";
    }
}
