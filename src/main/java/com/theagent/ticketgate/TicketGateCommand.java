package com.theagent.ticketgate;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

class TicketGateCommand implements CommandExecutor {

    private final ConfigManager config;

    TicketGateCommand(ConfigManager configManager) {
        config = configManager;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        // check if the sender is a player and has permission
        if (sender instanceof Player player && sender.hasPermission("ticketgate.use")) {
            // special case since the lore can contain spaces
            if (args.length > 3 && args[0].equals("editLore")) {
                StringBuilder lore = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    lore.append(args[i]);
                    if (i != args.length - 1) {
                        lore.append(" ");
                    }
                }
                editLore(player, args[1], lore.toString());
                return true;
            }

            switch (args.length) {
                case 0:
                    // TODO add message for help or information on the plugin
                    PlayerMessenger.sendMessage(player, "Information page coming soon!");
                    break;
                case 1:
                    switch (args[0]) {
                        case "getMaster":
                            giveMaster(player);
                            break;
                        case "regenMaster":
                            regenMaster(player);
                            break;
                        case "reload":
                            config.reloadConfig();
                            PlayerMessenger.sendMessage(player, "Configuration reloaded!");
                            break;
                        default:
                            // error
                            PlayerMessenger.sendCommandError(player);
                            break;
                    }
                    break;
                case 2:
                    switch (args[0]) {
                        case "setGate":
                            setGate(player, args[1]);
                            break;
                        case "remove":
                            removeGate(player, args[1]);
                            break;
                        case "ticket":
                            giveTicket(player, args[1]);
                            break;
                        default:
                            // error
                            PlayerMessenger.sendCommandError(player);
                            break;
                    }
                    break;
                case 3:
                    switch (args[0]) {
                        case "add":
                            addGate(player, args[1], args[2]);
                            break;
                        case "editBlock":
                            editBlock(player, args[1], args[2]);
                            break;
                        case "editName":
                            editName(player, args[1], args[2]);
                            break;
                        case "editLore":
                            editLore(player, args[1], args[2]);
                            break;
                        case "setOneTimeUse":
                            setOneTimeUse(player, args[1], Boolean.parseBoolean(args[2]));
                            break;
                        default:
                            // error
                            PlayerMessenger.sendCommandError(player);
                            break;
                    }
                    break;
                default:
                    // error
                    PlayerMessenger.sendCommandError(player);
                    break;
            }
        } else {
            sender.sendMessage(PlayerMessenger.PREFIX + ChatColor.RED + "Commands can only be used by a player!");
        }

