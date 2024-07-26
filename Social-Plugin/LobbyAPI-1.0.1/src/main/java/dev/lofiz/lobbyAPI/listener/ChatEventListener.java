package dev.lofiz.lobbyAPI.listener;

import dev.lofiz.lobbyAPI.command.MuteCommand;
import dev.lofiz.lobbyAPI.manager.IgnoreManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatEventListener implements Listener {
    private final MuteCommand muteCommand;
    private final IgnoreManager ignoreManager;

    public ChatEventListener(MuteCommand muteCommand, IgnoreManager ignoreManager) {
        this.muteCommand = muteCommand;
        this.ignoreManager = ignoreManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();

        if (muteCommand.isMuted(sender)) {
            sender.sendMessage("You are muted and cannot send messages.");
            event.setCancelled(true);
            return;
        }

        event.getRecipients().removeIf(receiver -> ignoreManager.isIgnored(sender, receiver));
    }
}
