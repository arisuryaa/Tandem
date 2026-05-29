package com.tandem.models;

import java.util.ArrayList;

public class Hustler extends User {

    private ArrayList<String> businessSkills;
    private String linkedinProfile;

    public Hustler(String userId, String name, String nim, String email,
                   String hashedPassword, String faculty, String major, String contactNumber) {
        super(userId, name, nim, email, hashedPassword, faculty, major, contactNumber);
        this.businessSkills = new ArrayList<>();
        this.linkedinProfile = "";
    }

    @Override
    public String getRole() { return "Hustler"; }

    @Override
    public String getSkillSummary() {
        if (businessSkills.isEmpty()) return "No business skills specified";
        return "Business: " + String.join(", ", businessSkills);
    }

    public ArrayList<String> getBusinessSkills()           { return new ArrayList<>(businessSkills); }
    public String getLinkedinProfile()                     { return linkedinProfile; }

    public void addBusinessSkill(String skill)             { businessSkills.add(skill); }
    public void setBusinessSkills(ArrayList<String> list)  { this.businessSkills = list; }
    public void setLinkedinProfile(String link)            { this.linkedinProfile = link; }
}
