package dev.lofiz.lobbyAPI.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class MuteCommand implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> mutedPlayers = new HashMap<>();

    public MuteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadMuteData();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("mute")) {
            return handleMuteCommand(commandSender, args);
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            return handleUnmuteCommand(commandSender, args);
        }
        return false;
    }

    private boolean handleMuteCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Please specify a player and mute duration.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        int muteDuration;
        try {
            muteDuration = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid mute duration.");
            return true;
        }

        mutePlayer(targetPlayer, muteDuration);
        sender.sendMessage(ChatColor.GREEN + "You have successfully muted " + targetPlayer.getName() + " for " + muteDuration + " seconds.");
        targetPlayer.sendMessage(ChatColor.RED + "You have been muted for " + muteDuration + " seconds.");
        return true;
    }

    private boolean handleUnmuteCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Please specify a player.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        unmutePlayer(targetPlayer.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "You have successfully unmuted " + targetPlayer.getName() + ".");
        targetPlayer.sendMessage(ChatColor.GREEN + "You have been unmuted.");
        return true;
    }

    public boolean isMuted(Player player) {
        return isMuted(player.getUniqueId());
    }

    public boolean isMuted(UUID playerUUID) {
        Long unmuteTime = mutedPlayers.get(playerUUID);
        return unmuteTime != null && System.currentTimeMillis() < unmuteTime;
    }

    private void mutePlayer(Player player, int duration) {
        final UUID playerUUID = player.getUniqueId();
        long unmuteTime = System.currentTimeMillis() + (long) duration * 1000L;
        mutedPlayers.put(playerUUID, unmuteTime);
        saveMuteData();
        new BukkitRunnable() {
            @Override
            public void run() {
                unmutePlayer(playerUUID);
            }
        }.runTaskLater(plugin, (long) duration * 20L);
    }

    private void unmutePlayer(UUID playerUUID) {
        mutedPlayers.remove(playerUUID);
        saveMuteData();
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "You have been unmuted.");
        }
    }

    private void saveMuteData() {
        FileConfiguration config = plugin.getConfig();
        for (UUID uuid : mutedPlayers.keySet()) {
            config.set("mutedPlayers." + uuid.toString(), mutedPlayers.get(uuid));
        }
        plugin.saveConfig();
    }

    private void loadMuteData() {
        FileConfiguration config = plugin.getConfig();
        if (config.contains("mutedPlayers")) {
            for (String key : config.getConfigurationSection("mutedPlayers").getKeys(false)) {
                final UUID uuid = UUID.fromString(key);
                long unmuteTime = config.getLong("mutedPlayers." + key);
                if (System.currentTimeMillis() < unmuteTime) {
                    mutedPlayers.put(uuid, unmuteTime);
                    long remainingTime = (unmuteTime - System.currentTimeMillis()) / 1000L;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            unmutePlayer(uuid);
                        }
                    }.runTaskLater(plugin, remainingTime * 20L);
                }
            }
        }
    }
}
