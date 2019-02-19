package com.songoda.ultimaterepairing.events;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
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
    public void onAnvilClick(PlayerInteractEvent event) {
        try {
            boolean repair = false;
            boolean anvil = false;
            Player player = event.getPlayer();
            if (event.getClickedBlock() == null) return;

            UAnvil anvil1 = instance.getAnvilManager().getAnvil(event.getClickedBlock());

            if (event.getClickedBlock().getType() != Material.ANVIL
                    || !(anvil1.isPermPlaced()
                    || !instance.getConfig().getBoolean("Main.Require Permission On UltimateRepairing Anvil Place"))) {
                return;
            }
            if (anvil1.isInfinity()) {
                event.getClickedBlock().setType(Material.AIR);
                event.getClickedBlock().setType(Material.ANVIL); //ToDO: This may not work.
            }
            if (!instance.getConfig().getBoolean("Main.Enable Default Anvil Function") && !player.isSneaking())
                event.setCancelled(true);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    !player.isSneaking()) {
                if (instance.getConfig().getBoolean("Main.Swap Right And Left Click Options"))
                    repair = true;
                else if (!instance.getConfig().getBoolean("Main.Enable Default Anvil Function"))
                    repair = true;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK &&
                    !player.isSneaking()) {
                if (instance.getConfig().getBoolean("Main.Swap Right And Left Click Options")) {
                    if (instance.getConfig().getBoolean("Main.Enable Default Anvil Function"))
                        anvil = true;
                    else
                        repair = true;
                } else
                    repair = true;
            } else if (player.isSneaking()
                    && player.hasPermission("ultimaterepairing.admin")
                    && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                instance.getEditor().open(player, event.getClickedBlock());
                event.setCancelled(true);
            }
            if (repair) {
                instance.getRepairHandler().initRepair(player, event.getClickedBlock().getLocation());
                event.setCancelled(true);
            } else if (anvil && instance.getConfig().getBoolean("Main.Enable Default Anvil Function")) {
                Inventory inv = Bukkit.createInventory(null, InventoryType.ANVIL, "Repair & Name");
                player.openInventory(inv);
                event.setCancelled(true);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}