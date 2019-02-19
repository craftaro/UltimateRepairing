package com.songoda.ultimaterepairing.utils;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData.RepairType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

/**
 * Created by songoda on 2/25/2017.
 */
public class Methods {

    public static ItemStack getGlass() {
        try {
            UltimateRepairing instance = UltimateRepairing.getInstance();
            return Methods.getGlass(instance.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), instance.getConfig().getInt("Interfaces.Glass Type 1"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        try {
            UltimateRepairing instance = UltimateRepairing.getInstance();
            if (type)
                return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 2"));
            else
                return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 3"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    private static ItemStack getGlass(Boolean rainbow, int type) {
        int randomNum = 1 + (int) (Math.random() * 6);
        ItemStack glass;
        if (rainbow) {
            glass = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) randomNum);
        } else {
            glass = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) type);
        }
        ItemMeta glassmeta = glass.getItemMeta();
        glassmeta.setDisplayName("§l");
        glass.setItemMeta(glassmeta);
        return glass;
    }

    public static int getCost(RepairType type, ItemStack item) {
        try {

            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");

            String equationXP = UltimateRepairing.getInstance().getConfig().getString("Main.Experience Cost Equation");
            String equationECO = UltimateRepairing.getInstance().getConfig().getString("Main.Economy Cost Equation");
            String equationITEM = UltimateRepairing.getInstance().getConfig().getString("Main.Item Cost Equation");

            equationXP = equationXP.replace("{MaxDurability}", Short.toString(item.getType().getMaxDurability()))
                    .replace("{Durability}", Short.toString(item.getDurability()));
            int XPCost = (int) Math.round(Double.parseDouble(engine.eval(equationXP).toString()));

            equationECO = equationECO.replace("{MaxDurability}", Short.toString(item.getType().getMaxDurability()))
                    .replace("{Durability}", Short.toString(item.getDurability()))
                    .replace("{XPCost}", Integer.toString(XPCost));

            int ECOCost = (int) Math.round(Double.parseDouble(engine.eval(equationECO).toString()));

            equationITEM = equationITEM.replace("{MaxDurability}", Short.toString(item.getType().getMaxDurability()))
                    .replace("{Durability}", Short.toString(item.getDurability()))
                    .replace("{XPCost}", Integer.toString(XPCost));

            int ITEMCost = (int) Math.round(Double.parseDouble(engine.eval(equationITEM).toString()));

            if (item.hasItemMeta() &&
                    item.getItemMeta().hasEnchants()) {
                int multi = UltimateRepairing.getInstance().getConfig().getInt("Main.Cost Multiplier For Enchanted Items");
                XPCost = XPCost * multi;
                ECOCost = ECOCost * multi;
                ITEMCost = ITEMCost * multi;
            }

            if (type == RepairType.XP)
                return XPCost;
            else if (type == RepairType.ITEM)
                return ITEMCost;
            else if (type == RepairType.ECONOMY)
                return ECOCost;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return 9999999;
    }

    public static Material getType(ItemStack item) {
        if (UltimateRepairing.getInstance().getConfig().getBoolean("Main.Repair Items Only With Items Of That Items Type")) {
            if (item.getType().name().contains("DIAMOND"))
                return Material.DIAMOND;
            if (item.getType().name().contains("IRON"))
                return Material.IRON_INGOT;
            if (item.getType().name().contains("GOLD"))
                return Material.GOLD_INGOT;
            if (item.getType().name().contains("LEATHER"))
                return Material.LEATHER;
            if (item.getType().name().contains("STONE"))
                return Material.STONE;
            if (item.getType().name().contains("WOOD"))
                return Material.OAK_WOOD;
        }
        return Material.valueOf(UltimateRepairing.getInstance().getConfig().getString("Interfaces.Item Icon"));
    }

    public static ItemStack createButton(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(formatText(name));
        if (lore != null && lore.length != 0) {
            List<String> newLore = new ArrayList<>();
            for (String line : lore) newLore.add(formatText(line));
            meta.setLore(newLore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createButton(Material material, String name, String... lore) {
        return createButton(new ItemStack(material), name, lore);
    }

    public static ItemStack createButton(Material material, String name, ArrayList<String> lore) {
        return createButton(material, name, lore.toArray(new String[0]));
    }
    public static boolean isAnvil(Material material){
        return material.equals(Material.ANVIL) || material.equals(Material.CHIPPED_ANVIL) || material.equals(Material.DAMAGED_ANVIL);
    }

    /**
     * Checks if the inventory contains the specified item.
     *
     * @param inventory The inventory to check
     * @param item      The item to check for.
     * @return Whether or not the inventory contains the item.
     */
    public static boolean inventoryContains(Inventory inventory, ItemStack item) {
        int count = 0;
        ItemStack[] items = inventory.getContents();
        for (ItemStack item1 : items) {
            if (item1 != null && item1.getType() == item.getType() && item1.getDurability() == item.getDurability()) {
                count += item1.getAmount();
            }
            if (count >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the specified item from the inventory
     *
     * @param inventory The inventory to remove from.
     * @param item      The item to remove.
     */
    public static void removeFromInventory(Inventory inventory, ItemStack item) {
        int amt = item.getAmount();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                if (items[i].getAmount() > amt) {
                    items[i].setAmount(items[i].getAmount() - amt);
                    break;
                } else if (items[i].getAmount() == amt) {
                    items[i] = null;
                    break;
                } else {
                    amt -= items[i].getAmount();
                    items[i] = null;
                }
            }
        }
        inventory.setContents(items);
    }

    /**
     * Serializes the location of the block specified.
     *
     * @param b The block whose location is to be saved.
     * @return The serialized data.
     */
    public static String serializeLocation(Block b) {
        if (b == null)
            return "";
        return serializeLocation(b.getLocation());
    }

    /**
     * Serializes the location specified.
     *
     * @param location The location that is to be saved.
     * @return The serialized data.
     */
    public static String serializeLocation(Location location) {
        if (location == null)
            return "";
        String w = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String str = w + ":" + x + ":" + y + ":" + z;
        str = str.replace(".0", "").replace("/", "");
        return str;
    }

    private static Map<String, Location> serializeCache = new HashMap<>();

    /**
     * Deserializes a location from the string.
     *
     * @param str The string to parse.
     * @return The location that was serialized in the string.
     */
    public static Location unserializeLocation(String str) {
        if (str == null || str.equals(""))
            return null;
        if (serializeCache.containsKey(str)) {
            return serializeCache.get(str).clone();
        }
        String cacheKey = str;
        str = str.replace("y:", ":").replace("z:", ":").replace("w:", "").replace("x:", ":").replace("/", ".");
        List<String> args = Arrays.asList(str.split("\\s*:\\s*"));

        World world = Bukkit.getWorld(args.get(0));
        double x = Double.parseDouble(args.get(1)), y = Double.parseDouble(args.get(2)), z = Double.parseDouble(args.get(3));
        Location location = new Location(world, x, y, z, 0, 0);
        serializeCache.put(cacheKey, location.clone());
        return location;
    }

    public static String formatText(String text) {
        if (text == null || text.equals(""))
            return "";
        return formatText(text, false);
    }

    public static String formatText(String text, boolean cap) {
        if (text == null || text.equals(""))
            return "";
        if (cap)
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
