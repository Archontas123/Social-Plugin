package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.PlayerProfileManager;
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

        if (args.length < 1) {
            player.sendMessage("Usage: /profile <create|setting|view|delete> [<key> <value>]");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "create":
                profileManager.createProfile(player);
                break;
            case "setting":
                if (args.length < 3) {
                    player.sendMessage("Usage: /profile setting <key> <value>");
                    return true;
                }
                String key = args[1];
                String value = args[2];
                profileManager.updateSetting(player, key, value);
                break;
            case "view":
                profileManager.viewProfile(player);
                break;
            case "delete":
                profileManager.deleteProfile(player);
                break;
            default:
                player.sendMessage("Usage: /profile <create|setting|view|delete> [<key> <value>]");
                break;
        }
        return true;
    }
}
