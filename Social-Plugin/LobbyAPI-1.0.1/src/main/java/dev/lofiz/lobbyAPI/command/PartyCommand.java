package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.PartyManager;
import dev.lofiz.lobbyAPI.model.Party;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PartyCommand implements CommandExecutor {
    private final PartyManager partyManager;

    public PartyCommand(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /party <create|invite|accept|deny|join|leave|kick|disband|warp|chat|promote|transfer> [<player>]");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "create":
                partyManager.createParty(player);
                break;
            case "invite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party invite <player>");
                    return true;
                }
                Player invitee = player.getServer().getPlayer(args[1]);
                if (invitee != null) {
                    partyManager.sendPartyInvite(player, invitee);
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                }
                break;
            case "accept":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party accept <leader>");
                    return true;
                }
                Player leader = player.getServer().getPlayer(args[1]);
                if (leader != null) {
                    partyManager.acceptPartyInvite(player, leader);
                } else {
                    player.sendMessage(ChatColor.RED + "Leader not found.");
                }
                break;
            case "deny":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party deny <leader>");
                    return true;
                }
                leader = player.getServer().getPlayer(args[1]);
                if (leader != null) {
                    partyManager.denyPartyInvite(player, leader);
                } else {
                    player.sendMessage(ChatColor.RED + "Leader not found.");
                }
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party join <leader>");
                    return true;
                }
                leader = player.getServer().getPlayer(args[1]);
                if (leader != null) {
                    partyManager.joinParty(player, leader);
                } else {
                    player.sendMessage(ChatColor.RED + "Leader not found.");
                }
                break;
            case "leave":
                partyManager.leaveParty(player);
                break;
            case "kick":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party kick <member>");
                    return true;
                }
                Player member = player.getServer().getPlayer(args[1]);
                if (member != null) {
                    partyManager.kickMember(player, member);
                } else {
                    player.sendMessage(ChatColor.RED + "Member not found.");
                }
                break;
            case "disband":
                partyManager.disbandParty(player);
                break;
            case "warp":
                Location location = player.getLocation();
                partyManager.warpParty(player, location);
                break;
            case "chat":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party chat <message>");
                    return true;
                }
                Party party = partyManager.getParty(player);
                if (party != null) {
                    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    party.partyChat(player, message);
                } else {
                    player.sendMessage(ChatColor.RED + "You are not in a party.");
                }
                break;
            case "promote":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party promote <player>");
                    return true;
                }
                Player promotee = player.getServer().getPlayer(args[1]);
                if (promotee != null) {
                    partyManager.promoteToModerator(player, promotee);
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                }
                break;
            case "transfer":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party transfer <player>");
                    return true;
                }
                Player newLeader = player.getServer().getPlayer(args[1]);
                if (newLeader != null) {
                    partyManager.transferLeadership(player, newLeader);
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /party <create|invite|accept|deny|join|leave|kick|disband|warp|chat|promote|transfer> [<player>]");
                break;
        }
        return true;
    }
}
