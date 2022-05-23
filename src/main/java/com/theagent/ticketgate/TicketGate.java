package com.theagent.ticketgate;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class TicketGate extends JavaPlugin {

    private File gatesFile;
    private FileConfiguration gatesConfig;

    /**
     * Runs when the plugin is starting
     */
    @Override
    public void onEnable() {
        // config files
        initConfig(); // default config for general settings
        initGatesConfig(); // custom config for saving gate settings

        /*
        File gatesFile = new File(getDataFolder(), "gates.yml");
        if (!gatesFile.exists()) {
            try {
                gatesFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error: Can't load config file!"); // TODO use bukkit's logger instead
                return;
            }
        }
        YamlConfiguration gatesConfig = YamlConfiguration.loadConfiguration(gatesFile);
        */

        // start message
        System.out.println("Enabling TicketGate"); // TODO use bukkit's logger instead
        // register commands
        Objects.requireNonNull(getCommand("ticketgate")).setExecutor(new TicketGateCommand());
        // register events
        getServer().getPluginManager().registerEvents(new GateEventListener(this), this);
    }

    /**
     * Runs when the plugin is stopping
     */
    @Override
    public void onDisable() {
        // stop message
        System.out.println("Disabling TicketGate"); // TODO use bukkit's logger instead
    }

    public void initConfig() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    public void initGatesConfig() {
        gatesFile = new File(getDataFolder(), "gates.yml");
        if (!gatesFile.exists()) {
            saveResource("gates.yml", false);
        }
        gatesConfig = new YamlConfiguration();
        try {
            gatesConfig.load(gatesFile);
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Error: The config is not working as it should!"); // TODO use bukkit's logger instead
        }
    }

    public FileConfiguration getGatesConfig() {
        return gatesConfig;
    }

}
