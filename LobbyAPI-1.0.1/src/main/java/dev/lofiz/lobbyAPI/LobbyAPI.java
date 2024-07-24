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
        getCommand("friend").setExecutor(new FriendCommand(friendManager));
        getCommand("ignore").setExecutor(new IgnoreCommand(ignoreManager));
        getCommand("profile").setExecutor(new ProfileCommand(playerProfileManager));
        getCommand("party").setExecutor(new PartyCommand(partyManager));
        getCommand("guild").setExecutor(new GuildCommand(guildManager));

        // Register mute commands
        MuteCommand muteCommand = new MuteCommand(this);
        getCommand("mute").setExecutor(muteCommand);
        getCommand("unmute").setExecutor(muteCommand);

        // Register GM commands
        GMCommand gmCommand = new GMCommand();
        getCommand("gmc").setExecutor(gmCommand);
        getCommand("gms").setExecutor(gmCommand);
        getCommand("gmsp").setExecutor(gmCommand);
        getCommand("gma").setExecutor(gmCommand);

        // Register link commands
        LinkCommand linkCommand = new LinkCommand();
        getCommand("discord").setExecutor(linkCommand);
        getCommand("store").setExecutor(linkCommand);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new ChatEventListener(muteCommand), this);
    }

    @Override
    public void onDisable() {
        // Perform any necessary cleanup here
    }
}
