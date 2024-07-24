import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PartyManager {
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
            leader.sendMessage("You already have a party.");
            return;
        }
        Party party = new Party(leader);
        parties.put(leader.getUniqueId(), party);
        saveParties();
        leader.sendMessage("Party created.");
    }

    public void sendPartyInvite(Player leader, Player invitee) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            partyInvites.put(invitee.getUniqueId(), leader.getUniqueId());
            leader.sendMessage("Invite sent to " + invitee.getName() + ".");
            invitee.sendMessage(leader.getName() + " has invited you to their party. Use /party accept " + leader.getName() + " to join.");
        } else {
            leader.sendMessage("You are not the leader of a party.");
        }
    }

    public void acceptPartyInvite(Player player, Player leader) {
        UUID leaderId = leader.getUniqueId();
        if (partyInvites.containsKey(player.getUniqueId()) && partyInvites.get(player.getUniqueId()).equals(leaderId)) {
            joinParty(player, leader);
            partyInvites.remove(player.getUniqueId());
        } else {
            player.sendMessage("No invite from " + leader.getName() + ".");
        }
    }

    public void joinParty(Player player, Player leader) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null) {
            party.addMember(player);
            parties.put(player.getUniqueId(), party);
            saveParties();
            player.sendMessage("Joined the party.");
            party.broadcast(player.getName() + " has joined the party.");
        } else {
            player.sendMessage("Party not found.");
        }
    }

    public void leaveParty(Player player) {
        Party party = parties.get(player.getUniqueId());
        if (party != null) {
            if (party.isLeader(player) && party.getMembers().size() > 1) {
                // Transfer leadership
                party.transferLeadership();
            }
            party.removeMember(player);
            parties.remove(player.getUniqueId());
            saveParties();
            player.sendMessage("You have left the party.");
            party.broadcast(player.getName() + " has left the party.");
        } else {
            player.sendMessage("You are not in a party.");
        }
    }

    public void kickMember(Player leader, Player member) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            party.removeMember(member);
            parties.remove(member.getUniqueId());
            saveParties();
            leader.sendMessage("You have kicked " + member.getName() + " from the party.");
            member.sendMessage("You have been kicked from the party.");
            party.broadcast(member.getName() + " has been kicked from the party.");
        } else {
            leader.sendMessage("You are not the leader of a party.");
        }
    }

    public void disbandParty(Player leader) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            party.broadcast("The party has been disbanded.");
            for (UUID memberId : party.getMembers()) {
                parties.remove(memberId);
            }
            parties.remove(leader.getUniqueId());
            saveParties();
            leader.sendMessage("Party disbanded.");
        } else {
            leader.sendMessage("You are not the leader of a party.");
        }
    }

    public void warpParty(Player leader, Location location) {
        Party party = parties.get(leader.getUniqueId());
        if (party != null && party.isLeader(leader)) {
            for (UUID memberId : party.getMembers()) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.teleport(location);
                    member.sendMessage("You have been warped to the leader's location.");
                }
            }
        } else {
            leader.sendMessage("You are not the leader of a party.");
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
            Party party = new Party(Bukkit.getPlayer(leaderId), memberUUIDs);
            parties.put(leaderId, party);
            for (UUID memberId : memberUUIDs) {
                parties.put(memberId, party);
            }
        }
    }
}
