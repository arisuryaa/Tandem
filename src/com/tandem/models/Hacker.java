package com.tandem.models;

import java.util.ArrayList;

public class Hacker extends User {

    private ArrayList<String> techStack;
    private ArrayList<String> programmingLanguages;

    public Hacker(String userId, String name, String nim, String email,
                  String hashedPassword, String faculty, String major, String contactNumber) {
        super(userId, name, nim, email, hashedPassword, faculty, major, contactNumber);
        this.techStack = new ArrayList<>();
        this.programmingLanguages = new ArrayList<>();
    }

    @Override
    public String getRole() { return "Hacker"; }

    @Override
    public String getSkillSummary() {
        if (techStack.isEmpty()) return "No tech stack specified";
        return "Tech: " + String.join(", ", techStack);
    }

    public ArrayList<String> getTechStack()            { return new ArrayList<>(techStack); }
    public ArrayList<String> getProgrammingLanguages() { return new ArrayList<>(programmingLanguages); }

    public void addTechStack(String tech)              { techStack.add(tech); }
    public void addProgrammingLanguage(String lang)    { programmingLanguages.add(lang); }
    public void setTechStack(ArrayList<String> list)   { this.techStack = list; }
    public void setProgrammingLanguages(ArrayList<String> list) { this.programmingLanguages = list; }
}
