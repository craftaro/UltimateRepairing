package com.songoda.ultimaterepairing.anvil.editor;

import com.songoda.arconix.api.ArconixAPI;
import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Editor {

    private final UltimateRepairing instance;

    private final Map<UUID, UAnvil> editing = new HashMap<>();

    public Editor(UltimateRepairing instance) {
        this.instance = instance;
    }

    public void open(Player player, Block block) {
        UAnvil anvil = instance.getAnvilManager().getAnvil(block);
        open(player, anvil);
    }

    private void open(Player player, UAnvil anvil) {
        Inventory inventory = Bukkit.createInventory(null, 27, TextComponent.formatTitle("Anvil Settings"));

        int nu = 0;
        while (nu != 27) {
            inventory.setItem(nu, Methods.getGlass());
            nu++;
        }

        inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(8, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));
        inventory.setItem(10, Methods.getBackgroundGlass(false));
        inventory.setItem(16, Methods.getBackgroundGlass(false));
        inventory.setItem(17, Methods.getBackgroundGlass(true));
        inventory.setItem(18, Methods.getBackgroundGlass(true));
        inventory.setItem(19, Methods.getBackgroundGlass(true));
        inventory.setItem(20, Methods.getBackgroundGlass(false));
        inventory.setItem(24, Methods.getBackgroundGlass(false));
        inventory.setItem(25, Methods.getBackgroundGlass(true));
        inventory.setItem(26, Methods.getBackgroundGlass(true));

        inventory.setItem(11, Methods.createButton(Material.NAME_TAG, "&9&lToggle Holograms", anvil.isHologram() ? "&7Currently: &aEnabled&7." : "&7Currently &cDisabled&7."));

        inventory.setItem(13, Methods.createButton(Material.BEACON, "&5&lToggle Infinity", anvil.isInfinity() ? "&7Currently: &aEnabled&7." : "&7Currently &cDisabled&7."));

        inventory.setItem(15, Methods.createButton(Material.FIREWORK_ROCKET, "&9&lToggle Particles", anvil.isParticles() ? "&7Currently: &aEnabled&7." : "&7Currently &cDisabled&7."));
        player.openInventory(inventory);
        editing.put(player.getUniqueId(), anvil);
    }

    public void toggleHologram(Player player) {
        UAnvil anvil = editing.get(player.getUniqueId());
        anvil.setHologram(!anvil.isHologram());
        open(player, anvil);
    }

    public void toggleInfinity(Player player) {
        UAnvil anvil = editing.get(player.getUniqueId());
        anvil.setInfinity(!anvil.isInfinity());
        open(player, anvil);
    }

    public void toggleParticles(Player player) {
        UAnvil anvil = editing.get(player.getUniqueId());
        anvil.setParticles(!anvil.isParticles());
        open(player, anvil);
    }

    public boolean isEditing(Player player) {
        return editing.containsKey(player.getUniqueId());
    }

    public void removeEditing(Player player) {
        editing.remove(player.getUniqueId());
    }

}
