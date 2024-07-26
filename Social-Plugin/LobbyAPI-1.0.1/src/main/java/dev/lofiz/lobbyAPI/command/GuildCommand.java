package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.model.Guild;
import dev.lofiz.lobbyAPI.manager.GuildManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GuildCommand implements CommandExecutor {
    private final GuildManager guildManager;

    public GuildCommand(GuildManager guildManager) {
        this.guildManager = guildManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /guild <create|invite|accept|deny|join|leave|disband|list|chat> [<name>]");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /guild create <name>");
                    return true;
                }
                String guildName = args[1];
                guildManager.createGuild(player, guildName);
                break;
            case "invite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /guild invite <player>");
                    return true;
                }
                Player invitee = player.getServer().getPlayer(args[1]);
                if (invitee != null) {
                    guildManager.sendGuildInvite(player, invitee);
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                }
                break;
            case "accept":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /guild accept <name>");
                    return true;
                }
                guildManager.acceptGuildInvite(player, args[1]);
                break;
            case "deny":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /guild deny <name>");
                    return true;
                }
                guildManager.denyGuildInvite(player, args[1]);
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /guild join <name>");
                    return true;
                }
                guildManager.joinGuild(player, args[1]);
                break;
            case "leave":
                guildManager.leaveGuild(player);
                break;
            case "disband":
                guildManager.disbandGuild(player);
                break;
            case "list":
                player.sendMessage("Guild members: " + String.join(", ", guildManager.listGuildMembers(player)));
                break;
            case "chat":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /guild chat <message>");
                    return true;
                }
                Guild guild = guildManager.getGuild(player);
                if (guild != null) {
                    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    guild.guildChat(player, message);
                } else {
                    player.sendMessage(ChatColor.RED + "You are not in a guild.");
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /guild <create|invite|accept|deny|join|leave|disband|list|chat> [<name>]");
                break;
        }
        return true;
    }
}
