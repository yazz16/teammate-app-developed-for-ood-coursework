package com.teammate.service;

import com.teammate.model.Player;
import com.teammate.model.Team;
import com.teammate.model.enums.Role;

import java.util.List;
import java.util.Scanner;

public class PlayerService {
    private final Scanner scanner = new Scanner(System.in);

    public void showMenu(Player player) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== PLAYER MENU ===");
            System.out.println("Welcome, " + player.getName() + "!");
            System.out.println("1. View My Profile");
            System.out.println("2. Complete/Update Survey");
            System.out.println("3. Edit Personal Information");
            System.out.println("4. View Assigned Team");
            System.out.println("5. View Team Members");
            System.out.println("6. View Personality Assessment");
            System.out.println("7. View Suggested Roles");
            System.out.println("0. Logout");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewProfile(player);
                    break;
                case "2":
                    completeSurvey(player);
                    break;
                case "3":
                    editPersonalInfo(player);
                    break;
                case "4":
                    viewAssignedTeam(player);
                    break;
                case "5":
                    viewTeamMembers(player);
                    break;
                case "6":
                    viewPersonalityAssessment(player);
                    break;
                case "7":
                    viewSuggestedRoles(player);
                    break;
                case "0":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void viewProfile(Player player) {
        System.out.println("\n=== MY PROFILE ===");
        System.out.println("ID: " + player.getId());
        System.out.println("Name: " + player.getName());
        System.out.println("Date of Birth: " +
                (player.getDob() != null ? player.getDob() : "Not set"));
        System.out.println("Email: " +
                (player.getEmail() != null ? player.getEmail() : "Not set"));
        System.out.println("Major: " +
                (player.getMajor() != null ? player.getMajor() : "Not set"));

        System.out.println("\nSurvey Status: " +
                (player.isSurveyCompleted() ? "COMPLETED" : "NOT COMPLETED"));

        if (player.isSurveyCompleted()) {
            System.out.println("Personality Type: " +
                    (player.getPersonalityType() != null ?
                            player.getPersonalityType().getName() : "Not assessed"));

            System.out.print("Preferred Roles: ");
            if (player.getPreferredRoles().isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < player.getPreferredRoles().size(); i++) {
                    if (i > 0) System.out.print(", ");
                    System.out.print(player.getPreferredRoles().get(i).getName());
                }
                System.out.println();
            }

            System.out.print("Interests: ");
            if (player.getInterests().isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < player.getInterests().size(); i++) {
                    if (i > 0) System.out.print(", ");
                    System.out.print(player.getInterests().get(i));
                }
                System.out.println();
            }
        }

        System.out.println("\nTeam Assignment: " +
                (player.getAssignedTeam() != null ?
                        player.getAssignedTeam().getName() : "Not assigned to any team"));
    }

    private void completeSurvey(Player player) {
        if (player.isSurveyCompleted()) {
            System.out.print("\nYou have already completed the survey. Update it? (y/n): ");
            String choice = scanner.nextLine().toLowerCase();
            if (!choice.equals("y")) {
                return;
            }
        }

        System.out.println("\n=== PERSONALITY SURVEY ===");

        // Collect basic information if not already set
        if (player.getDob() == null) {
            System.out.print("Enter your Date of Birth (YYYY-MM-DD): ");
            player.setDob(scanner.nextLine().trim());
        }

        if (player.getEmail() == null) {
            System.out.print("Enter your Email: ");
            player.setEmail(scanner.nextLine().trim());
        }

        if (player.getMajor() == null) {
            System.out.print("Enter your Major: ");
            player.setMajor(scanner.nextLine().trim());
        }

        // Personality Assessment
        System.out.println("\n=== PERSONALITY ASSESSMENT ===");
        System.out.println("Rate yourself on a scale of 1-5 for each trait (1=Strongly Disagree, 5=Strongly Agree):");

        int[] scores = new int[8];
        String[] questions = {
                "I enjoy analyzing data and solving logical problems",
                "I'm creative and enjoy coming up with new ideas",
                "I focus on practical solutions and getting results",
                "I'm outgoing and enjoy working with others",
                "I'm comfortable taking charge and making decisions",
                "I enjoy helping others and being a team player",
                "I pay close attention to details and quality",
                "I adapt easily to new situations and changes"
        };

        for (int i = 0; i < questions.length; i++) {
            while (true) {
                System.out.print("\n" + (i + 1) + ". " + questions[i] + " [1-5]: ");
                try {
                    int score = Integer.parseInt(scanner.nextLine().trim());
                    if (score >= 1 && score <= 5) {
                        scores[i] = score;
                        break;
                    } else {
                        System.out.println("Please enter a number between 1 and 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
        }

        // Classify personality
        PersonalityClassifier classifier = new PersonalityClassifier();
        com.teammate.model.enums.PersonalityType personality = classifier.classifyPersonality(scores);
        player.setPersonalityType(personality);

        System.out.println("\n✓ Your personality type is: " + personality.getName());
        System.out.println("Description: " + personality.getDescription());

        // Preferred Roles
        selectPreferredRoles(player);

        // Interests
        selectInterests(player);

        player.setSurveyCompleted(true);
        System.out.println("\n✓ Survey completed successfully!");
    }

    private void selectPreferredRoles(Player player) {
        System.out.println("\n=== PREFERRED ROLES ===");
        System.out.println("Select roles you're interested in (enter numbers separated by commas):");

        Role[] allRoles = Role.values();
        for (int i = 0; i < allRoles.length; i++) {
            System.out.println((i + 1) + ". " + allRoles[i].getName() +
                    " - " + allRoles[i].getDescription());
        }

        System.out.print("\nEnter your choices (e.g., 1,3,5 or press Enter to skip): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("No roles selected.");
            return;
        }

        String[] choices = input.split(",");
        java.util.List<Role> selectedRoles = new java.util.ArrayList<>();

        for (String choice : choices) {
            try {
                int index = Integer.parseInt(choice.trim()) - 1;
                if (index >= 0 && index < allRoles.length) {
                    selectedRoles.add(allRoles[index]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Skipping invalid entry: " + choice);
            }
        }

        player.setPreferredRoles(selectedRoles);
        System.out.println("✓ Selected " + selectedRoles.size() + " role(s).");
    }

    private void selectInterests(Player player) {
        System.out.println("\n=== INTERESTS ===");
        System.out.println("Enter your interests (comma separated, e.g., AI,Web Development,Gaming,Data Science): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("No interests entered.");
            return;
        }

        String[] interestArray = input.split(",");
        java.util.List<String> interests = new java.util.ArrayList<>();

        for (String interest : interestArray) {
            String trimmed = interest.trim();
            if (!trimmed.isEmpty()) {
                interests.add(trimmed);
            }
        }

        player.setInterests(interests);
        System.out.println("✓ Added " + interests.size() + " interest(s).");
    }

    private void editPersonalInfo(Player player) {
        System.out.println("\n=== EDIT PERSONAL INFORMATION ===");

        System.out.print("Name (current: " + player.getName() + "): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            player.setName(newName);
        }

        System.out.print("Email (current: " +
                (player.getEmail() != null ? player.getEmail() : "Not set") + "): ");
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.isEmpty()) {
            player.setEmail(newEmail);
        }

        System.out.print("Major (current: " +
                (player.getMajor() != null ? player.getMajor() : "Not set") + "): ");
        String newMajor = scanner.nextLine().trim();
        if (!newMajor.isEmpty()) {
            player.setMajor(newMajor);
        }

        System.out.println("✓ Personal information updated!");
    }

    private void viewAssignedTeam(Player player) {
        Team team = player.getAssignedTeam();
        if (team == null) {
            System.out.println("\nYou are not assigned to any team yet.");
            System.out.println("Teams will be assigned by the organizer after surveys are completed.");
        } else {
            System.out.println("\n=== MY ASSIGNED TEAM ===");
            System.out.println("Team Name: " + team.getName());
            System.out.println("Team ID: " + team.getId());
            System.out.println("Team Size: " + team.getSize() + " members");

            if (team.getTeamLeader() != null) {
                System.out.println("Team Leader: " + team.getTeamLeader().getName());
                if (team.getTeamLeader().equals(player)) {
                    System.out.println("(That's you!)");
                }
            }
        }
    }

    private void viewTeamMembers(Player player) {
        Team team = player.getAssignedTeam();
        if (team == null) {
            System.out.println("\nYou are not assigned to any team yet.");
            return;
        }

        System.out.println("\n=== MY TEAM MEMBERS ===");
        List<Player> members = team.getMembers();

        if (members.size() == 1) {
            System.out.println("You are the only member in this team.");
            return;
        }

        for (int i = 0; i < members.size(); i++) {
            Player member = members.get(i);
            System.out.print((i + 1) + ". " + member.getName());

            if (member.equals(player)) {
                System.out.print(" (You)");
            }

            if (team.getTeamLeader() != null && member.equals(team.getTeamLeader())) {
                System.out.print(" [Team Leader]");
            }

            if (member.getPersonalityType() != null) {
                System.out.print(" - " + member.getPersonalityType().getName());
            }
            System.out.println();
        }
    }

    private void viewPersonalityAssessment(Player player) {
        if (!player.isSurveyCompleted()) {
            System.out.println("\nYou haven't completed the personality survey yet.");
            System.out.println("Please complete the survey first to see your assessment.");
            return;
        }

        if (player.getPersonalityType() == null) {
            System.out.println("\nPersonality assessment not available.");
            return;
        }

        System.out.println("\n=== PERSONALITY ASSESSMENT ===");
        System.out.println("Your Personality Type: " + player.getPersonalityType().getName());
        System.out.println("\nDescription:");
        System.out.println(player.getPersonalityType().getDescription());

        System.out.println("\nStrengths:");
        switch (player.getPersonalityType()) {
            case ANALYTICAL:
                System.out.println("- Excellent problem-solving skills");
                System.out.println("- Strong logical thinking");
                System.out.println("- Good with data and analysis");
                break;
            case CREATIVE:
                System.out.println("- Innovative thinking");
                System.out.println("- Strong design sense");
                System.out.println("- Good at brainstorming");
                break;
            case PRAGMATIC:
                System.out.println("- Practical approach");
                System.out.println("- Results-oriented");
                System.out.println("- Good at implementation");
                break;
            case SOCIABLE:
                System.out.println("- Excellent communication");
                System.out.println("- Team collaboration");
                System.out.println("- Networking skills");
                break;
            case LEADER:
                System.out.println("- Decision-making ability");
                System.out.println("- Motivational skills");
                System.out.println("- Strategic thinking");
                break;
            case SUPPORTIVE:
                System.out.println("- Helpful and empathetic");
                System.out.println("- Team-focused");
                System.out.println("- Good at mentoring");
                break;
            case PERFECTIONIST:
                System.out.println("- Attention to detail");
                System.out.println("- Quality-focused");
                System.out.println("- Thorough work");
                break;
            case ADAPTABLE:
                System.out.println("- Flexible and versatile");
                System.out.println("- Quick to learn");
                System.out.println("- Good in changing environments");
                break;
        }
    }

    private void viewSuggestedRoles(Player player) {
        if (!player.isSurveyCompleted() || player.getPersonalityType() == null) {
            System.out.println("\nComplete the personality survey first to see suggested roles.");
            return;
        }

        System.out.println("\n=== SUGGESTED ROLES ===");
        System.out.println("Based on your " + player.getPersonalityType().getName() + " personality:");

        PersonalityClassifier classifier = new PersonalityClassifier();
        String suggestedRoles = classifier.getSuggestedRoles(player.getPersonalityType());
        System.out.println(suggestedRoles);

        System.out.println("\nYour Selected Roles:");
        if (player.getPreferredRoles().isEmpty()) {
            System.out.println("No roles selected yet.");
        } else {
            for (Role role : player.getPreferredRoles()) {
                System.out.println("- " + role.getName() + ": " + role.getDescription());
            }
        }
    }
}