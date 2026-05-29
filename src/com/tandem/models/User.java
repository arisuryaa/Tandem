package com.tandem.models;

import com.tandem.utils.PasswordUtils;
import java.io.Serializable;

public abstract class User implements Serializable {

    private String userId;
    private String name;
    private String nim;
    private String email;
    private String password; // stored as SHA-256 hash, never exposed raw
    private String faculty;
    private String major;
    private String contactNumber;

    public User(String userId, String name, String nim, String email,
                String hashedPassword, String faculty, String major, String contactNumber) {
        this.userId = userId;
        this.name = name;
        this.nim = nim;
        this.email = email;
        this.password = hashedPassword;
        this.faculty = faculty;
        this.major = major;
        this.contactNumber = contactNumber;
    }

    public abstract String getRole();

    public abstract String getSkillSummary();

    public boolean verifyPassword(String rawPassword) {
        return this.password.equals(PasswordUtils.hash(rawPassword));
    }

    public void changePassword(String rawPassword) {
        this.password = PasswordUtils.hash(rawPassword);
    }

    // Getters
    public String getUserId()        { return userId; }
    public String getName()          { return name; }
    public String getNim()           { return nim; }
    public String getEmail()         { return email; }
    public String getFaculty()       { return faculty; }
    public String getMajor()         { return major; }
    public String getContactNumber() { return contactNumber; }

    // Setters (no plain getter for password)
    public void setName(String name)                   { this.name = name; }
    public void setFaculty(String faculty)             { this.faculty = faculty; }
    public void setMajor(String major)                 { this.major = major; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String toString() {
        return name + " [" + getRole() + "] - " + nim;
    }
}
