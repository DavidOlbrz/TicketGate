package com.theagent.ticketgate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * A class for sending (error) messages to players
 */
class PlayerMessenger {

    // default prefix
    static final String PREFIX = "[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] ";

    private static final String COMMAND_ERROR = "Your command seems to be wrong :/ | Try /ticketgate help";

    /**
     * Sends a message to the player
     *
     * @param player  the player to send the message to
     * @param message the message to send
     */
    static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }

    /**
     * Sends an error message to the player
     *
     * @param player  the player to send the message to
     * @param message the error message to send
     */
    static void sendError(Player player, String message) {
        player.sendMessage(PREFIX + ChatColor.RED + message);
    }

    /**
     * Sends an error message to the player
     *
     * @param player the player to send the message to
     */
    static void sendCommandError(Player player) {
        player.sendMessage(PREFIX + ChatColor.RED + COMMAND_ERROR);
    }

}
