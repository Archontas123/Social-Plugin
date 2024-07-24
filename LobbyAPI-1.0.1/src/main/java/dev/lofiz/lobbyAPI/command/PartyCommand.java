package dev.lofiz.lobbyAPI.command;

import dev.lofiz.lobbyAPI.manager.PartyManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            player.sendMessage("Usage: /party <create|join|leave|kick|disband|warp>");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "create":
                partyManager.createParty(player);
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage("Usage: /party join <leader>");
                    return true;
                }
                Player leader = player.getServer().getPlayer(args[1]);
                if (leader != null) {
                    partyManager.joinParty(player, leader);
                } else {
                    player.sendMessage("Leader not found.");
                }
                break;
            case "leave":
                partyManager.leaveParty(player);
                break;
            case "kick":
                if (args.length < 2) {
                    player.sendMessage("Usage: /party kick <member>");
                    return true;
                }
                Player member = player.getServer().getPlayer(args[1]);
                if (member != null) {
                    partyManager.kickMember(player, member);
                } else {
                    player.sendMessage("Member not found.");
                }
                break;
            case "disband":
                partyManager.disbandParty(player);
                break;
            case "warp":
                Location location = player.getLocation();
                partyManager.warpParty(player, location);
                break;
            default:
                player.sendMessage("Usage: /party <create|join|leave|kick|disband|warp>");
                break;
        }
        return true;
    }
}
