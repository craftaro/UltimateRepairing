package com.songoda.ultimaterepairing.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by songoda on 2/25/2017.
 */

public class InteractListeners implements Listener {

    private final UltimateRepairing instance;

    public InteractListeners(UltimateRepairing instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onAnvilClick(PlayerInteractEvent e) {
        try {
            boolean repair = false;
            boolean anvil = false;
            Player p = e.getPlayer();
            if (e.getClickedBlock() == null) {
                return;
            }
            String loc = Arconix.pl().getApi().serialize().serializeLocation(e.getClickedBlock());
            if (e.getClickedBlock().getType() != Material.ANVIL
                    || !((instance.getConfig().getBoolean("data.anvil." + loc + ".permPlaced")
                    || !instance.getConfig().getBoolean("Main.Require Permission On UltimateRepairing Anvil Place")))) {
                return;
            }
            if (instance.getConfig().getString("data.anvil." + loc + ".inf") != null) {
                e.getClickedBlock().setType(Material.AIR);
                e.getClickedBlock().setType(Material.ANVIL); //ToDO: This may not work.
            }
            if (!instance.getConfig().getBoolean("Main.Enable Default Anvil Function") && !p.isSneaking())
                e.setCancelled(true);
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    !p.isSneaking()) {
                if (instance.getConfig().getBoolean("Main.Swap Right And Left Click Options"))
                    repair = true;
                else if (!instance.getConfig().getBoolean("Main.Enable Default Anvil Function"))
                    repair = true;
            } else if (e.getAction() == Action.LEFT_CLICK_BLOCK &&
                    !p.isSneaking()) {
                if (instance.getConfig().getBoolean("Main.Swap Right And Left Click Options")) {
                    if (instance.getConfig().getBoolean("Main.Enable Default Anvil Function"))
                        anvil = true;
                    else
                        repair = true;
                } else
                    repair = true;
            } else if (p.isSneaking() && p.hasPermission("ultimaterepairing.admin")) {
                instance.getEditor().open(p, e.getClickedBlock());
                e.setCancelled(true);
            }
            if (repair) {
                instance.getRepairHandler().initRepair(p, e.getClickedBlock().getLocation());
                e.setCancelled(true);
            } else if (anvil && instance.getConfig().getBoolean("Main.Enable Default Anvil Function")) {
                Inventory inv = Bukkit.createInventory(null, InventoryType.ANVIL, "Repair & Name");
                p.openInventory(inv);
                e.setCancelled(true);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}