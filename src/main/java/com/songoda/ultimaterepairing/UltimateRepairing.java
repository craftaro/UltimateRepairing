package com.songoda.ultimaterepairing;

import com.songoda.core.SongodaCore;
import com.songoda.core.SongodaPlugin;
import com.songoda.core.commands.CommandManager;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.hooks.HologramManager;
import com.songoda.ultimaterepairing.anvil.AnvilManager;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.commands.*;
import com.songoda.ultimaterepairing.handlers.ParticleTask;
import com.songoda.ultimaterepairing.handlers.RepairHandler;
import com.songoda.ultimaterepairing.listeners.BlockListeners;
import com.songoda.ultimaterepairing.listeners.InteractListeners;
import com.songoda.ultimaterepairing.listeners.InventoryListeners;
import com.songoda.ultimaterepairing.listeners.PlayerListeners;
import com.songoda.ultimaterepairing.settings.Settings;
import com.songoda.ultimaterepairing.utils.Debugger;
import com.songoda.ultimaterepairing.utils.Methods;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

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
    public void onPluginLoad() {
        INSTANCE = this;
    }

    @Override
    public void onPluginEnable() {
        // Register in Songoda Core
        SongodaCore.registerPlugin(this, 20, CompatibleMaterial.ANVIL);

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
        this.commandManager.addCommand(new CommandUltimateRepairing())
                .addSubCommands(
                        new CommandReload(),
                        new CommandSettings(guiManager));
        this.commandManager.addCommand(new CommandURAnvil());

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
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
        }, 6L);

        // Event registration
        guiManager.init();
        pluginManager.registerEvents(new PlayerListeners(this), this);
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this, guiManager), this);
        pluginManager.registerEvents(new InventoryListeners(this), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveToFile, 6000, 6000);
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
        try {
            this.setLocale(Settings.LANGUGE_MODE.getString(), true);
            particleTask.reload();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
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
}