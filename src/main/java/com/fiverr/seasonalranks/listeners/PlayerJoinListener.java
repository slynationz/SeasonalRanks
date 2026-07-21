package com.fiverr.seasonalranks.listeners;

import com.fiverr.seasonalranks.SeasonalRanks;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final SeasonalRanks plugin;

    public PlayerJoinListener(SeasonalRanks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        UUID uuid = event.getPlayer().getUniqueId();
        
        if (plugin.getDataManager().hasQueuedData(name)) {
            plugin.getLogger().info("Processing queued seasonal data for joining player: " + name + " (" + uuid + ")");
            
            List<String> ranks = plugin.getDataManager().getQueuedRanks(name);
            List<String> perms = plugin.getDataManager().getQueuedPermissions(name);
            
            for (String rank : ranks) {
                String lpCommand = String.format("lp user %s parent add %s", uuid.toString(), rank);
                plugin.getLogger().info("Executing queued LuckPerms command: " + lpCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);
                
                plugin.getDataManager().addRank(uuid.toString(), name, rank);
            }
            
            for (String perm : perms) {
                String lpCommand = String.format("lp user %s permission set %s true", uuid.toString(), perm);
                plugin.getLogger().info("Executing queued LuckPerms command: " + lpCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);
                
                plugin.getDataManager().addPermission(uuid.toString(), name, perm);
            }
            
            plugin.getDataManager().removeQueuedPlayer(name);
        }
    }
}
