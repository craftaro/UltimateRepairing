package com.songoda.ultimaterepairing.handlers;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
            if (instance.getAnvilManager().getAnvils().isEmpty()) return;

            int amt = instance.getConfig().getInt("Main.Particle Amount");
            String type = instance.getConfig().getString("Main.Particle Type");

            ConfigurationSection section = instance.getConfig().getConfigurationSection("data.anvil");
            for (UAnvil anvil : instance.getAnvilManager().getAnvils()) {
                if (anvil.getWorld() == null || !anvil.isParticles()) {
                    continue;
                }
                Location location = anvil.getLocation();
                location.add(.5, 0, .5);
                anvil.getWorld().spawnParticle(org.bukkit.Particle.valueOf(type), location, amt, 0.25, 0.25, 0.25);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}