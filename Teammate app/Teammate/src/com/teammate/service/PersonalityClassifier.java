package com.teammate.service;

import com.teammate.model.enums.PersonalityType;

public class PersonalityClassifier {

    public PersonalityType classifyPersonality(int[] scores) {
        // Score indices: 0=Analytical, 1=Creative, 2=Pragmatic,
        // 3=Sociable, 4=Leader, 5=Supportive, 6=Perfectionist, 7=Adaptable

        if (scores.length != 8) {
            throw new IllegalArgumentException("Expected 8 personality scores");
        }

        int maxScore = -1;
        int maxIndex = -1;

        // Find the highest score
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxIndex = i;
            }
        }

        // Check for ties and handle them
        int tieCount = 0;
        for (int score : scores) {
            if (score == maxScore) {
                tieCount++;
            }
        }

        if (tieCount > 1) {
            // Handle ties based on common combinations
            return handleTie(scores, maxScore);
        }

        // Return based on index
        switch (maxIndex) {
            case 0: return PersonalityType.ANALYTICAL;
            case 1: return PersonalityType.CREATIVE;
            case 2: return PersonalityType.PRAGMATIC;
            case 3: return PersonalityType.SOCIABLE;
            case 4: return PersonalityType.LEADER;
            case 5: return PersonalityType.SUPPORTIVE;
            case 6: return PersonalityType.PERFECTIONIST;
            case 7: return PersonalityType.ADAPTABLE;
            default: return PersonalityType.ADAPTABLE;
        }
    }

    private PersonalityType handleTie(int[] scores, int maxScore) {
        // Common combinations and their resulting types
        boolean analyticalCreative = scores[0] == maxScore && scores[1] == maxScore;
        boolean leaderSociable = scores[3] == maxScore && scores[4] == maxScore;
        boolean pragmaticPerfectionist = scores[2] == maxScore && scores[6] == maxScore;
        boolean supportiveAdaptable = scores[5] == maxScore && scores[7] == maxScore;

        if (analyticalCreative) {
            return PersonalityType.ANALYTICAL; // Analytical takes precedence
        } else if (leaderSociable) {
            return PersonalityType.LEADER; // Leader takes precedence
        } else if (pragmaticPerfectionist) {
            return PersonalityType.PRAGMATIC; // Pragmatic takes precedence
        } else if (supportiveAdaptable) {
            return PersonalityType.SUPPORTIVE; // Supportive takes precedence
        } else {
            // Default to the first one with max score
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] == maxScore) {
                    switch (i) {
                        case 0: return PersonalityType.ANALYTICAL;
                        case 1: return PersonalityType.CREATIVE;
                        case 2: return PersonalityType.PRAGMATIC;
                        case 3: return PersonalityType.SOCIABLE;
                        case 4: return PersonalityType.LEADER;
                        case 5: return PersonalityType.SUPPORTIVE;
                        case 6: return PersonalityType.PERFECTIONIST;
                        case 7: return PersonalityType.ADAPTABLE;
                        default: return PersonalityType.ADAPTABLE;
                    }
                }
            }
        }
        return PersonalityType.ADAPTABLE;
    }

    public String getPersonalityDescription(PersonalityType type) {
        if (type == null) return "Not assessed";
        return type.getDescription();
    }

    public String getSuggestedRoles(PersonalityType type) {
        if (type == null) return "Complete survey first";

        switch (type) {
            case ANALYTICAL:
                return "Developer, Data Analyst, Researcher, Tester";
            case CREATIVE:
                return "Designer, UI/UX Designer, Presenter, Frontend Developer";
            case PRAGMATIC:
                return "Backend Developer, Project Manager, Coordinator";
            case SOCIABLE:
                return "Presenter, Coordinator, Project Manager, Frontend Developer";
            case LEADER:
                return "Project Manager, Coordinator, Team Lead";
            case SUPPORTIVE:
                return "Documenter, Tester, Quality Assurance, Coordinator";
            case PERFECTIONIST:
                return "Tester, Quality Assurance, Documenter, Backend Developer";
            case ADAPTABLE:
                return "Any role, particularly good in changing environments";
            default:
                return "Various roles based on interests";
        }
    }
}