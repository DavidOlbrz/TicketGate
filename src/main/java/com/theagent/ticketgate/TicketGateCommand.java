package com.theagent.ticketgate;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TicketGateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // check if sender is player and has permission
        if (sender instanceof Player && sender.hasPermission("ticketgate.use")) {
            // cast sender as player
            Player p = (Player) sender;
            if (args.length == 0) {
                // if only base command
                p.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Nice! The plugin seems to work");
                // TODO better message like information about the plugin and it's usage
            } else if (args.length == 2) {
                if (args[0].equals("test")) {
                    p.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Test hat funktioniert! Nachricht: " + args[1]);
                }
            } else {
                p.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Your command seems to be wrong :/ | Try /ticketgate help");
            }
        }
        return false;
    }

}
