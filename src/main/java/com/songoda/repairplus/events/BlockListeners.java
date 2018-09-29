package com.songoda.repairplus.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.utils.Debugger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by songoda on 2/25/2017.
 */
public class BlockListeners implements Listener {

    private final RepairPlus instance;

    public BlockListeners(RepairPlus instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        try {
            if (!e.getPlayer().hasPermission("repairplus.permPlace") || !e.getBlockPlaced().getType().equals(Material.ANVIL)) {
                return;
            }

            String loc = Arconix.pl().getApi().serialize().serializeLocation(e.getBlock());
            instance.getConfig().set("data.anvil." + loc + ".permPlaced", true);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        try {
            String loc = Arconix.pl().getApi().serialize().serializeLocation(e.getBlock());

            if (!e.getBlock().getType().equals(Material.ANVIL) && !instance.getConfig().contains("data.anvil." + loc)) {
                return;
            }

            instance.getConfig().set("data.anvil." + loc + ".holo", null);
            instance.getHologramHandler().updateHolograms();
            instance.getConfig().set("data.anvil." + loc, null);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}