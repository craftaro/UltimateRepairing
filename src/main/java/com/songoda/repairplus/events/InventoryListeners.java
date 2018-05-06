package com.songoda.repairplus.events;

import com.songoda.repairplus.Lang;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.anvil.PlayerAnvilData.RepairType;
import com.songoda.repairplus.utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

/**
 * Created by songoda on 2/25/2017.
 */
public class InventoryListeners implements Listener {

    private final RepairPlus instance;

    public InventoryListeners(RepairPlus instance) {
        this.instance = instance;
    }

    @EventHandler
    public void OnPickup(InventoryPickupItemEvent event) {
        if (event.getItem().hasMetadata("RepairPlus"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        try {
            Player p = (Player) event.getWhoClicked();
            if (instance.repair.getDataFor(p).getInRepair()) {
                event.setCancelled(true);

                if (event.getSlot() == 11) {
                    instance.repair.finish(true, p);
                    p.closeInventory();
                } else if (event.getSlot() == 15) {
                    instance.repair.finish(false, p);
                    p.closeInventory();
                }
            } else if (event.getInventory().getTitle().equals(Lang.GUI_TITLE.getConfigValue(null))) {
                event.setCancelled(true);
                Location loc = instance.repair.getDataFor(p).getLocation();
                if (event.getSlot() == 11) {
                    p.closeInventory();
                    if (p.hasPermission("repairplus.use.ECO"))
                        instance.repair.preRepair(p, RepairType.ECONOMY, loc);
                } else if (event.getSlot() == 13) {
                    p.closeInventory();
                    if (p.hasPermission("repairplus.use.ITEM"))
                        instance.repair.preRepair(p, RepairType.ITEM, loc);
                } else if (event.getSlot() == 15) {
                    p.closeInventory();
                    if (p.hasPermission("repairplus.use.XP"))
                        instance.repair.preRepair(p, RepairType.XP, loc);
                }
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}