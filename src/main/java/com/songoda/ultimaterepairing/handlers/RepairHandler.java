package com.songoda.ultimaterepairing.handlers;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.utils.PlayerUtils;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData;
import com.songoda.ultimaterepairing.anvil.PlayerAnvilData.RepairType;
import com.songoda.ultimaterepairing.gui.RepairTypeGui;
import com.songoda.ultimaterepairing.gui.StartConfirmGui;
import com.songoda.ultimaterepairing.utils.Methods;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by songoda on 2/25/2017.
 */
public class RepairHandler {

    private final UltimateRepairing instance;
    private final GuiManager guiManager;

    private final Map<UUID, PlayerAnvilData> playerAnvilData = new HashMap<>();

    public RepairHandler(UltimateRepairing instance, GuiManager guiManager) {
        this.instance = instance;
        this.guiManager = guiManager;
    }

    private void repairType(Player p, Location l) {
        if (getDataFor(p).getInRepair()) {
            yesNo(p, getDataFor(p).getType(), getDataFor(p).getToBeRepaired());
        } else {
            guiManager.showGUI(p, new RepairTypeGui(p, l));
        }
    }


    public void preRepair(Player player, RepairType type, Location anvil) {
        ItemStack itemStack = player.getItemInHand();
        player.setItemInHand(null);
        Item item = player.getWorld().dropItem(anvil.add(0.5, 2, 0.5), itemStack);

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
        playerData.setToBeRepaired(itemStack);
        playerData.setLocations(anvil.add(0, -2, 0));

        yesNo(player, type, itemStack);

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            if (item.isValid() && !playerData.isBeingRepaired()) {

                instance.getLocale().getMessage("event.repair.timeout").sendPrefixedMessage(player);
                removeItem(playerData, player);
                player.closeInventory();

            }
        }, instance.getConfig().getLong("Main.Time Before Repair Auto Canceled"));
    }

    public void initRepair(Player player, Location anvil) {
        if (anvil.add(0, 1, 0).getBlock().getType() != Material.AIR) {
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

        int num = 0;
        if (player.hasPermission("ultimaterepairing.use.ECO"))
            num++;
        if (player.hasPermission("ultimaterepairing.use.XP"))
            num++;
        if (num != 2 && player.hasPermission("ultimaterepairing.use.ITEM"))
            num++;


        if (num >= 2 || player.hasPermission("ultimaterepairing.use.*")) {
            repairType(player, anvil);
            getDataFor(player).setLocation(anvil);
        } else if (player.hasPermission("ultimaterepairing.use.eco"))
            instance.getRepairHandler().preRepair(player, RepairType.ECONOMY, anvil);
        else if (player.hasPermission("ultimaterepairing.use.XP"))
            instance.getRepairHandler().preRepair(player, RepairType.XP, anvil);
        else if (player.hasPermission("ultimaterepairing.use.ITEM"))
            instance.getRepairHandler().preRepair(player, RepairType.ITEM, anvil);
    }

    private void yesNo(Player p, RepairType type, ItemStack item) {
        PlayerAnvilData playerData = getDataFor(p);

        if (playerData.isBeingRepaired()) {
            return;
        }

        int price = Methods.getCost(type, item);
        playerData.setInRepair(true);
        playerData.setType(type);
        playerData.setPrice(price);

        guiManager.showGUI(p, new StartConfirmGui(playerData.getLocation(), type, p, item));
    }


    public void finish(boolean answer, Player player) {
        PlayerAnvilData playerData = playerAnvilData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerAnvilData());
        if (!answer) {
            removeItem(playerData, player);
            instance.getLocale().getMessage("event.repair.cancelled").sendPrefixedMessage(player);
            return;
        }
        RepairType type = playerData.getType();
        ItemStack players = playerData.getToBeRepaired();

        boolean sold = false;
        if (type == RepairType.ECONOMY && EconomyManager.isEnabled()) {
            int price = playerData.getPrice();

            if (EconomyManager.hasBalance(player, price)) {
                EconomyManager.withdrawBalance(player, price);
                sold = true;
            }
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
                blockType = CompatibleMaterial.OAK_WOOD.getMaterial();
            }

            final Material blockTypeFinal = blockType;

            Location location = playerData.getLocations();
            player.getWorld().playEffect(location, effect, blockType);
            Runnable runnable = () -> player.getWorld().playEffect(location, effect, blockTypeFinal);
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, runnable, 5L);
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                player.getWorld().playEffect(location, effect, blockTypeFinal);
                player.getWorld().playEffect(location, effect, Material.STONE);
                CompatibleSound.BLOCK_ANVIL_LAND.play(player);
            }, 10L);
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, runnable, 15L);
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, runnable, 20L);
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                CompatibleSound.BLOCK_ANVIL_LAND.play(player);
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

        // we've failed to repair, so return the item
        removeItem(playerData, player);
    }

    public void removeItem(PlayerAnvilData playerData, Player player) {
        PlayerUtils.giveItem(player, playerData.getToBeRepaired());
        playerData.getItem().remove();

        this.playerAnvilData.remove(player.getUniqueId());
    }

    public boolean hasInstance(Player player) {
        return playerAnvilData.containsKey(player.getUniqueId());
    }

    public PlayerAnvilData getDataFor(Player player) {
        return playerAnvilData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerAnvilData());
    }

}