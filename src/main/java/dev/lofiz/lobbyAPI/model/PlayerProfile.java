package dev.lofiz.lobbyAPI.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProfile {
    private UUID playerId;
    private Map<String, String> settings;

    public PlayerProfile(UUID playerId) {
        this.playerId = playerId;
        this.settings = new HashMap<>();
        // Initialize default settings
        settings.put("privacy", "public");
        settings.put("notifications", "enabled");
    }

    public PlayerProfile(UUID playerId, Map<String, String> settings) {
        this.playerId = playerId;
        this.settings = settings;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void updateSetting(String key, String value) {
        settings.put(key, value);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
