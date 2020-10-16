package com.songoda.ultimaterepairing.listeners;

import com.songoda.core.gui.GuiManager;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.gui.AnvilSettingsGui;
import com.songoda.ultimaterepairing.gui.RepairGui;
import com.songoda.ultimaterepairing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by songoda on 2/25/2017.
 */

public class InteractListeners implements Listener {

    private final UltimateRepairing plugin;
    private final GuiManager guiManager;

    public InteractListeners(UltimateRepairing plugin, GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onAnvilClick(PlayerInteractEvent event) {
        boolean ourRepair = false;
        boolean vanillaRepair = false;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;

        UAnvil anvil1 = null;

        if (!block.getType().name().contains("ANVIL") // don't pay attention if it's not an anvil
                // also don't handle if we don't have perms to use this repair anvil
                || (Settings.PERMISSION_ANVIL_PLACE.getBoolean()
                && !(anvil1 = plugin.getAnvilManager().getAnvil(block)).isPermPlaced())) {
            return;
        }
        anvil1 = anvil1 != null ? anvil1 : plugin.getAnvilManager().getAnvil(block);
        // check if we should process this as a right click
        boolean rightClick = (event.getAction() == Action.RIGHT_CLICK_BLOCK) ^ (Settings.SWAP_LEFT_RIGHT.getBoolean());
        // admin interface?
        if (rightClick && player.isSneaking() && player.hasPermission("ultimaterepairing.admin")) {
            guiManager.showGUI(player, new AnvilSettingsGui(anvil1));
            event.setCancelled(true);
        } else if (!Settings.ENABLE_ANVIL_DEFAULT_FUNCTION.getBoolean()) {
            // if not allowing default anvil, then always use ourRepair
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
            }
            // replace our functions for vanilla mechanics only
            if (!rightClick) {
                return;
            }
            ourRepair = true;
        } else if (rightClick) {
            // allowing normal repair
            vanillaRepair = true;
        } else if (!player.isSneaking()) {
            // that's us!
            ourRepair = true;
        }

        if (ourRepair) {
            RepairGui.newGui(player, anvil1.getLocation());
            event.setCancelled(true);
        } else if (vanillaRepair && anvil1.isInfinity()) {
            player.openInventory(Bukkit.createInventory(null, InventoryType.ANVIL, ChatColor.DARK_GRAY + "Repair & Name"));
            event.setCancelled(true);
        }
    }
}