import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FriendManager {
    private Map<UUID, List<UUID>> friends;
    private Map<UUID, UUID> friendRequests;
    private File dataFile;

    public FriendManager(File dataFolder) {
        this.friends = new HashMap<>();
        this.friendRequests = new HashMap<>();
        this.dataFile = new File(dataFolder, "friends.yml");
        loadFriends();
    }

    public void sendFriendRequest(Player player, Player friend) {
        if (player.getUniqueId().equals(friend.getUniqueId())) {
            player.sendMessage("You cannot add yourself as a friend.");
            return;
        }
        if (friendRequests.containsKey(friend.getUniqueId()) && friendRequests.get(friend.getUniqueId()).equals(player.getUniqueId())) {
            addFriend(player, friend);
            player.sendMessage("You are now friends with " + friend.getName() + ".");
            friend.sendMessage("You are now friends with " + player.getName() + ".");
            friendRequests.remove(friend.getUniqueId());
        } else {
            friendRequests.put(player.getUniqueId(), friend.getUniqueId());
            player.sendMessage("Friend request sent to " + friend.getName() + ".");
            friend.sendMessage(player.getName() + " has sent you a friend request. Use /friend accept " + player.getName() + " to accept.");
        }
    }

    public void acceptFriendRequest(Player player, Player friend) {
        UUID friendId = friend.getUniqueId();
        if (friendRequests.containsKey(friendId) && friendRequests.get(friendId).equals(player.getUniqueId())) {
            addFriend(friend, player);
            player.sendMessage("You are now friends with " + friend.getName() + ".");
            friend.sendMessage("You are now friends with " + player.getName() + ".");
            friendRequests.remove(friendId);
        } else {
            player.sendMessage("No friend request from " + friend.getName() + ".");
        }
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
