package dev.lofiz.lobbyAPI.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Party {
    private UUID leader;
    private Set<UUID> members;
    private Set<UUID> moderators;

    public Party(Player leader) {
        this.leader = leader.getUniqueId();
        this.members = new HashSet<>();
        this.moderators = new HashSet<>();
        this.members.add(leader.getUniqueId());
    }

    public Party(UUID leaderId, List<UUID> memberIds) {
        this.leader = leaderId;
        this.members = new HashSet<>(memberIds);
        this.moderators = new HashSet<>();
    }

    public UUID getLeader() {
        return leader;
    }

    public boolean isLeader(Player player) {
        return player.getUniqueId().equals(leader);
    }

    public boolean isModerator(Player player) {
        return moderators.contains(player.getUniqueId());
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
        moderators.remove(player.getUniqueId());
    }

    public void addModerator(Player player) {
        moderators.add(player.getUniqueId());
    }

    public void removeModerator(Player player) {
        moderators.remove(player.getUniqueId());
    }

    public void transferLeadership(Player newLeader) {
        if (members.contains(newLeader.getUniqueId())) {
            leader = newLeader.getUniqueId();
        }
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void broadcast(String message) {
        for (UUID memberId : members) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }

    public void partyChat(Player sender, String message) {
        for (UUID memberId : members) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage("[Party] " + sender.getName() + ": " + message);
            }
        }
    }
}
