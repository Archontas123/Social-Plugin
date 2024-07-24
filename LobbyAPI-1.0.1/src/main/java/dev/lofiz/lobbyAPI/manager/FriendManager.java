package dev.lofiz.lobbyAPI.manager;

import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import org.bukkit.configuration.file.YamlConfiguration;

public class FriendManager {
    private Map<UUID, List<UUID>> friends;
    private File dataFile;

    public FriendManager(File dataFolder) {
        this.friends = new HashMap<>();
        this.dataFile = new File(dataFolder, "friends.yml");
        loadFriends();
    }

    public void addFriend(Player player, Player friend) {
        UUID playerId = player.getUniqueId();
        UUID friendId = friend.getUniqueId();
        friends.computeIfAbsent(playerId, k -> new ArrayList<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new ArrayList<>()).add(playerId);
        saveFriends();
    }

    public void removeFriend(Player player, Player friend) {
        UUID playerId = player.getUniqueId();
        UUID friendId = friend.getUniqueId();
        List<UUID> playerFriends = friends.get(playerId);
        List<UUID> friendFriends = friends.get(friendId);
        if (playerFriends != null) {
            playerFriends.remove(friendId);
        }
        if (friendFriends != null) {
            friendFriends.remove(playerId);
        }
        saveFriends();
    }

    public List<Player> listFriends(Player player) {
        List<UUID> friendIds = friends.getOrDefault(player.getUniqueId(), Collections.emptyList());
        List<Player> friendList = new ArrayList<>();
        for (UUID friendId : friendIds) {
            Player friend = player.getServer().getPlayer(friendId);
            if (friend != null) {
                friendList.add(friend);
            }
        }
        return friendList;
    }

    private void saveFriends() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, List<UUID>> entry : friends.entrySet()) {
            List<String> friendIds = new ArrayList<>();
            for (UUID friendId : entry.getValue()) {
                friendIds.add(friendId.toString());
            }
            config.set(entry.getKey().toString(), friendIds);
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFriends() {
        if (!dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            List<String> friendIds = config.getStringList(key);
            List<UUID> friendUUIDs = new ArrayList<>();
            for (String friendId : friendIds) {
                friendUUIDs.add(UUID.fromString(friendId));
            }
            friends.put(playerId, friendUUIDs);
        }
    }
}
