package com.teammate.model;

import com.teammate.model.enums.PersonalityType;
import com.teammate.model.enums.Role;
import java.util.ArrayList;
import java.util.List;

public class Player extends User {
    private PersonalityType personalityType;
    private List<Role> preferredRoles;
    private List<String> interests;
    private Team assignedTeam;
    private String dob;
    private String email;
    private String major;
    private boolean surveyCompleted;

    public Player(String id, String name) {
        super(id, name);
        this.preferredRoles = new ArrayList<>();
        this.interests = new ArrayList<>();
        this.surveyCompleted = false;
    }

    // Getters and Setters
    public PersonalityType getPersonalityType() {
        return personalityType;
    }

    public void setPersonalityType(PersonalityType personalityType) {
        this.personalityType = personalityType;
    }

    public List<Role> getPreferredRoles() {
        return preferredRoles;
    }

    public void setPreferredRoles(List<Role> preferredRoles) {
        this.preferredRoles = preferredRoles;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public Team getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(Team team) {
        this.assignedTeam = team;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public boolean isSurveyCompleted() {
        return surveyCompleted;
    }

    public void setSurveyCompleted(boolean surveyCompleted) {
        this.surveyCompleted = surveyCompleted;
    }

    public void addPreferredRole(Role role) {
        if (!preferredRoles.contains(role)) {
            preferredRoles.add(role);
        }
    }

    public void addInterest(String interest) {
        if (!interests.contains(interest)) {
            interests.add(interest);
        }
    }

    @Override
    public String getMenuHeader() {
        return "=== PLAYER DASHBOARD ===";
    }

    @Override
    public UserType getUserType() {
        return UserType.PLAYER;
    }

    @Override
    public String toString() {
        return getName() + " (ID: " + getId() +
                (personalityType != null ? ", Personality: " + personalityType.getName() : "") +
                (assignedTeam != null ? ", Team: " + assignedTeam.getName() : "") + ")";
    }
}