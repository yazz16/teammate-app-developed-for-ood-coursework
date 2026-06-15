package com.teammate.auth;

import com.teammate.model.Organizer;
import com.teammate.model.Player;
import com.teammate.service.CsvManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AuthManager {
    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, String> organizerCredentials;
    private final CsvManager csvManager;

    public AuthManager(CsvManager csvManager) {
        this.csvManager = csvManager;

        // Initialize organizer credentials
        organizerCredentials = new HashMap<>();
        organizerCredentials.put("admin", "admin123");
        organizerCredentials.put("organizer", "password123");
        organizerCredentials.put("teacher", "teacher123");
    }

    public Object authenticate(UserType userType) {
        System.out.println("\n=== " + (userType == UserType.ORGANIZER ? "Organizer" : "Player") + " Login ===");

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Username/ID: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
                attempts--;
                continue;
            }

            String prompt = userType == UserType.ORGANIZER ? "Password" : "Date of Birth (YYYY-MM-DD)";
            System.out.print(prompt + ": ");
            String password = scanner.nextLine().trim();

            if (userType == UserType.ORGANIZER) {
                if (organizerCredentials.containsKey(username) &&
                        organizerCredentials.get(username).equals(password)) {
                    System.out.println("✓ Login successful!");
                    return new Organizer(username, "Organizer");
                }
            } else {
                // Authenticate player using data from CSV
                if (authenticatePlayer(username, password)) {
                    System.out.println("✓ Login successful!");

                    // Get player from CSV data
                    List<Player> players = csvManager.loadPlayers();
                    Player player = findPlayerById(players, username);
                    if (player != null) {
                        return player;
                    } else {
                        // Create new player if not in CSV
                        return new Player(username, "Player " + username);
                    }
                }
            }

            attempts--;
            System.out.println("✗ Invalid credentials. Attempts left: " + attempts);

            if (attempts > 0) {
                System.out.print("Try again? (y/n): ");
                String choice = scanner.nextLine().toLowerCase();
                if (!choice.equals("y")) {
                    break;
                }
            }
        }

        System.out.println("Too many failed attempts. Returning to main menu.");
        return null;
    }

    private boolean authenticatePlayer(String playerId, String dob) {
        // Load players from CSV and check credentials
        List<Player> players = csvManager.loadPlayers();
        for (Player player : players) {
            if (player.getId().equals(playerId) && player.getDob() != null &&
                    player.getDob().equals(dob)) {
                return true;
            }
        }
        return false;
    }

    private Player findPlayerById(List<Player> players, String id) {
        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }
        return null;
    }

    public void addOrganizer(String username, String password) {
        organizerCredentials.put(username, password);
    }
}