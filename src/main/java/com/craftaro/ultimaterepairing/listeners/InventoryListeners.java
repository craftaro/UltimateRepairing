package com.craftaro.ultimaterepairing.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

/**
 * Created by songoda on 2/25/2017.
 */
public class InventoryListeners implements Listener {

    @EventHandler
    public void onPickup(InventoryPickupItemEvent event) {
        if (event.getItem().hasMetadata("UltimateRepairing"))
            event.setCancelled(true);
    }
}