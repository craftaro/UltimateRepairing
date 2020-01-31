package com.songoda.ultimaterepairing.handlers;

import com.songoda.core.compatibility.CompatibleParticleHandler;
import com.songoda.core.compatibility.CompatibleParticleHandler.ParticleType;
import com.songoda.ultimaterepairing.UltimateRepairing;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleTask implements Listener {

    private final UltimateRepairing instance;
    int amt;
    String typeName;
    ParticleType type;
    BukkitTask task = null;

    public ParticleTask(UltimateRepairing instance) {
        this.instance = instance;
    }

    public void start() {
        reload();
        if (task == null) {
            task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(instance, this::applyParticles, 0L, 4L);
        }
    }

    public void stop() {
        if (task != null) {
            Bukkit.getServer().getScheduler().cancelTask(task.getTaskId());
            task = null;
        }
    }

    public void reload() {
        amt = instance.getConfig().getInt("Main.Particle Amount", 24) / 2;
        typeName = instance.getConfig().getString("Main.Particle Type", "SPELL_WITCH");
        type = ParticleType.getParticle(typeName);
        if (type == null) {
            type = ParticleType.SPELL_WITCH;
        }
    }

    public void applyParticles() {
        if (instance.getAnvilManager().getAnvils().isEmpty()) return;
        instance.getAnvilManager().getAnvils().parallelStream()
                .filter(anvil -> anvil.isParticles() && anvil.isInLoadedChunk())
                .forEach(anvil -> CompatibleParticleHandler.spawnParticles(type, anvil.getLocation().add(.5, 0, .5), amt, 0.25, 0.25, 0.25));
    }
}
