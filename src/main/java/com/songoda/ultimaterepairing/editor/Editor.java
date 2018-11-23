package com.songoda.ultimaterepairing.editor;

import com.songoda.arconix.api.ArconixAPI;
import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.ultimaterepairing.UltimateRepairing;
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

    private final Map<UUID, Block> editing = new HashMap<>();

    public Editor(UltimateRepairing instance) {
        this.instance = instance;
    }


    public void open(Player player, Block block) {
        editing.put(player.getUniqueId(), block);

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

        inventory.setItem(11, Methods.createButton(Material.NAME_TAG, "&9&lToggle Holograms", instance.getConfig().getBoolean("data.anvil." + ArconixAPI.getApi().serialize().serializeLocation(block) + ".holo") ? "&7Currently: &aEnabled&7." : "&7Currently &cDisabled&7."));

        inventory.setItem(13, Methods.createButton(Material.BEACON, "&5&lToggle Infinity", instance.getConfig().getBoolean("data.anvil." + ArconixAPI.getApi().serialize().serializeLocation(block) + ".inf") ? "&7Currently: &aEnabled&7." : "&7Currently &cDisabled&7."));

        inventory.setItem(15, Methods.createButton(Material.FIREWORK_ROCKET, "&9&lToggle Particles", instance.getConfig().getBoolean("data.anvil." + ArconixAPI.getApi().serialize().serializeLocation(block) + ".particles") ? "&7Currently: &aEnabled&7." : "&7Currently &cDisabled&7."));
        player.openInventory(inventory);

    }

    public void toggleHologram(Player player) {
        String loc = ArconixAPI.getApi().serialize().serializeLocation(editing.get(player));
        if (instance.getConfig().getString("data.anvil." + loc + ".active") == null)
            instance.getConfig().set("data.anvil." + loc + ".active", true);

        if (instance.getConfig().getString("data.anvil." + loc + ".holo") == null) {
            instance.getConfig().set("data.anvil." + loc + ".holo", true);
        } else {
            instance.getConfig().set("data.anvil." + loc + ".holo", null);
        }
        instance.getHologramHandler().updateHolograms();
        instance.saveConfig();
        open(player, editing.get(player));
    }

    public void toggleInfinity(Player player) {
        String loc = ArconixAPI.getApi().serialize().serializeLocation(editing.get(player));
        if (instance.getConfig().getString("data.anvil." + loc + ".active") == null)
            instance.getConfig().set("data.anvil." + loc + ".active", true);
        if (instance.getConfig().getString("data.anvil." + loc + ".inf") == null) {
            instance.getConfig().set("data.anvil." + loc + ".inf", true);
        } else {
            instance.getConfig().set("data.anvil." + loc + ".inf", null);
        }
        instance.saveConfig();
        open(player, editing.get(player));
    }

    public void toggleParticles(Player player) {
        String loc = ArconixAPI.getApi().serialize().serializeLocation(editing.get(player));
        if (instance.getConfig().getString("data.anvil." + loc + ".active") == null)
            instance.getConfig().set("data.anvil." + loc + ".active", true);
        if (instance.getConfig().getString("data.anvil." + loc + ".particles") == null) {
            instance.getConfig().set("data.anvil." + loc + ".particles", true);
        } else {
            instance.getConfig().set("data.anvil." + loc + ".particles", null);
        }
        instance.saveConfig();
        open(player, editing.get(player));
    }

    public boolean isEditing(Player player) {
        return editing.containsKey(player.getUniqueId());
    }

}
