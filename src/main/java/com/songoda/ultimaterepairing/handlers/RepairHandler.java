package com.songoda.ultimaterepairing.handlers;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData.RepairType;
import com.songoda.ultimaterepairing.utils.Debugger;
import com.songoda.ultimaterepairing.utils.Methods;
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
public class RepairHandler {

    private final UltimateRepairing instance;

    private final Map<UUID, PlayerAnvilData> playerAnvilData = new HashMap<>();

    public RepairHandler(UltimateRepairing instance) {
        this.instance = instance;
    }

    private void repairType(Player p) {
        try {
            if (getDataFor(p).getInRepair()) {
                yesNo(p, getDataFor(p).getType(), getDataFor(p).getToBeRepaired());
                return;
            }
            Inventory i = Bukkit.createInventory(null, 27, instance.getLocale().getMessage("interface.repair.title"));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Economy Icon")), 1);
            ItemMeta itemmeta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(instance.getLocale().getMessage("interface.repair.ecolore"));
            itemmeta.setLore(lore);
            itemmeta.setDisplayName(instance.getLocale().getMessage("interface.repair.eco"));
            item.setItemMeta(itemmeta);

            Material mat = Methods.getType(p.getItemInHand());

            ItemStack item3 = new ItemStack(mat, 1);
            String name = (mat.name().substring(0, 1).toUpperCase() + mat.name().toLowerCase().substring(1)).replace("_", " ");
            ItemMeta itemmeta3 = item3.getItemMeta();
            ArrayList<String> lore3 = new ArrayList<>();
            lore3.add(instance.getLocale().getMessage("interface.repair.itemlore", name));
            itemmeta3.setLore(lore3);
            itemmeta3.setDisplayName(instance.getLocale().getMessage("interface.repair.item", name));
            item3.setItemMeta(itemmeta3);

            ItemStack item2 = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.XP Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            ArrayList<String> lore2 = new ArrayList<>();
            lore2.add(instance.getLocale().getMessage("interface.repair.xplore"));
            itemmeta2.setLore(lore2);
            itemmeta2.setDisplayName(instance.getLocale().getMessage("interface.repair.xp"));
            item2.setItemMeta(itemmeta2);

            if (p.hasPermission("ultimaterepairing.use.ECO"))
                i.setItem(11, item);
            if (p.hasPermission("ultimaterepairing.use.ITEM"))
                i.setItem(13, item3);
            if (p.hasPermission("ultimaterepairing.use.XP"))
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
                p.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.needspace"));
                return;
            }
            if (p.getItemInHand().getDurability() <= 0) {
                p.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.notdamaged"));
                return;
            }
            if (p.getItemInHand().getMaxStackSize() != 1) {
                p.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.cantrepair"));
                return;
            }

            Item i = p.getWorld().dropItem(loc.add(0.5, 2, 0.5), p.getItemInHand());

            // Support for EpicHoppers suction.
            i.setMetadata("grabbed", new FixedMetadataValue(instance, "true"));

            i.setMetadata("betterdrops_ignore", new FixedMetadataValue(instance, true));
            Vector vec = p.getEyeLocation().getDirection();
            vec.setX(0);
            vec.setY(0);
            vec.setZ(0);
            i.setVelocity(vec);
            i.setPickupDelay(3600);
            i.setMetadata("UltimateRepairing", new FixedMetadataValue(instance, ""));

            // Get from Map, put new instance in Map if it doesn't exist
            PlayerAnvilData playerData = playerAnvilData.computeIfAbsent(p.getUniqueId(), uuid -> new PlayerAnvilData());
            playerData.setItem(i);
            playerData.setToBeRepaired(p.getItemInHand());
            playerData.setLocations(loc.add(0, -2, 0));

            yesNo(p, type, p.getItemInHand());


            p.setItemInHand(null);
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (i.isValid() && !playerData.isBeingRepaired()) {
                    p.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.timeout")));
                    removeItem(playerData, p);
                    p.closeInventory();

                }
            }, instance.getConfig().getLong("Main.Time Before Repair Auto Canceled"));
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void initRepair(Player p, Location location) {
        int num = 0;
        if (p.hasPermission("ultimaterepairing.use.ECO"))
            num++;
        if (p.hasPermission("ultimaterepairing.use.XP"))
            num++;
        if (p.hasPermission("ultimaterepairing.use.ITEM"))
            num++;

        if (num >= 2 || p.hasPermission("ultimaterepairing.use.*")) {
            repairType(p);
            getDataFor(p).setLocation(location);
        } else if (p.hasPermission("ultimaterepairing.use.eco"))
            instance.getRepairHandler().preRepair(p, RepairType.ECONOMY, location);
        else if (p.hasPermission("ultimaterepairing.use.XP"))
            instance.getRepairHandler().preRepair(p, RepairType.XP, location);
        else if (p.hasPermission("ultimaterepairing.use.ITEM"))
            instance.getRepairHandler().preRepair(p, RepairType.ITEM, location);
    }

    private void yesNo(Player p, RepairType type, ItemStack item) {
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
                cost = "\\$" + price;
            else if (type == RepairType.ITEM)
                cost = price + " " + name;

            Inventory i = Bukkit.createInventory(null, 27, instance.getLocale().getMessage("interface.yesno.title", cost));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item2 = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Buy Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            itemmeta2.setDisplayName(instance.getLocale().getMessage("interface.yesno.yes"));
            item2.setItemMeta(itemmeta2);

            ItemStack item3 = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta itemmeta3 = item3.getItemMeta();
            itemmeta3.setDisplayName(instance.getLocale().getMessage("interface.yesno.no"));
            item3.setItemMeta(itemmeta3);

            i.setItem(4, item);
            i.setItem(11, item2);
            i.setItem(15, item3);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> p.openInventory(i), 1);

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


    public void finish(boolean answer, Player player) {
        try {
            PlayerAnvilData playerData = playerAnvilData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerAnvilData());
            if (!answer) {
                removeItem(playerData, player);
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.cancelled")));
                return;
            }
            RepairType type = playerData.getType();
            ItemStack players = playerData.getToBeRepaired();

            boolean economy = false;
            boolean sold = false;
            if (instance.getServer().getPluginManager().getPlugin("Vault") != null && type == RepairType.ECONOMY) {
                RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                int price = playerData.getPrice();

                if (econ.has(player, price)) {
                    econ.withdrawPlayer(player, price);
                    sold = true;
                }
                economy = true;
            }

            int cost = Methods.getCost(type, players);
            ItemStack item2 = new ItemStack(Methods.getType(players), cost);
            String name = (item2.getType().name().substring(0, 1).toUpperCase() + item2.getType().name().toLowerCase().substring(1)).replace("_", " ");
            if (type == RepairType.ITEM && Arconix.pl().getApi().getGUI().inventoryContains(player.getInventory(), item2)) {
                Arconix.pl().getApi().getGUI().removeFromInventory(player.getInventory(), item2);
                sold = true;
            }

            if (type == RepairType.XP && player.getLevel() >= playerData.getPrice() || sold || player.getGameMode() == GameMode.CREATIVE) {
                playerData.setBeingRepaired(true);

                Effect effect = Effect.STEP_SOUND;

                Location location = playerData.getLocations();
                player.getWorld().playEffect(location, effect, Material.REDSTONE_BLOCK);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> player.getWorld().playEffect(location, effect, Material.REDSTONE_BLOCK), 5L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    player.getWorld().playEffect(location, effect, Material.REDSTONE_BLOCK);
                    player.getWorld().playEffect(location, effect, Material.STONE);
                    Arconix.pl().getApi().getPlayer(player).playSound(Sound.valueOf("BLOCK_ANVIL_LAND"));
                }, 10L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> player.getWorld().playEffect(location, effect, Material.REDSTONE_BLOCK), 15L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> player.getWorld().playEffect(location, effect, Material.REDSTONE_BLOCK), 20L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    Arconix.pl().getApi().getPlayer(player).playSound(Sound.valueOf("BLOCK_ANVIL_LAND"));
                    player.getWorld().playEffect(location, effect, Material.REDSTONE_BLOCK);
                    player.getWorld().playEffect(location, effect, Material.ANVIL);
                    player.sendMessage(TextComponent.formatText(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.success")));
                    ItemStack repairedi = playerData.getToBeRepaired();
                    repairedi.setDurability((short) 0);
                    Item repaired = player.getWorld().dropItemNaturally(player.getLocation(), repairedi);
                    repaired.remove();
                    player.getInventory().addItem(playerData.getToBeRepaired());
                    playerData.getItem().remove();
                    if (player.getGameMode() != GameMode.CREATIVE &&
                            type == RepairType.XP) {
                        player.setLevel(player.getLevel() - playerData.getPrice());
                    }
                    this.playerAnvilData.remove(player.getUniqueId());
                    player.closeInventory();
                }, 25L);
                return;
            }

            if (type == RepairType.ECONOMY) {
                if (!economy)
                    player.sendMessage("Vault is not installed.");
                else
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.notenough", instance.getLocale().getMessage("interface.repair.eco"))));
            } else if (type == RepairType.XP)
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.notenough", instance.getLocale().getMessage("interface.repair.xp"))));
            else
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + instance.getLocale().getMessage("event.repair.notenough", name)));


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