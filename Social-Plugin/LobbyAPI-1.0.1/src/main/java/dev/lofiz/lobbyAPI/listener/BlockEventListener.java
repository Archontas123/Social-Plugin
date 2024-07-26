package dev.lofiz.lobbyAPI.listener;

import dev.lofiz.lobbyAPI.LobbyAPI;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEventListener implements Listener {
    public BlockEventListener() {
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (LobbyAPI.isLobby && event.getBlock().getBlockData().getMaterial() != Material.WHITE_WOOL) {
                event.getPlayer().sendMessage(String.valueOf(ChatColor.RED) + "You can't break blocks here");
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (LobbyAPI.isLobby && event.getBlock().getBlockData().getMaterial() != Material.WHITE_WOOL) {
                event.getPlayer().sendMessage(String.valueOf(ChatColor.RED) + "You can only place white wool here!");
                event.setCancelled(true);
            }

        }
    }
}