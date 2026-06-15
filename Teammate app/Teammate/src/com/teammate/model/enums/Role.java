package com.teammate.model.enums;

public enum Role {
    DEVELOPER("Developer", "Writes code and builds features"),
    DESIGNER("Designer", "Creates UI/UX designs"),
    TESTER("Tester", "Tests and ensures quality"),
    DOCUMENTER("Documenter", "Writes documentation"),
    PRESENTER("Presenter", "Presents work to stakeholders"),
    COORDINATOR("Coordinator", "Coordinates team activities"),
    RESEARCHER("Researcher", "Conducts research and analysis"),
    PROJECT_MANAGER("Project Manager", "Manages project timelines and resources"),
    UI_UX_DESIGNER("UI/UX Designer", "Designs user interfaces and experiences"),
    BACKEND_DEVELOPER("Backend Developer", "Works on server-side logic"),
    FRONTEND_DEVELOPER("Frontend Developer", "Works on client-side interfaces"),
    DATA_ANALYST("Data Analyst", "Analyzes and interprets data"),
    QUALITY_ASSURANCE("Quality Assurance", "Ensures product quality");

    private final String name;
    private final String description;

    Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " - " + description;
    }

    public static Role fromString(String name) {
        if (name == null) return null;
        for (Role role : values()) {
            if (role.name.equalsIgnoreCase(name.trim())) {
                return role;
            }
        }
        return null;
    }
}