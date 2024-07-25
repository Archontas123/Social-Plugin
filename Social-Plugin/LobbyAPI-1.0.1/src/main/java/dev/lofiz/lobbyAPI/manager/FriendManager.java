package dev.lofiz.lobbyAPI.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

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
            player.sendMessage(ChatColor.RED + "You cannot add yourself as a friend.");
            return;
        }
        if (friends.getOrDefault(player.getUniqueId(), Collections.emptyList()).contains(friend.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already friends with " + friend.getName() + ".");
            return;
        }
        if (friendRequests.containsKey(friend.getUniqueId()) && friendRequests.get(friend.getUniqueId()).equals(player.getUniqueId())) {
            addFriend(player, friend);
            player.sendMessage(ChatColor.GREEN + "You are now friends with " + friend.getName() + ".");
            friend.sendMessage(ChatColor.GREEN + "You are now friends with " + player.getName() + ".");
            friendRequests.remove(friend.getUniqueId());
        } else {
            friendRequests.put(player.getUniqueId(), friend.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Friend request sent to " + friend.getName() + ".");
            TextComponent acceptButton = new TextComponent("[Accept]");
            acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName()));
            TextComponent denyButton = new TextComponent("[Deny]");
            denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
            denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + player.getName()));
            friend.spigot().sendMessage(new TextComponent(player.getName() + " has sent you a friend request. "), acceptButton, new TextComponent(" "), denyButton);
        }
    }

    public void acceptFriendRequest(Player player, Player friend) {
        UUID friendId = friend.getUniqueId();
        if (friendRequests.containsKey(friendId) && friendRequests.get(friendId).equals(player.getUniqueId())) {
            addFriend(friend, player);
            player.sendMessage(ChatColor.GREEN + "You are now friends with " + friend.getName() + ".");
            friend.sendMessage(ChatColor.GREEN + "You are now friends with " + player.getName() + ".");
            friendRequests.remove(friendId);
        } else {
            player.sendMessage(ChatColor.RED + "No friend request from " + friend.getName() + ".");
        }
    }

    public void denyFriendRequest(Player player, Player friend) {
        UUID friendId = friend.getUniqueId();
        if (friendRequests.containsKey(friendId) && friendRequests.get(friendId).equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Friend request from " + friend.getName() + " denied.");
            friend.sendMessage(ChatColor.RED + player.getName() + " has denied your friend request.");
            friendRequests.remove(friendId);
        } else {
            player.sendMessage(ChatColor.RED + "No friend request from " + friend.getName() + ".");
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
        player.sendMessage(ChatColor.GREEN + "You have removed " + friend.getName() + " from your friends.");
        friend.sendMessage(ChatColor.RED + player.getName() + " has removed you from their friends.");
    }

    public Inventory getFriendListGUI(Player player) {
        List<UUID> friendIds = friends.getOrDefault(player.getUniqueId(), Collections.emptyList());
        Inventory gui = Bukkit.createInventory(null, 27, "Friends List");
        for (UUID friendId : friendIds) {
            Player friend = Bukkit.getPlayer(friendId);
            if (friend != null) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(friend);
                meta.setDisplayName(friend.getName());
                head.setItemMeta(meta);
                gui.addItem(head);
            }
        }
        return gui;
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
