package com.songoda.ultimaterepairing.gui;

import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.repair.RepairType;
import com.songoda.ultimaterepairing.settings.Settings;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RepairGui extends Gui {

    private final Location anvil;
    private final Player player;
    private static final UltimateRepairing plugin = UltimateRepairing.getInstance();

    public static void newGui(Player player, Location anvil) {
        RepairType type = RepairType.EXPERIENCE;
        if (!type.hasPermission(player))
            type = type.getNext(player);
        if (type == null) {
            plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(player);
            return;
        }
        plugin.getGuiManager().showGUI(player, new RepairGui(player, anvil, null, type));
    }

    private RepairGui(Player player, Location anvil, Gui gui, RepairType type) {
        super(gui);
        this.anvil = anvil;
        this.player = player;

        setRows(6);
        setTitle(plugin.getLocale().getMessage("interface.repair.title").getMessage());

        init(type);
    }

    protected void init(RepairType type) {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 53, null);

        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(glass1);

        mirrorFill(0, 0, true, true, glass2);
        mirrorFill(0, 1, true, true, glass2);
        mirrorFill(0, 2, true, true, glass3);


        if (Arrays.stream(RepairType.values()).filter(p -> p.hasPermission(player)).count() > 1)
            setButton(4, GuiUtils.createButtonItem(type.getMaterial(),
                    type.getTitle(),
                    plugin.getLocale().getMessage("interface.repair.swap").getMessage()), (event) ->
                    init(type.getNext(player)));

        int i = 9;
        for (int playerslot = 0; playerslot < player.getInventory().getContents().length; playerslot++) {
            ItemStack item = player.getInventory().getContents()[playerslot];
            if (item == null || item.getDurability() <= 0 || item.getMaxStackSize() != 1) continue;

            ItemStack toRepair = item.clone();

            short durability = item.getDurability();

            final String itemName = TextUtils.formatText(ItemUtils.getItemName(item).replace("_", " "), true);
            if (type == RepairType.ECONOMY) {
                item = GuiUtils.createButtonItem(item,
                        plugin.getLocale().getMessage("interface.repair.item")
                                .processPlaceholder("ITEM", itemName).getMessage(),
                        plugin.getLocale().getMessage("interface.repair.ecolore").getMessage());
            } else if (type == RepairType.ITEM) {
                item = GuiUtils.createButtonItem(item,
                        plugin.getLocale().getMessage("interface.repair.item")
                                .processPlaceholder("ITEM", itemName).getMessage(),
                        plugin.getLocale().getMessage("interface.repair.itemlore")
                                .processPlaceholder("item", itemName).getMessage());
            } else if (type == RepairType.EXPERIENCE) {
                item = GuiUtils.createButtonItem(item,
                        plugin.getLocale().getMessage("interface.repair.item")
                                .processPlaceholder("ITEM", itemName).getMessage(),
                        plugin.getLocale().getMessage("interface.repair.xplore").getMessage());
            }
            item.setDurability(durability);

            int finalplayerslot = playerslot;
            setButton(i, item, (event) -> {
                exit();
                if (!player.getInventory().contains(toRepair)) {
                    plugin.getLocale().getMessage("event.repair.notfound").sendPrefixedMessage(player);
                    return;
                }
                player.getInventory().removeItem(toRepair);
                plugin.getRepairHandler().preRepair(toRepair, finalplayerslot, player, type, anvil);
            });
            i++;
        }

        if (Settings.RAINBOW.getBoolean()) {
            for (int cell = 0; cell < rows * 9; ++cell) {
                if (getItem(cell) == null)
                    setItem(cell, GuiUtils.getBorderItem(Methods.getRainbowGlass()));
            }
        }
    }


}
