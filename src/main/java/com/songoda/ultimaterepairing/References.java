package com.songoda.ultimaterepairing;

public class References {

    private String prefix;

    public References() {
        prefix = UltimateRepairing.getInstance().getLocale().getMessage("general.nametag.prefix") + " ";
    }

    public String getPrefix() {
        return this.prefix;
    }
}
