package com.songoda.repairplus.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRPAnvil extends AbstractCommand {

    public CommandRPAnvil() {
        super("RPAnvil", null, true);
    }

    @Override
    protected ReturnType runCommand(RepairPlus instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        instance.getRepairHandler().initRepair(player, player.getLocation());
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "repairplus.rpanvil";
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
