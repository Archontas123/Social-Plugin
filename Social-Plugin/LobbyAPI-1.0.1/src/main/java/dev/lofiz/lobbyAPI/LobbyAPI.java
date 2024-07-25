package dev.lofiz.lobbyAPI;

import dev.lofiz.lobbyAPI.command.*;
import dev.lofiz.lobbyAPI.listener.ChatEventListener;
import dev.lofiz.lobbyAPI.manager.FriendManager;
import dev.lofiz.lobbyAPI.manager.IgnoreManager;
import dev.lofiz.lobbyAPI.manager.PlayerProfileManager;
import dev.lofiz.lobbyAPI.manager.PartyManager;
import dev.lofiz.lobbyAPI.manager.GuildManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LobbyAPI extends JavaPlugin {
    public static boolean isLobby = true;
    private FriendManager friendManager;
    private IgnoreManager ignoreManager;
    private PlayerProfileManager playerProfileManager;
    private PartyManager partyManager;
    private GuildManager guildManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize managers
        friendManager = new FriendManager(getDataFolder());
        ignoreManager = new IgnoreManager(getDataFolder());
        playerProfileManager = new PlayerProfileManager(getDataFolder());
        partyManager = new PartyManager(getDataFolder());
        guildManager = new GuildManager(getDataFolder());

        // Register commands
        if (getCommand("friend") != null) {
            getCommand("friend").setExecutor(new FriendCommand(friendManager));
        }
        if (getCommand("ignore") != null) {
            getCommand("ignore").setExecutor(new IgnoreCommand(ignoreManager));
        }
        if (getCommand("profile") != null) {
            getCommand("profile").setExecutor(new ProfileCommand(playerProfileManager));
        }
        if (getCommand("party") != null) {
            getCommand("party").setExecutor(new PartyCommand(partyManager));
        }
        if (getCommand("guild") != null) {
            getCommand("guild").setExecutor(new GuildCommand(guildManager));
        }
        if (getCommand("tempban") != null) {
            getCommand("tempban").setExecutor(new TempBanCommand());
        }

        // Register mute commands
        MuteCommand muteCommand = new MuteCommand(this);
        if (getCommand("mute") != null) {
            getCommand("mute").setExecutor(muteCommand);
        }
        if (getCommand("unmute") != null) {
            getCommand("unmute").setExecutor(muteCommand);
        }

        // Register GM commands
        GMCommand gmCommand = new GMCommand();
        if (getCommand("gmc") != null) {
            getCommand("gmc").setExecutor(gmCommand);
        }
        if (getCommand("gms") != null) {
            getCommand("gms").setExecutor(gmCommand);
        }
        if (getCommand("gmsp") != null) {
            getCommand("gmsp").setExecutor(gmCommand);
        }
        if (getCommand("gma") != null) {
            getCommand("gma").setExecutor(gmCommand);
        }

        // Register link commands
        LinkCommand linkCommand = new LinkCommand();
        if (getCommand("discord") != null) {
            getCommand("discord").setExecutor(linkCommand);
        }
        if (getCommand("store") != null) {
            getCommand("store").setExecutor(linkCommand);
        }

        // Register event listeners
        getServer().getPluginManager().registerEvents(new ChatEventListener(muteCommand, ignoreManager), this);
        getServer().getPluginManager().registerEvents(ignoreManager, this);
    }

    @Override
    public void onDisable() {
        // Perform any necessary cleanup here
    }
}
