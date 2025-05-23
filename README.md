# TicketGate

## About

Turns your fence gates into ticket gates

## Demonstration

![Ticket Gate Success](https://github.com/DavidOlbrz/TicketGate/blob/master/ticket_gate_success.gif)

![Ticket Gate Error](https://github.com/DavidOlbrz/TicketGate/blob/master/ticket_gate_error.gif)

*(Ticket Gate Model not included)*

## Prerequisites

- Java 17
- SpigotMC / PaperMC Server
- Minecraft Version 1.21.4

## Commands

Command alias: `/tg`

### Add a new gate configuration

`ticketgate add <name> <block>`

### Remove gate configuration

`ticketgate remove <name>`

### Get ticket/key for a specific gate

`ticketgate ticket <name>`

### Assign a configuration to a gate

`ticketgate setGate <name>`

### Edit the block for a gate configuration

`ticketgate editBlock <name> <block>`

### Edit the name of the key for a gate configuration

`ticketgate editName <name> <displayname>`

### Edit the lore of the key for a gate configuration

`ticketgate editLore <name> <lore>`

### (De-)Activate One-Time-Use of a key / ticket (per gate)

`ticketgate setOneTimeUse <name> <true|false>`

### Reload config

`ticketgate reload`

# Configuration

## Custom sounds

Set custom sounds that are played when interacting with ticket gates

### Default

```yml
# Opened illegaly
illegal-sound: "minecraft:item.trident.thunder"
# Opened with the correct ticket
success-sound: "minecraft:entity.experience_orb.pickup"
# Invalid or no ticket
invalid-sound: "minecraft:block.redstone_torch.burnout"
```

> [!Tip]
> To play no sound, leave the setting empty
>
> <ins>Example:</ins>
> ```yml
> invalid-sound: ""
> ```
