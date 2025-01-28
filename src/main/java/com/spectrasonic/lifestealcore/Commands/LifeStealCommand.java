package com.spectrasonic.lifestealcore.Commands;

import com.spectrasonic.lifestealcore.Main;
import com.spectrasonic.lifestealcore.Utils.MessageUtils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("lifesteal|ls")
public class LifeStealCommand extends BaseCommand {
    private final Main plugin;

    public LifeStealCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandPermission("lifesteal.help")
    public void onHelp(CommandSender sender) {
        MessageUtils.sendMessage(sender, 
            "<yellow>Comandos de LifeSteal:\n" +
            "<green>/lifesteal add <cantidad> <jugador> - Añade corazones\n" +
            "/lifesteal remove <cantidad> <jugador> - Quita corazones\n" +
            "/lifesteal reload - Recarga la configuración"
        );
    }

    @Subcommand("add")
    @CommandPermission("lifesteal.admin")
    @CommandCompletion("@range:1-10 @players")
    public void onAddHearts(CommandSender sender, int hearts, Player target) {
        addHearts(target, hearts);
        
        MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.heart-added")
            .replace("{hearts}", String.valueOf(hearts))
            .replace("{player}", target.getName()));
    }

    @Subcommand("remove")
    @CommandPermission("lifesteal.admin")
    @CommandCompletion("@hearts @players")
    public void onRemoveHearts(CommandSender sender, int hearts, Player target) {
        removeHearts(target, hearts);
        
        MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.heart-removed")
            .replace("{hearts}", String.valueOf(hearts))
            .replace("{player}", target.getName()));
    }

    @Subcommand("reload")
    @CommandPermission("lifesteal.admin")
    public void onReload(CommandSender sender) {
        plugin.reloadPluginConfig();
        MessageUtils.sendMessage(sender, "<green>Configuración recargada.");
    }

    @Subcommand("set")
    @CommandPermission("lifesteal.admin")
    @CommandCompletion("@hearts @players")
    public void onSetHearts(CommandSender sender, int hearts, Player target) {
        if (hearts <= 0) {
            MessageUtils.sendMessage(sender, "<red>No puedes establecer 0 o menos corazones.");
            return;
        }

        int maxHearts = plugin.getConfig().getInt("max-hearts", 60);
        if (hearts * 2 > maxHearts) {
            MessageUtils.sendMessage(sender, "<red>No puedes establecer más de " + (maxHearts/2) + " corazones.");
            return;
        }

        setHearts(target, hearts);
        
        MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.heart-set")
            .replace("{hearts}", String.valueOf(hearts))
            .replace("{player}", target.getName()));
    }

    private void addHearts(Player player, int hearts) {
        double currentMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double newMax = Math.min(currentMax + (hearts * 2), 
            plugin.getConfig().getInt("max-hearts", 60));
        
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMax);
        player.setHealth(newMax);
    }

    private void removeHearts(Player player, int hearts) {
        double currentMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double newMax = Math.max(currentMax - (hearts * 2), 2);
        
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMax);
        player.setHealth(newMax);
    }

    private void setHearts(Player player, int hearts) {
        double newMax = hearts * 2;
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMax);
        player.setHealth(newMax);
    }
}