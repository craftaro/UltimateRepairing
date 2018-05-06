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

        if (!instance.repair.hasInstance(event.getPlayer()) || !instance.repair.getDataFor(event.getPlayer()).getInRepair()) return;

        PlayerAnvilData playerData = instance.repair.getDataFor(event.getPlayer());
        instance.repair.removeItem(playerData, event.getPlayer());
    }
}