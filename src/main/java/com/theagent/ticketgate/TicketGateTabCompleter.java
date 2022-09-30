package com.theagent.ticketgate;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;

import java.util.*;

public class TicketGateTabCompleter implements TabCompleter {

    FileConfiguration config;

    public TicketGateTabCompleter(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "remove", "setGate", "ticket"), new ArrayList<>());
            case 2:
                if (args[0].equals("remove") || args[0].equals("setGate") || args[0].equals("ticket")) {
                    Set<String> gates = config.getConfigurationSection("gates").getKeys(false);
                    return StringUtil.copyPartialMatches(args[1], gates, new ArrayList<>());
                } else return new ArrayList<>();
            case 3:
                if (args[0].equals("add")) {
                    List<String> materials = new ArrayList();
                    for (Material material : Material.values()) {
                        materials.add(material.name());
                    }
                    return StringUtil.copyPartialMatches(args[2], materials, new ArrayList<>());
                } else return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }

}
