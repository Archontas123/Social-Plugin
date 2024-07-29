package dev.lofiz.lobbyAPI.listener;

import dev.lofiz.lobbyAPI.manager.IgnoreManager;
import dev.lofiz.lobbyAPI.manager.PartyManager;
import dev.lofiz.lobbyAPI.manager.GuildManager;
import dev.lofiz.lobbyAPI.model.Guild;
import dev.lofiz.lobbyAPI.model.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEventListener implements Listener {
    private final IgnoreManager ignoreManager;
    private final PartyManager partyManager;
    private final GuildManager guildManager;

    public ChatEventListener(IgnoreManager ignoreManager, PartyManager partyManager, GuildManager guildManager) {
        this.ignoreManager = ignoreManager;
        this.partyManager = partyManager;
        this.guildManager = guildManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Party chat
        if (message.startsWith("/p ")) {
            event.setCancelled(true);
            message = message.substring(3);
            Party party = partyManager.getParty(player);
            if (party != null) {
                party.broadcast(ChatColor.GRAY + "[" + ChatColor.BLUE + "Party" + ChatColor.GRAY + "] " + ChatColor.RESET + player.getName() + ": " + message);
            } else {
                player.sendMessage(ChatColor.RED + "You are not in a party.");
            }
            return;
        }

        // Guild chat
        if (message.startsWith("/gchat ")) {
            event.setCancelled(true);
            message = message.substring(7);
            Guild guild = guildManager.getGuild(player);
            if (guild != null) {
                guild.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Guild" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + player.getName() + ChatColor.WHITE + ": " + message);
            } else {
                player.sendMessage(ChatColor.RED + "You are not in a guild.");
            }
        }
    }
}
