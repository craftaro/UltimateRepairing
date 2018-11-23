package com.songoda.ultimaterepairing.utils;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData.RepairType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by songoda on 2/25/2017.
 */
public class Methods {

    public static ItemStack getGlass() {
        try {
            return Arconix.pl().getApi().getGUI().getGlass(UltimateRepairing.getInstance().getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), UltimateRepairing.getInstance().getConfig().getInt("Interfaces.Glass Type 1"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        try {
            if (type)
                return Arconix.pl().getApi().getGUI().getGlass(false, UltimateRepairing.getInstance().getConfig().getInt("Interfaces.Glass Type 2"));
            else
                return Arconix.pl().getApi().getGUI().getGlass(false, UltimateRepairing.getInstance().getConfig().getInt("Interfaces.Glass Type 3"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
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
        meta.setDisplayName(TextComponent.formatText(name));
        if (lore != null && lore.length != 0) {
            List<String> newLore = new ArrayList<>();
            for (String line : lore) newLore.add(TextComponent.formatText(line));
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
}