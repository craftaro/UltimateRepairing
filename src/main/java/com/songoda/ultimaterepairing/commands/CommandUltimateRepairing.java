package com.songoda.ultimaterepairing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.ultimaterepairing.utils.Methods;
import java.util.List;
import org.bukkit.command.CommandSender;

public class CommandUltimateRepairing extends AbstractCommand {

    public CommandUltimateRepairing() {
        super(false, "UltimateRepairing");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        sender.sendMessage("");
        UltimateRepairing instance = UltimateRepairing.getInstance();
        instance.getLocale().newMessage("&7Version " + instance.getDescription().getVersion()
                + " Created with <3 by &5&l&oSongoda").sendPrefixedMessage(sender);

        for (AbstractCommand command : instance.getCommandManager().getAllCommands()) {
            if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                sender.sendMessage(Methods.formatText("&8 - &a" + command.getSyntax() + "&7 - " + command.getDescription()));
            }
        }
        sender.sendMessage("");

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender cs, String... strings) {
        return null;
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
