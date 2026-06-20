package com.tandem.models;

import com.tandem.utils.PasswordUtils;
import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;
    private String nim;
    private String email;
    private String password;
    private String faculty;
    private String major;
    private String contactNumber;
    private String bio;
    private String cvLink;
    private String portfolioLink;

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
        this.bio = "";
        this.cvLink = "";
        this.portfolioLink = "";
    }

    public boolean verifyPassword(String rawPassword) {
        return this.password.equals(PasswordUtils.hash(rawPassword));
    }

    public void changePassword(String rawPassword) {
        this.password = PasswordUtils.hash(rawPassword);
    }

    public String getUserId()        { return userId; }
    public String getName()          { return name; }
    public String getNim()           { return nim; }
    public String getEmail()         { return email; }
    public String getFaculty()       { return faculty; }
    public String getMajor()         { return major; }
    public String getContactNumber() { return contactNumber; }
    public String getBio()           { return bio; }
    public String getCvLink()        { return cvLink; }
    public String getPortfolioLink() { return portfolioLink; }

    public void setName(String name)                   { this.name = name; }
    public void setFaculty(String faculty)             { this.faculty = faculty; }
    public void setMajor(String major)                 { this.major = major; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setBio(String bio)                     { this.bio = bio; }
    public void setCvLink(String cvLink)               { this.cvLink = cvLink; }
    public void setPortfolioLink(String link)          { this.portfolioLink = link; }

    @Override
    public String toString() {
        return name + " [" + faculty + "/" + major + "] - " + nim;
    }
}
