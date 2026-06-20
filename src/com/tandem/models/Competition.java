package com.tandem.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    private String competitionId;
    private String name;
    private String category;
    private String submissionDeadline;
    private String eventStartDate;
    private String eventEndDate;
    private int maxTeamSize;
    private ArrayList<String> tags;

    public Competition(String competitionId, String name, String category,
                       String submissionDeadline, String eventStartDate, String eventEndDate,
                       int maxTeamSize, ArrayList<String> tags) {
        this.competitionId      = competitionId;
        this.name               = name;
        this.category           = category;
        this.submissionDeadline = submissionDeadline;
        this.eventStartDate     = eventStartDate;
        this.eventEndDate       = eventEndDate;
        this.maxTeamSize        = maxTeamSize;
        this.tags               = new ArrayList<>(tags);
    }

    public Competition(String competitionId, String name, String category,
                       String submissionDeadline, String eventStartDate, String eventEndDate,
                       int maxTeamSize) {
        this(competitionId, name, category, submissionDeadline, eventStartDate, eventEndDate,
             maxTeamSize, new ArrayList<>());
    }

    public boolean isRelevantFor(String facultyOrMajor) {
        if (tags.contains("Semua")) return true;
        for (String tag : tags) {
            if (tag.equalsIgnoreCase(facultyOrMajor)) return true;
        }
        return false;
    }

    public String getCompetitionId()      { return competitionId; }
    public String getName()               { return name; }
    public String getCategory()           { return category; }
    public String getSubmissionDeadline() { return submissionDeadline; }
    public String getEventStartDate()     { return eventStartDate; }
    public String getEventEndDate()       { return eventEndDate; }
    public int getMaxTeamSize()           { return maxTeamSize; }
    public ArrayList<String> getTags()    { return new ArrayList<>(tags); }

    public void setName(String name)                   { this.name = name; }
    public void setCategory(String category)           { this.category = category; }
    public void setSubmissionDeadline(String deadline) { this.submissionDeadline = deadline; }
    public void setEventStartDate(String date)         { this.eventStartDate = date; }
    public void setEventEndDate(String date)           { this.eventEndDate = date; }
    public void setMaxTeamSize(int size)               { this.maxTeamSize = size; }
    public void addTag(String tag)                     { tags.add(tag); }
    public void setTags(ArrayList<String> tags)        { this.tags = new ArrayList<>(tags); }

    @Override
    public String toString() {
        return name + " [" + category + "] - " + eventStartDate + " s/d " + eventEndDate;
    }
}
