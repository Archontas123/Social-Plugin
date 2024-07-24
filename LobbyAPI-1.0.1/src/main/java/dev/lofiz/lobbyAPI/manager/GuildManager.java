import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GuildManager {
    private Map<String, Guild> guilds;
    private Map<UUID, String> playerGuilds;
    private File dataFile;

    public GuildManager(File dataFolder) {
        this.guilds = new HashMap<>();
        this.playerGuilds = new HashMap<>();
        this.dataFile = new File(dataFolder, "guilds.yml");
        loadGuilds();
    }

    public void createGuild(Player leader, String guildName) {
        if (guilds.containsKey(guildName)) {
            leader.sendMessage("A guild with this name already exists.");
            return;
        }
        if (playerGuilds.containsKey(leader.getUniqueId())) {
            leader.sendMessage("You are already in a guild.");
            return;
        }
        Guild guild = new Guild(guildName, leader);
        guilds.put(guildName, guild);
        playerGuilds.put(leader.getUniqueId(), guildName);
        saveGuilds();
        leader.sendMessage("Guild created.");
    }

    public void joinGuild(Player player, String guildName) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            if (playerGuilds.containsKey(player.getUniqueId())) {
                player.sendMessage("You are already in a guild.");
                return;
            }
            guild.addMember(player);
            playerGuilds.put(player.getUniqueId(), guildName);
            saveGuilds();
            player.sendMessage("Joined the guild.");
            guild.broadcast(player.getName() + " has joined the guild.");
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void leaveGuild(Player player) {
        String guildName = playerGuilds.get(player.getUniqueId());
        if (guildName != null) {
            Guild guild = guilds.get(guildName);
            guild.removeMember(player);
            playerGuilds.remove(player.getUniqueId());
            saveGuilds();
            player.sendMessage("You have left the guild.");
            guild.broadcast(player.getName() + " has left the guild.");
        } else {
            player.sendMessage("You are not in a guild.");
        }
    }

    public void disbandGuild(Player leader) {
        String guildName = playerGuilds.get(leader.getUniqueId());
        if (guildName != null) {
            Guild guild = guilds.get(guildName);
            if (guild.isLeader(leader)) {
                guild.broadcast("The guild has been disbanded.");
                for (UUID memberId : guild.getMembers()) {
                    playerGuilds.remove(memberId);
                }
                guilds.remove(guildName);
                saveGuilds();
                leader.sendMessage("Guild disbanded.");
            } else {
                leader.sendMessage("You are not the leader of this guild.");
            }
        } else {
            leader.sendMessage("You are not in a guild.");
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
            for (UUID memberId : memberUUIDs) {
                playerGuilds.put(memberId, guildName);
            }
        }
    }
}
