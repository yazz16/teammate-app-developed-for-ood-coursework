package com.teammate.service;

import com.teammate.model.Player;
import com.teammate.model.Team;
import com.teammate.model.enums.PersonalityType;

import java.util.*;

public class TeamBuilder {
    private static final int DEFAULT_TEAM_SIZE = 4;
    private static final int MIN_TEAM_SIZE = 3;
    private static final int MAX_TEAM_SIZE = 6;

    public List<Team> formTeams(List<Player> players) {
        return formTeams(players, DEFAULT_TEAM_SIZE);
    }

    public List<Team> formTeams(List<Player> players, int teamSize) {
        List<Team> teams = new ArrayList<>();

        if (players.isEmpty()) {
            System.out.println("No players to form teams.");
            return teams;
        }

        // Validate team size
        if (teamSize < MIN_TEAM_SIZE || teamSize > MAX_TEAM_SIZE) {
            System.out.println("Team size must be between " + MIN_TEAM_SIZE + " and " + MAX_TEAM_SIZE);
            teamSize = DEFAULT_TEAM_SIZE;
        }

        // Filter players who have completed survey
        List<Player> surveyedPlayers = new ArrayList<>();
        List<Player> nonSurveyedPlayers = new ArrayList<>();

        for (Player player : players) {
            if (player.isSurveyCompleted()) {
                surveyedPlayers.add(player);
            } else {
                nonSurveyedPlayers.add(player);
            }
        }

        System.out.println("Forming teams with " + surveyedPlayers.size() +
                " surveyed players and " + nonSurveyedPlayers.size() +
                " non-surveyed players");

        // Create teams from surveyed players first
        if (!surveyedPlayers.isEmpty()) {
            // Group by personality for balanced distribution
            Map<PersonalityType, List<Player>> personalityGroups = new HashMap<>();
            for (Player player : surveyedPlayers) {
                PersonalityType type = player.getPersonalityType();
                if (type != null) {
                    personalityGroups.putIfAbsent(type, new ArrayList<>());
                    personalityGroups.get(type).add(player);
                } else {
                    // Players without personality type
                    personalityGroups.putIfAbsent(null, new ArrayList<>());
                    personalityGroups.get(null).add(player);
                }
            }

            // Create balanced teams
            int teamNumber = 1;
            boolean teamsCreated = false;

            while (!surveyedPlayers.isEmpty() && !teamsCreated) {
                Team team = new Team("T" + String.format("%03d", teamNumber), "Team " + teamNumber);
                int targetSize = Math.min(teamSize, surveyedPlayers.size());

                // Try to get one of each personality type if possible
                for (PersonalityType type : personalityGroups.keySet()) {
                    if (type != null && !personalityGroups.get(type).isEmpty() && team.getSize() < targetSize) {
                        Player player = personalityGroups.get(type).remove(0);
                        surveyedPlayers.remove(player);
                        team.addMember(player);
                    }
                }

                // Fill remaining spots
                while (team.getSize() < targetSize && !surveyedPlayers.isEmpty()) {
                    Player player = surveyedPlayers.remove(0);
                    team.addMember(player);
                }

                if (team.getSize() > 0) {
                    teams.add(team);
                    teamNumber++;
                }

                if (surveyedPlayers.isEmpty()) {
                    teamsCreated = true;
                }
            }
        }

        // Add non-surveyed players to existing teams or create new teams
        if (!nonSurveyedPlayers.isEmpty()) {
            // First, try to add to existing teams
            for (Player player : nonSurveyedPlayers) {
                boolean added = false;

                // Find team with smallest size
                Team smallestTeam = null;
                for (Team team : teams) {
                    if (team.getSize() < teamSize) {
                        if (smallestTeam == null || team.getSize() < smallestTeam.getSize()) {
                            smallestTeam = team;
                        }
                    }
                }

                if (smallestTeam != null) {
                    smallestTeam.addMember(player);
                    added = true;
                }

                // If couldn't add to existing team, create new team
                if (!added) {
                    int teamNumber = teams.size() + 1;
                    Team newTeam = new Team("T" + String.format("%03d", teamNumber), "Team " + teamNumber);
                    newTeam.addMember(player);
                    teams.add(newTeam);
                }
            }
        }

        // Set team leaders based on personality
        for (Team team : teams) {
            setTeamLeader(team);
        }

        System.out.println("✓ Formed " + teams.size() + " teams with average size of " +
                getAverageTeamSize(teams) + " players");

        return teams;
    }

