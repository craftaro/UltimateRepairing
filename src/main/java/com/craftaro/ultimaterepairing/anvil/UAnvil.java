package com.craftaro.ultimaterepairing.anvil;

import com.craftaro.ultimaterepairing.UltimateRepairing;
import com.craftaro.core.hooks.HologramManager;
import com.craftaro.ultimaterepairing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Objects;

public class UAnvil {
    private static int nextHologramId = 0;
    private final String hologramId = "UR-Anvil#" + (++nextHologramId);

    private final Location location;

    private boolean hologram = false;
    private boolean particles = false;
    private boolean infinity = false;
    private boolean permPlaced = false;

    UAnvil(Location location) {
        this.location = location;
    }

    public boolean isHologram() {
        return hologram;
    }

    public void setHologram(boolean hologram) {
        this.hologram = hologram;
        if (HologramManager.getManager().isEnabled()) {
            Bukkit.getScheduler().runTaskLater(UltimateRepairing.getInstance(), () -> {
                if (!hologram) {
                    HologramManager.removeHologram(hologramId);
                } else {
                    ArrayList<String> lines = new ArrayList<>();

                    if (!Settings.ENABLE_ANVIL_DEFAULT_FUNCTION.getBoolean()) {
                        lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.oneclick").getMessage());
                    } else if (Settings.SWAP_LEFT_RIGHT.getBoolean()) {
                        lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.swapclick").getMessage());
                    } else {
                        lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.click").getMessage());
                    }

                    lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.torepair").getMessage());

                    if (!HologramManager.isHologramLoaded(hologramId)) {
                        HologramManager.createHologram(hologramId, getLocation().add(0, .1, 0), lines);
                        return;
                    }

                    HologramManager.updateHologram(hologramId, lines);
                }
            }, 1L);

        }
    }

    public boolean isParticles() {
        return particles;
    }

    public boolean isInLoadedChunk() {
        return location.getWorld() != null && location.getWorld().isChunkLoaded(((int) location.getX()) >> 4, ((int) location.getZ()) >> 4);
    }

    public void setParticles(boolean particles) {
        this.particles = particles;
    }

    public boolean isInfinity() {
        return infinity;
    }

    public void setInfinity(boolean infinity) {
        this.infinity = infinity;
    }

    public boolean isPermPlaced() {
        return permPlaced;
    }

    public void setPermPlaced(boolean permPlaced) {
        this.permPlaced = permPlaced;
    }

    public Location getLocation() {
        return location.clone();
    }

    public int getX() {
        return location.getBlockX();
    }

    public int getY() {
        return location.getBlockY();
    }

    public int getZ() {
        return location.getBlockZ();
    }

    public World getWorld() {
        return location.getWorld();
    }

    public boolean shouldSave() {
        return hologram || particles || infinity || permPlaced;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UAnvil uAnvil = (UAnvil) o;

        return hologram == uAnvil.hologram &&
                particles == uAnvil.particles &&
                infinity == uAnvil.infinity &&
                permPlaced == uAnvil.permPlaced &&
                Objects.equals(hologramId, uAnvil.hologramId) &&
                Objects.equals(location, uAnvil.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hologramId, location, hologram, particles, infinity, permPlaced);
    }

    @Override
    public String toString() {
        return "UAnvil{" +
                "hologramId='" + hologramId + '\'' +
                ", location=" + location +
                ", hologram=" + hologram +
                ", particles=" + particles +
                ", infinity=" + infinity +
                ", permPlaced=" + permPlaced +
                '}';
    }
}
