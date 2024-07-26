package dev.lofiz.lobbyAPI.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IgnoreManager implements Listener {
    private Map<UUID, List<UUID>> ignoredPlayers;
    private File dataFile;
    private FriendManager friendManager;

    public IgnoreManager(File dataFolder, FriendManager friendManager) {
        this.ignoredPlayers = new HashMap<>();
        this.dataFile = new File(dataFolder, "ignoredPlayers.yml");
        this.friendManager = friendManager;
        loadIgnoredPlayers();
    }

    public void addIgnoredPlayer(Player player, Player ignored) {
        if (player.getUniqueId().equals(ignored.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot ignore yourself.");
            return;
        }
        UUID playerId = player.getUniqueId();
        UUID ignoredId = ignored.getUniqueId();
        if (!ignoredPlayers.getOrDefault(playerId, Collections.emptyList()).contains(ignoredId)) {
            ignoredPlayers.computeIfAbsent(playerId, k -> new ArrayList<>()).add(ignoredId);
            saveIgnoredPlayers();
            player.sendMessage(ChatColor.GREEN + "You have ignored " + ignored.getName() + ".");
            friendManager.removeIgnoredPlayerFromFriends(player, ignored);
        } else {
            player.sendMessage(ChatColor.RED + "You have already ignored " + ignored.getName() + ".");
        }
    }

    public void removeIgnoredPlayer(Player player, Player ignored) {
        UUID playerId = player.getUniqueId();
        UUID ignoredId = ignored.getUniqueId();
        List<UUID> playerIgnoredList = ignoredPlayers.get(playerId);
        if (playerIgnoredList != null && playerIgnoredList.contains(ignoredId)) {
            playerIgnoredList.remove(ignoredId);
            saveIgnoredPlayers();
            player.sendMessage(ChatColor.GREEN + "You have unignored " + ignored.getName() + ".");
        } else {
            player.sendMessage(ChatColor.RED + "You are not ignoring this player.");
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

    public boolean isIgnored(Player sender, Player receiver) {
        List<UUID> ignoredList = ignoredPlayers.get(receiver.getUniqueId());
        return ignoredList != null && ignoredList.contains(sender.getUniqueId());
    }

    public boolean isIgnored(Player player) {
        return ignoredPlayers.values().stream().anyMatch(list -> list.contains(player.getUniqueId()));
    }

    public Inventory getIgnoredListGUI(Player player) {
        List<UUID> ignoredIds = ignoredPlayers.getOrDefault(player.getUniqueId(), Collections.emptyList());
        Inventory gui = Bukkit.createInventory(null, 27, "Ignored Players");
        for (UUID ignoredId : ignoredIds) {
            Player ignored = Bukkit.getPlayer(ignoredId);
            if (ignored != null) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(ignored);
                meta.setDisplayName(ignored.getName());
                head.setItemMeta(meta);
                gui.addItem(head);
            }
        }
        return gui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Ignored Players")) {
            event.setCancelled(true);
        }
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
