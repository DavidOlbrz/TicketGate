package com.theagent.ticketgate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class GateEventListener implements Listener {

    private TicketGate main; // so the config file can be used here
    private FileConfiguration config; // so the config file can be used here

    public GateEventListener (TicketGate main) {
        this.main = main;
        config = main.getConfig();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer(); // save the player

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            // if it is a right click on a block:
            Block clickedBlock = e.getClickedBlock();
            Material clickedBlockType = clickedBlock.getType();
            ItemStack item = p.getInventory().getItemInMainHand();
            String itemKey = itemLore(item);

            if (Objects.requireNonNull(clickedBlock.getType().equals(Material.ACACIA_FENCE_GATE))) {
                // if it is the correct gate type:
                if (p.isSneaking() && main.getConfig().getBoolean("allow-illegal-bypass")) {
                    // if the player is sneaking
                    illegalMessage(p, clickedBlock);
                    /*
                     * Temporary solution: Just open the gate
                     * Correct way would be the player teleporting behind the gate.
                     */
                } else {
                    if (item.getType().equals(Material.PAPER) && itemKey != null && checkTicket(p, item, clickedBlock)) {
                        // if the player has a ticket:
                        p.sendMessage(ChatColor.GREEN + "Success! (ItemKey:" + itemKey + ")"); // TODO Message yes or no? --> NO, REMOVE IT AFTER TESTING!
                        // TODO different ticket types (defined in item lore, specified in config file)
                    } else if (getGateType(clickedBlock).equals("default")) {
                        // allow opening the gate if it is a default gate
                    } else {
                        // if not cancel the event
                        e.setCancelled(true);
                        p.sendTitle("§4§lWrong ticket!", "§4You need a ticket to enter!", 10, 70, 20); // TODO Test title
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

    private String getGateType(Block block) {
        Material floor = block.getLocation().subtract(0, 1, 0).getBlock().getType(); // get block below gate
        String[] gates = config.getConfigurationSection("gates").getKeys(false).toArray(new String[0]); // get all possible gate configurations
        String gate = "default";

        // search if the block fits a gate configuration
        for (String gateConfig : gates) {
            if (floor.equals(Material.getMaterial(config.getString("gates." + gateConfig + ".block")))) {
                gate = gateConfig;
                break;
            }
        }

        return gate;
    }

    private boolean checkTicket(Player player, ItemStack item, Block block) {
        String key = itemLore(item); // get the key of the ticket

        String gate = getGateType(block); // get the gate type

        // the default gate does not need a ticket
        if (gate.equals("default")) {
            return true;
        }

        // check if the gate configuration fits the gate
        if (key.equals(config.getString("gates." + gate + ".id"))) {
            return true;
        } else {
            return false;
        }
    }

    private String itemLore(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore.size() >= 2) {
                    return lore.get(1);
                } else return null;
            } else return null;
        } else return null;
    }

    private void illegalMessage(Player player, Block block) {
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "* " + player.getDisplayName() + " opened a ticket gate illegally at " + block.getX() + ", " + block.getY() + ", " + block.getZ() + " *");
    }

}
