package com.songoda.repairplus.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleHandler implements Listener {

    private RepairPlus instance;

    public ParticleHandler(RepairPlus instance) {
        this.instance = instance;
        checkDefaults();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, this::applyParticles, 0L, 10L);
    }

    @SuppressWarnings("all")
    public void applyParticles() {
        try {
            if (instance.getConfig().getString("data.anvil") == null) return;

            int amt = instance.getConfig().getInt("data.particlesettings.ammount");
            String type = instance.getConfig().getString("data.particlesettings.type");
            if (type == null) {
                System.out.println("Critical error in your RepairPlus config. Please add a correct particle type or regenerate the config.");
                return;
            }
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
                if (instance.v1_8 || instance.v1_7)
                    w.playEffect(location, org.bukkit.Effect.valueOf(type), 1, 0);
                    //Again, can't get this to resolve
                    //w.spigot().playEffect(location, org.bukkit.Effect.valueOf(type), 1, 0, (float) 0.25, (float) 0.25, (float) 0.25, 1, amt, 100);
                else
                    w.spawnParticle(org.bukkit.Particle.valueOf(type), location, amt, 0.25, 0.25, 0.25);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private void checkDefaults() {
        try {
            if (instance.getConfig().getInt("data.particlesettings.ammount") == 0) {
                instance.getConfig().set("data.particlesettings.ammount", 25);
                instance.saveConfig();
            }

            if (instance.getConfig().getString("data.particlesettings.type") != null) {
                return;
            }

            instance.getConfig().set("data.particlesettings.type", (instance.v1_7 || instance.v1_8) ? "WITCH_MAGIC" : "SPELL_WITCH");

            instance.saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}