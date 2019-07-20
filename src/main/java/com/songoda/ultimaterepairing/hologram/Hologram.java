package com.songoda.ultimaterepairing.hologram;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Hologram {

    protected final UltimateRepairing instance;

    Hologram(UltimateRepairing instance) {
        this.instance = instance;
    }

    public void loadHolograms() {
        Collection<UAnvil> uAnvils = instance.getAnvilManager().getAnvils();
        if (uAnvils.size() == 0) return;

        for (UAnvil anvil : uAnvils) {
            if (anvil.getWorld() == null) continue;
                add(anvil);
        }
    }

    public void unloadHolograms() {
        Collection<UAnvil> uAnvils = instance.getAnvilManager().getAnvils();
        if (uAnvils.size() == 0) return;

        for (UAnvil anvil : uAnvils) {
            if (anvil.getWorld() == null) continue;
            remove(anvil);
        }
    }

    public void add(UAnvil anvil) {
        format(anvil, Action.ADD);
    }

    public void remove(UAnvil anvil) {
        format(anvil, Action.REMOVE);
    }

    public void update(UAnvil anvil) {
        format(anvil, Action.UPDATE);
    }

    private void format(UAnvil anvil, Action action) {
        ArrayList<String> lines = new ArrayList<>();

        if (!instance.getConfig().getBoolean("Main.Enable Default Anvil Function"))
            lines.add(Methods.formatText(instance.getLocale().getMessage("general.hologram.oneclick").getMessage()));
        else if (instance.getConfig().getBoolean("Main.Swap Right And Left Click Options"))
            lines.add(Methods.formatText(instance.getLocale().getMessage("general.hologram.swapclick").getMessage()));
        else
            lines.add(Methods.formatText(instance.getLocale().getMessage("general.hologram.click").getMessage()));

        lines.add(Methods.formatText(instance.getLocale().getMessage("general.hologram.torepair").getMessage()));

        Location location = anvil.getLocation();

        if (!anvil.isHologram()) {
            remove(location);
            return;
        }

        switch (action) {
            case UPDATE:
                update(location, lines);
                break;
            case ADD:
                add(location, lines);
                break;
            case REMOVE:
                remove(location);
                break;
        }
    }

    protected abstract void add(Location location, ArrayList<String> lines);

    protected abstract void remove(Location location);

    protected abstract void update(Location location, ArrayList<String> lines);

    public enum Action {

        UPDATE, ADD, REMOVE

    }

}
