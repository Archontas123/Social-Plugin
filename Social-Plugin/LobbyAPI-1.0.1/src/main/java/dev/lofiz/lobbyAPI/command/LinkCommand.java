
package dev.lofiz.lobbyAPI.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LinkCommand implements CommandExecutor {
    public LinkCommand() {
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("store")) {
            commandSender.sendMessage(String.valueOf(ChatColor.BLUE) + "https://youtube.com");
            return true;
        } else if (command.getName().equalsIgnoreCase("discord")) {
            commandSender.sendMessage(String.valueOf(ChatColor.BLUE) + "Join the Discord: discord.gg/idontthinkthisserverexists");
            return true;
        } else {
            return false;
        }
    }
}
