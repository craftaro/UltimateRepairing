package com.songoda.repairplus.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public class CommandRepairPlus extends AbstractCommand {

    public CommandRepairPlus() {
        super("RepairPlus", null, false);
    }

    @Override
    protected ReturnType runCommand(RepairPlus instance, CommandSender sender, String... args) {
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
        return "/RepairPlus";
    }

    @Override
    public String getDescription() {
        return "Displays this page.";
    }
}
