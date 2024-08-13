package com.craftaro.ultimaterepairing.repair;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimaterepairing.UltimateRepairing;
import org.bukkit.entity.Player;

public enum RepairType {
    ECONOMY(XMaterial.SUNFLOWER,
            "ultimaterepairing.use.ECO",
            "interface.repair.ecoTitle"),

    ITEM(XMaterial.DIAMOND,
            "ultimaterepairing.use.ITEM",
            "interface.repair.itemTitle"),

    EXPERIENCE(XMaterial.EXPERIENCE_BOTTLE,
            "ultimaterepairing.use.XP",
            "interface.repair.xpTitle");

    private final XMaterial material;
    private final String permission;
    private final String title;

    RepairType(XMaterial material, String permission, String titleLang) {
        this.material = material;
        this.permission = permission;
        this.title = UltimateRepairing.getInstance().getLocale().getMessage(titleLang).toText();
    }

    public String getTitle() {
        return title;
    }

    public XMaterial getMaterial() {
        return material;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }

    public RepairType getNext(Player player) {
        for (int i = 1; i < values().length + 1; i++) {
            int index = ordinal();
            int nextIndex = index + i;
            RepairType[] cars = RepairType.values();
            nextIndex %= cars.length;
            RepairType type = cars[nextIndex];
            if (!type.hasPermission(player))
                continue;
            return type;
        }
        return null;
    }
}
