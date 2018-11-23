package com.songoda.ultimaterepairing.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public class CommandUltimateRepairing extends AbstractCommand {

    public CommandUltimateRepairing() {
        super("UltimateRepairing", null, false);
    }

    @Override
    protected ReturnType runCommand(UltimateRepairing instance, CommandSender sender, String... args) {
        sender.sendMessage("");
        sender.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&7Version " + instance.getDescription().getVersion() + " Created with <3 by &5&l&oBrianna"));

        for (AbstractCommand command : instance.getCommandManager().getCommands()) {
            if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                sender.sendMessage(TextComponent.formatText("&8 - &a" + command.getSyntax() + "&7 - " + command.getDescription()));
            }
        }
        sender.sendMessage("");

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/UltimateRepairing";
    }

    @Override
    public String getDescription() {
        return "Displays this page.";
    }
}
