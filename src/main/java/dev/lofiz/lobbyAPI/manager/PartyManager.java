package dev.lofiz.lobbyAPI.manager;

import dev.lofiz.lobbyAPI.model.Party;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PartyManager implements Listener {
    private Map<UUID, Party> parties;
    private Map<UUID, UUID> partyInvites;
    private File dataFile;

    public PartyManager(File dataFolder) {
        this.parties = new HashMap<>();
        this.partyInvites = new HashMap<>();
        this.dataFile = new File(dataFolder, "parties.yml");
        loadParties();
    }

    public void createParty(Player leader) {
        if (parties.containsKey(leader.getUniqueId())) {
            leader.sendMessage(ChatColor.RED + "You already have a party.");
            return;
        }
        Party party = new Party(leader);
        parties.put(leader.getUniqueId(), party);
        saveParties();
        leader.sendMessage(ChatColor.GREEN + "Party created.");
    }

    public void sendPartyInvite(Player leader, Player invitee) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            partyInvites.put(invitee.getUniqueId(), leader.getUniqueId());
            leader.sendMessage(ChatColor.GREEN + "Invite sent to " + invitee.getName() + ".");
            TextComponent acceptButton = new TextComponent("[Accept]");
            acceptButton.setColor(ChatColor.GREEN);
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + leader.getName()));
            TextComponent denyButton = new TextComponent("[Deny]");
            denyButton.setColor(ChatColor.RED);
            denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + leader.getName()));
            invitee.spigot().sendMessage(new TextComponent(ChatColor.YELLOW + leader.getName() + " has invited you to a party! "), acceptButton, new TextComponent(" "), denyButton);
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader of a party.");
        }
    }

    public void acceptPartyInvite(Player player, Player leader) {
        UUID leaderId = leader.getUniqueId();
        if (partyInvites.containsKey(player.getUniqueId()) && partyInvites.get(player.getUniqueId()).equals(leaderId)) {
            joinParty(player, leader);
            partyInvites.remove(player.getUniqueId());
        } else {
            player.sendMessage(ChatColor.RED + "No invite from " + leader.getName() + ".");
        }
    }

    public void denyPartyInvite(Player player, Player leader) {
        UUID leaderId = leader.getUniqueId();
        if (partyInvites.containsKey(player.getUniqueId()) && partyInvites.get(player.getUniqueId()).equals(leaderId)) {
            player.sendMessage(ChatColor.RED + "Party invite from " + leader.getName() + " denied.");
            leader.sendMessage(ChatColor.RED + player.getName() + " has denied your party invite.");
            partyInvites.remove(player.getUniqueId());
        } else {
            player.sendMessage(ChatColor.RED + "No invite from " + leader.getName() + ".");
        }
    }

    public void joinParty(Player player, Player leader) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null) {
            party.addMember(player);
            parties.put(player.getUniqueId(), party);
            saveParties();
            player.sendMessage(ChatColor.GREEN + "Joined the party.");
            party.broadcast(player.getName() + " has joined the party.");
        } else {
            player.sendMessage(ChatColor.RED + "Party not found.");
        }
    }

    public void leaveParty(Player player) {
        Party party = parties.get(player.getUniqueId());
        if (party != null) {
            if (party.isLeader(player) && party.getMembers().size() > 1) {
                // Transfer leadership
                transferLeadership(party);
            }
            party.removeMember(player);
            parties.remove(player.getUniqueId());
            saveParties();
            player.sendMessage(ChatColor.GREEN + "You have left the party.");
            party.broadcast(player.getName() + " has left the party.");
        } else {
            player.sendMessage(ChatColor.RED + "You are not in a party.");
        }
    }

    public void removePlayerFromParty(Player player) {
        Party party = parties.get(player.getUniqueId());
        if (party != null) {
            party.removeMember(player);
            parties.remove(player.getUniqueId());
            saveParties();
            party.broadcast(ChatColor.RED + player.getName() + " has been kicked from the party.");
        }
    }

    public void kickMember(Player leader, Player member) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && (party.isLeader(leader) || party.isModerator(leader))) {
            if (party.isLeader(member)) {
                leader.sendMessage(ChatColor.RED + "You cannot kick the leader of the party.");
                return;
            }
            party.removeMember(member);
            parties.remove(member.getUniqueId());
            saveParties();
            leader.sendMessage(ChatColor.GREEN + "You have kicked " + member.getName() + " from the party.");
            member.sendMessage(ChatColor.RED + "You have been kicked from the party.");
            party.broadcast(member.getName() + " has been kicked from the party.");
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader or moderator of a party.");
        }
    }

    public void disbandParty(Player leader) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            party.broadcast(ChatColor.RED + "The party has been disbanded.");
            for (UUID memberId : party.getMembers()) {
                parties.remove(memberId);
            }
            parties.remove(leader.getUniqueId());
            saveParties();
            leader.sendMessage(ChatColor.GREEN + "Party disbanded.");
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader of a party.");
        }
    }

    public void warpParty(Player leader, Location location) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            for (UUID memberId : party.getMembers()) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.teleport(location);
                    member.sendMessage(ChatColor.GREEN + "You have been warped to the leader's location.");
                }
            }
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader of a party.");
        }
    }

    public void promoteToModerator(Player leader, Player promotee) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            if (party.isLeader(promotee)) {
                leader.sendMessage(ChatColor.RED + "You cannot promote the leader.");
                return;
            }
            party.addModerator(promotee);
            leader.sendMessage(ChatColor.GREEN + promotee.getName() + " has been promoted to moderator.");
            promotee.sendMessage(ChatColor.GREEN + "You have been promoted to moderator in the party.");
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader of a party.");
        }
    }

    public void demoteModerator(Player leader, Player demotee) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            if (!party.isModerator(demotee)) {
                leader.sendMessage(ChatColor.RED + demotee.getName() + " is not a moderator.");
                return;
            }
            party.removeModerator(demotee);
            leader.sendMessage(ChatColor.GREEN + demotee.getName() + " has been demoted from moderator.");
            demotee.sendMessage(ChatColor.RED + "You have been demoted from moderator in the party.");
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader of a party.");
        }
    }

    public void transferLeadership(Player leader, Player newLeader) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            party.transferLeadership(newLeader);
            leader.sendMessage(ChatColor.GREEN + "You have transferred leadership to " + newLeader.getName() + ".");
            newLeader.sendMessage(ChatColor.GREEN + "You are now the leader of the party.");
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader of a party.");
        }
    }

    private void transferLeadership(Party party) {
        for (UUID memberId : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && !member.getUniqueId().equals(party.getLeader())) {
                party.transferLeadership(member);
                party.broadcast(ChatColor.GREEN + member.getName() + " is now the leader of the party.");
                return;
            }
        }
        party.broadcast(ChatColor.RED + "The party has been disbanded because there are no members to transfer leadership to.");
        disbandParty(Bukkit.getPlayer(party.getLeader()));
    }

    public Party getParty(Player player) {
        return parties.get(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Party party = getParty(player);
        if (party != null) {
            removePlayerFromParty(player);
            party.broadcast(ChatColor.RED + player.getName() + " has been kicked from the party for logging off.");
        }
    }

    private void saveParties() {
        YamlConfiguration config = new YamlConfiguration();
        for (Party party : new HashSet<>(parties.values())) {
            List<String> memberIds = new ArrayList<>();
            for (UUID memberId : party.getMembers()) {
                memberIds.add(memberId.toString());
            }
            config.set(party.getLeader().toString(), memberIds);
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadParties() {
        if (!dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : config.getKeys(false)) {
            UUID leaderId = UUID.fromString(key);
            List<String> memberIds = config.getStringList(key);
            List<UUID> memberUUIDs = new ArrayList<>();
            for (String memberId : memberIds) {
                memberUUIDs.add(UUID.fromString(memberId));
            }
            Party party = new Party(leaderId, memberUUIDs);
            parties.put(leaderId, party);
            for (UUID memberId : memberUUIDs) {
                parties.put(memberId, party);
            }
        }
    }
}
