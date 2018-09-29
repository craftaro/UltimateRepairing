package com.songoda.repairplus.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHolo extends AbstractCommand {

    public CommandHolo(AbstractCommand parent) {
        super("holo", parent, true);
    }

    @Override
    protected ReturnType runCommand(RepairPlus instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 200);

        if (block.getType() != Material.ANVIL) return ReturnType.FAILURE;

        String loc = Arconix.pl().getApi().serialize().serializeLocation(block);
        if (instance.getConfig().getString("data.anvil." + loc + ".active") == null)
            instance.getConfig().set("data.anvil." + loc + ".active", true);

        if (instance.getConfig().getString("data.anvil." + loc + ".holo") == null) {
            instance.getConfig().set("data.anvil." + loc + ".holo", true);
            player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&aHolograms &9enabled &afor this anvil."));
        } else {
            instance.getConfig().set("data.anvil." + loc + ".holo", null);
            player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&aHolograms &9disabled &afor this anvil."));
        }
        instance.getHologramHandler().updateHolograms();
        instance.saveConfig();
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "repairplus.admin";
    }

    @Override
    public String getSyntax() {
        return "/rp holo";
    }

    @Override
    public String getDescription() {
        return "Toggle a hologram for the anvil you are looking at.";
    }
}
