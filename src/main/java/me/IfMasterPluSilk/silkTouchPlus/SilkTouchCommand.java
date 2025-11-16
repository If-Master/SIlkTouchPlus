package me.IfMasterPluSilk.silkTouchPlus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SilkTouchCommand implements CommandExecutor, TabCompleter {

    private final SilkTouchPlus plugin;

    public SilkTouchCommand(SilkTouchPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "SilkTouchPlus " + ChatColor.GRAY + "v1.0.0");
            sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/silktouchplus reload " + ChatColor.YELLOW + "to reload config");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("silktouchplus.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reload the plugin!");
                return true;
            }

            plugin.reloadPluginConfig();
            sender.sendMessage(ChatColor.GREEN + "SilkTouchPlus configuration reloaded!");
            sender.sendMessage(ChatColor.GRAY + "Whitelist: " + plugin.getConfig().getStringList("whitelist").size() + " blocks");
            sender.sendMessage(ChatColor.GRAY + "Blacklist: " + plugin.getConfig().getStringList("blacklist").size() + " blocks");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /silktouchplus reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("silktouchplus.reload")) {
                completions.add("reload");
            }
        }

        return completions;
    }
}