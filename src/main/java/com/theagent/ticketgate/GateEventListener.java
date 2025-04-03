package com.theagent.ticketgate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

class GateEventListener implements Listener {

    private final ConfigManager config; // so the config file can be used here

    // sounds
    private final String illegalSound;
    private final String successSound;
    private final String invalidSound;

    /**
     * Handles all interactions with ticket gates
     *
     * @param configManager Plugin class
     */
    GateEventListener(ConfigManager configManager) {
        config = configManager;
        // set sounds
        illegalSound = config.getString("illegal-sound");
        successSound = config.getString("success-sound");
        invalidSound = config.getString("invalid-sound");
    }

    /**
     * Catching the player's interaction with a ticket gate
     *
     * @param e PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer(); // save the player

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            // if it is a right click on a block:
            Block clickedBlock = e.getClickedBlock();
            // if no block was clicked
            if (clickedBlock == null) return;
            // get item in player's hand
            ItemStack item = p.getInventory().getItemInMainHand();
            String itemKey = itemLore(item);

            // if correct gate type
            if (clickedBlock.getType().equals(Material.ACACIA_FENCE_GATE)) {
                Gate gate = (Gate) clickedBlock.getBlockData();

                // if gate is already open
                if (gate.isOpen()) return;

                // allow opening the gate if it is a default gate
                if (getGateType(clickedBlock).equals("default")) {
                    playSound(p, successSound);
                    return;
                }

                // player is sneaking -> illegal bypass
                if (p.isSneaking() && config.getBoolean("allow-illegal-bypass")) {
                    // if the player is sneaking
                    illegalMessage(p, clickedBlock);
                    playSound(p, illegalSound);
                    /*
                     * Temporary solution: Just open the gate
                     * Correct way would be the player teleporting behind the gate.
                     */
                    return;
                }

                // if the player has a correct ticket in their hand
                if (item.getType().equals(Material.PAPER) && itemKey != null && checkTicket(p, item, clickedBlock)) {
                    playSound(p, successSound);
                    return;
                }

                // if nothing from above applies, cancel the event
                e.setCancelled(true);
                // notify the player
                p.sendTitle("§4§lWrong ticket!", "§4You need a ticket to enter!", 10, 70, 20);
                playSound(p, invalidSound);
            }
        }
    }

    /**
     * Returns the name of the specific block / gate
     *
     * @param block block
     * @return block / gate name
     */
    private String getGateType(Block block) {
        Material floor = block.getLocation().subtract(0, 1, 0).getBlock().getType(); // get block below gate
        String[] gates = config.getGates(); // get all possible gate configurations
        String gate = "default";

        // search if the block fits a gate configuration
        for (String gateConfig : gates) {
            if (floor.equals(Material.getMaterial(Objects.requireNonNull(config.getString("gates." + gateConfig + ".block"))))) {
                gate = gateConfig;
                break;
            }
        }

        return gate;
    }

    /**
     * Checks if the used ticket is valid for the specific gate
     *
     * @param player Player
     * @param item   Ticket
     * @param block  Gate
     * @return correct ticket
     */
    private boolean checkTicket(Player player, ItemStack item, Block block) {
        String key = itemLore(item); // get the key of the ticket

        // check if the gate configuration fits the gate
        if (key == null) {
            PlayerMessenger.sendError(player, "Something went wrong...");
            return false;
        }

        // check if the master key is used
        if (key.equals(config.getString("master-key"))) {
            PlayerMessenger.sendMessage(player, "§5Master key valid! Opening gate...");
            return true;
        }

        String gate = getGateType(block); // get the gate type

        // the default gate does not need a ticket
        if (gate.equals("default")) {
            return true;
        }

        if (key.equals(config.getString("gates." + gate + ".id"))) {
            if (config.getBoolean("gates." + gate + ".one-time-use")) {
                item.setAmount(item.getAmount() - 1);
                player.getInventory().setItemInMainHand(item);

            }
            return true;
        } else return false;
    }

    /**
     * Get the lore of an item
     *
     * @param item item in hand
     * @return the lore of the item
     */
    private String itemLore(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return null;
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore == null) return null;
                if (lore.size() >= 2) {
                    return lore.get(1);
                } else return null;
            } else return null;
        } else return null;
    }

    /**
     * Broadcast a message to all players on the server
     * when a player opens a gate illegally
     *
     * @param player Player who opened the gate illegally
     * @param block  Block that was opened
     */
    private void illegalMessage(Player player, Block block) {
        Bukkit.broadcastMessage(
                String.format("§5* %s opened a ticket gate illegally at %d, %d, %d *",
                        player.getDisplayName(),
                        block.getX(),
                        block.getY(),
                        block.getZ()
                )
        );
    }

    /**
     * Play a sound as ticket gate feedback
     *
     * @param player Player who triggered the event
     * @param sound  Sound to play
     */
    private void playSound(Player player, String sound) {
        if (!sound.isEmpty()) {
            Objects.requireNonNull(
                    Bukkit.getWorld(player.getWorld().getUID())
            ).playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

}
