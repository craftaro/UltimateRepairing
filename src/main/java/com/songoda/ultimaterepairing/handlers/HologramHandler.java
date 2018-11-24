package com.songoda.ultimaterepairing.handlers;

import com.songoda.arconix.api.packets.Hologram;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songoda on 2/24/2017.
 */
public class HologramHandler {

    private final UltimateRepairing instance;

    public HologramHandler(UltimateRepairing plugin) {
        this.instance = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::updateHolograms, 0L, 5000L);
    }

    public void updateHolograms() {
        try {
            FileConfiguration config = instance.getConfig();

            if (instance.getAnvilManager().getAnvils().isEmpty()) return;

            for (UAnvil anvil : instance.getAnvilManager().getAnvils()) {
                if (anvil.getWorld() == null) continue;

                Location location = anvil.getLocation();
                location.add(.5, 1.10, .5);

                this.remove(location);

                if (!anvil.isHologram()) continue;

                List<String> lines = new ArrayList<>();

                if (!config.getBoolean("Main.Enable Default Anvil Function"))
                    lines.add(Arconix.pl().getApi().format().formatText(instance.getLocale().getMessage("general.hologram.oneclick")));
                else if (config.getBoolean("Main.Swap Right And Left Click Options"))
                    lines.add(Arconix.pl().getApi().format().formatText(instance.getLocale().getMessage("general.hologram.swapclick")));
                else
                    lines.add(Arconix.pl().getApi().format().formatText(instance.getLocale().getMessage("general.hologram.click")));

                lines.add(Arconix.pl().getApi().format().formatText(instance.getLocale().getMessage("general.hologram.torepair")));
                Arconix.pl().getApi().packetLibrary.getHologramManager().spawnHolograms(location, lines);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private void remove(Location location) {
        try {
            Location loco = location.clone();
            Hologram hologramManager = Arconix.pl().getApi().packetLibrary.getHologramManager();

            hologramManager.despawnHologram(loco);
            hologramManager.despawnHologram(loco.subtract(0, .25, 0));
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}