package dev.lofiz.lobbyAPI.manager;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IgnoreManager {
    private Map<UUID, List<UUID>> ignoredPlayers;
    private File dataFile;

    public IgnoreManager(File dataFolder) {
        this.ignoredPlayers = new HashMap<>();
        this.dataFile = new File(dataFolder, "ignoredPlayers.yml");
        loadIgnoredPlayers();
    }

    public void addIgnoredPlayer(Player player, Player ignored) {
        UUID playerId = player.getUniqueId();
        UUID ignoredId = ignored.getUniqueId();
        ignoredPlayers.computeIfAbsent(playerId, k -> new ArrayList<>()).add(ignoredId);
        saveIgnoredPlayers();
        player.sendMessage("You have ignored " + ignored.getName() + ".");
    }

    public void removeIgnoredPlayer(Player player, Player ignored) {
        UUID playerId = player.getUniqueId();
        UUID ignoredId = ignored.getUniqueId();
        List<UUID> playerIgnoredList = ignoredPlayers.get(playerId);
        if (playerIgnoredList != null) {
            playerIgnoredList.remove(ignoredId);
            saveIgnoredPlayers();
            player.sendMessage("You have unignored " + ignored.getName() + ".");
        } else {
            player.sendMessage("You are not ignoring this player.");
        }
    }

    public List<Player> listIgnoredPlayers(Player player) {
        List<UUID> ignoredIds = ignoredPlayers.getOrDefault(player.getUniqueId(), Collections.emptyList());
        List<Player> ignoredList = new ArrayList<>();
        for (UUID ignoredId : ignoredIds) {
            Player ignoredPlayer = player.getServer().getPlayer(ignoredId);
            if (ignoredPlayer != null) {
                ignoredList.add(ignoredPlayer);
            }
        }
        return ignoredList;
    }

    private void saveIgnoredPlayers() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, List<UUID>> entry : ignoredPlayers.entrySet()) {
            List<String> ignoredIds = new ArrayList<>();
            for (UUID ignoredId : entry.getValue()) {
                ignoredIds.add(ignoredId.toString());
            }
            config.set(entry.getKey().toString(), ignoredIds);
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadIgnoredPlayers() {
        if (!dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            List<String> ignoredIds = config.getStringList(key);
            List<UUID> ignoredUUIDs = new ArrayList<>();
            for (String ignoredId : ignoredIds) {
                ignoredUUIDs.add(UUID.fromString(ignoredId));
            }
            ignoredPlayers.put(playerId, ignoredUUIDs);
        }
    }
}
