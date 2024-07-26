package dev.lofiz.lobbyAPI.listener;

import dev.lofiz.lobbyAPI.manager.FriendManager;
import dev.lofiz.lobbyAPI.manager.IgnoreManager;
import dev.lofiz.lobbyAPI.manager.PartyManager;
import dev.lofiz.lobbyAPI.manager.GuildManager;
import dev.lofiz.lobbyAPI.manager.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ProfileGUIListener implements Listener {
    private final FriendManager friendManager;
    private final IgnoreManager ignoreManager;
    private final PartyManager partyManager;
    private final GuildManager guildManager;
    private final PlayerProfileManager playerProfileManager;

    public ProfileGUIListener(FriendManager friendManager, IgnoreManager ignoreManager, PartyManager partyManager, GuildManager guildManager, PlayerProfileManager playerProfileManager) {
        this.friendManager = friendManager;
        this.ignoreManager = ignoreManager;
        this.partyManager = partyManager;
        this.guildManager = guildManager;
        this.playerProfileManager = playerProfileManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || (!event.getView().getTitle().equals("Player Profile") && !event.getView().getTitle().equals("Privacy Settings"))) {
            return;
        }

        event.setCancelled(true);  // Prevent moving items in the GUI

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        switch (event.getSlot()) {
            case 11:
                if (event.getView().getTitle().equals("Privacy Settings")) {
                    playerProfileManager.togglePrivacySetting(player, "guildInvites");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Type the name of the player you want to add as a friend:");
                }
                break;
            case 12:
                if (event.getView().getTitle().equals("Privacy Settings")) {
                    playerProfileManager.togglePrivacySetting(player, "friendInvites");
                } else {
                    player.openInventory(friendManager.getFriendListGUI(player));
                }
                break;
            case 13:
                if (event.getView().getTitle().equals("Privacy Settings")) {
                    playerProfileManager.togglePrivacySetting(player, "partyInvites");
                }
                break;
            case 14:
                if (event.getView().getTitle().equals("Privacy Settings")) {
                    playerProfileManager.togglePrivacySetting(player, "profileViewing");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Type the name of the player you want to ignore:");
                }
                break;
            case 15:
                player.openInventory(ignoreManager.getIgnoredListGUI(player));
                break;
            case 16:
                if (partyManager.getParty(player) != null) {
                    player.sendMessage(ChatColor.RED + "You already have a party.");
                } else {
                    partyManager.createParty(player);
                    player.sendMessage(ChatColor.GREEN + "Party created.");
                }
                break;
            case 10:
                player.sendMessage(ChatColor.GREEN + "Type the name of the guild you want to create:");
                break;
            case 19:
                player.openInventory(playerProfileManager.getPrivacySettingsGUI(player));
                break;
            default:
                break;
        }
    }
}
