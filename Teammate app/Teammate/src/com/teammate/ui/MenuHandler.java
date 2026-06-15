package com.teammate.ui;

import com.teammate.auth.AuthManager;
import com.teammate.auth.UserType;
import com.teammate.model.Organizer;
import com.teammate.model.Player;
import com.teammate.service.*;

import java.util.Scanner;

public class MenuHandler {
    private final Scanner scanner = new Scanner(System.in);
    private final AuthManager authManager;
    private final OrganizerService organizerService;
    private final PlayerService playerService;
    private final TeamService teamService;
    private final CsvManager csvManager;

    public MenuHandler() {
        this.csvManager = new CsvManager();
        this.teamService = new TeamService();
        this.authManager = new AuthManager(csvManager);
        this.organizerService = new OrganizerService(teamService, csvManager);
        this.playerService = new PlayerService();

        // Load existing data
        loadInitialData();
    }

    private void loadInitialData() {
        System.out.println("Loading data from CSV files...");
        teamService.setPlayers(csvManager.loadPlayers());
        teamService.setTeams(csvManager.loadTeams(teamService.getAllPlayers()));
        System.out.println("Data loaded successfully!");
    }

    public void start() {
        System.out.println("\n========================================");
        System.out.println("     WELCOME TO TEAM FORMATION SYSTEM    ");
        System.out.println("========================================\n");

        boolean systemRunning = true;

        while (systemRunning) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Organizer Login");
            System.out.println("2. Player Login");
            System.out.println("3. View System Information");
            System.out.println("0. Exit System");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleOrganizerLogin();
                    break;
                case "2":
                    handlePlayerLogin();
                    break;
                case "3":
                    showSystemInfo();
                    break;
                case "0":
                    systemRunning = false;
                    saveBeforeExit();
                    System.out.println("\n========================================");
                    System.out.println("   Thank you for using Team Formation   ");
                    System.out.println("              Goodbye!                  ");
                    System.out.println("========================================");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleOrganizerLogin() {
        Object user = authManager.authenticate(UserType.ORGANIZER);
        if (user instanceof Organizer) {
            System.out.println("\n" + ((Organizer) user).getMenuHeader());
            organizerService.showMenu();
        }
    }

    private void handlePlayerLogin() {
        Object user = authManager.authenticate(UserType.PLAYER);
        if (user instanceof Player) {
            System.out.println("\n" + ((Player) user).getMenuHeader());
            playerService.showMenu((Player) user);

            // Save player data after they logout
            teamService.addPlayer((Player) user);
            csvManager.savePlayers(teamService.getAllPlayers());
        }
    }

    private void showSystemInfo() {
        System.out.println("\n=== SYSTEM INFORMATION ===");
        System.out.println("Team Formation System v2.0");
        System.out.println("Features:");
        System.out.println("  • Organizer & Player authentication");
        System.out.println("  • Personality assessment survey");
        System.out.println("  • Automatic team formation");
        System.out.println("  • Manual team adjustment");
        System.out.println("  • CSV data persistence");
        System.out.println("  • Team compatibility analysis");
        System.out.println("\nCurrent Statistics:");
        teamService.showStatistics();

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void saveBeforeExit() {
        System.out.print("\nSave all data before exiting? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y")) {
            csvManager.savePlayers(teamService.getAllPlayers());
            csvManager.saveTeams(teamService.getAllTeams());
            System.out.println("All data saved successfully!");
        }
    }
}