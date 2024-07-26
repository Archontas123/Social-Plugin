package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand implements CommandExecutor {
    private final PlayerProfileManager profileManager;

    public ProfileCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("view")) {
            player.openInventory(profileManager.getProfileGUI(player));
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            profileManager.createProfile(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            // Add delete profile logic here
            return true;
        }

        if (args[0].equalsIgnoreCase("setting")) {
            if (args.length < 3) {
                player.sendMessage("Usage: /profile setting <key> <value>");
                return true;
            }
            String key = args[1];
            String value = args[2];
            // Add setting logic here
            return true;
        }

        player.sendMessage("Invalid command. Usage: /profile <create|view|delete|setting>");
        return true;
    }
}
