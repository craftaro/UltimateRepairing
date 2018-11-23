package com.songoda.ultimaterepairing.command.commands;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super("settings", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateRepairing instance, CommandSender sender, String... args) {
        Player p = (Player) sender;
        instance.getSettingsManager().openSettingsManager(p);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimaterepairing.admin";
    }

    @Override
    public String getSyntax() {
        return "/ur settings";
    }

    @Override
    public String getDescription() {
        return "Edit the UltimateRepairing Settings.";
    }
}
