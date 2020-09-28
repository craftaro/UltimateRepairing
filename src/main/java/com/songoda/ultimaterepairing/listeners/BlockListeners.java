package com.songoda.ultimaterepairing.listeners;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.settings.Settings;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by songoda on 2/25/2017.
 */
public class BlockListeners implements Listener {

    private final UltimateRepairing plugin;

    public BlockListeners(UltimateRepairing plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlockPlaced().getType().name().contains("ANVIL")
                || !event.getPlayer().hasPermission("ultimaterepairing.permPlace")) {
            return;
        }

        UAnvil anvil = plugin.getAnvilManager().getAnvil(event.getBlock());
        anvil.setParticles(Settings.SHOW_PARTICLES_BY_DEFAULT.getBoolean());
        anvil.setHologram(Settings.SHOW_HOLOGRAMS_BY_DEFAULT.getBoolean());
        anvil.setPermPlaced(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        String loc = Methods.serializeLocation(event.getBlock());

        if (!event.getBlock().getType().name().contains("ANVIL") && !plugin.getConfig().contains("data.anvil." + loc)) {
            return;
        }

        UAnvil anvil = plugin.getAnvilManager().getAnvil(event.getBlock());
        anvil.setHologram(false);
        plugin.getAnvilManager().removeAnvil(event.getBlock().getLocation());
    }
}