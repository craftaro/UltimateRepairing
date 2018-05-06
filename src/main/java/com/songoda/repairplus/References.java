package com.songoda.repairplus;

public class References {

    private String prefix;

    public References() {
        prefix = Lang.PREFIX.getConfigValue(null) + " ";
    }

    public String getPrefix() {
        return this.prefix;
    }
}
