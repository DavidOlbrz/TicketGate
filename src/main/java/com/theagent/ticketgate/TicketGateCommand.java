package com.theagent.ticketgate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicketGateCommand implements CommandExecutor {

    private TicketGate main;
    private FileConfiguration config;

    public TicketGateCommand(TicketGate main) {
        this.main = main;
        config = main.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // check if the sender is a player and has permission
        if (sender instanceof Player && sender.hasPermission("ticketgate.use")) {
            // cast sender to player
            Player player = (Player) sender;

            switch (args.length) {
                case 0:
                    // TODO add message for help or information on the plugin
                    PlayerMessenger.sendMessage(player, "Information page coming soon!");
                    break;
                case 2:
                    if (args[0].equals("setGate")) {
                        setGate(player, args[1]);
                    } else if (args[0].equals("remove")) {
                        removeGate(player, args[1]);
                    } else if (args[0].equals("ticket")) {
                        giveTicket(player, args[1]);
                    }
                    break;
                case 3:
                    if (args[0].equals("add")) {
                        addGate(player, args[1], args[2]);
                    } else if (args[0].equals("editBlock")) {
                        editBlock(player, args[1], args[2]);
                    } else if (args[0].equals("editName")) {
                        editName(player, args[1], args[2]);
                    } else if (args[0].equals("editLore")) {
                        editLore(player, args[1], args[2]);
                    }
                    break;
                default:
                    PlayerMessenger.sendError(player, "Your command seems to be wrong :/ | Try /ticketgate help");
                    break;
            }
        } else {
            sender.sendMessage("[TicketGate] Commands can only be used by a player!");
        }
        return false;
    }

    private void addGate(Player player, String name, String block) {
        if (!config.contains("gates." + name)) {
            config.set("gates." + name + ".gate", "ACACIA_FENCE_GATE");
            config.set("gates." + name + ".block", block);
            config.set("gates." + name + ".id", generateID());
            main.saveConfig();
            PlayerMessenger.sendMessage(player, "Gate added!");
        } else {
            PlayerMessenger.sendError(player, "Gate already exists!");
        }
    }

    private void removeGate(Player player, String name) {
        if (config.contains("gates." + name)) {
            if (!name.equals("default")) {
                config.set("gates." + name, null);
                main.saveConfig();
                PlayerMessenger.sendMessage(player, "Gate removed!");
            } else {
                PlayerMessenger.sendError(player, "You can't remove the default gate!");
            }
        } else {
            PlayerMessenger.sendError(player, "Gate could not be removed!");
        }
    }

    private void giveTicket(Player player, String name) {
        if (config.contains("gates." + name)) {
            if (!name.equals("default")) {
                List<String> lore = new ArrayList<>(); // the item's lore will be saved in this list
                lore.add(config.getString("gates." + name + ".lore")); // add the lore to the list
                lore.add(config.getString("gates." + name + ".id")); // add the id to the list

                ItemStack ticket = new ItemStack(Material.PAPER); // create a new item (paper)
                ItemMeta ticketMeta = ticket.getItemMeta(); // get the item's meta
                ticketMeta.setDisplayName(config.getString("gates." + name + ".name")); // set the item's name
                ticketMeta.setLore(lore); // set the item's lore

                ticket.setItemMeta(ticketMeta); // add updated meta back to the item
                player.getInventory().addItem(ticket); // give the player the ticket

                PlayerMessenger.sendMessage(player, "Ticket given!");
            } else {
                PlayerMessenger.sendError(player, "You don't need a ticket for the default gate!");
            }
        } else {
            PlayerMessenger.sendError(player, "Ticket could not be given!");
        }
    }

    private String generateID() {
        Random randomizer = new Random();
        int id = randomizer.nextInt(999999 - 100000) + 100000;
        return Integer.toString(id);
    }

    private List<String> getGateBlocks() {
        List<String> blocks = new ArrayList<>();
        for (String gate : config.getConfigurationSection("gates").getKeys(false)) {
            blocks.add(config.getString("gates." + gate + ".block"));
        }
        return blocks;
    }

    private void setGate(Player player, String name) {
        Block gate = player.getTargetBlockExact(3);
        if (gate != null && gate.getType().name().equals(config.get("gates." + name + ".gate"))) {
            Block floor = player.getTargetBlockExact(3).getLocation().subtract(0, 1, 0).getBlock();
            Material newFloor = Material.getMaterial(config.getString("gates." + name + ".block"));
            floor.setType(newFloor);
            PlayerMessenger.sendMessage(player, "Gate updated successfully!");
        } else {
            PlayerMessenger.sendError(player, "Gate not found!");
        }
    }

    private void editBlock(Player player, String name, String block) {
        // stops the method if the block is not a valid block
        if (Material.getMaterial(block) == null) {
            PlayerMessenger.sendError(player, "Block not found!");
            return;
        }

        List<String> blocks = getGateBlocks(); // get all the blocks that are currently used for gates

        // only change the block if the block is not already used for another gate
        if (!blocks.contains(block)) {
            config.set("gates." + name + ".block", block);
            main.saveConfig();
            PlayerMessenger.sendMessage(player, "Block updated successfully!");
        } else {
            PlayerMessenger.sendError(player, "Block already used for another gate!");
        }
    }

    private void editName(Player player, String name, String newName) {
        config.set("gates." + name + ".name", newName);
        main.saveConfig();
        PlayerMessenger.sendMessage(player, "Name updated successfully!");
    }

    private void editLore(Player player, String name, String newLore) {
        config.set("gates." + name + ".lore", newLore);
        main.saveConfig();
        PlayerMessenger.sendMessage(player, "Lore updated successfully!");
    }

}
