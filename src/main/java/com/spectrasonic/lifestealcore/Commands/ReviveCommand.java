package com.spectrasonic.lifestealcore.Commands;

import com.spectrasonic.lifestealcore.Main;
import com.spectrasonic.lifestealcore.Utils.MessageUtils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("revive|rv")
public class ReviveCommand extends BaseCommand {
    private final Main plugin;
    private static final int DEFAULT_REVIVE_HEARTS = 3; // 3 hearts = 6 health points

    public ReviveCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandPermission("lifesteal.revive")
    @CommandCompletion("@players")
    public void onRevive(CommandSender sender, String playerName) {
        // Try to find the player (online or offline)
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayerIfCached(playerName);

        if (targetPlayer == null) {
            // Try to find by exact name
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                    targetPlayer = offlinePlayer;
                    break;
                }
            }
        }

        if (targetPlayer == null) {
            MessageUtils.sendMessage(sender, "<red>No se encontró al jugador " + playerName);
            return;
        }

        // Check if player is online
        if (targetPlayer.isOnline()) {
            Player onlinePlayer = targetPlayer.getPlayer();
            // If player is online and has more than minimum hearts, they don't need revival
            if (onlinePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() > 2) {
                MessageUtils.sendMessage(sender, "<red>El jugador " + playerName + " no necesita ser revivido.");
                return;
            }

            // Revive the online player
            revivePlayer(onlinePlayer);
            MessageUtils.sendMessage(sender,
                    "<green>Has revivido a " + playerName + " con " + DEFAULT_REVIVE_HEARTS + " corazones.");
            return;
        }

        // For offline players, we need to store the revival information
        // This could be done in a database or config file
        // For simplicity, we'll use a config section to store players to be revived
        String uuid = targetPlayer.getUniqueId().toString();
        plugin.getConfig().set("revive-queue." + uuid, DEFAULT_REVIVE_HEARTS);
        plugin.saveConfig();

        MessageUtils.sendMessage(sender, "<green>El jugador " + playerName + " será revivido con " +
                DEFAULT_REVIVE_HEARTS + " corazones cuando vuelva a conectarse.");
    }

    /**
     * Revives a player by setting their health to the default revival amount
     * 
     * @param player The player to revive
     */
    private void revivePlayer(Player player) {
        double newHealth = DEFAULT_REVIVE_HEARTS * 2; // Convert hearts to health points
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHealth);
        player.setHealth(newHealth);

        // Notify the player
        MessageUtils.sendMessage(player, "<green>¡Has sido revivido con " + DEFAULT_REVIVE_HEARTS + " corazones!");
    }

    /**
     * Checks if a player needs to be revived when they join
     * This method should be called from a PlayerJoinEvent listener
     * 
     * @param player The player who joined
     * @return true if the player was revived, false otherwise
     */
    public static boolean checkAndReviveJoiningPlayer(Player player, Main plugin) {
        String uuid = player.getUniqueId().toString();
        String path = "revive-queue." + uuid;

        if (plugin.getConfig().contains(path)) {
            int hearts = plugin.getConfig().getInt(path, DEFAULT_REVIVE_HEARTS);

            // Set the player's health
            double newHealth = hearts * 2;
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHealth);
            player.setHealth(newHealth);

            // Remove from the queue
            plugin.getConfig().set(path, null);
            plugin.saveConfig();

            // Notify the player
            MessageUtils.sendMessage(player, "<green>¡Has sido revivido con " + hearts + " corazones!");
            return true;
        }

        return false;
    }
}
