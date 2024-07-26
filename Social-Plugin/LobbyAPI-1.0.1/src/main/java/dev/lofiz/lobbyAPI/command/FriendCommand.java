package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.FriendManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommand implements CommandExecutor {
    private final FriendManager friendManager;

    public FriendCommand(FriendManager friendManager) {
        this.friendManager = friendManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /friend <add|remove|list|accept|deny> [player]");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add":
                if (args.length < 2) {
                    player.sendMessage("Usage: /friend add <player>");
                    return true;
                }
                Player friendToAdd = player.getServer().getPlayer(args[1]);
                if (friendToAdd != null) {
                    friendManager.sendFriendRequest(player, friendToAdd);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage("Usage: /friend remove <player>");
                    return true;
                }
                Player friendToRemove = player.getServer().getPlayer(args[1]);
                if (friendToRemove != null) {
                    friendManager.removeFriend(player, friendToRemove);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            case "list":
                player.openInventory(friendManager.getFriendListGUI(player));
                break;
            case "accept":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /friend accept <player>");
                    return true;
                }
                Player friendToAccept = player.getServer().getPlayer(args[1]);
                if (friendToAccept != null) {
                    friendManager.acceptFriendRequest(player, friendToAccept);
                } else {
                    player.sendMessage(ChatColor.RED +"Player not found.");
                }
                break;
            case "deny":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /friend deny <player>");
                    return true;
                }
                Player friendToDeny = player.getServer().getPlayer(args[1]);
                if (friendToDeny != null) {
                    friendManager.denyFriendRequest(player, friendToDeny);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /friend <add|remove|list|accept|deny> [player]");
                break;
        }
        return true;
    }
}
