package com.songoda.repairplus.handlers;

import com.songoda.arconix.api.packets.Hologram;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.utils.Debugger;
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

    private final RepairPlus instance;

    public HologramHandler(RepairPlus plugin) {
        this.instance = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::updateHolograms, 0L, 5000L);
    }

    public void updateHolograms() {
        try {
            FileConfiguration config = instance.getConfig();

            if (config.getString("data.anvil") == null) return;

            ConfigurationSection section = config.getConfigurationSection("data.anvil");
            for (String loc : section.getKeys(false)) {
                String str[] = loc.split(":");
                String worldName = str[1].substring(0, str[1].length() - 1);

                if (Bukkit.getWorld(worldName) == null) continue;

                Location location = Arconix.pl().getApi().serialize().unserializeLocation(loc);
                location.add(.5, 1.10, .5);

                this.remove(location);
                if (!config.getBoolean("data.anvil." + loc + ".holo")) continue;

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