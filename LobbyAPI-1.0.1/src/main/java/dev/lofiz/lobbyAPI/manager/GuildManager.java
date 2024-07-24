package dev.lofiz.lobbyAPI.manager;

import dev.lofiz.lobbyAPI.Guild;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GuildManager {
    private Map<String, Guild> guilds;
    private File dataFile;

    public GuildManager(File dataFolder) {
        this.guilds = new HashMap<>();
        this.dataFile = new File(dataFolder, "guilds.yml");
        loadGuilds();
    }

    public void createGuild(Player leader, String guildName) {
        if (guilds.containsKey(guildName)) {
            leader.sendMessage("A guild with this name already exists.");
            return;
        }
        Guild guild = new Guild(guildName, leader);
        guilds.put(guildName, guild);
        saveGuilds();
        leader.sendMessage("Guild created.");
    }

    public void joinGuild(Player player, String guildName) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            guild.addMember(player);
            saveGuilds();
            player.sendMessage("Joined the guild.");
            guild.broadcast(player.getName() + " has joined the guild.");
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void leaveGuild(Player player, String guildName) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            guild.removeMember(player);
            saveGuilds();
            player.sendMessage("You have left the guild.");
            guild.broadcast(player.getName() + " has left the guild.");
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void disbandGuild(Player leader, String guildName) {
        Guild guild = guilds.get(guildName);
        if (guild != null && guild.isLeader(leader)) {
            guild.broadcast("The guild has been disbanded.");
            guilds.remove(guildName);
            saveGuilds();
            leader.sendMessage("Guild disbanded.");
        } else {
            leader.sendMessage("You are not the leader of this guild.");
        }
    }

    private void saveGuilds() {
        YamlConfiguration config = new YamlConfiguration();
        for (Guild guild : guilds.values()) {
            config.set(guild.getName() + ".leader", guild.getLeader().toString());
            List<String> memberIds = new ArrayList<>();
            for (UUID memberId : guild.getMembers()) {
                memberIds.add(memberId.toString());
            }
            config.set(guild.getName() + ".members", memberIds);
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGuilds() {
        if (!dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : config.getKeys(false)) {
            String guildName = key;
            UUID leaderId = UUID.fromString(config.getString(key + ".leader"));
            List<String> memberIds = config.getStringList(key + ".members");
            List<UUID> memberUUIDs = new ArrayList<>();
            for (String memberId : memberIds) {
                memberUUIDs.add(UUID.fromString(memberId));
            }
            Guild guild = new Guild(guildName, leaderId, memberUUIDs);
            guilds.put(guildName, guild);
        }
    }
}
