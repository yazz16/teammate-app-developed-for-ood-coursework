package com.teammate.service;

import com.teammate.model.Player;
import com.teammate.model.Team;
import com.teammate.model.enums.PersonalityType;

import java.util.*;

public class TeamService {
    private final List<Player> players = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();
    private final TeamBuilder teamBuilder = new TeamBuilder();
    private final CsvManager csvManager = new CsvManager();

    public TeamService() {
        // Constructor - data will be loaded via separate method
    }

    // Player management methods
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }

    public void setPlayers(List<Player> players) {
        this.players.clear();
        this.players.addAll(players);
    }

    public void addPlayer(Player player) {
        if (findPlayerById(player.getId()) == null) {
            players.add(player);
        }
    }

    public Player findPlayerById(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    public Player findPlayerByName(String name) {
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public List<Player> getPlayersBySurveyStatus(boolean completed) {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.isSurveyCompleted() == completed) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> getPlayersByPersonality(PersonalityType personalityType) {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getPersonalityType() == personalityType) {
                result.add(player);
            }
        }
        return result;
    }

    // Team management methods
    public List<Team> getAllTeams() {
        return new ArrayList<>(teams);
    }

    public void setTeams(List<Team> teams) {
        this.teams.clear();
        this.teams.addAll(teams);
    }

    public Team findTeamById(String teamId) {
        for (Team team : teams) {
            if (team.getId().equals(teamId)) {
                return team;
            }
        }
        return null;
    }

    public Team findTeamByName(String teamName) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(teamName)) {
                return team;
            }
        }
        return null;
    }

    public void autoFormTeams(int teamSize) {
        // Clear existing team assignments
        for (Player player : players) {
            player.setAssignedTeam(null);
        }
        teams.clear();

        // Use TeamBuilder to form teams
        List<Team> newTeams = teamBuilder.formTeams(players, teamSize);
        teams.addAll(newTeams);

        // Update player team assignments
        for (Team team : teams) {
            for (Player member : team.getMembers()) {
                member.setAssignedTeam(team);
            }
        }
    }

    public void viewAllTeams() {
        System.out.println("\n=== ALL TEAMS ===");

        if (teams.isEmpty()) {
            System.out.println("No teams formed yet.");
            return;
        }

        for (Team team : teams) {
            System.out.println("\n" + team.getName() + " [ID: " + team.getId() + "]");
            System.out.println("Members: " + team.getSize());
            System.out.println("Team Leader: " +
                    (team.getTeamLeader() != null ? team.getTeamLeader().getName() : "Not assigned"));

            List<Player> members = team.getMembers();
            if (!members.isEmpty()) {
                System.out.println("Members List:");
                for (int i = 0; i < members.size(); i++) {
                    Player member = members.get(i);
                    System.out.print("  " + (i + 1) + ". " + member.getName());
                    if (team.getTeamLeader() != null && member.equals(team.getTeamLeader())) {
                        System.out.print(" (Leader)");
                    }
                    if (member.getPersonalityType() != null) {
                        System.out.print(" - " + member.getPersonalityType().getName());
                    }
                    System.out.println();
                }
            }
            System.out.println("-".repeat(50));
        }

        System.out.println("\nTotal: " + teams.size() + " teams");
        System.out.println("Total Players Assigned: " + getTotalAssignedPlayers());
        System.out.println("Unassigned Players: " + (players.size() - getTotalAssignedPlayers()));
    }

    public boolean movePlayerToTeam(String playerId, String teamIdentifier) {
        Player player = findPlayerById(playerId);
        if (player == null) {
            return false;
        }

        Team targetTeam = findTeamById(teamIdentifier);
        if (targetTeam == null) {
            targetTeam = findTeamByName(teamIdentifier);
        }

        if (targetTeam == null) {
            return false;
        }

        // Remove from current team if any
        Team currentTeam = player.getAssignedTeam();
        if (currentTeam != null) {
            if (currentTeam.equals(targetTeam)) {
                return false; // Already in this team
            }
            currentTeam.removeMember(player);
        }

        // Add to new team
        targetTeam.addMember(player);
        return true;
    }

    public boolean movePlayerBetweenTeams(String playerId, String sourceTeamId, String targetTeamId) {
        Player player = findPlayerById(playerId);
        Team sourceTeam = findTeamById(sourceTeamId);
        Team targetTeam = findTeamById(targetTeamId);

        if (player == null || sourceTeam == null || targetTeam == null) {
            // Try by name if not found by ID
            if (sourceTeam == null) sourceTeam = findTeamByName(sourceTeamId);
            if (targetTeam == null) targetTeam = findTeamByName(targetTeamId);

            if (player == null || sourceTeam == null || targetTeam == null) {
                return false;
            }
        }

        if (!sourceTeam.contains(player)) {
            return false;
        }

        if (targetTeam.contains(player)) {
            return false; // Already in target team
        }

        sourceTeam.removeMember(player);
        targetTeam.addMember(player);
        return true;
    }

    public void createOrAssignTeam(String teamId, String teamName, Player player) {
        Team team = findTeamById(teamId);
        if (team == null) {
            team = new Team(teamId, teamName);
            teams.add(team);
        }

        // Remove from current team if any
        Team currentTeam = player.getAssignedTeam();
        if (currentTeam != null) {
            currentTeam.removeMember(player);
        }

        team.addMember(player);
    }

    public String getTeamAnalysis(Team team) {
        return teamBuilder.getTeamAnalysis(team);
    }

    public double getTeamCompatibility(Team team) {
        return teamBuilder.calculateTeamCompatibility(team);
    }

    public List<Team> getTeamsBySize(int minSize, int maxSize) {
        List<Team> result = new ArrayList<>();
        for (Team team : teams) {
            if (team.getSize() >= minSize && team.getSize() <= maxSize) {
                result.add(team);
            }
        }
        return result;
    }

    public List<Team> getTeamsWithPersonality(PersonalityType personalityType) {
        List<Team> result = new ArrayList<>();
        for (Team team : teams) {
            if (team.hasPersonalityType(personalityType)) {
                result.add(team);
            }
        }
        return result;
    }

    private int getTotalAssignedPlayers() {
        int count = 0;
        for (Player player : players) {
            if (player.getAssignedTeam() != null) {
                count++;
            }
        }
        return count;
    }

    // Statistics
    public void showStatistics() {
        System.out.println("\n=== SYSTEM STATISTICS ===");
        System.out.println("Total Players: " + players.size());
        System.out.println("Players with completed survey: " + getPlayersBySurveyStatus(true).size());
        System.out.println("Players with pending survey: " + getPlayersBySurveyStatus(false).size());
        System.out.println("Total Teams: " + teams.size());
        System.out.println("Assigned Players: " + getTotalAssignedPlayers());
        System.out.println("Unassigned Players: " + (players.size() - getTotalAssignedPlayers()));

        // Personality distribution
        System.out.println("\nPersonality Distribution:");
        Map<PersonalityType, Integer> personalityCount = new HashMap<>();
        for (Player player : players) {
            if (player.getPersonalityType() != null) {
                personalityCount.put(player.getPersonalityType(),
                        personalityCount.getOrDefault(player.getPersonalityType(), 0) + 1);
            }
        }

        for (Map.Entry<PersonalityType, Integer> entry : personalityCount.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / players.size();
            System.out.printf("  %-15s: %d (%.1f%%)\n",
                    entry.getKey().getName(), entry.getValue(), percentage);
        }

        // Team size distribution
        System.out.println("\nTeam Size Distribution:");
        Map<Integer, Integer> sizeCount = new HashMap<>();
        for (Team team : teams) {
            int size = team.getSize();
            sizeCount.put(size, sizeCount.getOrDefault(size, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : sizeCount.entrySet()) {
            System.out.println("  Teams with " + entry.getKey() + " members: " + entry.getValue());
        }

        // Average compatibility score
        if (!teams.isEmpty()) {
            double totalCompatibility = 0;
            for (Team team : teams) {
                totalCompatibility += getTeamCompatibility(team);
            }
            double avgCompatibility = totalCompatibility / teams.size();
            System.out.printf("\nAverage Team Compatibility: %.1f/100\n", avgCompatibility);
        }
    }
}