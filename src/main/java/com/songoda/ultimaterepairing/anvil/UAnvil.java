package com.songoda.ultimaterepairing.anvil;

import com.songoda.core.hooks.HologramManager;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.settings.Settings;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import org.bukkit.Bukkit;

public class UAnvil {

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

            ArrayList<String> lines = new ArrayList<>();

            if (!Settings.ENABLE_ANVIL_DEFAULT_FUNCTION.getBoolean()) {
                lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.oneclick").getMessage());
            } else if (Settings.SWAP_LEFT_RIGHT.getBoolean()) {
                lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.swapclick").getMessage());
            } else {
                lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.click").getMessage());
            }

            lines.add(UltimateRepairing.getInstance().getLocale().getMessage("general.hologram.torepair").getMessage());

            Location location = getLocation().add(0, .1, 0);

            Bukkit.getScheduler().runTaskLater(UltimateRepairing.getInstance(), ()->{
                if (!hologram) {
                    HologramManager.removeHologram(location);
                } else {
                    HologramManager.updateHologram(location, lines);
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

    @Override
    public int hashCode() {
        return 31 * (location == null ? 0 : location.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UAnvil)) return false;

        UAnvil other = (UAnvil) obj;
        return Objects.equals(location, other.location);
    }
    public boolean shouldSave(){
        return hologram || particles || infinity || permPlaced;
    }

    @Override
    public String toString() {
        return "UAnvil:{"
                + "Location:{"
                + "World:\"" + location.getWorld().getName() + "\","
                + "X:" + location.getBlockX() + ","
                + "Y:" + location.getBlockY() + ","
                + "Z:" + location.getBlockZ()
                + "}"
                + "}";
    }
}
