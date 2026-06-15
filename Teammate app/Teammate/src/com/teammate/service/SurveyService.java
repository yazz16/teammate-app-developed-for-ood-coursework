package com.teammate.service;

import com.teammate.model.Player;
import com.teammate.model.enums.PersonalityType;
import com.teammate.model.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SurveyService {
    private final Scanner scanner = new Scanner(System.in);
    private final PersonalityClassifier personalityClassifier = new PersonalityClassifier();

    public void administerSurvey(Player player) {
        System.out.println("\n=== TEAM FORMATION SURVEY ===");
        System.out.println("This survey will help us understand your personality,");
        System.out.println("preferences, and skills for optimal team formation.\n");

        // Section 1: Personal Information
        System.out.println("=== SECTION 1: PERSONAL INFORMATION ===");

        if (player.getDob() == null || player.getDob().isEmpty()) {
            System.out.print("Date of Birth (YYYY-MM-DD): ");
            player.setDob(scanner.nextLine().trim());
        }

        if (player.getEmail() == null || player.getEmail().isEmpty()) {
            System.out.print("Email Address: ");
            player.setEmail(scanner.nextLine().trim());
        }

        if (player.getMajor() == null || player.getMajor().isEmpty()) {
            System.out.print("Major/Field of Study: ");
            player.setMajor(scanner.nextLine().trim());
        }

        // Section 2: Personality Assessment
        System.out.println("\n=== SECTION 2: PERSONALITY ASSESSMENT ===");
        System.out.println("For each statement, rate how well it describes you:");
        System.out.println("1 = Strongly Disagree, 2 = Disagree, 3 = Neutral, 4 = Agree, 5 = Strongly Agree\n");

        int[] personalityScores = new int[8];
        String[] statements = {
                "I enjoy working with numbers and data analysis",
                "I often come up with creative solutions to problems",
                "I prefer practical, realistic approaches over theoretical ones",
                "I enjoy socializing and working in group settings",
                "I naturally take leadership roles in group projects",
                "I enjoy helping others and ensuring team harmony",
                "I pay close attention to details and accuracy",
                "I adapt easily to new situations and changing requirements"
        };

        for (int i = 0; i < statements.length; i++) {
            while (true) {
                System.out.println((i + 1) + ". " + statements[i]);
                System.out.print("Your rating (1-5): ");
                String input = scanner.nextLine().trim();

                try {
                    int rating = Integer.parseInt(input);
                    if (rating >= 1 && rating <= 5) {
                        personalityScores[i] = rating;
                        break;
                    } else {
                        System.out.println("Please enter a number between 1 and 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
        }

        // Calculate and set personality type
        PersonalityType personalityType = personalityClassifier.classifyPersonality(personalityScores);
        player.setPersonalityType(personalityType);

        System.out.println("\n=== PERSONALITY RESULT ===");
        System.out.println("Your dominant personality type: " + personalityType.getName());
        System.out.println("Description: " + personalityType.getDescription());

        // Section 3: Skills and Experience
        System.out.println("\n=== SECTION 3: SKILLS AND EXPERIENCE ===");
        System.out.println("Rate your proficiency level (1=Beginner, 3=Intermediate, 5=Expert):\n");

        String[] skills = {
                "Programming/Coding",
                "UI/UX Design",
                "Project Management",
                "Technical Writing",
                "Public Speaking",
                "Research & Analysis",
                "Testing & Quality Assurance",
                "Team Coordination"
        };

        int[] skillScores = new int[skills.length];
        for (int i = 0; i < skills.length; i++) {
            while (true) {
                System.out.println((i + 1) + ". " + skills[i]);
                System.out.print("Proficiency (1-5): ");
                String input = scanner.nextLine().trim();

                try {
                    int score = Integer.parseInt(input);
                    if (score >= 1 && score <= 5) {
                        skillScores[i] = score;
                        break;
                    } else {
                        System.out.println("Please enter a number between 1 and 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
        }

        // Section 4: Role Preferences
        System.out.println("\n=== SECTION 4: ROLE PREFERENCES ===");
        System.out.println("Select the roles you would be interested in (enter numbers separated by commas):\n");

        Role[] allRoles = Role.values();
        for (int i = 0; i < allRoles.length; i++) {
            System.out.println((i + 1) + ". " + allRoles[i].getName());
            System.out.println("   " + allRoles[i].getDescription());
        }

        System.out.print("\nYour choices: ");
        String roleInput = scanner.nextLine().trim();

        List<Role> selectedRoles = new ArrayList<>();
        if (!roleInput.isEmpty()) {
            String[] roleChoices = roleInput.split(",");
            for (String choice : roleChoices) {
                try {
                    int roleIndex = Integer.parseInt(choice.trim()) - 1;
                    if (roleIndex >= 0 && roleIndex < allRoles.length) {
                        selectedRoles.add(allRoles[roleIndex]);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        player.setPreferredRoles(selectedRoles);

        // Section 5: Interests
        System.out.println("\n=== SECTION 5: INTERESTS ===");
        System.out.println("List your interests (comma separated):");
        System.out.println("Examples: AI, Web Development, Mobile Apps, Data Science, Gaming, Design");
        System.out.print("\nYour interests: ");

        String interestInput = scanner.nextLine().trim();
        List<String> interests = new ArrayList<>();

        if (!interestInput.isEmpty()) {
            String[] interestArray = interestInput.split(",");
            for (String interest : interestArray) {
                String trimmed = interest.trim();
                if (!trimmed.isEmpty()) {
                    interests.add(trimmed);
                }
            }
        }
        player.setInterests(interests);

        // Section 6: Work Preferences
        System.out.println("\n=== SECTION 6: WORK PREFERENCES ===");
        System.out.println("1. Do you prefer working independently or in a team?");
        System.out.println("   (1=Mostly independent, 3=Balanced, 5=Mostly team)");
        System.out.print("   Your preference: ");

        int workPreference = getNumberInput(1, 5);

        System.out.println("\n2. How do you handle deadlines?");
        System.out.println("   (1=Prefer flexible deadlines, 3=Manageable, 5=Work well under pressure)");
        System.out.print("   Your response: ");

        int deadlinePreference = getNumberInput(1, 5);

        // Mark survey as completed
        player.setSurveyCompleted(true);

        // Display summary
        System.out.println("\n=== SURVEY COMPLETE ===");
        System.out.println("✓ Personal information collected");
        System.out.println("✓ Personality type identified: " + personalityType.getName());
        System.out.println("✓ " + selectedRoles.size() + " role preferences selected");
        System.out.println("✓ " + interests.size() + " interests recorded");
        System.out.println("\nThank you for completing the survey!");
        System.out.println("Your data will be used for optimal team formation.");
    }

    private int getNumberInput(int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    public void quickSurvey(Player player) {
        System.out.println("\n=== QUICK SURVEY ===");

        // Just collect personality and basic preferences
        System.out.println("Rate yourself on these traits (1-5):\n");

        int[] scores = new int[8];
        String[] traits = {
                "Analytical Thinking",
                "Creativity",
                "Practicality",
                "Sociability",
                "Leadership",
                "Supportiveness",
                "Attention to Detail",
                "Adaptability"
        };

        for (int i = 0; i < traits.length; i++) {
            System.out.print(traits[i] + " (1-5): ");
            scores[i] = getNumberInput(1, 5);
        }

        PersonalityType personality = personalityClassifier.classifyPersonality(scores);
        player.setPersonalityType(personality);

        System.out.println("\nSelect up to 3 roles you prefer:");
        System.out.println("1. Developer    2. Designer    3. Tester");
        System.out.println("4. Documenter   5. Presenter   6. Coordinator");
        System.out.print("Your choices (e.g., 1,3,5): ");

        String input = scanner.nextLine().trim();
        List<Role> roles = new ArrayList<>();

        if (!input.isEmpty()) {
            String[] choices = input.split(",");
            Role[] allRoles = Role.values();
            for (String choice : choices) {
                try {
                    int index = Integer.parseInt(choice.trim()) - 1;
                    if (index >= 0 && index < 6) { // Only first 6 roles for quick survey
                        roles.add(allRoles[index]);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid
                }
            }
        }

        player.setPreferredRoles(roles);
        player.setSurveyCompleted(true);

        System.out.println("\n✓ Quick survey completed!");
        System.out.println("Personality: " + personality.getName());
        System.out.println("Roles selected: " + roles.size());
    }
}