package com.theagent.ticketgate;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A class for tab completion of the /ticketgate command
 */
class TicketGateTabCompleter implements TabCompleter {

    private final ConfigManager config;

    TicketGateTabCompleter(ConfigManager configManager) {
        config = configManager;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "remove", "setGate", "ticket", "editBlock", "editName", "editLore", "setOneTimeUse", "reload"), new ArrayList<>());
            case 2:
                if (args[0].equals("remove") || args[0].equals("setGate") || args[0].equals("ticket") || args[0].equals("editBlock") || args[0].equals("editName") || args[0].equals("editLore") || args[0].equals("setOneTimeUse")) {
                    Set<String> gates = config.getGatesSet();
                    return StringUtil.copyPartialMatches(args[1], gates, new ArrayList<>());
                } else return new ArrayList<>();
            case 3:
                if (args[0].equals("add") || args[0].equals("editBlock")) {
                    return StringUtil.copyPartialMatches(args[2], getMaterials(), new ArrayList<>());
                } else if (args[0].equals("setOneTimeUse")) {
                    return StringUtil.copyPartialMatches(args[2], Arrays.asList("true", "false"), new ArrayList<>());
                } else return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Gets a list of all materials
     *
     * @return a list of all materials
     */
    private List<String> getMaterials() {
        List<String> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            materials.add(material.name());
        }
        return materials;
    }

}
