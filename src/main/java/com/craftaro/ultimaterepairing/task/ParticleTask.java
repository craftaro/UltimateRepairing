package com.craftaro.ultimaterepairing.task;

import com.craftaro.core.compatibility.CompatibleParticleHandler;
import com.craftaro.core.compatibility.CompatibleParticleHandler.ParticleType;
import com.craftaro.ultimaterepairing.UltimateRepairing;
import com.craftaro.ultimaterepairing.anvil.UAnvil;
import com.craftaro.ultimaterepairing.settings.Settings;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleTask extends BukkitRunnable {

    private final UltimateRepairing plugin;
    private int amt;
    private String typeName;
    private ParticleType type;
    private boolean hasStarted = false;

    public ParticleTask(UltimateRepairing plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (hasStarted && !isCancelled())
            return;

        reload();
        if (hasStarted)
            run();
        else
            runTaskTimerAsynchronously(plugin, 0L, 4L);

        hasStarted = true;
    }

    public void reload() {
        amt = Settings.PARTICLE_AMOUNT.getInt(24) / 2;
        typeName = Settings.PARTICLE_TYPE.getString("SPELL_WITCH");
        type = ParticleType.getParticle(typeName);
        if (type == null)
            type = ParticleType.SPELL_WITCH;
    }

    @Override
    public void run() {
        if (plugin.getAnvilManager().getAnvils().isEmpty()) {
            cancel();
            return;
        }

        List<UAnvil> anvils = plugin.getAnvilManager().getAnvils().parallelStream()
                .filter(UAnvil::isParticles).collect(Collectors.toList());

        if (anvils.isEmpty()) {
            cancel();
            return;
        }

        anvils.stream().filter(UAnvil::isInLoadedChunk)
                .forEach(anvil -> CompatibleParticleHandler
                        .spawnParticles(type, anvil.getLocation().add(.5, 0, .5), amt,
                                0.25, 0.25, 0.25));
    }
}
