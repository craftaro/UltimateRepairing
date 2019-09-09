package com.songoda.ultimaterepairing.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData;
import com.songoda.ultimaterepairing.settings.Settings;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairTypeGui extends Gui {
    
    final Location anvil;
    final Player player;
    final UltimateRepairing instance = UltimateRepairing.getInstance();
    final ItemStack item;

    public RepairTypeGui(Player player, Location anvil) {
        this(player, anvil, null);
    }
    
    public RepairTypeGui(Player player, Location anvil, Gui gui) {
        super(gui);
        this.anvil = anvil;
        this.player = player;
        this.item = player.getItemInHand();
        init();
    }
    
    protected void init() {
        setRows(3);
        setTitle(instance.getLocale().getMessage("interface.repair.title").getMessage());
        
        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(glass1);

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 1, 0, false, true, glass2);
        GuiUtils.mirrorFill(this, 1, 1, false, true, glass3);
        
        if(player.hasPermission("ultimaterepairing.use.ECO")) {
            setButton(11, GuiUtils.createButtonItem(Settings.ECO_ICON.getMaterial(CompatibleMaterial.SUNFLOWER), 
                    instance.getLocale().getMessage("interface.repair.eco").getMessage(),
                    instance.getLocale().getMessage("interface.repair.ecolore").getMessage()),
                    (event) -> {
                        exit();
                        instance.getRepairHandler().preRepair(player, PlayerAnvilData.RepairType.ECONOMY, anvil);
                    });
        }

        // Settings.ITEM_ICON.getMaterial(CompatibleMaterial.DIAMOND)
        if(player.hasPermission("ultimaterepairing.use.ITEM")) {
            final String itemName = TextUtils.formatText(ItemUtils.getItemName(item).replace("_", " "), true);
            setButton(15, GuiUtils.createButtonItem(CompatibleMaterial.getMaterial(item), 
                    instance.getLocale().getMessage("interface.repair.item")
                        .processPlaceholder("ITEM", itemName).getMessage(),
                    instance.getLocale().getMessage("interface.repair.itemlore")
                        .processPlaceholder("item", itemName).getMessage()),
                    (event) -> {
                        exit();
                        instance.getRepairHandler().preRepair(player, PlayerAnvilData.RepairType.ITEM, anvil);
                    });
        }

        if(player.hasPermission("ultimaterepairing.use.XP")) {
            setButton(13, GuiUtils.createButtonItem(Settings.XP_ICON.getMaterial(CompatibleMaterial.EXPERIENCE_BOTTLE), 
                    instance.getLocale().getMessage("interface.repair.xp").getMessage(),
                    instance.getLocale().getMessage("interface.repair.xplore").getMessage()),
                    (event) -> {
                        exit();
                        instance.getRepairHandler().preRepair(player, PlayerAnvilData.RepairType.XP, anvil);
                    });
        }

        if(Settings.RAINBOW.getBoolean()) {
            for(int cell = 0; cell < rows * 9; ++cell) {
                if(getItem(cell) == null) {
                    setItem(cell, GuiUtils.getBorderItem(Methods.getRainbowGlass()));
                }
            }
        }
    }
}
