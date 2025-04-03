package com.theagent.ticketgate;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TicketGate extends JavaPlugin {

    /**
     * Runs when the plugin is starting
     */
    @Override
    public void onEnable() {
        // config handling
        ConfigManager configManager = new ConfigManager(this);
        // register commands
        Objects.requireNonNull(getCommand("ticketgate")).setExecutor(new TicketGateCommand(configManager));
        Objects.requireNonNull(getCommand("ticketgate")).setTabCompleter(new TicketGateTabCompleter(configManager));
        // register events
        getServer().getPluginManager().registerEvents(new GateEventListener(configManager), this);
        // start message
        getLogger().info("TicketGate enabled");
    }

    /**
     * Runs when the plugin is stopping
     */
    @Override
    public void onDisable() {
        // stop message
        getLogger().info("TicketGate disabled");
    }

}
