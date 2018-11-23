package com.songoda.ultimaterepairing;

import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.command.CommandManager;
import com.songoda.ultimaterepairing.events.BlockListeners;
import com.songoda.ultimaterepairing.events.InteractListeners;
import com.songoda.ultimaterepairing.events.InventoryListeners;
import com.songoda.ultimaterepairing.events.PlayerListeners;
import com.songoda.ultimaterepairing.handlers.HologramHandler;
import com.songoda.ultimaterepairing.handlers.ParticleHandler;
import com.songoda.ultimaterepairing.handlers.RepairHandler;
import com.songoda.ultimaterepairing.utils.Debugger;
import com.songoda.ultimaterepairing.utils.SettingsManager;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class UltimateRepairing extends JavaPlugin implements Listener {
    private static CommandSender console = Bukkit.getConsoleSender();

    private static UltimateRepairing INSTANCE;

    public References references = null;

    private Locale locale;

    private RepairHandler repairHandler;
    private HologramHandler hologramHandler;
    private SettingsManager settingsManager;
    private CommandManager commandManager;

    private boolean checkVersion() {
        int maxVersion = 12; // also supports 1.8 and higher
        int currentVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);
        if (currentVersion > maxVersion) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You installed the legacy (1.8 - 1.12) only version of " + this.getDescription().getName() + " on a 1." + currentVersion + " server. Since you are on the wrong version we disabled the plugin for you. Please install correct version to continue using " + this.getDescription().getName() + ".");
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

        Arconix.pl().hook(this);

        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7UltimateRepairing " + this.getDescription().getVersion()  + " by &5Brianna <3!"));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7Action: &aEnabling&7..."));
        Bukkit.getPluginManager().registerEvents(this, this);

        settingsManager = new SettingsManager(this);
        settingsManager.updateSettings();
        setupConfig();

        // Locales
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(this.getConfig().getString("Locale", "en_US"));

        references = new References();

        this.repairHandler = new RepairHandler(this);
        this.hologramHandler = new HologramHandler(this);
        this.commandManager = new CommandManager(this);
        new ParticleHandler(this);

        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
    }

    public void onDisable() {
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7UltimateRepairing " + this.getDescription().getVersion()  + " by &5Brianna <3!"));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        saveConfig();
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
            hologramHandler.updateHolograms();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public RepairHandler getRepairHandler() {
        return repairHandler;
    }

    public HologramHandler getHologramHandler() {
        return hologramHandler;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static UltimateRepairing getInstance() {
        return INSTANCE;
    }
}