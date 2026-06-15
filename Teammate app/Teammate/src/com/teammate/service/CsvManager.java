package com.teammate.service;

import com.teammate.model.Player;
import com.teammate.model.Team;
import com.teammate.model.enums.PersonalityType;
import com.teammate.model.enums.Role;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvManager {
    private static final String PLAYERS_CSV = "players.csv";
    private static final String TEAMS_CSV = "teams.csv";

    public List<Player> loadPlayers() {
        List<Player> players = new ArrayList<>();

        File file = new File(PLAYERS_CSV);
        if (!file.exists()) {
            System.out.println("No players CSV file found. Starting fresh.");
            return players;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(PLAYERS_CSV))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (data.length >= 9) {
                    Player player = new Player(
                            cleanField(data[0]),  // ID
                            cleanField(data[1])   // Name
                    );

                    player.setDob(cleanField(data[2]));
                    player.setEmail(cleanField(data[3]));
                    player.setMajor(cleanField(data[4]));

                    // Parse personality type
                    String personalityStr = cleanField(data[5]);
                    if (!personalityStr.isEmpty()) {
                        PersonalityType personality = PersonalityType.fromString(personalityStr);
                        if (personality != null) {
                            player.setPersonalityType(personality);
                        }
                    }

                    // Parse survey completed flag
                    String surveyStr = cleanField(data[6]);
                    player.setSurveyCompleted(Boolean.parseBoolean(surveyStr));

                    // Parse preferred roles
                    String rolesStr = cleanField(data[7]);
                    if (!rolesStr.isEmpty()) {
                        String[] roles = rolesStr.split(";");
                        for (String roleStr : roles) {
                            Role role = Role.fromString(roleStr);
                            if (role != null) {
                                player.addPreferredRole(role);
                            }
                        }
                    }

                    // Parse interests
                    String interestsStr = cleanField(data[8]);
                    if (!interestsStr.isEmpty()) {
                        String[] interests = interestsStr.split(";");
                        for (String interest : interests) {
                            player.addInterest(interest);
                        }
                    }

                    players.add(player);
                }
            }
            System.out.println("✓ Loaded " + players.size() + " players from CSV.");
        } catch (IOException e) {
            System.out.println("✗ Error loading players CSV: " + e.getMessage());
        }

        return players;
    }

    public void savePlayers(List<Player> players) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PLAYERS_CSV))) {
            // Write header
            pw.println("ID,Name,DOB,Email,Major,PersonalityType,SurveyCompleted,PreferredRoles,Interests");

            // Write data
            for (Player player : players) {
                StringBuilder sb = new StringBuilder();

                sb.append(quoteIfNeeded(player.getId())).append(",");
                sb.append(quoteIfNeeded(player.getName())).append(",");
                sb.append(quoteIfNeeded(player.getDob() != null ? player.getDob() : "")).append(",");
                sb.append(quoteIfNeeded(player.getEmail() != null ? player.getEmail() : "")).append(",");
                sb.append(quoteIfNeeded(player.getMajor() != null ? player.getMajor() : "")).append(",");
                sb.append(quoteIfNeeded(player.getPersonalityType() != null ? player.getPersonalityType().getName() : "")).append(",");
                sb.append(player.isSurveyCompleted()).append(",");

                // Roles
                StringBuilder rolesBuilder = new StringBuilder();
                for (Role role : player.getPreferredRoles()) {
                    if (rolesBuilder.length() > 0) rolesBuilder.append(";");
                    rolesBuilder.append(role.getName());
                }
                sb.append(quoteIfNeeded(rolesBuilder.toString())).append(",");

                // Interests
                StringBuilder interestsBuilder = new StringBuilder();
                for (String interest : player.getInterests()) {
                    if (interestsBuilder.length() > 0) interestsBuilder.append(";");
                    interestsBuilder.append(interest);
                }
                sb.append(quoteIfNeeded(interestsBuilder.toString()));

                pw.println(sb.toString());
            }

            System.out.println("✓ " + players.size() + " players saved to CSV successfully!");
        } catch (IOException e) {
            System.out.println("✗ Error saving players to CSV: " + e.getMessage());
        }
    }

    public List<Team> loadTeams(List<Player> allPlayers) {
        List<Team> teams = new ArrayList<>();

        File file = new File(TEAMS_CSV);
        if (!file.exists()) {
            System.out.println("No teams CSV file found.");
            return teams;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(TEAMS_CSV))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (data.length >= 3) {
                    String teamId = cleanField(data[0]);
                    String teamName = cleanField(data[1]);

                    Team team = new Team(teamId, teamName);

                    // Parse member IDs
                    String membersStr = cleanField(data[2]);
                    if (!membersStr.isEmpty()) {
                        String[] memberIds = membersStr.split(";");
                        for (String memberId : memberIds) {
                            Player player = findPlayerById(allPlayers, memberId);
                            if (player != null) {
                                team.addMember(player);
                            }
                        }
                    }

                    teams.add(team);
                }
            }
            System.out.println("✓ Loaded " + teams.size() + " teams from CSV.");
        } catch (IOException e) {
            System.out.println("✗ Error loading teams CSV: " + e.getMessage());
        }

        return teams;
    }

    public void saveTeams(List<Team> teams) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TEAMS_CSV))) {
            pw.println("TeamID,TeamName,MemberIDs");

            for (Team team : teams) {
                StringBuilder sb = new StringBuilder();
                sb.append(quoteIfNeeded(team.getId())).append(",");
                sb.append(quoteIfNeeded(team.getName())).append(",");

                // Member IDs
                StringBuilder membersBuilder = new StringBuilder();
                for (Player member : team.getMembers()) {
                    if (membersBuilder.length() > 0) membersBuilder.append(";");
                    membersBuilder.append(member.getId());
                }
                sb.append(quoteIfNeeded(membersBuilder.toString()));

                pw.println(sb.toString());
            }

            System.out.println("✓ " + teams.size() + " teams saved to CSV successfully!");
        } catch (IOException e) {
            System.out.println("✗ Error saving teams to CSV: " + e.getMessage());
        }
    }

    private Player findPlayerById(List<Player> players, String id) {
        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }
        return null;
    }

    private String cleanField(String field) {
        if (field == null) return "";
        field = field.trim();
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
        }
        return field.replace("\"\"", "\"");
    }

    private String quoteIfNeeded(String field) {
        if (field == null) return "\"\"";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}