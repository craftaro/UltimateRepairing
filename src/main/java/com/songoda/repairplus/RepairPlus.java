package com.songoda.repairplus;

import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.events.BlockListeners;
import com.songoda.repairplus.events.InteractListeners;
import com.songoda.repairplus.events.InventoryListeners;
import com.songoda.repairplus.events.PlayerListeners;
import com.songoda.repairplus.handlers.CommandHandler;
import com.songoda.repairplus.handlers.HologramHandler;
import com.songoda.repairplus.handlers.ParticleHandler;
import com.songoda.repairplus.handlers.RepairHandler;
import com.songoda.repairplus.utils.Debugger;
import com.songoda.repairplus.utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class RepairPlus extends JavaPlugin implements Listener {
    public static CommandSender console = Bukkit.getConsoleSender();

    public boolean v1_7 = Bukkit.getServer().getClass().getPackage().getName().contains("1_7");
    public boolean v1_8 = Bukkit.getServer().getClass().getPackage().getName().contains("1_8");
    public boolean v1_13 = Bukkit.getServer().getClass().getPackage().getName().contains("1_13");

    private static RepairPlus INSTANCE;

    public References references = null;
    private ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");

    public RepairHandler repair;
    public HologramHandler holo = null;
    public SettingsManager settingsManager;

    public void onEnable() {
        INSTANCE = this;

        Arconix.pl().hook(this);

        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7RepairPlus " + this.getDescription().getVersion()  + " by &5Brianna <3!"));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7Action: &aEnabling&7..."));
        Bukkit.getPluginManager().registerEvents(this, this);

        settingsManager = new SettingsManager(this);
        settingsManager.updateSettings();
        setupConfig();

        langFile.createNewFile("Loading language file", "RepairPlus language file");
        loadLanguageFile();
        references = new References();

        repair = new RepairHandler();
        holo = new HologramHandler(this);
        new ParticleHandler(this);

        new com.massivestats.MassiveStats(this, 900);

        this.getCommand("RPAnvil").setExecutor(new CommandHandler(this));
        this.getCommand("RepairPlus").setExecutor(new CommandHandler(this));

        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
    }

    public void onDisable() {
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7RepairPlus " + this.getDescription().getVersion()  + " by &5Brianna <3!"));
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
            langFile.createNewFile("Loading language file", "RepairPlus language file");
            loadLanguageFile();
            references = new References();
            reloadConfig();
            saveConfig();
            holo.updateHolograms();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private void loadLanguageFile() {
        try {
            Lang.setFile(langFile.getConfig());

            for (final Lang value : Lang.values()) {
                langFile.getConfig().addDefault(value.getPath(), value.getDefault());
            }

            langFile.getConfig().options().copyDefaults(true);
            langFile.saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public static RepairPlus getInstance() {
        return INSTANCE;
    }
}