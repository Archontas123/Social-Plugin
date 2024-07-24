import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerProfileManager {
    private Map<UUID, PlayerProfile> profiles;
    private File dataFile;

    public PlayerProfileManager(File dataFolder) {
        this.profiles = new HashMap<>();
        this.dataFile = new File(dataFolder, "profiles.yml");
        loadProfiles();
    }

    public void createProfile(Player player) {
        UUID playerId = player.getUniqueId();
        if (!profiles.containsKey(playerId)) {
            profiles.put(playerId, new PlayerProfile(playerId));
            saveProfiles();
            player.sendMessage("Profile created.");
        } else {
            player.sendMessage("Profile already exists.");
        }
    }

    public PlayerProfile getProfile(Player player) {
        return profiles.get(player.getUniqueId());
    }

    public void updateSetting(Player player, String setting, String value) {
        PlayerProfile profile = getProfile(player);
        if (profile != null) {
            profile.updateSetting(setting, value);
            saveProfiles();
            player.sendMessage("Setting updated: " + setting + " = " + value);
        } else {
            player.sendMessage("Profile not found.");
        }
    }

    public void viewProfile(Player player) {
        PlayerProfile profile = getProfile(player);
        if (profile != null) {
            player.sendMessage("Profile Settings:");
            for (Map.Entry<String, String> entry : profile.getSettings().entrySet()) {
                player.sendMessage(entry.getKey() + ": " + entry.getValue());
            }
        } else {
            player.sendMessage("Profile not found.");
        }
    }

    public void deleteProfile(Player player) {
        UUID playerId = player.getUniqueId();
        if (profiles.containsKey(playerId)) {
            profiles.remove(playerId);
            saveProfiles();
            player.sendMessage("Profile deleted.");
        } else {
            player.sendMessage("Profile not found.");
        }
    }

    private void saveProfiles() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, PlayerProfile> entry : profiles.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue().toMap());
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProfiles() {
        if (!dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            Map<String, String> settings = new HashMap<>();
            for (String setting : config.getConfigurationSection(key).getKeys(false)) {
                settings.put(setting, config.getString(key + "." + setting));
            }
            profiles.put(playerId, new PlayerProfile(playerId, settings));
        }
    }
}
