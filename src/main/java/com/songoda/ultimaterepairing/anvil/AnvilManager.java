package com.songoda.ultimaterepairing.anvil;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnvilManager {

    private static final Map<Location, UAnvil> registeredAnvils = new HashMap<>();

    public UAnvil addAnvil(UAnvil uAnvil) {
        return registeredAnvils.put(roundLocation(uAnvil.getLocation()), uAnvil);
    }

    public UAnvil removeAnvil(Location location) {
        return registeredAnvils.remove(roundLocation(location));
    }

    public UAnvil getAnvil(Location location) {
        return registeredAnvils.computeIfAbsent(location, UAnvil::new);
    }

    public UAnvil getAnvil(Block block) {
        return this.getAnvil(block.getLocation());
    }

    public Collection<UAnvil> getAnvils() {
        return Collections.unmodifiableCollection(registeredAnvils.values());
    }

    private Location roundLocation(Location location) {
        location = location.clone();
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }
}
