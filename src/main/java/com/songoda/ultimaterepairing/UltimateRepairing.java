package com.songoda.ultimaterepairing;

import com.songoda.ultimaterepairing.anvil.AnvilManager;
import com.songoda.ultimaterepairing.anvil.UAnvil;
import com.songoda.ultimaterepairing.anvil.editor.Editor;
import com.songoda.ultimaterepairing.command.CommandManager;
import com.songoda.ultimaterepairing.economy.Economy;
import com.songoda.ultimaterepairing.economy.PlayerPointsEconomy;
import com.songoda.ultimaterepairing.economy.ReserveEconomy;
import com.songoda.ultimaterepairing.economy.VaultEconomy;
import com.songoda.ultimaterepairing.handlers.ParticleHandler;
import com.songoda.ultimaterepairing.handlers.RepairHandler;
import com.songoda.ultimaterepairing.hologram.Hologram;
import com.songoda.ultimaterepairing.hologram.HologramHolographicDisplays;
import com.songoda.ultimaterepairing.listeners.BlockListeners;
import com.songoda.ultimaterepairing.listeners.InteractListeners;
import com.songoda.ultimaterepairing.listeners.InventoryListeners;
import com.songoda.ultimaterepairing.listeners.PlayerListeners;
import com.songoda.ultimaterepairing.utils.*;
import com.songoda.ultimaterepairing.utils.locale.Locale;
import com.songoda.ultimaterepairing.utils.settings.Setting;
import com.songoda.ultimaterepairing.utils.settings.SettingsManager;
import com.songoda.ultimaterepairing.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateRepairing extends JavaPlugin {
    private static CommandSender console = Bukkit.getConsoleSender();

    private static UltimateRepairing INSTANCE;

    private ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

    private Locale locale;

    private RepairHandler repairHandler;
    private SettingsManager settingsManager;
    private CommandManager commandManager;
    private AnvilManager anvilManager;

    private Hologram hologram;
    private Economy economy;

    private Editor editor;

    public static UltimateRepairing getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateRepairing " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        //Running Songoda Updater
        Plugin plugin = new Plugin(this, 20);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        PluginManager pluginManager = getServer().getPluginManager();

        // Setup Economy
        if (Setting.VAULT_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Vault"))
            this.economy = new VaultEconomy();
        else if (Setting.RESERVE_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Reserve"))
            this.economy = new ReserveEconomy();
        else if (Setting.PLAYER_POINTS_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("PlayerPoints"))
            this.economy = new PlayerPointsEconomy();

        this.editor = new Editor(this);
        this.anvilManager = new AnvilManager();

        this.repairHandler = new RepairHandler(this);
        this.commandManager = new CommandManager(this);
        new ParticleHandler(this);

        // Register Hologram Plugin
        if (pluginManager.isPluginEnabled("HolographicDisplays"))
            hologram = new HologramHolographicDisplays(this);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
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

        }, 6L);

        // Start Metrics
        new Metrics(this);

        // Event registration
        pluginManager.registerEvents(new PlayerListeners(this), this);
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);
        pluginManager.registerEvents(new InventoryListeners(this), this);

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


    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
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
            if (!anvil.shouldSave()) continue;
            String locationStr = Methods.serializeLocation(anvil.getLocation());
            dataFile.getConfig().set("data." + locationStr + ".hologram", anvil.isHologram());
            dataFile.getConfig().set("data." + locationStr + ".particles", anvil.isParticles());
            dataFile.getConfig().set("data." + locationStr + ".infinity", anvil.isInfinity());
            dataFile.getConfig().set("data." + locationStr + ".permPlaced", anvil.isPermPlaced());
        }

        // Save to file
        dataFile.saveConfig();
    }

    public void reload() {
        try {
            this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));
            this.locale.reloadMessages();
            this.settingsManager.reloadConfig();
            reloadConfig();
            saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public Economy getEconomy() {
        return economy;
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