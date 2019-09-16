package com.songoda.ultimaterepairing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimaterepairing.UltimateRepairing;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandURAnvil extends AbstractCommand {

    public CommandURAnvil() {
        super(true, "URAnvil");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        UltimateRepairing.getInstance().getRepairHandler().initRepair(player, player.getLocation());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender cs, String... strings) {
        return null;
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
