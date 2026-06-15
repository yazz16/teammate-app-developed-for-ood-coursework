package com.teammate.service;

import com.teammate.model.Player;
import com.teammate.model.Team;

import java.util.List;
import java.util.Scanner;

public class OrganizerService {
    private final Scanner scanner = new Scanner(System.in);
    private final TeamService teamService;
    private final CsvManager csvManager;
    private static final int DEFAULT_TEAM_SIZE = 4;

    public OrganizerService(TeamService teamService, CsvManager csvManager) {
        this.teamService = teamService;
        this.csvManager = csvManager;
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== ORGANIZER MENU ===");
            System.out.println("1. View All Players");
            System.out.println("2. View Players by Survey Status");
            System.out.println("3. View Player Details");
            System.out.println("4. View All Teams");
            System.out.println("5. Auto-Form Teams");
            System.out.println("6. View Team Analysis");
            System.out.println("7. Manually Adjust Teams");
            System.out.println("8. Assign Player to Team");
            System.out.println("9. Move Player Between Teams");
            System.out.println("10. Save All Data");
            System.out.println("11. Load All Data");
            System.out.println("0. Logout");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAllPlayers();
                    break;
                case "2":
                    viewPlayersBySurveyStatus();
                    break;
                case "3":
                    viewPlayerDetails();
                    break;
                case "4":
                    teamService.viewAllTeams();
                    break;
                case "5":
                    autoFormTeams();
                    break;
                case "6":
                    viewTeamAnalysis();
                    break;
                case "7":
                    manuallyAdjustTeams();
                    break;
                case "8":
                    assignPlayerToTeam();
                    break;
                case "9":
                    movePlayerBetweenTeams();
                    break;
                case "10":
                    saveAllData();
                    break;
                case "11":
                    loadAllData();
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

    private void viewAllPlayers() {
        System.out.println("\n=== ALL PLAYERS ===");
        List<Player> players = teamService.getAllPlayers();

        if (players.isEmpty()) {
            System.out.println("No players registered yet.");
            return;
        }

        System.out.printf("%-10s %-20s %-15s %-12s %-20s\n",
                "ID", "Name", "Survey", "Personality", "Team");
        System.out.println("-".repeat(80));

        for (Player player : players) {
            String surveyStatus = player.isSurveyCompleted() ? "Completed" : "Pending";
            String personality = player.getPersonalityType() != null ?
                    player.getPersonalityType().getName() : "N/A";
            String teamName = player.getAssignedTeam() != null ?
                    player.getAssignedTeam().getName() : "Unassigned";

            System.out.printf("%-10s %-20s %-15s %-12s %-20s\n",
                    player.getId(), player.getName(), surveyStatus, personality, teamName);
        }

        System.out.println("\nTotal: " + players.size() + " players");
    }

    private void viewPlayersBySurveyStatus() {
        System.out.println("\n=== PLAYERS BY SURVEY STATUS ===");

        System.out.println("1. View Completed Surveys");
        System.out.println("2. View Pending Surveys");
        System.out.print("Select option: ");

        String choice = scanner.nextLine();
        List<Player> players = teamService.getAllPlayers();

        if (choice.equals("1")) {
            System.out.println("\n=== PLAYERS WITH COMPLETED SURVEYS ===");
            int count = 0;
            for (Player player : players) {
                if (player.isSurveyCompleted()) {
                    System.out.println(++count + ". " + player);
                }
            }
            if (count == 0) {
                System.out.println("No players have completed the survey yet.");
            }
        } else if (choice.equals("2")) {
            System.out.println("\n=== PLAYERS WITH PENDING SURVEYS ===");
            int count = 0;
            for (Player player : players) {
                if (!player.isSurveyCompleted()) {
                    System.out.println(++count + ". " + player.getName() + " (ID: " + player.getId() + ")");
                }
            }
            if (count == 0) {
                System.out.println("All players have completed the survey!");
            }
        }
    }

    private void viewPlayerDetails() {
        System.out.print("\nEnter Player ID to view details: ");
        String playerId = scanner.nextLine().trim();

        Player player = teamService.findPlayerById(playerId);
        if (player == null) {
            System.out.println("Player not found.");
            return;
        }

        System.out.println("\n=== PLAYER DETAILS ===");
        System.out.println("ID: " + player.getId());
        System.out.println("Name: " + player.getName());
        System.out.println("Date of Birth: " +
                (player.getDob() != null ? player.getDob() : "Not set"));
        System.out.println("Email: " +
                (player.getEmail() != null ? player.getEmail() : "Not set"));
        System.out.println("Major: " +
                (player.getMajor() != null ? player.getMajor() : "Not set"));

        System.out.println("\nSurvey Status: " +
                (player.isSurveyCompleted() ? "COMPLETED" : "PENDING"));

        if (player.isSurveyCompleted()) {
            System.out.println("Personality Type: " +
                    (player.getPersonalityType() != null ?
                            player.getPersonalityType().getName() + " - " +
                                    player.getPersonalityType().getDescription() : "Not assessed"));

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

    private void autoFormTeams() {
        System.out.print("\nEnter team size (3-6, default 4): ");
        String sizeInput = scanner.nextLine().trim();

        int teamSize = DEFAULT_TEAM_SIZE;
        if (!sizeInput.isEmpty()) {
            try {
                teamSize = Integer.parseInt(sizeInput);
                if (teamSize < 3 || teamSize > 6) {
                    System.out.println("Team size must be between 3 and 6. Using default (4).");
                    teamSize = DEFAULT_TEAM_SIZE;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Using default team size (4).");
            }
        }

        System.out.println("\nAuto-forming teams with size " + teamSize + "...");
        teamService.autoFormTeams(teamSize);
        System.out.println("✓ Teams formed successfully!");
    }

    private void viewTeamAnalysis() {
        List<Team> teams = teamService.getAllTeams();

        if (teams.isEmpty()) {
            System.out.println("No teams formed yet.");
            return;
        }

        System.out.println("\n=== TEAM ANALYSIS ===");
        for (int i = 0; i < teams.size(); i++) {
            System.out.println((i + 1) + ". " + teams.get(i).getName());
        }

        System.out.print("\nSelect team number (or 0 for all): ");
        String choice = scanner.nextLine();

        if (choice.equals("0")) {
            for (Team team : teams) {
                System.out.println("\n" + teamService.getTeamAnalysis(team));
                System.out.println("-".repeat(50));
            }
        } else {
            try {
                int teamNum = Integer.parseInt(choice) - 1;
                if (teamNum >= 0 && teamNum < teams.size()) {
                    System.out.println("\n" + teamService.getTeamAnalysis(teams.get(teamNum)));
                } else {
                    System.out.println("Invalid team number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private void manuallyAdjustTeams() {
        System.out.println("\n=== MANUAL TEAM ADJUSTMENT ===");
        teamService.viewAllTeams();

        if (teamService.getAllTeams().isEmpty()) {
            System.out.println("No teams to adjust. Please create teams first.");
            return;
        }

        System.out.print("\nEnter player ID to move: ");
        String playerId = scanner.nextLine().trim();

        Player player = teamService.findPlayerById(playerId);
        if (player == null) {
            System.out.println("Player not found.");
            return;
        }

        System.out.println("Current team: " +
                (player.getAssignedTeam() != null ? player.getAssignedTeam().getName() : "None"));

        System.out.print("Enter target team name or ID: ");
        String teamIdentifier = scanner.nextLine().trim();

        boolean success = teamService.movePlayerToTeam(playerId, teamIdentifier);
        if (success) {
            System.out.println("✓ Player moved successfully!");
        } else {
            System.out.println("✗ Failed to move player. Team not found or player already in team.");
        }
    }

    private void assignPlayerToTeam() {
        System.out.println("\n=== ASSIGN PLAYER TO TEAM ===");

        System.out.print("Enter player ID: ");
        String playerId = scanner.nextLine().trim();

        Player player = teamService.findPlayerById(playerId);
        if (player == null) {
            System.out.println("Player not found.");
            return;
        }

        System.out.print("Enter team name (new or existing): ");
        String teamName = scanner.nextLine().trim();

        if (teamName.isEmpty()) {
            System.out.println("Team name cannot be empty.");
            return;
        }

        System.out.print("Enter team ID (or leave empty for auto-generated): ");
        String teamId = scanner.nextLine().trim();

        if (teamId.isEmpty()) {
            teamId = "T" + String.format("%03d", teamService.getAllTeams().size() + 1);
        }

        teamService.createOrAssignTeam(teamId, teamName, player);
        System.out.println("✓ Player assigned to team!");
    }

    private void movePlayerBetweenTeams() {
        System.out.println("\n=== MOVE PLAYER BETWEEN TEAMS ===");
        teamService.viewAllTeams();

        List<Team> teams = teamService.getAllTeams();
        if (teams.size() < 2) {
            System.out.println("Need at least 2 teams to move players between them.");
            return;
        }

        System.out.print("\nEnter source team name or ID: ");
        String sourceTeam = scanner.nextLine().trim();

        System.out.print("Enter target team name or ID: ");
        String targetTeam = scanner.nextLine().trim();

        System.out.print("Enter player ID to move: ");
        String playerId = scanner.nextLine().trim();

        boolean success = teamService.movePlayerBetweenTeams(playerId, sourceTeam, targetTeam);
        if (success) {
            System.out.println("✓ Player moved successfully!");
        } else {
            System.out.println("✗ Failed to move player. Please check team names and player ID.");
        }
    }

    private void saveAllData() {
        System.out.println("\n=== SAVE ALL DATA ===");
        csvManager.savePlayers(teamService.getAllPlayers());
        csvManager.saveTeams(teamService.getAllTeams());
        System.out.println("✓ All data saved successfully!");
    }

    private void loadAllData() {
        System.out.println("\n=== LOAD ALL DATA ===");
        List<Player> players = csvManager.loadPlayers();
        teamService.setPlayers(players);

        List<Team> teams = csvManager.loadTeams(players);
        teamService.setTeams(teams);

        System.out.println("✓ All data loaded successfully!");
        System.out.println("Loaded " + players.size() + " players and " + teams.size() + " teams.");
    }
}