/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;
import wickersoft.root.StringUtil;

/**
 *
 * @author Dennis
 */
public class Lore extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "add " + ChatColor.BLUE + "[text | text | ... ]");
            player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "set " + ChatColor.BLUE + "[line] [text]");
            player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "del " + ChatColor.BLUE + "[line / all]");
            player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "insert " + ChatColor.BLUE + "[line] [text | text | ... ]");
            player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "copy");
            player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "paste");
            return true;
        }

        ItemStack is = player.getInventory().getItemInMainHand();
        if (is == null) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey! " + ChatColor.GRAY + "Hold the item you want to edit!");
            return true;
        }

        ItemMeta meta = is.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "How to even deal with this");
            return true;
        }

        List<String> lore;
        if (meta.hasLore()) {
            lore = meta.getLore();
        } else {
            lore = new ArrayList<>();
        }
        boolean success;
        switch (args[0]) {
            case "add":
                success = addLore(lore, args, player);
                break;
            case "set":
                success = setLore(lore, args, player);
                break;
            case "del":
                success = delLore(lore, args, player);
                break;
            case "insert":
                success = insertLore(lore, args, player);
                break;
            case "copy":
                success = copyLore(lore, player);
                break;
            case "paste":
                success = pasteLore(lore, player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown: " + ChatColor.GRAY + " Use /lore for help");
                return true;
        }
        if (success) {
            meta.setLore(lore);
            is.setItemMeta(meta);
            player.updateInventory();
            player.sendMessage(ChatColor.GRAY + "Lore changed");
        }
        return true;
    }

    private boolean addLore(List<String> lore, String[] args, Player player) {
        if (args.length == 1) {
            player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "add " + ChatColor.BLUE + "[text | text | ... ]"
                    + ChatColor.GRAY + " - Removes one or all lines of lore text.");
            return false;
        }
        String[] loreLines = StringUtil.joinAndFormat(args, 1).split("\\|");
        for (String line : loreLines) {
            lore.add(line);
        }
        return true;
    }

    private boolean setLore(List<String> lore, String[] args, Player player) {
        if (args.length > 2) {
            try {
                int lineNumber = Integer.parseInt(args[1]);
                if (lineNumber > 0 && lineNumber <= lore.size()) {
                    lore.set(lineNumber - 1, StringUtil.joinAndFormat(args, 2));
                    return true;
                }
            } catch (NumberFormatException ex) {
            }
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Invalid line number");
            return false;
        }
        player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "set " + ChatColor.BLUE + "[line] [text]"
                + ChatColor.GRAY + " - Sets a single line of the item's lore to the given text.");
        return false;
    }

    private boolean delLore(List<String> lore, String[] args, Player player) {
        if (args.length == 2) {
            if (args[1].equals("all")) {
                lore.clear();
                return true;
            } else {
                try {
                    int lineNumber = Integer.parseInt(args[1]);
                    if (lineNumber > 0 && lineNumber <= lore.size()) {
                        lore.remove(lineNumber - 1);
                        return true;
                    }
                } catch (NumberFormatException ex) {
                }
                player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Invalid line number");
                return false;
            }
        }
        player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "del " + ChatColor.BLUE + "[line / all]"
                + ChatColor.GRAY + " - Removes one or all lines of lore text.");
        return false;
    }

    private boolean insertLore(List<String> lore, String[] args, Player player) {
        if (args.length > 2) {
            try {
                int lineNumber = Integer.parseInt(args[1]);
                if (lineNumber > 0 && lineNumber <= lore.size()) {
                    String[] loreLines = StringUtil.joinAndFormat(args, 2).split("\\|");
                    LinkedList<String> newLore = new LinkedList<>();
                    for (int i = 0; i < lineNumber; i++) {
                        newLore.add(lore.get(i));
                    }
                    for (int i = 0; i < loreLines.length; i++) {
                        newLore.add(loreLines[i]);
                    }
                    for (int i = lineNumber; i < lore.size(); i++) {
                        newLore.add(lore.get(i));
                    }
                    lore.clear();
                    lore.addAll(newLore);
                    return true;
                }
            } catch (NumberFormatException ex) {
            }
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Invalid line number");
            return false;
        }
        player.sendMessage(ChatColor.BLUE + "/lore " + ChatColor.DARK_AQUA + "insert " + ChatColor.BLUE + "[line] [text | text | ...]"
                + ChatColor.GRAY + " - Inserts one or more lines of lore starting at the given line.");
        return false;
    }

    private boolean copyLore(List<String> lore, Player player) {
        if (lore.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "There is no lore on this item");
            return false;
        }
        UserData data = UserDataProvider.getOrCreateUser(player);
        data.setClipboard(lore);
        player.sendMessage(ChatColor.GRAY + "Lore copied");
        return false;
    }

    private boolean pasteLore(List<String> lore, Player player) {
        UserData data = UserDataProvider.getOrCreateUser(player);
        if (data.getClipboard() != null) {
            if (data.getClipboard() instanceof List) {
                List list = (List) data.getClipboard();
                if (!list.isEmpty()) {
                    if (list.get(0) instanceof String) {
                        list = (List<String>) list;
                        lore.clear();
                        lore.addAll(list);
                        return true;
                    }
                    player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Your clipboard does not contain lore");
                    return false;
                }
            }
        }
        player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Your clipboard is empty");
        return false;
    }
    
    public String getSyntax() {
        return "/lore";
    }
    
    public String getDescription() {
        return "Edits an item's lore";
    }
}