    private void setTeamLeader(Team team) {
        // Prefer LEADER personality type for team leader
        for (Player member : team.getMembers()) {
            if (member.getPersonalityType() == PersonalityType.LEADER) {
                team.setTeamLeader(member);
                return;
            }
        }

        // If no LEADER, look for other suitable personalities
        for (Player member : team.getMembers()) {
            if (member.getPersonalityType() == PersonalityType.SOCIABLE ||
                    member.getPersonalityType() == PersonalityType.ANALYTICAL) {
                team.setTeamLeader(member);
                return;
            }
        }

        // Otherwise, choose first member
        if (!team.getMembers().isEmpty()) {
            team.setTeamLeader(team.getMembers().get(0));
        }
    }

    private double getAverageTeamSize(List<Team> teams) {
        if (teams.isEmpty()) return 0;
        int total = 0;
        for (Team team : teams) {
            total += team.getSize();
        }
        return (double) total / teams.size();
    }

    public double calculateTeamCompatibility(Team team) {
        if (team.getSize() < 2) return 100.0;

        int compatibilityScore = 0;
        int maxPossibleScore = 0;
        List<Player> members = team.getMembers();

        // Calculate based on personality diversity
        Set<PersonalityType> personalityTypes = new HashSet<>();
        for (Player member : members) {
            if (member.getPersonalityType() != null) {
                personalityTypes.add(member.getPersonalityType());
            }
        }

        // More diverse personalities = better compatibility
        double diversityScore = (double) personalityTypes.size() / members.size();

        // Calculate based on role coverage (simplified)
        Set<String> rolesCovered = new HashSet<>();
        for (Player member : members) {
            for (com.teammate.model.enums.Role role : member.getPreferredRoles()) {
                rolesCovered.add(role.getName());
            }
        }
        double roleCoverageScore = (double) rolesCovered.size() / (members.size() * 2); // Normalized

        // Final compatibility score (weighted)
        double finalScore = (diversityScore * 0.6 + roleCoverageScore * 0.4) * 100;

        return Math.min(100, Math.max(0, finalScore));
    }

    public String getTeamAnalysis(Team team) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("Team Analysis: ").append(team.getName()).append("\n");
        analysis.append("Size: ").append(team.getSize()).append(" members\n");
        analysis.append("Compatibility Score: ").append(String.format("%.1f", calculateTeamCompatibility(team))).append("/100\n");

        analysis.append("\nPersonality Distribution:\n");
        Map<PersonalityType, Integer> personalityCount = new HashMap<>();
        for (Player member : team.getMembers()) {
            PersonalityType type = member.getPersonalityType();
            if (type != null) {
                personalityCount.put(type, personalityCount.getOrDefault(type, 0) + 1);
            }
        }

        for (Map.Entry<PersonalityType, Integer> entry : personalityCount.entrySet()) {
            analysis.append("  ").append(entry.getKey().getName()).append(": ")
                    .append(entry.getValue()).append("\n");
        }

        analysis.append("\nRoles Covered:\n");
        Set<String> roles = new HashSet<>();
        for (Player member : team.getMembers()) {
            for (com.teammate.model.enums.Role role : member.getPreferredRoles()) {
                roles.add(role.getName());
            }
        }

        if (roles.isEmpty()) {
            analysis.append("  No specific roles preferred\n");
        } else {
            for (String role : roles) {
                analysis.append("  ").append(role).append("\n");
            }
        }

        return analysis.toString();
    }
}