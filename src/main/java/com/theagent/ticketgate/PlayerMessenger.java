package com.theagent.ticketgate;

import org.bukkit.entity.Player;

/**
 * A class for sending (error) messages to players
 */
public class PlayerMessenger {

    private static final String PREFIX = "[§6TicketGate§r] "; // default prefix

    /**
     * Sends a message to the player
     *
     * @param player  the player to send the message to
     * @param message the message to send
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }

    /**
     * Sends an error message to the player
     *
     * @param player  the player to send the message to
     * @param message the error message to send
     */
    public static void sendError(Player player, String message) {
        player.sendMessage(PREFIX + "§c" + message);
    }

}
