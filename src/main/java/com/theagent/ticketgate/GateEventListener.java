package com.theagent.ticketgate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class GateEventListener implements Listener {

    private TicketGate main; // so the config file can be used here

    public GateEventListener (TicketGate main) {
        this.main = main;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer(); // save the player

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            // if it is a right click on a block:
            if (e.getClickedBlock().getType().equals(Material.ACACIA_FENCE_GATE)) {
                // if it is the correct gate type:
                if (p.isSneaking() && main.getConfig().getBoolean("allow-illegal-bypass")) {
                    // if the player is sneaking
                    Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "* " + p.getDisplayName() + " jumps over a ticket gate illegally *");
                    p.sendMessage(ChatColor.RED + "[TicketGate] Feature not implemented yet!"); // TODO implementation of illegal use
                } else {
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
                        // if the player has a ticket:
                        p.sendMessage(ChatColor.GREEN + "Du darfst das Tor jetzt nutzen"); // TODO Message yes or no?
                        // TODO different ticket types (defined in item lore, specified in config file)
                    } else {
                        // if not cancel the event
                        e.setCancelled(true);
                        p.sendMessage(ChatColor.RED + "Du hast kein Ticket (Papier)!"); // TODO Message yes or no?
                    }
                }
            }
        }
    }

    /**
     * Warning for players joining the server while this experimental plugin is installed
     * @param e PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) { // TODO remove when finished
        Player p = e.getPlayer();
        p.sendMessage(ChatColor.RED + "[TicketGate] Warning: This Plugin is in early development so don't expect everything to work flawlessly!");
    }

}
