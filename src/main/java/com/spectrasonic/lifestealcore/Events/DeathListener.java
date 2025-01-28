package com.spectrasonic.lifestealcore.Events;

import com.spectrasonic.lifestealcore.Utils.MessageUtils;

import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import com.spectrasonic.lifestealcore.Main;

public class DeathListener implements Listener {
    private Main plugin;

    public DeathListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setCancelled(true);
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            removeHeart(victim);
            addHeart(killer);

            String gainedHeartMsg = plugin.getConfig().getString("messages.gained-heart");
            String lostHeartMsg = plugin.getConfig().getString("messages.lost-heart");

            MessageUtils.sendMessage(killer, gainedHeartMsg);
            MessageUtils.sendMessage(victim, lostHeartMsg);
        }
    }

    // Métodos de añadir y quitar corazones igual que antes
    private void removeHeart(Player player) {
        double currentMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double newMax = Math.max(currentMax - 2, 2);
        
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMax);
        player.setHealth(newMax);

        if (newMax <= 2) {
            String banMsg = plugin.getConfig().getString("messages.ban-message");
            Component banComponent = Component.text(banMsg);
            MessageUtils.sendMessage(player, banMsg);
            player.kick(banComponent);
        }
    }

    private void addHeart(Player player) {
        double currentMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double newMax = Math.min(currentMax + 2, 
            plugin.getConfig().getInt("max-hearts", 60));
        
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMax);
        player.setHealth(newMax);
    }
}