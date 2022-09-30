package com.theagent.ticketgate;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TicketGate extends JavaPlugin {

    /**
     * Runs when the plugin is starting
     */
    @Override
    public void onEnable() {
        // start message
        System.out.println("Enabling TicketGate"); // TODO use bukkit's logger instead
        // save default config
        saveDefaultConfig();
        // register commands
        Objects.requireNonNull(getCommand("ticketgate")).setExecutor(new TicketGateCommand(this));
        Objects.requireNonNull(getCommand("ticketgate")).setTabCompleter(new TicketGateTabCompleter(this.getConfig()));
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

}
