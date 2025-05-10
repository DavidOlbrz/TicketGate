package com.theagent.ticketgate;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
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
            // if it is a right-click on a block:
            Block clickedBlock = e.getClickedBlock();
            // if no block was clicked
            if (clickedBlock == null) return;
            // get item in the player's hand
            ItemStack item = p.getInventory().getItemInMainHand();
            String itemKey = itemLore(item);

            // if correct gate type
            if (Tag.FENCE_GATES.isTagged(clickedBlock.getType())) {
                Gate gate = (Gate) clickedBlock.getBlockData();

                // if the gate is already open
                if (gate.isOpen()) return;

                String gateType = getGateType(clickedBlock);
                // do nothing if it is not a configured gate
                if (gateType == null) return;

                // allow opening the gate if it is a default gate
                if (gateType.equals("default")) {
                    playSound(clickedBlock, successSound);
                    return;
                }

                // player is sneaking -> illegal bypass
                if (p.isSneaking() && config.getBoolean("allow-illegal-bypass")) {
                    // if the player is sneaking
                    illegalMessage(p, clickedBlock);
                    playSound(clickedBlock, illegalSound);
                    /*
                     * Temporary solution: Just open the gate
                     * Correct way would be the player teleporting behind the gate.
                     */
                    return;
                }

                // if the player has a correct ticket in their hand
                if (item.getType().equals(Material.PAPER) && itemKey != null && checkTicket(p, item, clickedBlock)) {
                    playSound(clickedBlock, successSound);
                    return;
                }

                // if nothing from the above applies, cancel the event
                e.setCancelled(true);
                // notify the player
                p.sendTitle("§4§lWrong ticket!", "§4You need a ticket to enter!", 10, 70, 20);
                playSound(clickedBlock, invalidSound);
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
        Material gateMaterial = block.getLocation().getBlock().getType();
        Material floorMaterial = block.getLocation().subtract(0, 1, 0).getBlock().getType();

        String[] gates = config.getGates();

        String gateType = null;

        for (String gate : gates) {
            Material gateConfig = Material.getMaterial(config.getString("gates." + gate + ".gate"));
            Material floorConfig = Material.getMaterial(config.getString("gates." + gate + ".block"));
            if (gateConfig == null || floorConfig == null) {
                throw new RuntimeException("There seems to be an error in your config.yml!");
            }
            if (gateConfig.equals(gateMaterial) && floorConfig.equals(floorMaterial)) {
                gateType = gate;
                break;
            }
        }

        return gateType;
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
                        ChatColor.stripColor(player.getDisplayName()),
                        block.getX(),
                        block.getY(),
                        block.getZ()
                )
        );
    }

    /**
     * Play a sound as ticket gate feedback
     *
     * @param gate  Gate that was interacted with
     * @param sound Sound to play
     */
    private void playSound(Block gate, String sound) {
        Location gateLocation = gate.getLocation();

        Collection<Entity> entities = Objects.requireNonNull(Bukkit.getWorld(gate.getWorld().getUID()))
                .getNearbyEntities(gateLocation, 10, 10, 10);

        for (Entity entity : entities) {
            if (entity instanceof Player) {
                ((Player) entity).playSound(gateLocation, sound, SoundCategory.BLOCKS, 0.625f, 1.0f, 0);
            }
        }
    }

}
