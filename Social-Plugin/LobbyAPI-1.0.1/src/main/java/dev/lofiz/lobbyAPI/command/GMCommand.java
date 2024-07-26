

package dev.lofiz.lobbyAPI.command;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GMCommand implements CommandExecutor {
    public GMCommand() {
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player)commandSender;
        if (command.getName().equalsIgnoreCase("gmc")) {
            commandSender.sendMessage(String.valueOf(ChatColor.GREEN) + "Changed your Gamemode to Creative!");
            player.setGameMode(GameMode.CREATIVE);
            return true;
        } else if (command.getName().equalsIgnoreCase("gms")) {
            commandSender.sendMessage(String.valueOf(ChatColor.GREEN) + "Changed your Gamemode to Survival!");
            player.setGameMode(GameMode.SURVIVAL);
            return true;
        } else if (command.getName().equalsIgnoreCase("gmsp")) {
            commandSender.sendMessage(String.valueOf(ChatColor.GREEN) + "Changed your Gamemode to Spectator!");
            player.setGameMode(GameMode.SPECTATOR);
            return true;
        } else if (command.getName().equalsIgnoreCase("gma")) {
            commandSender.sendMessage(String.valueOf(ChatColor.GREEN) + "Changed your Gamemode to Adventure!");
            player.setGameMode(GameMode.ADVENTURE);
            return true;
        } else {
            return false;
        }
    }
}
