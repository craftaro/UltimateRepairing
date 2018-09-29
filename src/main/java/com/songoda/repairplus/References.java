package com.songoda.repairplus;

public class References {

    private String prefix;

    public References() {
        prefix = RepairPlus.getInstance().getLocale().getMessage("general.nametag.prefix") + " ";
    }

    public String getPrefix() {
        return this.prefix;
    }
}
