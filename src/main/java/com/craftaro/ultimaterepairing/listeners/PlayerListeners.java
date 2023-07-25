package com.craftaro.ultimaterepairing.listeners;

import com.craftaro.ultimaterepairing.UltimateRepairing;
import com.craftaro.ultimaterepairing.anvil.PlayerAnvilData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final UltimateRepairing plugin;

    public PlayerListeners(UltimateRepairing plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        if (!plugin.getRepairHandler().hasInstance(event.getPlayer())
                || !plugin.getRepairHandler().getDataFor(event.getPlayer()).getInRepair())
            return;

        PlayerAnvilData playerData = plugin.getRepairHandler().getDataFor(event.getPlayer());
        plugin.getRepairHandler().removeItem(playerData, event.getPlayer());
    }
}