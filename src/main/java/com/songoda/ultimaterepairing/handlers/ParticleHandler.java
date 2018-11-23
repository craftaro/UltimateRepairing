package com.songoda.ultimaterepairing.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleHandler implements Listener {

    private UltimateRepairing instance;

    public ParticleHandler(UltimateRepairing instance) {
        this.instance = instance;
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, this::applyParticles, 0L, 10L);
    }

    @SuppressWarnings("all")
    public void applyParticles() {
        try {
            if (instance.getConfig().getString("data.anvil") == null) return;

            int amt = instance.getConfig().getInt("Main.Particle Amount");
            String type = instance.getConfig().getString("Main.Particle Type");

            ConfigurationSection section = instance.getConfig().getConfigurationSection("data.anvil");
            for (String loc : section.getKeys(false)) {
                String str[] = loc.split(":");
                String worldName = str[1].substring(0, str[1].length() - 1);
                if (Bukkit.getServer().getWorld(worldName) == null ||
                        instance.getConfig().getString("data.anvil." + loc + ".particles") == null) {
                    continue;
                }
                World w = Bukkit.getServer().getWorld(str[1].substring(0, str[1].length() - 1));
                Location location = Arconix.pl().getApi().serialize().unserializeLocation(loc);
                location.add(.5, 0, .5);
                    w.spawnParticle(org.bukkit.Particle.valueOf(type), location, amt, 0.25, 0.25, 0.25);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}