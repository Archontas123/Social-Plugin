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
            player.sendMessage("Usage: /friend <add|remove|list|accept> <player>");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add":
                if (args.length < 2) {
                    player.sendMessage("Usage: /friend add <player>");
                    return true;
                }
                Player friend = player.getServer().getPlayer(args[1]);
                if (friend != null) {
                    friendManager.sendFriendRequest(player, friend);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage("Usage: /friend remove <player>");
                    return true;
                }
                friend = player.getServer().getPlayer(args[1]);
                if (friend != null) {
                    friendManager.removeFriend(player, friend);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            case "list":
                player.sendMessage("Friends:");
                for (Player friendPlayer : friendManager.listFriends(player)) {
                    player.sendMessage("- " + friendPlayer.getName());
                }
                break;
            case "accept":
                if (args.length < 2) {
                    player.sendMessage("Usage: /friend accept <player>");
                    return true;
                }
                friend = player.getServer().getPlayer(args[1]);
                if (friend != null) {
                    friendManager.acceptFriendRequest(player, friend);
                } else {
                    player.sendMessage("Player not found.");
                }
                break;
            default:
                player.sendMessage("Usage: /friend <add|remove|list|accept> <player>");
                break;
        }
        return true;
    }
}