        return false;
    }

    private void addGate(Player player, String name, String block) {
        if (!config.containsKey("gates." + name)) {
            config.setProperties(
                    new GateProperty(name, "gate", "ACACIA_FENCE_GATE"),
                    new GateProperty(name, "block", block),
                    new GateProperty(name, "id", generateID()),
                    new GateProperty(name, "name", name),
                    new GateProperty(name, "lore", ""),
                    new GateProperty(name, "one-item-use", false)
            );
            PlayerMessenger.sendMessage(player, "Gate added!");
        } else {
            PlayerMessenger.sendError(player, "Gate already exists!");
        }
    }

    private void removeGate(Player player, String name) {
        if (config.containsKey("gates." + name)) {
            if (!name.equals("default")) {
                config.deleteGateConfig(name);
                PlayerMessenger.sendMessage(player, "Gate removed!");
            } else {
                PlayerMessenger.sendError(player, "You can't remove the default gate!");
            }
        } else {
            PlayerMessenger.sendError(player, "Gate could not be removed!");
        }
    }

    private void giveTicket(Player player, String name) {
        if (config.containsKey("gates." + name)) {
            if (!name.equals("default")) {
                List<String> lore = new ArrayList<>(); // the item's lore will be saved in this list

                String itemLore = config.getString("gates." + name + ".lore");
                lore.add((itemLore == null) ? "" : itemLore); // add the lore to the list

                String ticketId = config.getString("gates." + name + ".id");
                if (ticketId == null) {
                    PlayerMessenger.sendError(player, "Ticket ID missing in config!");
                    return;
                }
                lore.add(ticketId); // add the id to the list

                ItemStack ticket = new ItemStack(Material.PAPER); // create a new item (paper)
                ItemMeta ticketMeta = ticket.getItemMeta(); // get the item's meta
                if (ticketMeta == null) {
                    PlayerMessenger.sendError(player, "Something went wrong...");
                    return;
                }
                String itemName = config.getString("gates." + name + ".name");
                itemName = (itemName == null) ? "Ticket" : itemName;
                ticketMeta.setItemName(itemName); // set the item's name
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

    /**
     * Gives the player the master key
     *
     * @param player the player to give the master key to
     */
    private void giveMaster(Player player) {
        // create a new item (paper)
        ItemStack masterTicket = new ItemStack(Material.PAPER);
        ItemMeta masterTicketMeta = masterTicket.getItemMeta();

        if (masterTicketMeta == null) {
            PlayerMessenger.sendError(player, "Something went wrong...");
            return;
        }

        // set the item's lore
        List<String> lore = new ArrayList<>();
        lore.add("Master Key:");
        String masterKey = config.getString("master-key");
        if (masterKey == null) {
            PlayerMessenger.sendError(player, "Master Key not defined in config!");
            return;
        }
        lore.add(masterKey);

        // add meta to the item
        masterTicketMeta.setItemName("§5§nMaster Key");
        masterTicketMeta.setLore(lore);
        masterTicket.setItemMeta(masterTicketMeta);
        player.getInventory().addItem(masterTicket);

        PlayerMessenger.sendMessage(player, "§5Master key given!");
    }

    /**
     * Regenerates the master key
     *
     * @param player the player who executed the command
     */
    private void regenMaster(Player player) {
        config.setMasterKey(generateID()); // generate a new key
        PlayerMessenger.sendMessage(player, "§5Master key regenerated!");
    }

    private String generateID() {
        Random randomizer = new Random();
        int id = randomizer.nextInt(999999 - 100000) + 100000;
        return Integer.toString(id);
    }

    private List<String> getGates() {
        return new ArrayList<>(List.of(config.getGates()));
    }

    private List<String> getGateBlocks() {
        List<String> blocks = new ArrayList<>();
        for (String gate : config.getGates()) {
            blocks.add(config.getString("gates." + gate + ".block"));
        }
        return blocks;
    }

    private void setGate(Player player, String name) {
        Block gate = player.getTargetBlockExact(3);
        if (gate != null && gate.getType().name().equals(config.get("gates." + name + ".gate"))) {
            Block floor = Objects.requireNonNull(player.getTargetBlockExact(3)).getLocation().subtract(0, 1, 0).getBlock();
            Material newFloor = Material.getMaterial(Objects.requireNonNull(config.getString("gates." + name + ".block")));
            if (newFloor == null) {
                PlayerMessenger.sendError(player, "Something went wrong...");
                return;
            }
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
            config.setProperty(name, "block", block);
            PlayerMessenger.sendMessage(player, "Block updated successfully!");
        } else {
            PlayerMessenger.sendError(player, "Block already used for another gate!");
        }
    }

    private void editName(Player player, String name, String newName) {
        config.setProperty(name, "name", newName);
        PlayerMessenger.sendMessage(player, "Name updated successfully!");
    }

    private void editLore(Player player, String name, String newLore) {
        config.setProperty(name, "lore", newLore);
        PlayerMessenger.sendMessage(player, "Lore updated successfully!");
    }

    private void setOneTimeUse(Player player, String name, boolean consume) {
        if (name.equals("default")) {
            PlayerMessenger.sendError(player, "You can't change the default gate!");
            return;
        }
        if (getGates().contains(name)) {
            config.setProperty(name, "one-time-use", consume);
            PlayerMessenger.sendMessage(player, (consume ? "Ticket will now be consumed after usage!" : "Ticket will no longer be consumed!"));
        } else {
            PlayerMessenger.sendError(player, "Gate not found!");
        }

    }

}
