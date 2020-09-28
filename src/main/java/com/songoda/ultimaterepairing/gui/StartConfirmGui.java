package com.songoda.ultimaterepairing.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData;
import com.songoda.ultimaterepairing.repair.RepairType;
import com.songoda.ultimaterepairing.settings.Settings;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StartConfirmGui extends Gui {

    private final Player player;
    private final UltimateRepairing instance = UltimateRepairing.getInstance();
    private final ItemStack item;
    private final RepairType type;
    boolean isYes = false;

    public StartConfirmGui(RepairType type, Player player, ItemStack item) {
        this(type, player, item, null);
    }

    public StartConfirmGui(RepairType type, Player player, ItemStack item, Gui gui) {
        super(gui);
        this.player = player;
        this.item = item;
        this.type = type;
        init();
    }

    protected void init() {
        setRows(3);

        String cost = "0";
        PlayerAnvilData playerData = instance.getRepairHandler().getDataFor(player);

        if (type == RepairType.EXPERIENCE) {
            cost = playerData.getPrice() + " XP";
        } else if (type == RepairType.ECONOMY) {
            cost = "$" + playerData.getPrice();
        } else if (type == RepairType.ITEM) {
            cost = playerData.getPrice() + " " + TextUtils.formatText(Methods.getType(item).name(), true);
        }

        setTitle(instance.getLocale().getMessage("interface.yesno.title")
                .processPlaceholder("cost", cost).getMessage());

        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        setDefaultItem(glass1);

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 1, 0, false, true, glass2);
        GuiUtils.mirrorFill(this, 1, 1, false, true, glass3);

        setItem(4, item);

        setButton(11, GuiUtils.createButtonItem(Settings.BUY_ICON.getMaterial(CompatibleMaterial.EMERALD),
                instance.getLocale().getMessage("interface.yesno.yes").getMessage()),
                (event) -> {
                    isYes = true;
                    exit();
                    instance.getRepairHandler().finish(true, player);
                });

        setButton(15, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(CompatibleMaterial.OAK_DOOR),
                instance.getLocale().getMessage("interface.yesno.no").getMessage()),
                (event) -> {
                    exit();
                    instance.getRepairHandler().finish(false, player);
                });

        if (Settings.RAINBOW.getBoolean()) {
            for (int cell = 0; cell < rows * 9; ++cell) {
                if (getItem(cell) == null) {
                    setItem(cell, GuiUtils.getBorderItem(Methods.getRainbowGlass()));
                }
            }
        }
        setOnClose((event) -> {
            if (!isYes) instance.getRepairHandler().finish(false, player);
        });
    }
}
