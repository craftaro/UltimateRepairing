package com.songoda.ultimaterepairing.command.commands;

import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRPAnvil extends AbstractCommand {

    public CommandRPAnvil() {
        super("RPAnvil", null, true);
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
        return "/RPAnvil";
    }

    @Override
    public String getDescription() {
        return "Open the repair interface from anywhere.";
    }
}
