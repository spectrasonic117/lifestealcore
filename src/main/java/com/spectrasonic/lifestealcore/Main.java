package com.spectrasonic.lifestealcore;

import com.spectrasonic.lifestealcore.Utils.MessageUtils;
import com.spectrasonic.lifestealcore.Commands.LifeStealCommand;
import com.spectrasonic.lifestealcore.Events.DeathListener;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;

import java.util.Arrays;


public class Main extends JavaPlugin {

    private static Main instance;
    private FileConfiguration config;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Configuración
        saveDefaultConfig();
        config = getConfig();

        MessageUtils.sendStartupMessage(this);
    }
    
    @Override
    public void onDisable() {
        if (this.commandManager != null) {
            this.commandManager.unregisterCommands();
        }
        MessageUtils.sendShutdownMessage(this);
    }
    
    public void registerCommand() {
        this.commandManager = new PaperCommandManager(this);
        
        // Register command completions
        commandManager.getCommandCompletions().registerCompletion("hearts", c -> {
            return Arrays.asList("1", "2", "3", "4", "5", "10", "15", "20", "25", "30");
        });
        
        this.commandManager.registerCommand(new LifeStealCommand(this));

    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
    }

    public static Main getInstance() {
        return instance;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        config = getConfig();
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }
}