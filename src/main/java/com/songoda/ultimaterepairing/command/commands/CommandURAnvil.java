package com.songoda.ultimaterepairing.command.commands;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandURAnvil extends AbstractCommand {

    public CommandURAnvil() {
        super("URAnvil", null, true);
    }

    @Override
    protected ReturnType runCommand(UltimateRepairing instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        instance.getRepairHandler().initRepair(player, player.getLocation());
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimaterepairing.rpanvil";
    }

    @Override
    public String getSyntax() {
        return "/URAnvil";
    }

    @Override
    public String getDescription() {
        return "Open the repair interface from anywhere.";
    }
}
