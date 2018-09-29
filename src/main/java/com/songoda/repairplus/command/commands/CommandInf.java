package com.songoda.repairplus.command.commands;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInf extends AbstractCommand {

    public CommandInf(AbstractCommand parent) {
        super("inf", parent, true);
    }

    @Override
    protected ReturnType runCommand(RepairPlus instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        Block b = player.getTargetBlock(null, 200);
        if (b.getType() == Material.ANVIL) {
            String loc = Arconix.pl().getApi().serialize().serializeLocation(b);
            if (instance.getConfig().getString("data.anvil." + loc + ".active") == null)
                instance.getConfig().set("data.anvil." + loc + ".active", true);
            if (instance.getConfig().getString("data.anvil." + loc + ".inf") == null) {
                instance.getConfig().set("data.anvil." + loc + ".inf", true);
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&aInfinity &9enabled &afor this anvil."));
            } else {
                instance.getConfig().set("data.anvil." + loc + ".inf", null);
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&aInfinity &9disabled &afor this anvil."));
            }
            instance.saveConfig();
        }
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "repairplus.admin";
    }

    @Override
    public String getSyntax() {
        return "/rp inf";
    }

    @Override
    public String getDescription() {
        return "Toggle unbreaking for the anvil you are looking at.";
    }
}
