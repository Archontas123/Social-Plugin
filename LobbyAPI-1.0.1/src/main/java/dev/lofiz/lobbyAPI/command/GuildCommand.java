package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.GuildManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuildCommand implements CommandExecutor {
    private final GuildManager guildManager;

    public GuildCommand(GuildManager guildManager) {
        this.guildManager = guildManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /guild <create|join|leave|disband> [name]");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("Usage: /guild create <name>");
                    return true;
                }
                String guildName = args[1];
                guildManager.createGuild(player, guildName);
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage("Usage: /guild join <name>");
                    return true;
                }
                guildName = args[1];
                guildManager.joinGuild(player, guildName);
                break;
            case "leave":
                if (args.length < 2) {
                    player.sendMessage("Usage: /guild leave <name>");
                    return true;
                }
                guildName = args[1];
                guildManager.leaveGuild(player, guildName);
                break;
            case "disband":
                if (args.length < 2) {
                    player.sendMessage("Usage: /guild disband <name>");
                    return true;
                }
                guildName = args[1];
                guildManager.disbandGuild(player, guildName);
                break;
            default:
                player.sendMessage("Usage: /guild <create|join|leave|disband> [name]");
                break;
        }
        return true;
    }
}
