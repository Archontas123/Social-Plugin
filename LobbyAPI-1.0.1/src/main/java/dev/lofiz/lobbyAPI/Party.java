package dev.lofiz.lobbyAPI;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Party {
    private UUID leader;
    private Set<UUID> members;

    public Party(Player leader) {
        this.leader = leader.getUniqueId();
        this.members = new HashSet<>();
        this.members.add(leader.getUniqueId());
    }

    public Party(Player leader, List<UUID> memberIds) {
        this.leader = leader.getUniqueId();
        this.members = new HashSet<>(memberIds);
    }

    public UUID getLeader() {
        return leader;
    }

    public boolean isLeader(Player player) {
        return player.getUniqueId().equals(leader);
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void broadcast(String message) {
        for (UUID memberId : members) {
            Player member = org.bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }
}
