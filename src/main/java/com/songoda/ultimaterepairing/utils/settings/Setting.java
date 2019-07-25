package com.songoda.ultimaterepairing.utils.settings;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.utils.ServerVersion;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

    TIMEOUT("Main.Time Before Repair Auto Canceled", 200L),

    EXPERIENCE_EQUATION("Main.Experience Cost Equation", "{MaxDurability} - ({MaxDurability} - {Durability} / 40) + 1",
            "The equation used to generate experience repairing cost."),

    ECONOMY_EQUATION("Main.Economy Cost Equation", "{XPCost} * 5",
            "The equation used to generate economy repairing cost."),

    ITEM_EQUATION("Main.Item Cost Equation", "{XPCost} * 3",
            "The equation used to generate item repairing cost."),

    MULTIPLY_COST_FOR_ENCHANTED("Main.Cost Multiplier For Enchanted Items", 2,
            "Should enchanted items have their repair costs multiplied?"),

    VAULT_ECONOMY("Economy.Use Vault Economy", true,
            "Should Vault be used?"),

    RESERVE_ECONOMY("Economy.Use Reserve Economy", true,
            "Should Reserve be used?"),

    PLAYER_POINTS_ECONOMY("Economy.Use Player Points Economy", false,
            "Should PlayerPoints be used?"),


    ECO_ICON("Interfaces.Economy Icon", UltimateRepairing.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "SUNFLOWER" : "DOUBLE_PLANT"),
    XP_ICON("Interfaces.XP Icon", UltimateRepairing.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "EXPERIENCE_BOTTLE" : "EXP_BOTTLE"),
    ITEM_ICON("Interfaces.Item Icon", "DIAMOND"),

    EXIT_ICON("Interfaces.Exit Icon", UltimateRepairing.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "OAK_DOOR" : "WOOD_DOOR"),
    BUY_ICON("Interfaces.Buy Icon", "EMERALD"),

    GLASS_TYPE_1("Interfaces.Glass Type 1", 7),
    GLASS_TYPE_2("Interfaces.Glass Type 2", 11),
    GLASS_TYPE_3("Interfaces.Glass Type 3", 3),

    RAINBOW("Interfaces.Replace Glass Type 1 With Rainbow Glass", false),

    REPAIR_ONLY_SAME_TYPE("Main.Repair Items Only With Items Of That Items Type", true,
            "Should repairing with items only utilize items of the same type?"),

    ENABLE_ANVIL_DEFAULT_FUNCTION("Main.Enable Default Anvil Function", true,
            "Should the default anvil function be disabled?"),

    SWAP_LEFT_RIGHT("Main.Swap Right And Left Click Options", false,
            "Should punching an anvil open up the anvil GUI and right clicking",
            "open up the repair GUI?"),

    PERMISSION_ANVIL_PLACE("Main.Require Permission On UltimateRepairing Anvil Place", false,
            "Should players need admin permissions to place anvils?"),

    PARTICLE_AMOUNT("Main.Particle Amount", 25),

    PARTICLE_TYPE("Main.Particle Type", "SPELL_WITCH"),

    LANGUGE_MODE("System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    private String setting;
    private Object option;
    private String[] comments;

    Setting(String setting, Object option, String... comments) {
        this.setting = setting;
        this.option = option;
        this.comments = comments;
    }

    Setting(String setting, Object option) {
        this.setting = setting;
        this.option = option;
        this.comments = null;
    }

    public static Setting getSetting(String setting) {
        List<Setting> settings = Arrays.stream(values()).filter(setting1 -> setting1.setting.equals(setting)).collect(Collectors.toList());
        if (settings.isEmpty()) return null;
        return settings.get(0);
    }

    public String getSetting() {
        return setting;
    }

    public Object getOption() {
        return option;
    }

    public String[] getComments() {
        return comments;
    }

    public List<String> getStringList() {
        return UltimateRepairing.getInstance().getConfig().getStringList(setting);
    }

    public boolean getBoolean() {
        return UltimateRepairing.getInstance().getConfig().getBoolean(setting);
    }

    public int getInt() {
        return UltimateRepairing.getInstance().getConfig().getInt(setting);
    }

    public long getLong() {
        return UltimateRepairing.getInstance().getConfig().getLong(setting);
    }

    public String getString() {
        return UltimateRepairing.getInstance().getConfig().getString(setting);
    }

    public char getChar() {
        return UltimateRepairing.getInstance().getConfig().getString(setting).charAt(0);
    }

    public double getDouble() {
        return UltimateRepairing.getInstance().getConfig().getDouble(setting);
    }
}