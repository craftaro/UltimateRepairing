package com.songoda.ultimaterepairing;

import com.songoda.ultimaterepairing.anvil.AnvilManager;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.anvil.editor.Editor;
import com.songoda.ultimaterepairing.command.CommandManager;
import com.songoda.ultimaterepairing.events.BlockListeners;
import com.songoda.ultimaterepairing.events.InteractListeners;
import com.songoda.ultimaterepairing.events.InventoryListeners;
import com.songoda.ultimaterepairing.events.PlayerListeners;
import com.songoda.ultimaterepairing.handlers.ParticleHandler;
import com.songoda.ultimaterepairing.handlers.RepairHandler;
import com.songoda.ultimaterepairing.hologram.Hologram;
import com.songoda.ultimaterepairing.hologram.HologramHolographicDisplays;
import com.songoda.ultimaterepairing.utils.ConfigWrapper;
import com.songoda.ultimaterepairing.utils.Debugger;
import com.songoda.ultimaterepairing.utils.Methods;
import com.songoda.ultimaterepairing.utils.SettingsManager;
import com.songoda.ultimaterepairing.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class UltimateRepairing extends JavaPlugin implements Listener {
    private static CommandSender console = Bukkit.getConsoleSender();

    private static UltimateRepairing INSTANCE;

    private ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");

    public References references = null;

    private Locale locale;

    private RepairHandler repairHandler;
    private SettingsManager settingsManager;
    private CommandManager commandManager;
    private AnvilManager anvilManager;

    private Hologram hologram;

    private Editor editor;

    public static UltimateRepairing getInstance() {
        return INSTANCE;
    }

    private boolean checkVersion() {
        int workingVersion = 13;
        int currentVersion = Integer.parseInt(Bukkit.getServer().getClass()
                .getPackage().getName().split("\\.")[3].split("_")[1]);

        if (currentVersion < workingVersion) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You installed the 1." + workingVersion + "+ only version of " + this.getDescription().getName() + " on a 1." + currentVersion + " server. Since you are on the wrong version we disabled the plugin for you. Please install correct version to continue using " + this.getDescription().getName() + ".");
                Bukkit.getConsoleSender().sendMessage("");
            }, 20L);
            return false;
        }
        return true;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        // Check to make sure the Bukkit version is compatible.
        if (!checkVersion()) return;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateRepairing " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));
        Bukkit.getPluginManager().registerEvents(this, this);

        settingsManager = new SettingsManager(this);
        settingsManager.updateSettings();
        setupConfig();

        String langMode = getConfig().getString("System.Language Mode");
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode", langMode));

        //Running Songoda Updater
        Plugin plugin = new Plugin(this, 20);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        this.editor = new Editor(this);
        this.anvilManager = new AnvilManager();

        references = new References();

        this.repairHandler = new RepairHandler(this);
        this.commandManager = new CommandManager(this);
        new ParticleHandler(this);

        PluginManager pluginManager = getServer().getPluginManager();

        // Register Hologram Plugin
        if (pluginManager.isPluginEnabled("HolographicDisplays"))
            hologram = new HologramHolographicDisplays(this);

        /*
         * Register anvils into AnvilManager from Configuration.
         */
        if (dataFile.getConfig().contains("data")) {
            for (String key : dataFile.getConfig().getConfigurationSection("data").getKeys(false)) {
                Location location = Methods.unserializeLocation(key);
                UAnvil anvil = anvilManager.getAnvil(location);
                anvil.setHologram(dataFile.getConfig().getBoolean("data." + key + ".hologram"));
                anvil.setInfinity(dataFile.getConfig().getBoolean("data." + key + ".infinity"));
                anvil.setParticles(dataFile.getConfig().getBoolean("data." + key + ".particles"));
                anvil.setPermPlaced(dataFile.getConfig().getBoolean("data." + key + ".permPlaced"));
            }
        }

        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveToFile, 6000, 6000);
        console.sendMessage(Methods.formatText("&a============================="));
    }

    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateRepairing " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
        saveConfig();
        saveToFile();
    }

    /*
     * Saves registered kits to file.
     */
    private void saveToFile() {
        // Wipe old kit information
        dataFile.getConfig().set("data", null);

        if (anvilManager.getAnvils() == null) return;
        
        /*
         * Save anvils from AnvilManager to Configuration.
         */
        for (UAnvil anvil : anvilManager.getAnvils()) {
            if (!anvil.shouldSave())continue;
            String locationStr = Methods.serializeLocation(anvil.getLocation());
            dataFile.getConfig().set("data." + locationStr + ".hologram", anvil.isHologram());
            dataFile.getConfig().set("data." + locationStr + ".particles", anvil.isParticles());
            dataFile.getConfig().set("data." + locationStr + ".infinity", anvil.isInfinity());
            dataFile.getConfig().set("data." + locationStr + ".permPlaced", anvil.isPermPlaced());
        }

        // Save to file
        dataFile.saveConfig();
    }

    private void setupConfig() {
        try {
            settingsManager.updateSettings();
            getConfig().options().copyDefaults(true);
            saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void reload() {
        try {
            locale.reloadMessages();
            references = new References();
            reloadConfig();
            saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public Editor getEditor() {
        return editor;
    }

    public RepairHandler getRepairHandler() {
        return repairHandler;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AnvilManager getAnvilManager() {
        return anvilManager;
    }
}