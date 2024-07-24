package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.IgnoreManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandExecutor {
    private final IgnoreManager ignoreManager;

    public IgnoreCommand(IgnoreManager ignoreManager) {
        this.ignoreManager = ignoreManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /ignore <add|remove|list> <player>");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add":
                if (args.length < 2) {
                    player.sendMessage("Usage: /ignore add <player>");
                    return true;
                }
                Player toIgnore = player.getServer().getPlayer(args[1]);
                if (toIgnore != null) {
                    ignoreManager.addIgnoredPlayer(player, toIgnore);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage("Usage: /ignore remove <player>");
                    return true;
                }
                Player toUnignore = player.getServer().getPlayer(args[1]);
                if (toUnignore != null) {
                    ignoreManager.removeIgnoredPlayer(player, toUnignore);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            case "list":
                player.sendMessage("Ignored Players:");
                for (Player ignoredPlayer : ignoreManager.listIgnoredPlayers(player)) {
                    player.sendMessage("- " + ignoredPlayer.getName());
                }
                break;
            default:
                player.sendMessage("Usage: /ignore <add|remove|list> <player>");
                break;
        }
        return true;
    }
}
