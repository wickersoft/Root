/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wickersoft.root.StringUtil;

/**
 *
 * @author Dennis
 */
public class Name extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
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
        if (args.length == 0) {
            meta.setDisplayName(null);
            player.sendMessage(ChatColor.GRAY + "Name removed");
        } else {
            String newName = StringUtil.joinAndFormat(args, 0);
            meta.setDisplayName(newName);
            player.sendMessage(ChatColor.GRAY + "Name changed");
        }
        is.setItemMeta(meta);
        player.updateInventory();
        return true;
    }

    public String getSyntax() {
        return "/name";
    }

    public String getDescription() {
        return "Edits an item's name";
    }

}
