package dev.lofiz.lobbyAPI.manager;

import dev.lofiz.lobbyAPI.model.Guild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GuildManager {
    private Map<String, Guild> guilds;
    private Map<UUID, String> playerGuilds;
    private Map<UUID, String> guildInvites;
    private File dataFile;

    public GuildManager(File dataFolder) {
        this.guilds = new HashMap<>();
        this.playerGuilds = new HashMap<>();
        this.guildInvites = new HashMap<>();
        this.dataFile = new File(dataFolder, "guilds.yml");
        loadGuilds();
    }

    public void createGuild(Player leader, String guildName) {
        if (guilds.containsKey(guildName)) {
            leader.sendMessage(ChatColor.RED + "A guild with this name already exists.");
            return;
        }
        if (playerGuilds.containsKey(leader.getUniqueId())) {
            leader.sendMessage(ChatColor.RED + "You are already in a guild.");
            return;
        }
        Guild guild = new Guild(guildName, leader);
        guilds.put(guildName, guild);
        playerGuilds.put(leader.getUniqueId(), guildName);
        saveGuilds();
        leader.sendMessage(ChatColor.GREEN + "Guild created.");
    }

    public void joinGuild(Player player, String guildName) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            if (playerGuilds.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are already in a guild.");
                return;
            }
            guild.addMember(player);
            playerGuilds.put(player.getUniqueId(), guildName);
            saveGuilds();
            player.sendMessage(ChatColor.GREEN + "Joined the guild.");
            guild.broadcast(ChatColor.GREEN + player.getName() + " has joined the guild.");
        } else {
            player.sendMessage(ChatColor.RED + "Guild not found.");
        }
    }

    public void leaveGuild(Player player) {
        String guildName = playerGuilds.get(player.getUniqueId());
        if (guildName != null) {
            Guild guild = guilds.get(guildName);
            guild.removeMember(player);
            playerGuilds.remove(player.getUniqueId());
            saveGuilds();
            player.sendMessage(ChatColor.GREEN + "You have left the guild.");
            guild.broadcast(ChatColor.RED + player.getName() + " has left the guild.");
        } else {
            player.sendMessage(ChatColor.RED + "You are not in a guild.");
        }
    }

    public void disbandGuild(Player leader) {
        String guildName = playerGuilds.get(leader.getUniqueId());
        if (guildName != null) {
            Guild guild = guilds.get(guildName);
            if (guild.isLeader(leader)) {
                guild.broadcast(ChatColor.RED + "The guild has been disbanded.");
                for (UUID memberId : guild.getMembers()) {
                    playerGuilds.remove(memberId);
                }
                guilds.remove(guildName);
                saveGuilds();
                leader.sendMessage(ChatColor.GREEN + "Guild disbanded.");
            } else {
                leader.sendMessage(ChatColor.RED + "You are not the leader of this guild.");
            }
        } else {
            leader.sendMessage(ChatColor.RED + "You are not in a guild.");
        }
    }

    public void sendGuildInvite(Player leader, Player invitee) {
        Guild guild = guilds.get(playerGuilds.get(leader.getUniqueId()));
        if (guild != null && guild.isLeader(leader)) {
            guildInvites.put(invitee.getUniqueId(), guild.getName());
            TextComponent acceptButton = new TextComponent("[Accept]");
            acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild accept " + guild.getName()));
            TextComponent denyButton = new TextComponent("[Deny]");
            denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
            denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild deny " + guild.getName()));
            invitee.spigot().sendMessage(new TextComponent(leader.getName() + " has invited you to join the guild " + guild.getName() + ". "), acceptButton, new TextComponent(" "), denyButton);
            leader.sendMessage(ChatColor.GREEN + "Invite sent to " + invitee.getName() + ".");
        } else {
            leader.sendMessage(ChatColor.RED + "You are not the leader of a guild.");
        }
    }

    public void acceptGuildInvite(Player player, String guildName) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            if (playerGuilds.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are already in a guild.");
                return;
            }
            if (guildInvites.containsKey(player.getUniqueId()) && guildInvites.get(player.getUniqueId()).equals(guildName)) {
                guild.addMember(player);
                playerGuilds.put(player.getUniqueId(), guildName);
                saveGuilds();
                player.sendMessage(ChatColor.GREEN + "Joined the guild.");
                guild.broadcast(ChatColor.GREEN + player.getName() + " has joined the guild.");
                guildInvites.remove(player.getUniqueId());
            } else {
                player.sendMessage(ChatColor.RED + "No invite from guild " + guildName + ".");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Guild not found.");
        }
    }

    public void denyGuildInvite(Player player, String guildName) {
        if (guildInvites.containsKey(player.getUniqueId()) && guildInvites.get(player.getUniqueId()).equals(guildName)) {
            player.sendMessage(ChatColor.RED + "Guild invite from " + guildName + " denied.");
            guildInvites.remove(player.getUniqueId());
        } else {
            player.sendMessage(ChatColor.RED + "No invite from guild " + guildName + ".");
        }
    }

    public Inventory getGuildMembersGUI(Player player) {
        String guildName = playerGuilds.get(player.getUniqueId());
        Guild guild = guilds.get(guildName);
        Inventory gui = Bukkit.createInventory(null, 27, "Guild Members");
        if (guild != null) {
            for (UUID memberId : guild.getMembers()) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null) {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    meta.setOwningPlayer(member);
                    meta.setDisplayName(member.getName());
                    head.setItemMeta(meta);
                    gui.addItem(head);
                }
            }
        }
        return gui;
    }

    public String getGuildName(Player player) {
        return playerGuilds.get(player.getUniqueId());
    }

    public List<String> listGuildMembers(Player player) {
        String guildName = playerGuilds.get(player.getUniqueId());
        Guild guild = guilds.get(guildName);
        List<String> memberNames = new ArrayList<>();
        if (guild != null) {
            for (UUID memberId : guild.getMembers()) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null) {
                    memberNames.add(member.getName());
                }
            }
        }
        return memberNames;
    }

    public Guild getGuild(Player player) {
        String guildName = playerGuilds.get(player.getUniqueId());
        return guildName != null ? guilds.get(guildName) : null;
    }

    private void saveGuilds() {
        YamlConfiguration config = new YamlConfiguration();
        for (Guild guild : guilds.values()) {
            config.set(guild.getName() + ".leader", guild.getLeader().toString());
            List<String> memberIds = new ArrayList<>();
            for (UUID memberId : guild.getMembers()) {
                memberIds.add(memberId.toString());
            }
            config.set(guild.getName() + ".members", memberIds);
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGuilds() {
        if (!dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : config.getKeys(false)) {
            String guildName = key;
            UUID leaderId = UUID.fromString(config.getString(key + ".leader"));
            List<String> memberIds = config.getStringList(key + ".members");
            List<UUID> memberUUIDs = new ArrayList<>();
            for (String memberId : memberIds) {
                memberUUIDs.add(UUID.fromString(memberId));
            }
            Guild guild = new Guild(guildName, leaderId, memberUUIDs);
            guilds.put(guildName, guild);
            for (UUID memberId : memberUUIDs) {
                playerGuilds.put(memberId, guildName);
            }
        }
    }
}
