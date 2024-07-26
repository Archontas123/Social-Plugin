package dev.lofiz.lobbyAPI.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerProfileManager implements Listener {
    private final File dataFolder;
    private final Map<UUID, Map<String, String>> profiles;
    private final Map<UUID, Map<String, Boolean>> privacySettings;

    public PlayerProfileManager(File dataFolder) {
        this.dataFolder = dataFolder;
        this.profiles = new HashMap<>();
        this.privacySettings = new HashMap<>();
        loadProfiles();
        loadPrivacySettings();
    }

    public void createProfile(Player player) {
        UUID playerId = player.getUniqueId();
        if (!profiles.containsKey(playerId)) {
            profiles.put(playerId, new HashMap<>());
            saveProfiles();
            player.sendMessage("Profile created successfully.");
        } else {
            player.sendMessage("You already have a profile.");
        }
    }

    public Inventory getProfileGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Player Profile");

        // Player's head
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        headMeta.setOwningPlayer(player);
        headMeta.setDisplayName(getPlayerRank(player) + " " + player.getName());
        playerHead.setItemMeta(headMeta);
        gui.setItem(13, playerHead);

        // Friend Add button
        ItemStack friendAdd = new ItemStack(Material.EMERALD, 1);
        ItemMeta friendAddMeta = friendAdd.getItemMeta();
        friendAddMeta.setDisplayName("Add Friend");
        friendAdd.setItemMeta(friendAddMeta);
        gui.setItem(11, friendAdd);

        // Friend List button
        ItemStack friendList = new ItemStack(Material.BOOK, 1);
        ItemMeta friendListMeta = friendList.getItemMeta();
        friendListMeta.setDisplayName("Friend List");
        friendList.setItemMeta(friendListMeta);
        gui.setItem(12, friendList);

        // Ignore Add button
        ItemStack ignoreAdd = new ItemStack(Material.REDSTONE, 1);
        ItemMeta ignoreAddMeta = ignoreAdd.getItemMeta();
        ignoreAddMeta.setDisplayName("Ignore Player");
        ignoreAdd.setItemMeta(ignoreAddMeta);
        gui.setItem(14, ignoreAdd);

        // Ignore List button
        ItemStack ignoreList = new ItemStack(Material.BOOK, 1);
        ItemMeta ignoreListMeta = ignoreList.getItemMeta();
        ignoreListMeta.setDisplayName("Ignored List");
        ignoreList.setItemMeta(ignoreListMeta);
        gui.setItem(15, ignoreList);

        // Party Create button
        ItemStack partyCreate = new ItemStack(Material.DIAMOND, 1);
        ItemMeta partyCreateMeta = partyCreate.getItemMeta();
        partyCreateMeta.setDisplayName("Create Party");
        partyCreate.setItemMeta(partyCreateMeta);
        gui.setItem(16, partyCreate);

        // Guild Create button
        ItemStack guildCreate = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta guildCreateMeta = guildCreate.getItemMeta();
        guildCreateMeta.setDisplayName("Create Guild");
        guildCreate.setItemMeta(guildCreateMeta);
        gui.setItem(10, guildCreate);

        // Privacy Settings button
        ItemStack privacySettings = new ItemStack(Material.IRON_DOOR, 1);
        ItemMeta privacySettingsMeta = privacySettings.getItemMeta();
        privacySettingsMeta.setDisplayName("Privacy Settings");
        privacySettings.setItemMeta(privacySettingsMeta);
        gui.setItem(19, privacySettings);

        // Configurable slots
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(dataFolder.getParent(), "config.yml"));
        List<Map<?, ?>> slots = config.getMapList("profile.slots");
        if (slots.isEmpty()) {
            // Default items if config is empty
            for (int i = 20; i <= 25; i++) {
                ItemStack paper = new ItemStack(Material.PAPER, 1);
                ItemMeta paperMeta = paper.getItemMeta();
                paperMeta.setDisplayName("COMING SOON");
                paper.setItemMeta(paperMeta);
                gui.setItem(i, paper);
            }
        } else {
            for (Map<?, ?> slotConfig : slots) {
                int slot = (Integer) slotConfig.get("slot");
                Material material = Material.valueOf((String) slotConfig.get("material"));
                String displayName = (String) slotConfig.get("display_name");
                String placeholder = (String) slotConfig.get("placeholder");

                ItemStack item = new ItemStack(material, 1);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(displayName);

                if (placeholder != null) {
                    String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                    itemMeta.setLore(Collections.singletonList(parsedPlaceholder));
                }

                item.setItemMeta(itemMeta);
                gui.setItem(slot, item);
            }
        }

        return gui;
    }

    public Inventory getPrivacySettingsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Privacy Settings");

        ItemStack guildInvites = new ItemStack(Material.PAPER, 1);
        ItemMeta guildInvitesMeta = guildInvites.getItemMeta();
        guildInvitesMeta.setDisplayName("Guild Invites");
        guildInvites.setItemMeta(guildInvitesMeta);
        gui.setItem(11, guildInvites);

        ItemStack friendInvites = new ItemStack(Material.PAPER, 1);
        ItemMeta friendInvitesMeta = friendInvites.getItemMeta();
        friendInvitesMeta.setDisplayName("Friend Invites");
        friendInvites.setItemMeta(friendInvitesMeta);
        gui.setItem(12, friendInvites);

        ItemStack partyInvites = new ItemStack(Material.PAPER, 1);
        ItemMeta partyInvitesMeta = partyInvites.getItemMeta();
        partyInvitesMeta.setDisplayName("Party Invites");
        partyInvites.setItemMeta(partyInvitesMeta);
        gui.setItem(13, partyInvites);

        ItemStack profileViewing = new ItemStack(Material.PAPER, 1);
        ItemMeta profileViewingMeta = profileViewing.getItemMeta();
        profileViewingMeta.setDisplayName("Profile Viewing");
        profileViewing.setItemMeta(profileViewingMeta);
        gui.setItem(14, profileViewing);

        return gui;
    }

    public boolean isProfileViewingAllowed(Player player) {
        Map<String, Boolean> settings = privacySettings.get(player.getUniqueId());
        return settings != null && settings.getOrDefault("profileViewing", true);
    }

    public void togglePrivacySetting(Player player, String setting) {
        UUID playerId = player.getUniqueId();
        Map<String, Boolean> settings = privacySettings.computeIfAbsent(playerId, k -> new HashMap<>());
        settings.put(setting, !settings.getOrDefault(setting, true));
        savePrivacySettings();
        player.sendMessage(ChatColor.GREEN + setting + " is now " + (settings.get(setting) ? "enabled" : "disabled"));
    }

    private String getPlayerRank(Player player) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                QueryOptions queryOptions = QueryOptions.defaultContextualOptions();
                String prefix = user.getCachedData().getMetaData(queryOptions).getPrefix();
                return prefix != null ? prefix : "[NO RANK]";
            }
        } catch (IllegalStateException e) {
            // LuckPerms API not available
        }
        return "[NO RANK]";
    }

    public void saveProfiles() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, String>> entry : profiles.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(new File(dataFolder, "profiles.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProfiles() {
        File file = new File(dataFolder, "profiles.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            Map<String, String> profileData = new HashMap<>();
            for (String dataKey : config.getConfigurationSection(key).getKeys(false)) {
                profileData.put(dataKey, config.getString(key + "." + dataKey));
            }
            profiles.put(playerId, profileData);
        }
    }

    private void savePrivacySettings() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Boolean>> entry : privacySettings.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(new File(dataFolder, "privacySettings.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPrivacySettings() {
        File file = new File(dataFolder, "privacySettings.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            Map<String, Boolean> settings = new HashMap<>();
            for (String settingKey : config.getConfigurationSection(key).getKeys(false)) {
                settings.put(settingKey, config.getBoolean(key + "." + settingKey));
            }
            privacySettings.put(playerId, settings);
        }
    }
}
