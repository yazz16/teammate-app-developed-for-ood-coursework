package com.teammate.model.enums;

public enum PersonalityType {
    ANALYTICAL("Analytical", "Logical, data-driven, detail-oriented. Good at: problem-solving, analysis"),
    CREATIVE("Creative", "Imaginative, innovative, artistic. Good at: brainstorming, design"),
    PRAGMATIC("Pragmatic", "Practical, realistic, results-focused. Good at: implementation, execution"),
    SOCIABLE("Sociable", "Outgoing, collaborative, communicative. Good at: communication, teamwork"),
    LEADER("Leader", "Decisive, motivating, strategic. Good at: leadership, decision-making"),
    SUPPORTIVE("Supportive", "Helpful, empathetic, team-focused. Good at: support, collaboration"),
    PERFECTIONIST("Perfectionist", "Detail-oriented, quality-focused. Good at: quality assurance, testing"),
    ADAPTABLE("Adaptable", "Flexible, versatile. Good at: multi-tasking, adjusting to changes");

    private final String name;
    private final String description;

    PersonalityType(String name, String description) {
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

    public static PersonalityType fromString(String name) {
        if (name == null) return null;
        for (PersonalityType type : values()) {
            if (type.name.equalsIgnoreCase(name.trim())) {
                return type;
            }
        }
        return null;
    }
}