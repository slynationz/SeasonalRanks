package com.fiverr.seasonalranks.commands;

import com.fiverr.seasonalranks.SeasonalRanks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SeasonCommand implements CommandExecutor, TabCompleter {
    private final SeasonalRanks plugin;

    public SeasonCommand(SeasonalRanks plugin) {
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

        if (args.length < 4) {
            sendUsage(sender);
            return true;
        }

        String type = args[0]; // "rank" or "perm"
        String action = args[1]; // "add"
        String playerName = args[2];
        String targetValue = args[3]; // rank name or permission string

        if (!action.equalsIgnoreCase("add")) {
            sendUsage(sender);
            return true;
        }

        Player onlinePlayer = Bukkit.getPlayer(playerName);

        if (onlinePlayer != null) {
            UUID uuid = onlinePlayer.getUniqueId();
            String resolvedName = onlinePlayer.getName();

            if (type.equalsIgnoreCase("rank")) {
                String lpCommand = String.format("lp user %s parent add %s", uuid.toString(), targetValue);
                plugin.getLogger().info("Executing LuckPerms command: " + lpCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);

                plugin.getDataManager().addRank(uuid.toString(), resolvedName, targetValue);
                sender.sendMessage(color("&a&l[SeasonalRanks] &aSuccessfully added seasonal rank &e" + targetValue + " &ato online player &e" + resolvedName + " &a(" + uuid + ")."));
                return true;
            } else if (type.equalsIgnoreCase("perm")) {
                String lpCommand = String.format("lp user %s permission set %s true", uuid.toString(), targetValue);
                plugin.getLogger().info("Executing LuckPerms command: " + lpCommand);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);

                plugin.getDataManager().addPermission(uuid.toString(), resolvedName, targetValue);
                sender.sendMessage(color("&a&l[SeasonalRanks] &aSuccessfully added seasonal permission &e" + targetValue + " &ato online player &e" + resolvedName + " &a(" + uuid + ")."));
                return true;
            } else {
                sendUsage(sender);
            }
        } else {
            // Player is offline. Check if we already have their UUID saved.
            String savedUUID = plugin.getDataManager().findSavedUUID(playerName);
            if (savedUUID != null) {
                if (type.equalsIgnoreCase("rank")) {
                    String lpCommand = String.format("lp user %s parent add %s", savedUUID, targetValue);
                    plugin.getLogger().info("Executing LuckPerms command (offline player with saved UUID): " + lpCommand);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);

                    plugin.getDataManager().addRank(savedUUID, playerName, targetValue);
                    sender.sendMessage(color("&a&l[SeasonalRanks] &aSuccessfully added seasonal rank &e" + targetValue + " &ato offline player &e" + playerName + " &a(" + savedUUID + ")."));
                    return true;
                } else if (type.equalsIgnoreCase("perm")) {
                    String lpCommand = String.format("lp user %s permission set %s true", savedUUID, targetValue);
                    plugin.getLogger().info("Executing LuckPerms command (offline player with saved UUID): " + lpCommand);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), lpCommand);

                    plugin.getDataManager().addPermission(savedUUID, playerName, targetValue);
                    sender.sendMessage(color("&a&l[SeasonalRanks] &aSuccessfully added seasonal permission &e" + targetValue + " &ato offline player &e" + playerName + " &a(" + savedUUID + ")."));
                    return true;
                } else {
                    sendUsage(sender);
                }
            } else {
                // Player is offline and UUID is not saved. Add to queue.
                if (type.equalsIgnoreCase("rank")) {
                    plugin.getDataManager().addQueuedRank(playerName, targetValue);
                    sender.sendMessage(color("&a&l[SeasonalRanks] &ePlayer &6" + playerName + " &eis offline. Seasonal rank &6" + targetValue + " &ehas been added to their login queue."));
                    return true;
                } else if (type.equalsIgnoreCase("perm")) {
                    plugin.getDataManager().addQueuedPermission(playerName, targetValue);
                    sender.sendMessage(color("&a&l[SeasonalRanks] &ePlayer &6" + playerName + " &eis offline. Seasonal permission &6" + targetValue + " &ehas been added to their login queue."));
                    return true;
                } else {
                    sendUsage(sender);
                }
            }
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(color("&c&m----------------------------------------"));
        sender.sendMessage(color("&6&lSeasonal Ranks &7- Admin Commands"));
        sender.sendMessage(color("&e/season rank add {player} {rank}"));
        sender.sendMessage(color("&7  - Adds seasonal LuckPerms rank to player and tracks it."));
        sender.sendMessage(color("&e/season perm add {player} {permission}"));
        sender.sendMessage(color("&7  - Adds seasonal LuckPerms permission to player and tracks it."));
        sender.sendMessage(color("&c&m----------------------------------------"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("seasonalranks.admin")) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("rank".startsWith(input)) completions.add("rank");
            if ("perm".startsWith(input)) completions.add("perm");
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            if ((args[0].equalsIgnoreCase("rank") || args[0].equalsIgnoreCase("perm")) && "add".startsWith(input)) {
                completions.add("add");
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("add")) {
                String input = args[2].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("rank") && args[1].equalsIgnoreCase("add")) {
                String input = args[3].toLowerCase();
                for (String group : new String[]{"default", "vip", "vip+", "mvp", "admin", "moderator"}) {
                    if (group.startsWith(input)) {
                        completions.add(group);
                    }
                }
            }
        }

        return completions;
    }
}
