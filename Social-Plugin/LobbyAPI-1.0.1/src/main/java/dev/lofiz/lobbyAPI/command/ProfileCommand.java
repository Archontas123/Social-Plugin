package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.PlayerProfileManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProfileCommand implements CommandExecutor {
    private final PlayerProfileManager playerProfileManager;

    public ProfileCommand(PlayerProfileManager playerProfileManager) {
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.openInventory(playerProfileManager.getProfileGUI(player));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("view")) {
            player.sendMessage(ChatColor.RED + "Usage: /profile view <player>");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("view")) {
            Player target = player.getServer().getPlayer(args[1]);
            if (target != null) {
                if (playerProfileManager.isProfileViewingAllowed(target)) {
                    player.openInventory(playerProfileManager.getProfileGUI(target));
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view this player's profile.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Player not found.");
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /profile or /profile view <player>");
        return true;
    }
}
