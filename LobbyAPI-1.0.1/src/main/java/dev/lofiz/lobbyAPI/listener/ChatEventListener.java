package dev.lofiz.lobbyAPI.listener;

import dev.lofiz.lobbyAPI.command.MuteCommand;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEventListener implements Listener {
    private final MuteCommand muteCommand;

    public ChatEventListener(MuteCommand command) {
        this.muteCommand = command;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (this.muteCommand.isMuted(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(String.valueOf(ChatColor.RED) + "You Are Currently Muted!");
            event.setCancelled(true);
        }

    }
}
