package com.songoda.ultimaterepairing.handlers;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData.RepairType;
import com.songoda.ultimaterepairing.utils.Debugger;
import com.songoda.ultimaterepairing.utils.Methods;
import com.songoda.ultimaterepairing.utils.ServerVersion;
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
            Inventory i = Bukkit.createInventory(null, 27, Methods.formatText(instance.getLocale().getMessage("interface.repair.title").getMessage()));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Economy Icon")), 1);
            ItemMeta itemmeta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(instance.getLocale().getMessage("interface.repair.ecolore").getMessage());
            itemmeta.setLore(lore);
            itemmeta.setDisplayName(instance.getLocale().getMessage("interface.repair.eco").getMessage());
            item.setItemMeta(itemmeta);

            Material mat = Methods.getType(p.getItemInHand());

            ItemStack item3 = new ItemStack(mat, 1);
            String name = (mat.name().substring(0, 1).toUpperCase() + mat.name().toLowerCase().substring(1)).replace("_", " ");
            ItemMeta itemmeta3 = item3.getItemMeta();
            ArrayList<String> lore3 = new ArrayList<>();
            lore3.add(instance.getLocale().getMessage("interface.repair.itemlore")
                    .processPlaceholder("item", name).getMessage());
            itemmeta3.setLore(lore3);
            itemmeta3.setDisplayName(instance.getLocale().getMessage("interface.repair.item")
                    .processPlaceholder("ITEM", name).getMessage());
            item3.setItemMeta(itemmeta3);

            ItemStack item2 = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.XP Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            ArrayList<String> lore2 = new ArrayList<>();
            lore2.add(instance.getLocale().getMessage("interface.repair.xplore").getMessage());
            itemmeta2.setLore(lore2);
            itemmeta2.setDisplayName(instance.getLocale().getMessage("interface.repair.xp").getMessage());
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


    public void preRepair(Player player, RepairType type, Location loc) {
        try {
            Item item = player.getWorld().dropItem(loc.add(0.5, 2, 0.5), player.getItemInHand());

            // Support for EpicHoppers suction.
            item.setMetadata("grabbed", new FixedMetadataValue(instance, "true"));

            item.setMetadata("betterdrops_ignore", new FixedMetadataValue(instance, true));
            Vector vec = player.getEyeLocation().getDirection();
            vec.setX(0);
            vec.setY(0);
            vec.setZ(0);
            item.setVelocity(vec);
            item.setPickupDelay(3600);
            item.setMetadata("UltimateRepairing", new FixedMetadataValue(instance, ""));

            // Get from Map, put new instance in Map if it doesn't exist
            PlayerAnvilData playerData = playerAnvilData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerAnvilData());
            playerData.setItem(item);
            playerData.setToBeRepaired(player.getItemInHand());
            playerData.setLocations(loc.add(0, -2, 0));

            yesNo(player, type, player.getItemInHand());


            player.setItemInHand(null);
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (item.isValid() && !playerData.isBeingRepaired()) {

                    instance.getLocale().getMessage("event.repair.timeout").sendPrefixedMessage(player);
                    removeItem(playerData, player);
                    player.closeInventory();

                }
            }, instance.getConfig().getLong("Main.Time Before Repair Auto Canceled"));
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void initRepair(Player player, Location location) {
        int num = 0;
        if (player.hasPermission("ultimaterepairing.use.ECO"))
            num++;
        if (player.hasPermission("ultimaterepairing.use.XP"))
            num++;
        if (player.hasPermission("ultimaterepairing.use.ITEM"))
            num++;


        if (location.add(0, 1, 0).getBlock().getType() != Material.AIR) {
            instance.getLocale().getMessage("event.repair.needspace").sendPrefixedMessage(player);
            return;
        }
        if (player.getItemInHand().getDurability() <= 0) {
            instance.getLocale().getMessage("event.repair.notdamaged").sendPrefixedMessage(player);
            return;
        }
        if (player.getItemInHand().getMaxStackSize() != 1) {
            instance.getLocale().getMessage("event.repair.cantrepair").sendPrefixedMessage(player);
            return;
        }

        if (num >= 2 || player.hasPermission("ultimaterepairing.use.*")) {
            repairType(player);
            getDataFor(player).setLocation(location);
        } else if (player.hasPermission("ultimaterepairing.use.eco"))
            instance.getRepairHandler().preRepair(player, RepairType.ECONOMY, location);
        else if (player.hasPermission("ultimaterepairing.use.XP"))
            instance.getRepairHandler().preRepair(player, RepairType.XP, location);
        else if (player.hasPermission("ultimaterepairing.use.ITEM"))
            instance.getRepairHandler().preRepair(player, RepairType.ITEM, location);
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

            Material mat = Methods.getType(item);
            String name = Methods.formatText(mat.name(), true);

            if (type == RepairType.XP)
                cost = price + " XP";
            else if (type == RepairType.ECONOMY)
                cost = "\\$" + price;
            else if (type == RepairType.ITEM)
                cost = price + " " + name;

            Inventory inventory = Bukkit.createInventory(null, 27,
                    Methods.formatTitle(instance.getLocale().getMessage("interface.yesno.title")
                            .processPlaceholder("cost", cost).getMessage()));

            int nu = 0;
            while (nu != 27) {
                inventory.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item2 = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Buy Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            itemmeta2.setDisplayName(instance.getLocale().getMessage("interface.yesno.yes").getMessage());
            item2.setItemMeta(itemmeta2);

            ItemStack item3 = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta itemmeta3 = item3.getItemMeta();
            itemmeta3.setDisplayName(instance.getLocale().getMessage("interface.yesno.no").getMessage());
            item3.setItemMeta(itemmeta3);

            inventory.setItem(4, item);
            inventory.setItem(11, item2);
            inventory.setItem(15, item3);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> p.openInventory(inventory), 1);

            playerData.setType(type);
            playerData.setPrice(price);

            inventory.setItem(0, Methods.getBackgroundGlass(true));
            inventory.setItem(1, Methods.getBackgroundGlass(true));
            inventory.setItem(2, Methods.getBackgroundGlass(false));
            inventory.setItem(6, Methods.getBackgroundGlass(false));
            inventory.setItem(7, Methods.getBackgroundGlass(true));
            inventory.setItem(8, Methods.getBackgroundGlass(true));
            inventory.setItem(9, Methods.getBackgroundGlass(true));
            inventory.setItem(10, Methods.getBackgroundGlass(false));
            inventory.setItem(16, Methods.getBackgroundGlass(false));
            inventory.setItem(17, Methods.getBackgroundGlass(true));
            inventory.setItem(18, Methods.getBackgroundGlass(true));
            inventory.setItem(19, Methods.getBackgroundGlass(true));
            inventory.setItem(20, Methods.getBackgroundGlass(false));
            inventory.setItem(24, Methods.getBackgroundGlass(false));
            inventory.setItem(25, Methods.getBackgroundGlass(true));
            inventory.setItem(26, Methods.getBackgroundGlass(true));

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void finish(boolean answer, Player player) {
        try {
            PlayerAnvilData playerData = playerAnvilData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerAnvilData());
            if (!answer) {
                removeItem(playerData, player);
                instance.getLocale().getMessage("event.repair.cancelled").sendPrefixedMessage(player);
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
            if (type == RepairType.ITEM && Methods.inventoryContains(player.getInventory(), item2)) {
                Methods.removeFromInventory(player.getInventory(), item2);
                sold = true;
            }

            if (type == RepairType.XP && player.getLevel() >= playerData.getPrice() || sold || player.getGameMode() == GameMode.CREATIVE) {
                playerData.setBeingRepaired(true);

                Effect effect = Effect.STEP_SOUND;

                Material blockType = Material.REDSTONE_BLOCK;

                String typeStr = playerData.getToBeRepaired().getType().name().toUpperCase();

                if (typeStr.contains("DIAMOND")) {
                    blockType = Material.DIAMOND_BLOCK;
                } else if (typeStr.contains("IRON")) {
                    blockType = Material.IRON_BLOCK;
                } else if (typeStr.contains("GOLD")) {
                    blockType = Material.GOLD_BLOCK;
                } else if (typeStr.contains("STONE")) {
                    blockType = Material.STONE;
                } else if (typeStr.contains("WOOD")) {
                    blockType = instance.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_PLANKS : Material.valueOf("WOOD");
                }

                final Material blockTypeFinal = blockType;

                Location location = playerData.getLocations();
                player.getWorld().playEffect(location, effect, blockType);
                Runnable runnable = () -> player.getWorld().playEffect(location, effect, blockTypeFinal);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, runnable, 5L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    player.getWorld().playEffect(location, effect, blockTypeFinal);
                    player.getWorld().playEffect(location, effect, Material.STONE);
                    if (instance.isServerVersion(ServerVersion.V1_8))
                        player.playSound(location, Sound.valueOf("ANVIL_LAND"), 1L, 1L);
                    else
                        player.playSound(location, Sound.valueOf("BLOCK_ANVIL_LAND"), 1L, 1L);
                }, 10L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, runnable, 15L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, runnable, 20L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    if (instance.isServerVersion(ServerVersion.V1_8))
                        player.playSound(location, Sound.valueOf("ANVIL_LAND"), 1L, 1L);
                    else
                        player.playSound(location, Sound.valueOf("BLOCK_ANVIL_LAND"), 1L, 1L);
                    player.getWorld().playEffect(location, effect, blockTypeFinal);
                    player.getWorld().playEffect(location, effect, Material.ANVIL);
                    instance.getLocale().getMessage("event.repair.success").sendPrefixedMessage(player);

                    playerData.getToBeRepaired().setDurability((short) 0);
                    HashMap<Integer, ItemStack> items = player.getInventory().addItem(playerData.getToBeRepaired());
                    for (ItemStack item : items.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                    }

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
                    instance.getLocale().getMessage("event.repair.notenough")
                            .processPlaceholder("type", instance.getLocale().getMessage("interface.repair.eco").getMessage())
                            .sendPrefixedMessage(player);
            } else if (type == RepairType.XP)
                instance.getLocale().getMessage("event.repair.notenough")
                        .processPlaceholder("type", instance.getLocale().getMessage("interface.repair.xp").getMessage())
                        .sendPrefixedMessage(player);
            else
                instance.getLocale().getMessage("event.repair.notenough")
                        .processPlaceholder("type", name).sendPrefixedMessage(player);


        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void removeItem(PlayerAnvilData playerData, Player player) {
        try {
            player.getInventory().addItem(playerData.getToBeRepaired());
            playerData.getItem().remove();

            this.playerAnvilData.remove(player.getUniqueId());
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public boolean hasInstance(Player player) {
        return playerAnvilData.containsKey(player.getUniqueId());
    }

    public PlayerAnvilData getDataFor(Player player) {
        return playerAnvilData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerAnvilData());
    }

}