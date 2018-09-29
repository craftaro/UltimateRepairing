package com.songoda.repairplus.command.commands;

import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super("settings", parent, true);
    }

    @Override
    protected ReturnType runCommand(RepairPlus instance, CommandSender sender, String... args) {
        Player p = (Player) sender;
        instance.getSettingsManager().openSettingsManager(p);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "repairplus.admin";
    }

    @Override
    public String getSyntax() {
        return "/rp settings";
    }

    @Override
    public String getDescription() {
        return "Edit the RepairPlus Settings.";
    }
}
