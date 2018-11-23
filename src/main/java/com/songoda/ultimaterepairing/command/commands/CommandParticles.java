package com.songoda.ultimaterepairing.command.commands;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParticles extends AbstractCommand {

    public CommandParticles(AbstractCommand parent) {
        super("particles", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateRepairing instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        Block block = player.getTargetBlock(null, 200);
        if (block.getType() == Material.ANVIL) {
            String loc = Arconix.pl().getApi().serialize().serializeLocation(block);
            if (instance.getConfig().getString("data.anvil." + loc + ".active") == null)
                instance.getConfig().set("data.anvil." + loc + ".active", true);
            if (instance.getConfig().getString("data.anvil." + loc + ".particles") == null) {
                instance.getConfig().set("data.anvil." + loc + ".particles", true);
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&aParticles &9enabled &afor this anvil."));
            } else {
                instance.getConfig().set("data.anvil." + loc + ".particles", null);
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&aParticles &9disabled &afor this anvil."));
            }
            instance.saveConfig();
        }
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimaterepairing.admin";
    }

    @Override
    public String getSyntax() {
        return "/rp particles";
    }

    @Override
    public String getDescription() {
        return "Toggle particles for the anvil you are looking at.";
    }
}
