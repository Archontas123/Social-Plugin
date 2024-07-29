package dev.lofiz.lobbyAPI;

import dev.lofiz.lobbyAPI.command.*;
import dev.lofiz.lobbyAPI.listener.ChatEventListener;
import dev.lofiz.lobbyAPI.listener.PlayerStateListener;
import dev.lofiz.lobbyAPI.listener.ProfileGUIListener;
import dev.lofiz.lobbyAPI.manager.*;
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
        ignoreManager = new IgnoreManager(getDataFolder(), friendManager);
        playerProfileManager = new PlayerProfileManager(getDataFolder());
        partyManager = new PartyManager(getDataFolder());
        guildManager = new GuildManager(getDataFolder());

        // Register commands and tab completers
        if (getCommand("friend") != null) {
            getCommand("friend").setExecutor(new FriendCommand(friendManager));
            getCommand("friend").setTabCompleter(new FriendTabCompleter());
        }
        if (getCommand("ignore") != null) {
            getCommand("ignore").setExecutor(new IgnoreCommand(ignoreManager));
            getCommand("ignore").setTabCompleter(new IgnoreTabCompleter());
        }
        if (getCommand("profile") != null) {
            getCommand("profile").setExecutor(new ProfileCommand(playerProfileManager));
            getCommand("profile").setTabCompleter(new ProfileTabCompleter());
        }
        if (getCommand("party") != null) {
            getCommand("party").setExecutor(new PartyCommand(partyManager));
            getCommand("party").setTabCompleter(new PartyTabCompleter());
        }
        if (getCommand("guild") != null) {
            getCommand("guild").setExecutor(new GuildCommand(guildManager));
            getCommand("guild").setTabCompleter(new GuildTabCompleter());
        }
        if (getCommand("tempban") != null) {
            getCommand("tempban").setExecutor(new TempBanCommand());
            getCommand("tempban").setTabCompleter(new TempBanTabCompleter());
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
        PlayerStateListener playerStateListener = new PlayerStateListener(friendManager, ignoreManager, guildManager);

        getServer().getPluginManager().registerEvents(new ChatEventListener(ignoreManager, partyManager, guildManager), this);
        getServer().getPluginManager().registerEvents(ignoreManager, this);
        getServer().getPluginManager().registerEvents(playerProfileManager, this);
        getServer().getPluginManager().registerEvents(new ProfileGUIListener(friendManager, ignoreManager, partyManager, guildManager, playerProfileManager), this);
    }

    @Override
    public void onDisable() {
        // Perform any necessary cleanup here
    }
}
