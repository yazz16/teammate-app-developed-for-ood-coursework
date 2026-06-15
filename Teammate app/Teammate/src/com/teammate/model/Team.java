package com.teammate.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String id;
    private String name;
    private List<Player> members;
    private Player teamLeader;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(Player player) {
        if (!members.contains(player)) {
            members.add(player);
            player.setAssignedTeam(this);

            // If this is the first member or no leader assigned, make them leader
            if (teamLeader == null) {
                teamLeader = player;
            }
        }
    }

    public void removeMember(Player player) {
        if (members.remove(player)) {
            player.setAssignedTeam(null);

            // If removed player was leader, assign new leader
            if (teamLeader != null && teamLeader.equals(player)) {
                if (!members.isEmpty()) {
                    teamLeader = members.get(0);
                } else {
                    teamLeader = null;
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getMembers() {
        return new ArrayList<>(members);
    }

    public int getSize() {
        return members.size();
    }

    public boolean contains(Player player) {
        return members.contains(player);
    }

    public Player getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(Player teamLeader) {
        if (members.contains(teamLeader)) {
            this.teamLeader = teamLeader;
        }
    }

    public boolean hasPersonalityType(com.teammate.model.enums.PersonalityType type) {
        for (Player member : members) {
            if (member.getPersonalityType() == type) {
                return true;
            }
        }
        return false;
    }

    public List<com.teammate.model.enums.PersonalityType> getPersonalityTypes() {
        List<com.teammate.model.enums.PersonalityType> types = new ArrayList<>();
        for (Player member : members) {
            if (member.getPersonalityType() != null && !types.contains(member.getPersonalityType())) {
                types.add(member.getPersonalityType());
            }
        }
        return types;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Team: ").append(name).append(" [ID: ").append(id).append("]\n");
        sb.append("Members: ").append(members.size()).append("\n");
        for (Player member : members) {
            sb.append("  - ").append(member.getName());
            if (teamLeader != null && member.equals(teamLeader)) {
                sb.append(" [Team Leader]");
            }
            if (member.getPersonalityType() != null) {
                sb.append(" (").append(member.getPersonalityType().getName()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}