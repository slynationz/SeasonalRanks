package com.fiverr.seasonalranks;

import com.fiverr.seasonalranks.commands.NewSeasonCommand;
import com.fiverr.seasonalranks.commands.SeasonCommand;
import com.fiverr.seasonalranks.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SeasonalRanks extends JavaPlugin {
    private DataManager dataManager;

    @Override
    public void onEnable() {
        // Initialize DataManager
        this.dataManager = new DataManager(this);
        this.dataManager.reload();

        // Check if LuckPerms is present
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            getLogger().warning("LuckPerms was not found on this server! This plugin requires LuckPerms to modify user ranks and permissions.");
        } else {
            getLogger().info("LuckPerms integration hook verified.");
        }

        // Register events
        getServer().getPluginManager().registerEvents(new com.fiverr.seasonalranks.listeners.PlayerJoinListener(this), this);

        // Register commands
        registerCommands();

        getLogger().info("SeasonalRanks Plugin has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Save config
        if (dataManager != null) {
            dataManager.save();
        }
        getLogger().info("SeasonalRanks Plugin has been disabled.");
    }

    private void registerCommands() {
        SeasonCommand seasonCmd = new SeasonCommand(this);
        if (getCommand("season") != null) {
            getCommand("season").setExecutor(seasonCmd);
            getCommand("season").setTabCompleter(seasonCmd);
        }

        NewSeasonCommand newSeasonCmd = new NewSeasonCommand(this);
        if (getCommand("newseason") != null) {
            getCommand("newseason").setExecutor(newSeasonCmd);
            getCommand("newseason").setTabCompleter(newSeasonCmd);
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
