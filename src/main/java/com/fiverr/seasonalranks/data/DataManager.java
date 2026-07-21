package com.fiverr.seasonalranks.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class DataManager {
    private final JavaPlugin plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
    }

    public void reload() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public FileConfiguration getConfig() {
        if (dataConfig == null) {
            reload();
        }
        return dataConfig;
    }

    public void save() {
        if (dataConfig == null || dataFile == null) {
            return;
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save data to " + dataFile, e);
        }
    }

    public void addRank(String uuid, String name, String rank) {
        FileConfiguration config = getConfig();
        String path = "players." + uuid;
        config.set(path + ".name", name);
        
        List<String> ranks = config.getStringList(path + ".ranks");
        if (!ranks.contains(rank)) {
            ranks.add(rank);
            config.set(path + ".ranks", ranks);
            save();
        }
    }

    public void addPermission(String uuid, String name, String permission) {
        FileConfiguration config = getConfig();
        String path = "players." + uuid;
        config.set(path + ".name", name);
        
        List<String> permissions = config.getStringList(path + ".permissions");
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            config.set(path + ".permissions", permissions);
            save();
        }
    }

    public Set<String> getTrackedPlayers() {
        FileConfiguration config = getConfig();
        if (config.contains("players")) {
            return config.getConfigurationSection("players").getKeys(false);
        }
        return Collections.emptySet();
    }

    public List<String> getRanks(String uuid) {
        return getConfig().getStringList("players." + uuid + ".ranks");
    }

    public List<String> getPermissions(String uuid) {
        return getConfig().getStringList("players." + uuid + ".permissions");
    }

    public String getPlayerName(String uuid) {
        return getConfig().getString("players." + uuid + ".name");
    }

    public String findSavedUUID(String playerName) {
        FileConfiguration config = getConfig();
        if (config.contains("players")) {
            for (String uuid : config.getConfigurationSection("players").getKeys(false)) {
                String savedName = config.getString("players." + uuid + ".name");
                if (savedName != null && savedName.equalsIgnoreCase(playerName)) {
                    return uuid;
                }
            }
        }
        return null;
    }

    public void addQueuedRank(String playerName, String rank) {
        FileConfiguration config = getConfig();
        String path = "queue." + playerName.toLowerCase();
        config.set(path + ".name", playerName);
        List<String> ranks = config.getStringList(path + ".ranks");
        if (!ranks.contains(rank)) {
            ranks.add(rank);
            config.set(path + ".ranks", ranks);
            save();
        }
    }

    public void addQueuedPermission(String playerName, String permission) {
        FileConfiguration config = getConfig();
        String path = "queue." + playerName.toLowerCase();
        config.set(path + ".name", playerName);
        List<String> perms = config.getStringList(path + ".permissions");
        if (!perms.contains(permission)) {
            perms.add(permission);
            config.set(path + ".permissions", perms);
            save();
        }
    }

    public boolean hasQueuedData(String playerName) {
        return getConfig().contains("queue." + playerName.toLowerCase());
    }

    public List<String> getQueuedRanks(String playerName) {
        return getConfig().getStringList("queue." + playerName.toLowerCase() + ".ranks");
    }

    public List<String> getQueuedPermissions(String playerName) {
        return getConfig().getStringList("queue." + playerName.toLowerCase() + ".permissions");
    }

    public void removeQueuedPlayer(String playerName) {
        getConfig().set("queue." + playerName.toLowerCase(), null);
        save();
    }

    public void clearAll() {
        getConfig().set("players", null);
        getConfig().set("queue", null);
        save();
    }
}
