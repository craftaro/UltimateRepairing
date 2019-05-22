package com.songoda.ultimaterepairing.utils;

import com.songoda.ultimaterepairing.UltimateRepairing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by songoda on 6/4/2017.
 */
public class SettingsManager implements Listener {


    private static final Pattern SETTINGS_PATTERN = Pattern.compile("(.{1,28}(?:\\s|$))|(.{0,28})", Pattern.DOTALL);
    private final UltimateRepairing instance;
    private String pluginName = "UltimateRepairing";
    private Map<Player, String> cat = new HashMap<>();
    private Map<Player, String> current = new HashMap<>();

    public SettingsManager(UltimateRepairing plugin) {
        this.instance = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (event.getInventory() != event.getWhoClicked().getOpenInventory().getTopInventory()
                || clickedItem == null || !clickedItem.hasItemMeta()
                || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        if (event.getView().getTitle().equals(Methods.formatTitle(pluginName + " Settings Manager"))) {
            event.setCancelled(true);
            if (clickedItem.getType().name().contains("STAINED_GLASS")) return;

            String type = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            this.cat.put((Player) event.getWhoClicked(), type);
            this.openEditor((Player) event.getWhoClicked());
        } else if (event.getView().getTitle().equals(Methods.formatTitle(pluginName + " Settings Editor"))) {
            event.setCancelled(true);
            if (clickedItem.getType().name().contains("STAINED_GLASS")) return;

            Player player = (Player) event.getWhoClicked();

            String key = cat.get(player) + "." + ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (instance.getConfig().get(key).getClass().getName().equals("java.lang.Boolean")) {
                this.instance.getConfig().set(key, !instance.getConfig().getBoolean(key));
                this.finishEditing(player);
            } else {
                this.editObject(player, key);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!current.containsKey(player)) return;

        String value = current.get(player);
        FileConfiguration config = instance.getConfig();
        if (config.isInt(value)) {
            config.set(value, Integer.parseInt(event.getMessage()));
        } else if (config.isDouble(value)) {
            config.set(value, Double.parseDouble(event.getMessage()));
        } else if (config.isString(value)) {
            config.set(value, event.getMessage());
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () ->
                this.finishEditing(player), 0L);

        event.setCancelled(true);
    }

    private void finishEditing(Player player) {
        this.current.remove(player);
        this.instance.saveConfig();
        this.openEditor(player);
    }

    private void editObject(Player player, String current) {
        this.current.put(player, ChatColor.stripColor(current));

        player.closeInventory();
        player.sendMessage("");
        player.sendMessage(Methods.formatText("&7Please enter a value for &6" + current + "&7."));
        if (instance.getConfig().isInt(current) || instance.getConfig().isDouble(current)) {
            player.sendMessage(Methods.formatText("&cUse only numbers."));
        }
        player.sendMessage("");
    }

    public void openSettingsManager(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Methods.formatTitle(pluginName + " Settings Manager"));
        ItemStack glass = Methods.getGlass();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        int slot = 10;
        for (String key : instance.getConfig().getDefaultSection().getKeys(false)) {
            ItemStack item = new ItemStack(instance.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.WHITE_WOOL : Material.valueOf("WOOL"), 1, (byte) (slot - 9)); //ToDo: Make this function as it was meant to.
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(Methods.formatText("&6Click To Edit This Category.")));
            meta.setDisplayName(Methods.formatText("&f&l" + key));
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
            slot++;
        }

        player.openInventory(inventory);
    }

    private void openEditor(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, Methods.formatTitle(pluginName + " Settings Editor"));
        FileConfiguration config = instance.getConfig();

        int slot = 0;
        for (String key : config.getConfigurationSection(cat.get(player)).getKeys(true)) {
            String fKey = cat.get(player) + "." + key;
            ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Methods.formatText("&6" + key));

            List<String> lore = new ArrayList<>();
            if (config.isBoolean(fKey)) {
                item.setType(Material.LEVER);
                lore.add(Methods.formatText(config.getBoolean(fKey) ? "&atrue" : "&cfalse"));
            } else if (config.isString(fKey)) {
                item.setType(Material.PAPER);
                lore.add(Methods.formatText("&9" + config.getString(fKey)));
            } else if (config.isInt(fKey)) {
                item.setType(instance.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.CLOCK : Material.valueOf("WATCH"));
                lore.add(Methods.formatText("&5" + config.getInt(fKey)));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;
        }

        player.openInventory(inventory);
    }

    public void updateSettings() {
        FileConfiguration config = instance.getConfig();

        for (Setting setting : Setting.values()) {
            config.addDefault(setting.setting, setting.option);
        }
    }

    public enum Setting {

        o1("Timeout", "Main.Time Before Repair Auto Canceled", 200L),

        o2("XP-Cost-Equation", "Main.Experience Cost Equation", "{MaxDurability} - ({MaxDurability} - {Durability} / 40) + 1"),
        o3("ECO-Cost-Equation", "Main.Economy Cost Equation", "{XPCost} * 5"),
        o4("ITEM-Cost-Equation", "Main.Item Cost Equation", "{XPCost} * 3"),
        o5("Enchanted-Item-Multiplier", "Main.Cost Multiplier For Enchanted Items", 2),

        o6("ECO-Icon", "Interfaces.Economy Icon", "SUNFLOWER"),
        o7("XP-Icon", "Interfaces.XP Icon", "EXPERIENCE_BOTTLE"),
        o8("ITEM", "Interfaces.Item Icon", "DIAMOND"),

        o9("Exit-Icon", "Interfaces.Exit Icon", "OAK_DOOR"),
        o10("Buy-Icon", "Interfaces.Buy Icon", "EMERALD"),

        o12("Glass-Type-1", "Interfaces.Glass Type 1", 7),
        o13("Glass-Type-2", "Interfaces.Glass Type 2", 11),
        o14("Glass-Type-3", "Interfaces.Glass Type 3", 3),

        o15("Rainbow-Glass", "Interfaces.Replace Glass Type 1 With Rainbow Glass", false),

        o16("Item-Match-Type", "Main.Repair Items Only With Items Of That Items Type", true),

        o17("Enable-Default-Anvil-Function", "Main.Enable Default Anvil Function", true),

        o18("Swap-Functions", "Main.Swap Right And Left Click Options", false),

        o19("Perms-Only", "Main.Require Permission On UltimateRepairing Anvil Place", false),

        o20("-", "Main.Particle Amount", 25),

        o21("-", "Main.Particle Type", "SPELL_WITCH"),

        DOWNLOAD_FILES("-", "System.Download Needed Data Files", true),
        LANGUGE_MODE("-", "System.Language Mode", "en_US"),
        o22("Debug-Mode", "System.Debugger Enabled", false);

        private final String setting, oldSetting;
        private final Object option;

        private Setting(String oldSetting, String setting, Object option) {
            this.oldSetting = oldSetting;
            this.setting = setting;
            this.option = option;
        }

    }
}
