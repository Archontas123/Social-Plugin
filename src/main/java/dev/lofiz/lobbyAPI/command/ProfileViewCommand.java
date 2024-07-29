package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ProfileViewCommand implements CommandExecutor {
    private PlayerProfileManager playerProfileManager;

    public ProfileViewCommand(PlayerProfileManager playerProfileManager) {
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /profile view <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        if (playerProfileManager.isProfileViewingAllowed(target)) {
            player.openInventory(playerProfileManager.getProfileGUI(target));
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to view this player's profile.");
        }

        return true;
    }
}
