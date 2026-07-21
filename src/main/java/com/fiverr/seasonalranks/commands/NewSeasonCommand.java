package com.fiverr.seasonalranks.commands;

import com.fiverr.seasonalranks.SeasonalRanks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NewSeasonCommand implements CommandExecutor, TabCompleter {
    private final SeasonalRanks plugin;

    public NewSeasonCommand(SeasonalRanks plugin) {
        this.plugin = plugin;
    }

    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("seasonalranks.admin")) {
            sender.sendMessage(color("&cYou do not have permission to execute this command."));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            resetSeason(sender);
            return true;
        }

        sender.sendMessage(color("&c&lWARNING: &cThis will remove all tracked seasonal ranks and permissions from LuckPerms for all players!"));
        sender.sendMessage(color("&cTo proceed, type: &e/newseason confirm"));
        return true;
    }

    private void resetSeason(CommandSender sender) {
        sender.sendMessage(color("&aStarting season reset..."));
        plugin.getLogger().info("Starting season reset initiated by " + sender.getName());

        Set<String> players = plugin.getDataManager().getTrackedPlayers();
        if (players.isEmpty()) {
            sender.sendMessage(color("&eNo seasonal ranks or permissions are currently tracked. Nothing to reset."));
            plugin.getDataManager().clearAll();
            return;
        }

        int ranksRemovedCount = 0;
        int permsRemovedCount = 0;

        for (String uuidStr : players) {
            String displayName = plugin.getDataManager().getPlayerName(uuidStr);
            List<String> ranks = plugin.getDataManager().getRanks(uuidStr);
            List<String> perms = plugin.getDataManager().getPermissions(uuidStr);

            if (!ranks.isEmpty() || !perms.isEmpty()) {
                plugin.getLogger().info(String.format("Resetting data for %s (%s)...", displayName, uuidStr));
            }

            for (String rank : ranks) {
                // lp user {uuid} parent remove {rank}
                String lpCommand = String.format("lp user %s parent remove %s", uuidStr, rank);
                plugin.getLogger().info("Executing LuckPerms command: " + lpCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);
                ranksRemovedCount++;
            }

            for (String perm : perms) {
                // lp user {uuid} permission unset {permission}
                String lpCommand = String.format("lp user %s permission unset %s", uuidStr, perm);
                plugin.getLogger().info("Executing LuckPerms command: " + lpCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);
                permsRemovedCount++;
            }
        }

        // Clears all data after everything is removed
        plugin.getDataManager().clearAll();

        sender.sendMessage(color("&a&lSUCCESS: &aSeason reset complete!"));
        sender.sendMessage(color(String.format("&aRemoved &e%d&a seasonal ranks and &e%d&a seasonal permissions.", ranksRemovedCount, permsRemovedCount)));
        plugin.getLogger().info(String.format("Season reset complete. Removed %d ranks and %d permissions.", ranksRemovedCount, permsRemovedCount));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("seasonalranks.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("confirm".startsWith(input)) {
                return Collections.singletonList("confirm");
            }
        }
        return Collections.emptyList();
    }
}
