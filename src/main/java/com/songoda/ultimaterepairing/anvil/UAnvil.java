package com.songoda.ultimaterepairing.anvil;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Objects;

public class UAnvil {

    private Location location;

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
        UltimateRepairing.getInstance().getHologramHandler().updateHolograms();
    }

    public boolean isParticles() {
        return particles;
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
        return hologram || particles || infinity || permPlaced && Methods.isAnvil(getLocation().getBlock().getType());
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
