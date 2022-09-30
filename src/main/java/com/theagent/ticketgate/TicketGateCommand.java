package com.theagent.ticketgate;

import org.bukkit.ChatColor;
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
                    player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Information page coming soon!");
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
                        if (addGate(args)) {
                            player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Gate added!");
                        } else {
                            player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Gate could not be added!");
                        }
                    }
                    break;
                default:
                    player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Your command seems to be wrong :/ | Try /ticketgate help");
                    break;
            }
        } else {
            sender.sendMessage("[TicketGate] Commands can only be used by a player!");
        }
        return false;
    }

    private boolean addGate(String[] args) {
        if (!config.contains("gates." + args[1])) {
            config.set("gates." + args[1] + ".gate", "ACACIA_FENCE_GATE");
            config.set("gates." + args[1] + ".block", args[2]);
            config.set("gates." + args[1] + ".id", generateID());
            main.saveConfig();
            return true;
        } else {
            return false;
        }
    }

    private void removeGate(Player player, String name) {
        if (config.contains("gates." + name)) {
            if (!name.equals("default")) {
                config.set("gates." + name, null);
                main.saveConfig();
                player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Gate removed!");
            } else {
                player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + " ] You can't remove the default gate!");
            }
        } else {
            player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Gate could not be removed!");
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

                player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Ticket given!");
            } else {
                player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] You don't need a ticket for the default gate!");
            }
        } else {
            player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Ticket could not be given!");
        }
    }

    private String generateID() {
        Random randomizer = new Random();
        int id = randomizer.nextInt(999999 - 100000) + 100000;
        return Integer.toString(id);
    }

    private void setGate(Player player, String name) {
        Block gate = player.getTargetBlockExact(3);
        if (gate != null && gate.getType().name().equals(config.get("gates." + name + ".gate"))) {
            Block floor = player.getTargetBlockExact(3).getLocation().subtract(0, 1, 0).getBlock();
            Material newFloor = Material.getMaterial(config.getString("gates." + name + ".block"));
            floor.setType(newFloor);
            player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Gate updated successfully!");
        } else {
            player.sendMessage("[" + ChatColor.GOLD + "TicketGate" + ChatColor.RESET + "] Gate not found!");
        }
    }

}
