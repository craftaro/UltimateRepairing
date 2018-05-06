package com.songoda.repairplus.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.utils.Debugger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 2/25/2017.
 */
public class CommandHandler implements CommandExecutor {

    private final RepairPlus instance;

    public CommandHandler(RepairPlus instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (cmd.getName().equalsIgnoreCase("RPAnvil")) {
                Player player = (Player) sender;
                if (player.hasPermission("repairplus.rpanvil"))
                    instance.repair.initRepair(player, player.getLocation());
            } else if (cmd.getName().equalsIgnoreCase("repairplus")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                    sender.sendMessage("");
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&7" + instance.getDescription().getVersion() + " Created by &5&l&oBrianna"));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aRP help &7Displays this page."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aRP reload &7Reload the Configuration and Language files."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aRP holo &7Toggle a hologram for the anvil you are looking at."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aRP particles &7Toggle particles for the anvil you are looking at."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aRP inf &7Toggle unbreaking for the anvil you are looking at."));
                    sender.sendMessage("");
                } else if (args[0].equalsIgnoreCase("reload") &&
                        sender.hasPermission("repairplus.admin")) {
                    instance.reload();
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8Configuration and Language files reloaded."));
                } else if (args[0].equalsIgnoreCase("holo")) {
                    if (instance.v1_7) {
                        sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&7Holograms are not currently supported on 1.7... SORRY!"));
                        return true;
                    }
                    Player player = (Player) sender;
                    if (player.hasPermission("repairplus.admin")) {
                        Block block = player.getTargetBlock(null, 200);

                        if (block.getType() != Material.ANVIL) return true;

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
                        instance.holo.updateHolograms();
                        instance.saveConfig();
                    }
                } else if (args[0].equalsIgnoreCase("particles")) {
                    Player player = (Player) sender;
                    if (player.hasPermission("repairplus.admin")) {
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
                    }
                } else if (args[0].equalsIgnoreCase("settings")) {
                    if (sender.hasPermission("repairPlus.admin")) {
                        Player player = (Player) sender;
                        instance.settingsManager.openEditor(player);
                    }
                } else if (args[0].equalsIgnoreCase("inf") || args[0].equalsIgnoreCase("infinity")) {
                    Player player = (Player) sender;
                    if (player.hasPermission("repairplus.admin") || player.hasPermission("repairplus.infinity")) {
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
                    }
                } else
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8Invalid argument.."));
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
        return true;
    }
}