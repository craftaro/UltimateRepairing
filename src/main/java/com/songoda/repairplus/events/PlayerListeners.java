package com.songoda.repairplus.events;

import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.anvil.PlayerAnvilData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {


    private final RepairPlus instance;

    public PlayerListeners(RepairPlus instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        if (!instance.getRepairHandler().hasInstance(event.getPlayer()) || !instance.getRepairHandler().getDataFor(event.getPlayer()).getInRepair()) return;

        PlayerAnvilData playerData = instance.getRepairHandler().getDataFor(event.getPlayer());
        instance.getRepairHandler().removeItem(playerData, event.getPlayer());
    }
}