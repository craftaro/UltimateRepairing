package com.songoda.repairplus.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.Lang;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.anvil.PlayerAnvilData;
import com.songoda.repairplus.anvil.PlayerAnvilData.RepairType;
import com.songoda.repairplus.utils.Debugger;
import com.songoda.repairplus.utils.Methods;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by songoda on 2/25/2017.
 */
@SuppressWarnings("deprecation")
public class RepairHandler {

    private final Map<UUID, PlayerAnvilData> playerAnvilData = new HashMap<>();

    public void repairType(Player p) {
        try {
            if (getDataFor(p).getInRepair()) {
                yesNo(p,getDataFor(p).getType(), getDataFor(p).getToBeRepaired());
                return;
            }
            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatTitle(Lang.GUI_TITLE.getConfigValue()));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("settings.ECO-Icon")), 1);
            ItemMeta itemmeta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.ECO_LORE.getConfigValue());
            itemmeta.setLore(lore);
            itemmeta.setDisplayName(Lang.ECO.getConfigValue());
            item.setItemMeta(itemmeta);

            Material mat = Methods.getType(p.getItemInHand());

            ItemStack item3 = new ItemStack(mat, 1);
            String name = (mat.name().substring(0, 1).toUpperCase() + mat.name().toLowerCase().substring(1)).replace("_", " ");
            ItemMeta itemmeta3 = item3.getItemMeta();
            ArrayList<String> lore3 = new ArrayList<>();
            lore3.add(Lang.ITEM_LORE.getConfigValue(name));
            itemmeta3.setLore(lore3);
            itemmeta3.setDisplayName(Lang.ITEM.getConfigValue(name));
            item3.setItemMeta(itemmeta3);

            ItemStack item2 = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("settings.XP-Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            ArrayList<String> lore2 = new ArrayList<>();
            lore2.add(Lang.XP_LORE.getConfigValue(item3.toString()));
            itemmeta2.setLore(lore2);
            itemmeta2.setDisplayName(Lang.XP.getConfigValue());
            item2.setItemMeta(itemmeta2);

            if (p.hasPermission("repairplus.use.ECO"))
                i.setItem(11, item);
            if (p.hasPermission("repairplus.use.ITEM"))
                i.setItem(13, item3);
            if (p.hasPermission("repairplus.use.XP"))
                i.setItem(15, item2);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            p.openInventory(i);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void preRepair(Player p, RepairType type, Location loc) {
        try {
            if (loc.add(0, 1, 0).getBlock().getType() != Material.AIR) {
                p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NEED_SPACE.getConfigValue()));
                return;
            }
            if (p.getItemInHand().getDurability() <= 0) {
                p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_DAMAGED.getConfigValue()));
                return;
            }
            if (p.getItemInHand().getMaxStackSize() != 1) {
                p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.CANT_REPAIR.getConfigValue()));
                return;
            }

            Item i = p.getWorld().dropItem(loc.add(0.5, 2, 0.5), p.getItemInHand());

            // Support for EpicHoppers suction.
            i.setMetadata("grabbed", new FixedMetadataValue(RepairPlus.getInstance(), "true"));

            i.setMetadata("betterdrops_ignore", new FixedMetadataValue(RepairPlus.getInstance(), true));
            Vector vec = p.getEyeLocation().getDirection();
            vec.setX(0);
            vec.setY(0);
            vec.setZ(0);
            i.setVelocity(vec);
            i.setPickupDelay(3600);
            i.setMetadata("RepairPlus", new FixedMetadataValue(RepairPlus.getInstance(), ""));

            // Get from Map, put new instance in Map if it doesn't exist
            PlayerAnvilData playerData = playerAnvilData.computeIfAbsent(p.getUniqueId(), uuid -> new PlayerAnvilData());
            playerData.setItem(i);
            playerData.setToBeRepaired(p.getItemInHand());
            playerData.setLocations(loc.add(0, -2, 0));

            yesNo(p, type, p.getItemInHand());


            p.setItemInHand(null);
            Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> {
                if (i.isValid() && !playerData.isBeingRepaired()) {
                    p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.TIME_OUT.getConfigValue()));
                    removeItem(playerData, p);
                    p.closeInventory();

                }
            }, RepairPlus.getInstance().getConfig().getLong("settings.Timeout"));
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void initRepair(Player p, Location location) {
        int num = 0;
        if (p.hasPermission("repairplus.use.ECO"))
            num++;
        if (p.hasPermission("repairplus.use.XP"))
            num++;
        if (p.hasPermission("repairplus.use.ITEM"))
            num++;

        if (num >= 2 || p.hasPermission("repairplus.use.*")) {
            repairType(p);
            getDataFor(p).setLocation(location);
        } else if (p.hasPermission("repairplus.use.eco"))
            RepairPlus.getInstance().repair.preRepair(p, RepairType.ECONOMY, location);
        else if (p.hasPermission("repairplus.use.XP"))
            RepairPlus.getInstance().repair.preRepair(p, RepairType.XP, location);
        else if (p.hasPermission("repairplus.use.ITEM"))
            RepairPlus.getInstance().repair.preRepair(p, RepairType.ITEM, location);
    }

    public void yesNo(Player p, RepairType type, ItemStack item) {
        try {
            PlayerAnvilData playerData = getDataFor(p);

            if (playerData.isBeingRepaired()) {
                return;
            }

            playerData.setInRepair(true);

            int price = Methods.getCost(type, item);
            String cost = "0";

            Material mat = new Methods().getType(item);
            String name = Arconix.pl().getApi().format().formatText(mat.name(), true);

            if (type == RepairType.XP)
                cost = price + " XP";
            else if (type == RepairType.ECONOMY)
                cost = Lang.ECO_GUI.getConfigValue(Integer.toString(price));
            else if (type == RepairType.ITEM)
                cost = price + " " + name;

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatTitle(Lang.GUI_TITLE_YESNO.getConfigValue(cost)));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item2 = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("settings.Buy-Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            itemmeta2.setDisplayName(Lang.YES_GUI.getConfigValue());
            item2.setItemMeta(itemmeta2);

            ItemStack item3 = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("settings.Exit-Icon")), 1);
            ItemMeta itemmeta3 = item3.getItemMeta();
            itemmeta3.setDisplayName(Lang.NO_GUI.getConfigValue());
            item3.setItemMeta(itemmeta3);

            i.setItem(4, item);
            i.setItem(11, item2);
            i.setItem(15, item3);

            Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.openInventory(i), 1);

            playerData.setType(type);
            playerData.setPrice(price);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void finish(boolean answer, Player p) {
        try {
            PlayerAnvilData playerData = playerAnvilData.computeIfAbsent(p.getUniqueId(), uuid -> new PlayerAnvilData());
            if (!answer) {
                removeItem(playerData, p);
                p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.CANCELLED.getConfigValue()));
                return;
            }
            RepairType type = playerData.getType();
            ItemStack players = playerData.getToBeRepaired();

            boolean economy = false;
            boolean sold = false;
            if (RepairPlus.getInstance().getServer().getPluginManager().getPlugin("Vault") != null && type == RepairType.ECONOMY) {
                RegisteredServiceProvider<Economy> rsp = RepairPlus.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                int price = playerData.getPrice();

                if (econ.has(p, price)) {
                    econ.withdrawPlayer(p, price);
                    sold = true;
                }
                economy = true;
            }

            int cost = Methods.getCost(type, players);
            ItemStack item2 = new ItemStack(Methods.getType(players), cost);
            String name = (item2.getType().name().substring(0, 1).toUpperCase() + item2.getType().name().toLowerCase().substring(1)).replace("_", " ");
            if (type == RepairType.ITEM && Arconix.pl().getApi().getGUI().inventoryContains(p.getInventory(), item2)) {
                    Arconix.pl().getApi().getGUI().removeFromInventory(p.getInventory(), item2);
                    sold = true;
                }

            if (type == RepairType.XP && p.getLevel() >= playerData.getPrice() || sold || p.getGameMode() == GameMode.CREATIVE) {
                playerData.setBeingRepaired(true);

                Location location = playerData.getLocations();
                p.getWorld().playEffect(location, Effect.STEP_SOUND, 152);
                Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.getWorld().playEffect(location, Effect.STEP_SOUND, 152), 5L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> {
                    p.getWorld().playEffect(location, Effect.STEP_SOUND, 152);
                    p.getWorld().playEffect(location, Effect.STEP_SOUND, 1);
                    if (RepairPlus.getInstance().v1_8)
                        Arconix.pl().getApi().getPlayer(p).playSound(Sound.valueOf("ANVIL_LAND"));
                    else
                        Arconix.pl().getApi().getPlayer(p).playSound(Sound.valueOf("BLOCK_ANVIL_LAND"));
                }, 10L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.getWorld().playEffect(location, Effect.STEP_SOUND, 152), 15L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.getWorld().playEffect(location, Effect.STEP_SOUND, 152), 20L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> {
                    if (RepairPlus.getInstance().v1_8 || RepairPlus.getInstance().v1_7)
                        Arconix.pl().getApi().getPlayer(p).playSound(Sound.valueOf("ANVIL_LAND"));
                    else
                        Arconix.pl().getApi().getPlayer(p).playSound(Sound.valueOf("BLOCK_ANVIL_LAND"));
                    p.getWorld().playEffect(location, Effect.STEP_SOUND, 152);
                    p.getWorld().playEffect(location, Effect.STEP_SOUND, 145);
                    p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.SUCCESS.getConfigValue()));
                    ItemStack repairedi = playerData.getToBeRepaired();
                    repairedi.setDurability((short) 0);
                    Item repaired = p.getWorld().dropItemNaturally(p.getLocation(), repairedi);
                    repaired.remove();
                    p.getInventory().addItem(playerData.getToBeRepaired());
                    playerData.getItem().remove();
                    if (p.getGameMode() != GameMode.CREATIVE &&
                            type == RepairType.XP) {
                        p.setLevel(p.getLevel() - playerData.getPrice());
                    }
                    this.playerAnvilData.remove(p.getUniqueId());
                    p.closeInventory();
                }, 25L);
                return;
            }

            if (type == RepairType.ECONOMY) {
                if (!economy)
                    p.sendMessage("Vault is not installed.");
                else
                    p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_ENOUGH.getConfigValue(Lang.ECO.getConfigValue())));
            } else if (type == RepairType.XP)
                p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_ENOUGH.getConfigValue(Lang.XP.getConfigValue())));
            else
                p.sendMessage(Arconix.pl().getApi().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_ENOUGH.getConfigValue(name)));


        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void removeItem(PlayerAnvilData playerData, Player p) {
        try {
            p.getInventory().addItem(playerData.getToBeRepaired());
            playerData.getItem().remove();

            this.playerAnvilData.remove(p.getUniqueId());
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public boolean hasInstance(Player player) {
        return playerAnvilData.containsKey(player);
    }

    public PlayerAnvilData getDataFor(Player player) {
        return playerAnvilData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerAnvilData());
    }

}
