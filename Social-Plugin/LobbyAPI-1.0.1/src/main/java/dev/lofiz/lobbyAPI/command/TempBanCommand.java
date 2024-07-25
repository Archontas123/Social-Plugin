package dev.lofiz.lobbyAPI.command;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Arrays;

public class TempBanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <time> <days/hours/weeks/minutes> <reason>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        long duration;
        try {
            duration = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid time duration.");
            return true;
        }

        String timeUnit = args[2];
        long multiplier;

        switch (timeUnit.toLowerCase()) {
            case "days":
                multiplier = 24 * 60 * 60 * 1000L;
                break;
            case "hours":
                multiplier = 60 * 60 * 1000L;
                break;
            case "weeks":
                multiplier = 7 * 24 * 60 * 60 * 1000L;
                break;
            case "minutes":
                multiplier = 60 * 1000L;
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid time unit. Use days, hours, weeks, or minutes.");
                return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        long banEnd = System.currentTimeMillis() + duration * multiplier;
        Date banEndDate = new Date(banEnd);
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, banEndDate, sender.getName());

        target.kickPlayer(ChatColor.RED + "You have been banned for " + duration + " " + timeUnit + " for: " + reason);

        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been banned for " + duration + " " + timeUnit + " for: " + reason);
        return true;
    }
}
