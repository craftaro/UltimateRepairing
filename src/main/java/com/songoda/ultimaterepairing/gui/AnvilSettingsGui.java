package com.songoda.ultimaterepairing.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class AnvilSettingsGui extends Gui {

    private final UAnvil anvil;

    public AnvilSettingsGui(UAnvil anvil, Gui gui) {
        super(gui);
        this.anvil = anvil;
        init();
    }

    public AnvilSettingsGui(UAnvil anvil) {
        this.anvil = anvil;
        init();
    }

    protected void init() {
        setRows(3);
        setTitle("Anvil Settings");

        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(glass1);

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 1, 0, false, true, glass2);
        GuiUtils.mirrorFill(this, 1, 1, false, true, glass3);

        setButton(1, 2, GuiUtils.createButtonItem(CompatibleMaterial.NAME_TAG, ChatColor.BLUE.toString() + ChatColor.BOLD + "Toggle Holograms",
                ChatColor.GRAY + "Currently: " + (anvil.isHologram() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") + ChatColor.GRAY + "."),
                (event) -> {
                    anvil.setHologram(!anvil.isHologram());
                    updateItemLore(event.slot, ChatColor.GRAY + "Currently: " + (anvil.isHologram() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") + ChatColor.GRAY + ".");
                });

        setButton(1, 4, GuiUtils.createButtonItem(CompatibleMaterial.BEACON, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Toggle Infinity",
                ChatColor.GRAY + "Currently: " + (anvil.isInfinity() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") + ChatColor.GRAY + "."),
                (event) -> {
                    anvil.setInfinity(!anvil.isInfinity());
                    updateItemLore(event.slot, ChatColor.GRAY + "Currently: " + (anvil.isInfinity() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") + ChatColor.GRAY + ".");
                });

        setButton(1, 6, GuiUtils.createButtonItem(CompatibleMaterial.FIREWORK_ROCKET, ChatColor.BLUE.toString() + ChatColor.BOLD + "Toggle Particles",
                ChatColor.GRAY + "Currently: " + (anvil.isParticles() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") + ChatColor.GRAY + "."),
                (event) -> {
                    anvil.setParticles(!anvil.isParticles());
                    updateItemLore(event.slot, ChatColor.GRAY + "Currently: " + (anvil.isParticles() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") + ChatColor.GRAY + ".");
                });

    }
}
