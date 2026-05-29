package com.tandem.models;

import java.util.ArrayList;

public class Hipster extends User {

    private ArrayList<String> designTools;
    private String portfolioLink;

    public Hipster(String userId, String name, String nim, String email,
                   String hashedPassword, String faculty, String major, String contactNumber) {
        super(userId, name, nim, email, hashedPassword, faculty, major, contactNumber);
        this.designTools = new ArrayList<>();
        this.portfolioLink = "";
    }

    @Override
    public String getRole() { return "Hipster"; }

    @Override
    public String getSkillSummary() {
        if (designTools.isEmpty()) return "No design tools specified";
        return "Design: " + String.join(", ", designTools);
    }

    public ArrayList<String> getDesignTools()          { return new ArrayList<>(designTools); }
    public String getPortfolioLink()                   { return portfolioLink; }

    public void addDesignTool(String tool)             { designTools.add(tool); }
    public void setDesignTools(ArrayList<String> list) { this.designTools = list; }
    public void setPortfolioLink(String link)          { this.portfolioLink = link; }
}
