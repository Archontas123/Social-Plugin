package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.FriendManager;
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

        if (args.length < 2) {
            player.sendMessage("Usage: /friend <add|remove|list> <player>");
            return true;
        }

        String action = args[0].toLowerCase();
        String targetName = args[1];
        Player target = player.getServer().getPlayer(targetName);

        if (target == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        switch (action) {
            case "add":
                friendManager.addFriend(player, target);
                player.sendMessage("Added " + targetName + " as a friend.");
                break;
            case "remove":
                friendManager.removeFriend(player, target);
                player.sendMessage("Removed " + targetName + " from friends.");
                break;
            case "list":
                player.sendMessage("Friends:");
                for (Player friend : friendManager.listFriends(player)) {
                    player.sendMessage("- " + friend.getName());
                }
                break;
            default:
                player.sendMessage("Usage: /friend <add|remove|list> <player>");
                break;
        }
        return true;
    }
}
