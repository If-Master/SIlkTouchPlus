package me.IfMasterPluSilk.silkTouchPlus;

import org.bukkit.plugin.java.JavaPlugin;

public class SilkTouchPlus extends JavaPlugin {

    private static SilkTouchPlus instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        SilkTouchCommand commandHandler = new SilkTouchCommand(this);
        getCommand("silktouchplus").setExecutor(commandHandler);
        getCommand("silktouchplus").setTabCompleter(commandHandler);

        getLogger().info("SilkTouchPlus has been enabled!");
        getLogger().info("Whitelist blocks: " + getConfig().getStringList("whitelist").size());
        getLogger().info("Blacklist blocks: " + getConfig().getStringList("blacklist").size());
    }

    @Override
    public void onDisable() {
        getLogger().info("SilkTouchPlus has been disabled!");
    }

    public static SilkTouchPlus getInstance() {
        return instance;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        getLogger().info("Configuration reloaded!");
    }
}