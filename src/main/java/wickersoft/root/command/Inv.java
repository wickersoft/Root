/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.io.File;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import syn.root.user.InventoryProvider;
import syn.root.user.UserDataProvider;
import wickersoft.root.Root;
import wickersoft.root.Storage;
import wickersoft.root.StringUtil;

/**
 *
 * @author Dennis
 */
public class Inv extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (args.length == 0) {
            loadInventory(player);
            return true;
        }

        switch (args[0]) {
            case "help":
                player.sendMessage(ChatColor.BLUE + "/inv");
                player.sendMessage(ChatColor.BLUE + "/inv (load) [name]" + (player.hasPermission("root.inventory.other") ? " ([player])" : ""));
                player.sendMessage(ChatColor.BLUE + "/inv " + ChatColor.DARK_AQUA + "save " + ChatColor.BLUE + "[name]");
                player.sendMessage(ChatColor.BLUE + "/inv " + ChatColor.DARK_AQUA + "del " + ChatColor.BLUE + "[name]");
                player.sendMessage(ChatColor.BLUE + "/inv " + ChatColor.DARK_AQUA + "list" + ChatColor.BLUE
                        + (player.hasPermission("root.inventory.other") ? " ([player])" : ""));
                return true;
            case "list":
                UUID uuid = null;
                int listPage = 1;

                if (args.length >= 2) {
                    if (args[1].matches("\\d{1,3}")) {
                        listPage = Integer.parseInt(args[1]);
                        uuid = player.getUniqueId();
                    } else if (player.hasPermission("root.inventory.other")) {
                        uuid = UserDataProvider.getUUID(args[1]);
                        if (uuid == null) {
                            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Player name not recognized");
                            return true;
                        }
                        if (args.length >= 3 && args[2].matches("\\d{1,3}")) {
                            listPage = Integer.parseInt(args[2]);
                        } else {
                            listPage = 1;
                        }
                    } else {
                        uuid = player.getUniqueId();
                        listPage = 1;
                    }
                } else {
                    listPage = 1;
                    uuid = player.getUniqueId();
                }

                player.sendMessage(StringUtil.generateHLineTitle("Saved Inventories"));
                player.sendMessage("");
                String[] inventories = InventoryProvider.listInventories(uuid);
                if (inventories == null || inventories.length == 0) {
                    player.sendMessage(ChatColor.GRAY + " (none)");
                    player.sendMessage("");
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(" ");
                    int numPages = Math.max((inventories.length + 19) / 20, 1);
                    if (listPage > numPages) {
                        listPage = numPages;
                    }
                    int i = 20 * (listPage - 1);
                    for (; i < 20 * listPage - 1 && i < inventories.length - 1; i++) {
                        sb.append(ChatColor.DARK_AQUA).append(inventories[i]).append(ChatColor.DARK_GRAY).append(", ");
                    }
                    sb.append(ChatColor.DARK_AQUA).append(inventories[i]);
                    player.sendMessage(sb.toString());
                    player.sendMessage("");
                    player.sendMessage(StringUtil.generateHLineTitle("Page " + listPage + " of " + numPages));
                }
                //player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH
                //        + "                                                                                ");
                break;
            case "load":
                File inventoryFile;
                if (args.length >= 3 && player.hasPermission("root.inventory.other")) {
                    uuid = UserDataProvider.getUUID(args[2]);
                    if (uuid == null) {
                        player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Player name not recognized");
                        return true;
                    }
                    loadInventory(player, args[1], uuid);
                } else if (args.length == 1) {
                    loadInventory(player);
                } else {
                    loadInventory(player, args[1]);
                }
                break;
            case "save":
                if (args.length >= 3 && player.hasPermission("root.inventory.other")) {
                    uuid = UserDataProvider.getUUID(args[2]);
                    if (uuid == null) {
                        player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Player name not recognized");
                        return true;
                    }
                    saveInventory(player, args[1], uuid);
                } else {
                    saveInventory(player, args[1]);
                }
                break;
            case "del":
                uuid = player.getUniqueId();
                inventoryFile = new File(Root.instance().getDataFolder(), "users/" + uuid + "/inventories/" + args[1] + ".yml");
                if (!inventoryFile.exists()) {
                    player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Inventory does not exist");
                    return true;
                }
                inventoryFile.delete();
                player.sendMessage(ChatColor.GRAY + "Inventory " + ChatColor.BLUE + args[1] + ChatColor.GRAY + " deleted!");
                break;
            default:
                inventoryFile = InventoryProvider.getInventoryFile(player, args[0]);
                if (!inventoryFile.exists()) {
                    player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Inventory does not exist");
                    return true;
                }
                InventoryProvider.loadInventory(player, inventoryFile);
                player.sendMessage(ChatColor.GRAY + "Inventory " + ChatColor.BLUE + args[0] + ChatColor.GRAY + " loaded!");
                return true;
        }

        return true;
    }

    public String getSyntax() {
        return "/inv";
    }

    public String getDescription() {
        return "Saves and loads inventories";
    }

    private void loadInventory(Player player) {
        loadInventory(player, ".last");
    }

    private void loadInventory(Player player, String invName) {
        loadInventory(player, invName, player.getUniqueId());
    }

    private void loadInventory(Player player, String invName, UUID uuid) {
        File inventoryFile = InventoryProvider.getInventoryFile(uuid, invName);
        if (!inventoryFile.exists()) {
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Inventory does not exist");
            return;
        }
        InventoryProvider.loadInventory(player, inventoryFile);
        player.sendMessage(ChatColor.GRAY + "Inventory " + ChatColor.BLUE + invName + ChatColor.GRAY + " loaded!");
    }

    private void saveInventory(Player player, String invName) {
        saveInventory(player, invName, player.getUniqueId());
    }

    private void saveInventory(Player player, String invName, UUID uuid) {
        if (!invName.matches("^[0-9A-Za-z-_]+$")) {
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Invalid inventory name.");
            return;
        }
        File inventoryFile = InventoryProvider.getInventoryFile(uuid, invName);
        if (inventoryFile.exists() && !Storage.INV_SAVE_AUTO_OVERWRITE) {
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Inventory already exists! Delete it first!");
            return;
        }
        InventoryProvider.saveInventory(player, inventoryFile);
        player.sendMessage(ChatColor.GRAY
                + "Inventory " + ChatColor.BLUE + invName + ChatColor.GRAY + " saved!");
    }

}
