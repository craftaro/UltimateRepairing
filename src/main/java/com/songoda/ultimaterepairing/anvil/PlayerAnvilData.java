package com.songoda.ultimaterepairing.anvil;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class PlayerAnvilData {

    public enum RepairType { ECONOMY, ITEM, XP }

    private Location location;
    private int price;
    private RepairType type;
    private Item item;
    private ItemStack toBeRepaired;
    private Location locations;
    private boolean inRepair;
    private boolean beingRepaired;

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setType(RepairType type) {
        this.type = type;
    }

    public RepairType getType() {
        return type;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setToBeRepaired(ItemStack toBeRepaired) {
        this.toBeRepaired = toBeRepaired;
    }

    public ItemStack getToBeRepaired() {
        return toBeRepaired;
    }

    public void setLocations(Location locations) {
        this.locations = locations;
    }

    public Location getLocations() {
        return locations;
    }

    public boolean getInRepair() {
        return inRepair;
    }

    public void setInRepair(boolean inRepair) {
        this.inRepair = inRepair;
    }

    public boolean isBeingRepaired() {
        return beingRepaired;
    }

    public void setBeingRepaired(boolean beingRepaired) {
        this.beingRepaired = beingRepaired;
    }
}