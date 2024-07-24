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
    private final HashMap<UUID, Long> mutedPlayers = new HashMap();

    public MuteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadMuteData();
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String var10001;
        Player targetPlayer;
        if (command.getName().equalsIgnoreCase("mute")) {
            if (strings.length < 2) {
                commandSender.sendMessage(String.valueOf(ChatColor.RED) + "Please Specify a Player And Mute Time");
                return true;
            } else {
                targetPlayer = Bukkit.getPlayer(strings[0]);
                if (targetPlayer == null) {
                    commandSender.sendMessage(String.valueOf(ChatColor.RED) + "Player Not Found");
                    return true;
                } else {
                    int muteDuration;
                    try {
                        muteDuration = Integer.parseInt(strings[1]);
                    } catch (NumberFormatException var8) {
                        commandSender.sendMessage(String.valueOf(ChatColor.RED) + "This Mute Duration is Invalid!");
                        return false;
                    }

                    this.mutePlayer(targetPlayer, muteDuration);
                    var10001 = String.valueOf(ChatColor.GREEN);
                    commandSender.sendMessage(var10001 + "You Have Successfully Muted " + targetPlayer.getName() + " for " + muteDuration + " seconds");
                    var10001 = String.valueOf(ChatColor.RED);
                    targetPlayer.sendMessage(var10001 + "You Have Been Muted For " + muteDuration + " seconds");
                    return true;
                }
            }
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            if (strings.length != 1) {
                commandSender.sendMessage(String.valueOf(ChatColor.RED) + "Please Specify a Player");
                return true;
            } else {
                targetPlayer = Bukkit.getPlayer(strings[0]);
                if (targetPlayer == null) {
                    commandSender.sendMessage(String.valueOf(ChatColor.RED) + "Player Not Found");
                    return true;
                } else {
                    this.unmutePlayer(targetPlayer.getUniqueId());
                    var10001 = String.valueOf(ChatColor.GREEN);
                    commandSender.sendMessage(var10001 + "You Have Successfully UnMuted " + targetPlayer.getName());
                    targetPlayer.sendMessage(String.valueOf(ChatColor.GREEN) + "You Have Been UnMuted");
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public boolean isMuted(UUID playerUUID) {
        Long unmuteTime = (Long)this.mutedPlayers.get(playerUUID);
        return unmuteTime != null && System.currentTimeMillis() < unmuteTime;
    }

    private void mutePlayer(Player player, int duration) {
        final UUID playerUUID = player.getUniqueId();
        long unmuteTime = System.currentTimeMillis() + (long)duration * 1000L;
        this.mutedPlayers.put(playerUUID, unmuteTime);
        this.saveMuteData();
        (new BukkitRunnable() {
            public void run() {
                MuteCommand.this.unmutePlayer(playerUUID);
            }
        }).runTaskLater(this.plugin, (long)duration * 20L);
    }

    private void unmutePlayer(UUID playerUUID) {
        this.mutedPlayers.remove(playerUUID);
        this.saveMuteData();
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + "You Have Been Unmuted");
        }

    }

    private void saveMuteData() {
        FileConfiguration config = this.plugin.getConfig();
        Iterator var2 = this.mutedPlayers.keySet().iterator();

        while(var2.hasNext()) {
            UUID uuid = (UUID)var2.next();
            config.set("mutedPlayers." + uuid.toString(), this.mutedPlayers.get(uuid));
        }

        this.plugin.saveConfig();
    }

    private void loadMuteData() {
        FileConfiguration config = this.plugin.getConfig();
        if (config.contains("mutedPlayers")) {
            Iterator var2 = config.getConfigurationSection("mutedPlayers").getKeys(false).iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                final UUID uuid = UUID.fromString(key);
                long unmuteTime = config.getLong("mutedPlayers." + key);
                if (System.currentTimeMillis() < unmuteTime) {
                    this.mutedPlayers.put(uuid, unmuteTime);
                    long remainingTime = (unmuteTime - System.currentTimeMillis()) / 1000L;
                    (new BukkitRunnable() {
                        public void run() {
                            MuteCommand.this.unmutePlayer(uuid);
                        }
                    }).runTaskLater(this.plugin, remainingTime * 20L);
                }
            }
        }

    }
}