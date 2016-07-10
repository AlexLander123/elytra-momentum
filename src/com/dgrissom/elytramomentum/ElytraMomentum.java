package com.dgrissom.elytramomentum;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ElytraMomentum extends JavaPlugin {
    private static Set<UUID> enabledPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // this should not happen, since we have copyDefaults set to true.
        if (!getConfig().contains("update-delay")) {
            getConfig().set("update-delay", 5);
            saveDefaultConfig();
        }

        // this should not happen, since we have copyDefaults set to true.
        if (!getConfig().contains("speed")) {
            getConfig().set("speed", 1);
            saveDefaultConfig();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (UUID id : enabledPlayers) {
                Player player = Bukkit.getPlayer(id);
                // must be offline
                if (player == null) {
                    enabledPlayers.remove(id);
                    continue;
                }

                if (player.isGliding() && enabledPlayers.contains(player.getUniqueId()))
                    player.setVelocity(player.getLocation().getDirection().normalize().multiply(getConfig().getDouble("speed")));
            }
        }, 0, getConfig().getInt("update-delay"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getLabel().equalsIgnoreCase("toggleglide")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can run that command!");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("elytramomentum.toggleglide")) {
                player.sendMessage(ChatColor.RED + "You are not allowed to use that command!");
                return true;
            }

            if (!enabledPlayers.contains(player.getUniqueId())) {
                enabledPlayers.add(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "You have enabled elytra gliding.");
            } else {
                enabledPlayers.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "You have disabled elytra gliding.");
            }

            return true;
        }

        return false;
    }
}
