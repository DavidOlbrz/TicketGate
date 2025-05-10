package com.theagent.ticketgate;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Path;
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

        updateConfig();
    }

    void initialize() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    /**
     * Checks if the config file is outdated and creates a backup if so
     */
    void updateConfig() {
        // get versions of plugin and config
        String[][] versions = new String[2][3];
        versions[0] = plugin.getDescription().getVersion().split("\\.");
        versions[1] = Objects.requireNonNull(config.getString("version")).split("\\.");

        // TODO remove
        plugin.getLogger().info("Plugin version: " + versions[0][0] + "." + versions[0][1] + "." + versions[0][2]);
        plugin.getLogger().info("Config version: " + versions[1][0] + "." + versions[1][1] + "." + versions[1][2]);

        // check if the config version is outdated
        if (versions[1].length != 3 || isOlderVersion(versions)) {
            plugin.getLogger().warning("Outdated config! Creating backup of old config file...");
            backupConfig();
        }
    }

    /**
     * Checks if the config version is older than the plugin version
     *
     * @param versions config version and plugin version
     * @return true if the config is outdated
     */
    boolean isOlderVersion(String[][] versions) {
        for (int i = 0; i < 3; i++) {
            if (Integer.parseInt(versions[0][i]) > Integer.parseInt(versions[1][i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * creates a backup of the old config file
     */
    void backupConfig() {
        // path to the plugin folder
        Path pluginFolder = plugin.getDataFolder().toPath();
        // current config file
        File configFile = pluginFolder.resolve("config.yml").toFile();
        // renaming the config file
        boolean success = configFile.renameTo(new File(pluginFolder.resolve(
                "config-backup-"
                        + config.getString("version", "noVersionDefined")
                        + ".yml"
        ).toString()));
        // send error if renaming wasn't successful
        if (!success) {
            plugin.getLogger().severe("Could not backup config file!");
            return;
        }

        // re-initializes the config file
        initialize();

        plugin.getLogger().info("Finished creating backup of old config file.");
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
