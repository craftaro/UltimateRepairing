package com.craftaro.ultimaterepairing;

import com.craftaro.core.SongodaCore;
import com.craftaro.core.SongodaPlugin;
import com.craftaro.core.commands.CommandManager;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.dependency.Dependency;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.hooks.HologramManager;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimaterepairing.anvil.AnvilManager;
import com.craftaro.ultimaterepairing.anvil.UAnvil;
import com.craftaro.ultimaterepairing.commands.CommandReload;
import com.craftaro.ultimaterepairing.commands.CommandSettings;
import com.craftaro.ultimaterepairing.commands.CommandURAnvil;
import com.craftaro.ultimaterepairing.task.ParticleTask;
import com.craftaro.ultimaterepairing.handlers.RepairHandler;
import com.craftaro.ultimaterepairing.listeners.BlockListeners;
import com.craftaro.ultimaterepairing.listeners.InteractListeners;
import com.craftaro.ultimaterepairing.listeners.InventoryListeners;
import com.craftaro.ultimaterepairing.listeners.PlayerListeners;
import com.craftaro.ultimaterepairing.settings.Settings;
import com.craftaro.ultimaterepairing.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UltimateRepairing extends SongodaPlugin {
    private static UltimateRepairing INSTANCE;

    private final Config dataFile = new Config(this, "data.yml");
    private final GuiManager guiManager = new GuiManager(this);
    private final ParticleTask particleTask = new ParticleTask(this);

    private RepairHandler repairHandler;
    private CommandManager commandManager;
    private AnvilManager anvilManager;

    public static UltimateRepairing getInstance() {
        return INSTANCE;
    }

    @Override
    protected Set<Dependency> getDependencies() {
        return new HashSet<>();
    }

    @Override
    public void onPluginLoad() {
        INSTANCE = this;
    }

    @Override
    public void onPluginEnable() {
        // Register in Songoda Core
        SongodaCore.registerPlugin(this, 20, XMaterial.ANVIL);

        Settings.setupConfig();

        // Load Economy & Hologram hooks
        EconomyManager.load();
        HologramManager.load(this);

        this.setLocale(Settings.LANGUGE_MODE.getString(), false);

        PluginManager pluginManager = getServer().getPluginManager();

        // Set Economy & Hologram preference
        EconomyManager.getManager().setPreferredHook(Settings.ECONOMY.getString());
        HologramManager.getManager().setPreferredHook(Settings.HOLOGRAM.getString());

        this.anvilManager = new AnvilManager();

        this.repairHandler = new RepairHandler(this, guiManager);
        this.commandManager = new CommandManager(this);
        this.commandManager.addMainCommand("ur")
                .addSubCommands(
                        new CommandReload(),
                        new CommandSettings(guiManager));
        this.commandManager.addCommand(new CommandURAnvil());

        // Event registration
        guiManager.init();
        pluginManager.registerEvents(new PlayerListeners(this), this);
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this, guiManager), this);
        pluginManager.registerEvents(new InventoryListeners(), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveToFile, 6000, 6000);
    }

    @Override
    public void onDataLoad() {
        /*
         * Register anvils into AnvilManager from Configuration.
         */
        dataFile.load();
        if (dataFile.contains("data")) {
            for (String key : dataFile.getConfigurationSection("data").getKeys(false)) {
                Location location = Methods.unserializeLocation(key);
                UAnvil anvil = anvilManager.getAnvil(location);
                anvil.setHologram(dataFile.getBoolean("data." + key + ".hologram"));
                anvil.setInfinity(dataFile.getBoolean("data." + key + ".infinity"));
                anvil.setParticles(dataFile.getBoolean("data." + key + ".particles"));
                anvil.setPermPlaced(dataFile.getBoolean("data." + key + ".permPlaced"));
            }
        }
        particleTask.start();
    }

    @Override
    public void onPluginDisable() {
        saveConfig();
        saveToFile();
    }

    @Override
    public List<Config> getExtraConfig() {
        return Arrays.asList(dataFile);
    }

    /*
     * Saves registered kits to file.
     */
    private void saveToFile() {
        // Wipe old information
        dataFile.set("data", null);

        if (anvilManager.getAnvils() == null) return;

        /*
         * Save anvils from AnvilManager to Configuration.
         */
        for (UAnvil anvil : anvilManager.getAnvils()) {
            if (!anvil.shouldSave()) continue;
            String locationStr = Methods.serializeLocation(anvil.getLocation());
            dataFile.set("data." + locationStr + ".hologram", anvil.isHologram());
            dataFile.set("data." + locationStr + ".particles", anvil.isParticles());
            dataFile.set("data." + locationStr + ".infinity", anvil.isInfinity());
            dataFile.set("data." + locationStr + ".permPlaced", anvil.isPermPlaced());
        }

        // Save to file
        dataFile.save();
    }

    @Override
    public void onConfigReload() {
        this.setLocale(Settings.LANGUGE_MODE.getString(), true);
        particleTask.reload();
    }

    public RepairHandler getRepairHandler() {
        return repairHandler;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AnvilManager getAnvilManager() {
        return anvilManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public ParticleTask getParticleTask() {
        return particleTask;
    }
}
