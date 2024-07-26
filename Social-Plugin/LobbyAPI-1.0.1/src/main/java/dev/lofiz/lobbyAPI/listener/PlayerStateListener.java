package dev.lofiz.lobbyAPI.listener;

import dev.lofiz.lobbyAPI.manager.FriendManager;
import dev.lofiz.lobbyAPI.manager.IgnoreManager;
import dev.lofiz.lobbyAPI.manager.GuildManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerStateListener implements Listener {
    private final FriendManager friendManager;
    private final IgnoreManager ignoreManager;
    private final GuildManager guildManager;
    private final Map<Player, String> playerStates;

    public PlayerStateListener(FriendManager friendManager, IgnoreManager ignoreManager, GuildManager guildManager) {
        this.friendManager = friendManager;
        this.ignoreManager = ignoreManager;
        this.guildManager = guildManager;
        this.playerStates = new HashMap<>();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String state = playerStates.get(player);
        if (state != null) {
            event.setCancelled(true);
            String input = event.getMessage();
            Player target = player.getServer().getPlayer(input);
            switch (state) {
                case "addFriend":
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    } else {
                        friendManager.sendFriendRequest(player, target);
                    }
                    break;
                case "ignorePlayer":
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    } else {
                        ignoreManager.addIgnoredPlayer(player, target);
                        friendManager.removeFriend(player, target);
                    }
                    break;
                case "createGuild":
                    if (guildManager.getGuild(player) != null) {
                        player.sendMessage(ChatColor.RED + "You already have a guild.");
                    } else {
                        guildManager.createGuild(player, input);
                        player.sendMessage(ChatColor.GREEN + "Guild '" + input + "' created.");
                    }
                    break;
            }
            playerStates.remove(player);
        }
    }

    public void setPlayerState(Player player, String state) {
        playerStates.put(player, state);
    }
}
