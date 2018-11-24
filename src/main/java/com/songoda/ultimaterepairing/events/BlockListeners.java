package com.songoda.ultimaterepairing.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.utils.Debugger;
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

    private final UltimateRepairing instance;

    public BlockListeners(UltimateRepairing instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        try {
            if (!event.getPlayer().hasPermission("ultimaterepairing.permPlace") || !event.getBlockPlaced().getType().equals(Material.ANVIL)) {
                return;
            }

            instance.getAnvilManager().getAnvil(event.getBlock()).setPermPlaced(true);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        try {
            String loc = Arconix.pl().getApi().serialize().serializeLocation(event.getBlock());

            if (!event.getBlock().getType().equals(Material.ANVIL) && !instance.getConfig().contains("data.anvil." + loc)) {
                return;
            }

            UAnvil anvil = instance.getAnvilManager().getAnvil(event.getBlock());
            anvil.setHologram(false);
            instance.getAnvilManager().removeAnvil(event.getBlock().getLocation());
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}