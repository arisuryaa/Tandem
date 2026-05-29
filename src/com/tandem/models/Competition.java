package com.tandem.models;

import java.io.Serializable;

public class Competition implements Serializable {

    private String competitionId;
    private String name;
    private String category;
    private String deadline;
    private int maxTeamSize;

    public Competition(String competitionId, String name, String category,
                       String deadline, int maxTeamSize) {
        this.competitionId = competitionId;
        this.name = name;
        this.category = category;
        this.deadline = deadline;
        this.maxTeamSize = maxTeamSize;
    }

    public String getCompetitionId() { return competitionId; }
    public String getName()          { return name; }
    public String getCategory()      { return category; }
    public String getDeadline()      { return deadline; }
    public int getMaxTeamSize()      { return maxTeamSize; }

    public void setName(String name)         { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setMaxTeamSize(int size)     { this.maxTeamSize = size; }

    @Override
    public String toString() {
        return name + " [" + category + "] - Deadline: " + deadline;
    }
}
