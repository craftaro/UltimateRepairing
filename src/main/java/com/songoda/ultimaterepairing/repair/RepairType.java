package com.songoda.ultimaterepairing.repair;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.ultimaterepairing.UltimateRepairing;
import org.bukkit.entity.Player;

public enum RepairType {
    ECONOMY(CompatibleMaterial.SUNFLOWER,
            "ultimaterepairing.use.ECO",
            "interface.repair.ecoTitle"),

    ITEM(CompatibleMaterial.DIAMOND,
            "ultimaterepairing.use.ITEM",
            "interface.repair.itemTitle"),

    EXPERIENCE(CompatibleMaterial.EXPERIENCE_BOTTLE,
            "ultimaterepairing.use.XP",
            "interface.repair.xpTitle");

    private final CompatibleMaterial material;
    private final String permission;
    private final String title;

    RepairType(CompatibleMaterial material, String permission, String titleLang) {
        this.material = material;
        this.permission = permission;
        this.title = UltimateRepairing.getInstance().getLocale().getMessage(titleLang).getMessage();
    }

    public String getTitle() {
        return title;
    }

    public CompatibleMaterial getMaterial() {
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
