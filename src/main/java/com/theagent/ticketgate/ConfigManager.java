package com.theagent.ticketgate;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Set;

/**
 * Handles everything related to the configuration
 */
class ConfigManager {

    private final TicketGate plugin;
    private FileConfiguration config;

    ConfigManager(TicketGate plugin) {
        this.plugin = plugin;

        initialize();
    }

    void initialize() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    /**
     * Check if a specific key is already present
     *
     * @param key key
     * @return key already exists
     */
    boolean containsKey(String key) {
        return config.contains(key);
    }

    /**
     * Get a specific value as Object
     *
     * @param path path to value
     * @return Object value
     */
    Object get(String path) {
        return config.get(path);
    }

    /**
     * Get a specific value as String
     *
     * @param path path to value
     * @return String value
     */
    String getString(String path) {
        return config.getString(path);
    }

    /**
     * Get a specific value as Boolean
     *
     * @param path path to value
     * @return Boolean value
     */
    Boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    /**
     * Get a String Set of all configured gates
     *
     * @return String Set of all gates
     */
    Set<String> getGatesSet() {
        return Objects.requireNonNull(config.getConfigurationSection("gates")).getKeys(false);
    }

    /**
     * Get a String Array of all configured gates
     *
     * @return String Array of all gates
     */
    String[] getGates() {
        return getGatesSet().toArray(new String[0]);
    }

    /**
     * Set a specific property of a gate
     *
     * @param name     name of the gate
     * @param property property to edit
     * @param value    new value
     */
    void setProperty(String name, String property, Object value) {
        config.set(
                "gates." + name + "." + property,
                value
        );
        saveConfig();
    }

    /**
     * Set a specific property of a gate
     *
     * @param property gate property
     */
    void setProperty(GateProperty property) {
        config.set(
                property.getPath(),
                property.getValue()
        );
        saveConfig();
    }

    /**
     * Set multiple properties at once
     *
     * @param properties gate properties
     */
    void setProperties(GateProperty... properties) {
        for (GateProperty property : properties) {
            config.set(
                    property.getPath(),
                    property.getValue()
            );
        }
        saveConfig();
    }

    /**
     * Set a new key for the Master Key
     *
     * @param key new key
     */
    void setMasterKey(String key) {
        config.set("master-key", key);
        saveConfig();
    }

    /**
     * Deletes a configured gate from the configuration
     *
     * @param name gate to delete
     */
    void deleteGateConfig(String name) {
        config.set("gates." + name, null);
        saveConfig();
    }

    /**
     * save to config file
     */
    private void saveConfig() {
        plugin.saveConfig();
    }

    /**
     * reloads the config file
     */
    void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

}
