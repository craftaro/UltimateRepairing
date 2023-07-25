package com.craftaro.ultimaterepairing.settings;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimaterepairing.UltimateRepairing;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.configuration.ConfigSetting;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.hooks.HologramManager;

import java.util.stream.Collectors;

public class Settings {

    static final Config config = UltimateRepairing.getInstance().getCoreConfig();

    public static final ConfigSetting TIMEOUT = new ConfigSetting(config, "Main.Time Before Repair Auto Canceled", 200L);

    public static final ConfigSetting EXPERIENCE_EQUATION = new ConfigSetting(config, "Main.Experience Cost Equation", "{MaxDurability} - ({MaxDurability} - {Durability} / 40) + 1",
            "The equation used to generate experience repairing cost.");

    public static final ConfigSetting ECONOMY_EQUATION = new ConfigSetting(config, "Main.Economy Cost Equation", "{XPCost} * 5",
            "The equation used to generate economy repairing cost.");

    public static final ConfigSetting ITEM_EQUATION = new ConfigSetting(config, "Main.Item Cost Equation", "{XPCost} * 3",
            "The equation used to generate item repairing cost.");

    public static final ConfigSetting MULTIPLY_COST_FOR_ENCHANTED = new ConfigSetting(config, "Main.Cost Multiplier For Enchanted Items", 2,
            "Should enchanted items have their repair costs multiplied?");

    public static final ConfigSetting ECONOMY = new ConfigSetting(config, "Main.Economy",
            EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
            "Which economy plugin should be used?",
            "You can choose from \"" + EconomyManager.getManager().getRegisteredPlugins().stream().collect(Collectors.joining("\", \"")) + "\".");

    public static final ConfigSetting HOLOGRAM = new ConfigSetting(config, "Main.Hologram",
            HologramManager.getHolograms() == null ? "HolographicDisplays" : HologramManager.getHolograms().getName(),
            "Which hologram plugin should be used?",
            "You can choose from \"" + HologramManager.getManager().getRegisteredPlugins().stream().collect(Collectors.joining(", ")) + "\".");

    public static final ConfigSetting ECO_ICON = new ConfigSetting(config, "Interfaces.Economy Icon", "SUNFLOWER");
    public static final ConfigSetting XP_ICON = new ConfigSetting(config, "Interfaces.XP Icon", "EXPERIENCE_BOTTLE");
    public static final ConfigSetting ITEM_ICON = new ConfigSetting(config, "Interfaces.Item Icon", "DIAMOND");

    public static final ConfigSetting EXIT_ICON = new ConfigSetting(config, "Interfaces.Exit Icon", "OAK_DOOR");
    public static final ConfigSetting BUY_ICON = new ConfigSetting(config, "Interfaces.Buy Icon", "EMERALD");

    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(config, "Interfaces.Glass Type 1", "GRAY_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(config, "Interfaces.Glass Type 2", "BLUE_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(config, "Interfaces.Glass Type 3", "LIGHT_BLUE_STAINED_GLASS_PANE");

    public static final ConfigSetting RAINBOW = new ConfigSetting(config, "Interfaces.Replace Glass Type 1 With Rainbow Glass", false);

    public static final ConfigSetting REPAIR_ONLY_SAME_TYPE = new ConfigSetting(config, "Main.Repair Items Only With Items Of That Items Type", true,
            "Should repairing with items only utilize items of the same type?");

    public static final ConfigSetting ENABLE_ANVIL_DEFAULT_FUNCTION = new ConfigSetting(config, "Main.Enable Default Anvil Function", true,
            "Should the default anvil function be disabled?");

    public static final ConfigSetting SWAP_LEFT_RIGHT = new ConfigSetting(config, "Main.Swap Right And Left Click Options", false,
            "Should punching an anvil open up the anvil GUI and right clicking",
            "open up the repair GUI?");

    public static final ConfigSetting PERMISSION_ANVIL_PLACE = new ConfigSetting(config, "Main.Require Permission On UltimateRepairing Anvil Place", false,
            "Should players need admin permissions to place anvils?");

    public static final ConfigSetting PARTICLE_AMOUNT = new ConfigSetting(config, "Main.Particle Amount", 25);

    public static final ConfigSetting PARTICLE_TYPE = new ConfigSetting(config, "Main.Particle Type", "SPELL_WITCH");

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static final ConfigSetting SHOW_PARTICLES_BY_DEFAULT = new ConfigSetting(config, "Main.Show Particles By Default", true,
            "Should particles be enabled when an anvil is placed down?");

    public static final ConfigSetting SHOW_HOLOGRAMS_BY_DEFAULT = new ConfigSetting(config, "Main.Show Holograms By Default", true,
            "Should holograms be enabled when an anvil is placed down?");

    /**
     * In order to set dynamic economy comment correctly, this needs to be
     * called after EconomyManager load
     */
    public static void setupConfig() {
        config.load();
        config.setAutoremove(true).setAutosave(true);

        // convert glass pane settings
        int color;
        if ((color = GLASS_TYPE_1.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }

        // convert economy settings
        if (config.getBoolean("Economy.Use Vault Economy") && EconomyManager.getManager().isEnabled("Vault")) {
            config.set("Main.Economy", "Vault");
        } else if (config.getBoolean("Economy.Use Reserve Economy") && EconomyManager.getManager().isEnabled("Reserve")) {
            config.set("Main.Economy", "Reserve");
        } else if (config.getBoolean("Economy.Use Player Points Economy") && EconomyManager.getManager().isEnabled("PlayerPoints")) {
            config.set("Main.Economy", "PlayerPoints");
        }

        config.saveChanges();
    }
}