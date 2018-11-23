package com.songoda.ultimaterepairing.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload(AbstractCommand parent) {
        super("reload", parent, false);
    }

    @Override
    protected ReturnType runCommand(UltimateRepairing instance, CommandSender sender, String... args) {
        instance.reload();
        sender.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&7Configuration and Language files reloaded."));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimaterepairing.admin";
    }

    @Override
    public String getSyntax() {
        return "/rp reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Configuration and Language files.";
    }
}
