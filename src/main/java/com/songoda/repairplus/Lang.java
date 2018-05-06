package com.songoda.repairplus;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {

    PREFIX("prefix", "&7[&9RepairPlus&7]"),

    NOT_ENOUGH("not-enough", "&cYou don't have enough {TYPE} &cto repair this item!"),

    NEED_SPACE("need-space", "&cYou need to have free space above the anvil!"),

    CANT_REPAIR("cant-repair", "&cYou can't repair this!"),

    ALREADY_REPAIRING("already-repairing", "&cYou are already repairing something!"),

    TIME_OUT("time-out", "&cYour repair timed out..."),

    COST("cost", "&aRepairing will cost you: &9{COST} XP Level(s)&a."),

    COST_ECO("cost-eco", "&aRepairing will cost you: &9${COST}&a."),

    YES_NO("yes-no", "&aType &9yes &aor &9no &ain chat."),

    NOT_DAMAGED("not-damaged", "&aThis item is not damaged."),

    SUCCESS("success", "&aYour item has been successfully repaired!"),

    WOULD_YOU_LIKE("would-you-like", "&aWould you like to repair your &9{ITEM}&a?"),

    CANCELLED("cancelled", "&cCancelled repairing."),

    YES("yes", "Yes"),

    NO("no", "No"),

    ECO_GUI("eco-gui", "${COST}"),

    CLICK("click", "Left-Click with an item"),

    SWAPCLICK("swap-click", "Right-Click with an item"),

    ONECLICK("one-click", "Click with an item"),

    TOREPAIR("torepair", "to &6Repair&r!"),

    XP("XP", "&9XP"),
    ECO("ECO", "&9Economy"),
    ITEM("Item", "&9{ITEM}"),

    XP_LORE("XP-lore", "&7Click to repair with XP."),
    ECO_LORE("ECO-lore", "&7Click to repair with Economy."),
    ITEM_LORE("Item-lore", "&7Click to repair with {ITEM}."),

    GUI_TITLE("gui-title", "&9How do you want to repair?"),

    GUI_TITLE_YESNO("gui-title-yesno", "&9Repair for &a{COST}&9?"),

    NEXT("Next", "&9Next"),
    BACK("Back", "&9Back"),

    YES_GUI("yes-gui", "&a&lYes"),

    NO_GUI("no-gui", "&c&lNo");

    private String path;
    private String def;
    private static FileConfiguration LANG;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(final FileConfiguration config) {
        LANG = config;
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path;
    }

    public String getConfigValue() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));
    }

    public String getConfigValue(String arg) {
        String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

        if (arg != null) {
            value = value.replace("{ITEM}", arg);
            value = value.replace("{XP}", arg);
            value = value.replace("{COST}", arg);
            value = value.replace("{TYPE}", arg);
        }
        return value;
    }
}
