/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wickersoft.root.SpecialItemUtil;

/**
 *
 * @author Dennis
 */
public class Wand extends PlayerCommand {

    @Override
    boolean onCommand(org.bukkit.entity.Player player, String[] args) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        if (stack == null || stack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.GRAY + "Hold an item to create a wand!");
            return true;
        }
        if (!wickersoft.root.Wand.isWandKnown(args[0])) {
            player.sendMessage(ChatColor.GRAY + "Wand name not recognized!");
            return true;
        }
        if (!player.hasPermission("root.wand." + args[0])) {
            player.sendMessage(ChatColor.GRAY + "You do not have permission to create this wand!");
            return true;
        }
        
        String loreName = wickersoft.root.Wand.getLoreString(args[0]);
        
        if (SpecialItemUtil.hasLoreFlag(stack, loreName)) {
            stack = SpecialItemUtil.setLoreFlag(stack, loreName, false);
            player.sendMessage(ChatColor.GRAY + "Wand removed!");
        } else {
            stack = SpecialItemUtil.setLoreFlag(stack, loreName, true);
            player.sendMessage(ChatColor.GRAY + "Wand added!");
        }
        player.getInventory().setItemInMainHand(stack);
        player.updateInventory();
        return true;
    }

    @Override
    public String getSyntax() {
        return "/wand [wand]";
    }

    @Override
    public String getDescription() {
        return "Adds special right click actions to the item held";
    }
}
