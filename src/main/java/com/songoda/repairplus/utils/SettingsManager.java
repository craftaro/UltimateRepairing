package com.songoda.repairplus.utils;

import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.Lang;
import com.songoda.repairplus.RepairPlus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by songo on 6/4/2017.
 */
public class SettingsManager implements Listener {

    private final RepairPlus instance;

    public Map<Player, Integer> page = new HashMap<>();

    private static ConfigWrapper defs;

    public SettingsManager(RepairPlus instance) {
        this.instance = instance;
        instance.saveResource("SettingDefinitions.yml", true);
        defs = new ConfigWrapper(instance, "", "SettingDefinitions.yml");
        defs.createNewFile("Loading data file", "RepairPlus SettingDefinitions file");
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    public Map<Player, String> current = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().getTitle().equals("RepairPlus Settings Editor")) {
                Player p = (Player) e.getWhoClicked();
                if (e.getCurrentItem().getType().name().contains("STAINED_GLASS")) {
                    e.setCancelled(true);
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(Lang.NEXT.getConfigValue())) {
                    page.put(p, 2);
                    openEditor(p);
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(Lang.BACK.getConfigValue())) {
                    page.put(p, 1);
                    openEditor(p);
                } else if (e.getCurrentItem() != null) {
                    e.setCancelled(true);

                    String key = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);

                    if (instance.getConfig().get("settings." + key).getClass().getName().equals("java.lang.Boolean")) {
                        boolean bool = (Boolean) instance.getConfig().get("settings." + key);
                        if (!bool)
                            instance.getConfig().set("settings." + key, true);
                        else
                            instance.getConfig().set("settings." + key, false);
                        finishEditing(p);
                    } else {
                        editObject(p, key);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (!current.containsKey(p)) {
            return;
        }
        switch (instance.getConfig().get("settings." + current.get(p)).getClass().getName()) {
            case "java.lang.Integer":
                instance.getConfig().set("settings." + current.get(p), Integer.parseInt(e.getMessage()));
                break;
            case "java.lang.Double":
                instance.getConfig().set("settings." + current.get(p), Double.parseDouble(e.getMessage()));
                break;
            case "java.lang.String":
                instance.getConfig().set("settings." + current.get(p), e.getMessage());
                break;
        }

        finishEditing(p);
        e.setCancelled(true);
    }

    public void finishEditing(Player p) {
        current.remove(p);
        instance.saveConfig();
        openEditor(p);
    }


    public void editObject(Player p, String current) {
        this.current.put(p, current);
        p.closeInventory();
        p.sendMessage("");
        p.sendMessage(Arconix.pl().getApi().format().formatText("&7Please enter a value for &6" + current + "&7."));
        if (instance.getConfig().get("settings." + current).getClass().getName().equals("java.lang.Integer"))
            p.sendMessage(Arconix.pl().getApi().format().formatText("&cUse only numbers."));
        p.sendMessage("");
    }


    public void openEditor(Player p) {
        int pmin = 1;

        if (page.containsKey(p))
            pmin = page.get(p);

        if (pmin != 1)
            pmin = 45;

        int pmax = pmin * 44;

        Inventory i = Bukkit.createInventory(null, 54, "RepairPlus Settings Editor");

        int num = 0;
        int total = 0;
        ConfigurationSection cs = instance.getConfig().getConfigurationSection("settings");
        for (String key : cs.getKeys(true)) {
            if (!key.contains("levels") && total >= pmin - 1 && total <= pmax) {
                ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Arconix.pl().getApi().format().formatText("&6" + key));
                ArrayList<String> lore = new ArrayList<>();
                switch (instance.getConfig().get("settings." + key).getClass().getName()) {
                    case "java.lang.Boolean":

                        item.setType(Material.LEVER);
                        boolean bool = (Boolean) instance.getConfig().get("settings." + key);

                        if (!bool)
                            lore.add(Arconix.pl().getApi().format().formatText("&c" + false));
                        else
                            lore.add(Arconix.pl().getApi().format().formatText("&a" + true));

                        break;
                    case "java.lang.String":
                        item.setType(Material.PAPER);
                        String str = (String) instance.getConfig().get("settings." + key);
                        lore.add(Arconix.pl().getApi().format().formatText("&9" + str));
                        break;
                    case "java.lang.Integer":
                        item.setType(Material.CLOCK);

                        int in = (Integer) instance.getConfig().get("settings." + key);
                        lore.add(Arconix.pl().getApi().format().formatText("&5" + in));
                        break;
                }
                if (defs.getConfig().contains(key)) {
                    String text = defs.getConfig().getString(key);

                    Pattern regex = Pattern.compile("(.{1,28}(?:\\s|$))|(.{0,28})", Pattern.DOTALL);
                    Matcher m = regex.matcher(text);
                    while (m.find()) {
                        if (m.end() != text.length() || m.group().length() != 0)
                            lore.add(Arconix.pl().getApi().format().formatText("&7" + m.group()));
                    }
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                i.setItem(num, item);
                num++;
            }
            total++;
        }


        int nu = 45;
        while (nu != 54) {
            i.setItem(nu, Methods.getGlass());
            nu++;
        }


        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        ItemStack skull = Arconix.pl().getApi().getGUI().addTexture(head, "http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skull.setDurability((short) 3);
        skullMeta.setDisplayName(Lang.NEXT.getConfigValue());
        skull.setItemMeta(skullMeta);

        ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        ItemStack skull2 = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) skull2.getItemMeta();
        skull2.setDurability((short) 3);
        skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
        skull2.setItemMeta(skull2Meta);

        if (pmin != 1)
            i.setItem(46, skull2);
        if (pmin == 1)
            i.setItem(52, skull);

        p.openInventory(i);
    }


    public void updateSettings() {
        for (settings s : settings.values()) {
            if (s.setting.equals("Upgrade-particle-type")) {
                if (instance.v1_7 || instance.v1_8)
                    instance.getConfig().addDefault("settings." + s.setting, "WITCH_MAGIC");
                else
                    instance.getConfig().addDefault("settings." + s.setting, s.option);
            } else
                instance.getConfig().addDefault("settings." + s.setting, s.option);
        }

        ConfigurationSection cs = instance.getConfig().getConfigurationSection("settings");
        for (String key : cs.getKeys(true)) {
            if (key.contains("levels") ||
                    contains(key)) {
                continue;
            }
            instance.getConfig().set("settings." + key, null);
        }
    }

    public static boolean contains(String test) {
        for (settings c : settings.values()) {
            if (c.setting.equals(test))
                return true;
        }
        return false;
    }

    public enum settings {

        o1("Timeout", 200L),

        o2("XP-Cost-Equation", "{MaxDurability} - ({MaxDurability} - {Durability} / 40) + 1"),
        o3("ECO-Cost-Equation", "{XPCost} * 5"),
        o4("ITEM-Cost-Equation", "{XPCost} * 3"),
        o5("Enchanted-Item-Multiplier", 2),

        o6("ECO-Icon", (RepairPlus.getInstance().v1_13 ? "SUNFLOWER" : "DOUBLE_PLANT")),
        o7("XP-Icon", (RepairPlus.getInstance().v1_13 ? "EXPERIENCE_BOTTLE" : "EXP_BOTTLE")),
        o8("ITEM", "DIAMOND"),

        o9("Exit-Icon", "WOOD_DOOR"),
        o10("Buy-Icon", "EMERALD"),
        o11("Buy-Icon", "EMERALD"),

        o12("Glass-Type-1", 7),
        o13("Glass-Type-2", 11),
        o14("Glass-Type-3", 3),

        o15("Rainbow-Glass", false),

        o16("Item-Match-Type", true),

        o17("Enable-Default-Anvil-Function", true),

        o18("Swap-Functions", false),

        o19("Perms-Only", false),
        o20("Debug-Mode", false);

        private String setting;
        private Object option;

        private settings(String setting, Object option) {
            this.setting = setting;
            this.option = option;
        }

    }
}
