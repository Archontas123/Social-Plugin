package dev.lofiz.lobbyAPI.model;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Guild {
    private String name;
    private UUID leader;
    private Set<UUID> members;

    public Guild(String name, Player leader) {
        this.name = name;
        this.leader = leader.getUniqueId();
        this.members = new HashSet<>();
        this.members.add(leader.getUniqueId());
    }

    public Guild(String name, UUID leaderId, List<UUID> memberIds) {
        this.name = name;
        this.leader = leaderId;
        this.members = new HashSet<>(memberIds);
    }

    public String getName() {
        return name;
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
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }

    public void guildChat(Player sender, String message) {
        for (UUID memberId : members) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Guild" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + ": " + message);
            }
        }
    }

}
