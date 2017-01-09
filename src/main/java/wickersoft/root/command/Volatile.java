/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wickersoft.root.SpecialItemUtil;

/**
 *
 * @author Dennis
 */
public class Volatile extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack == null || stack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.GRAY + "Hold an item to make it volatile!");
            return true;
        }
        if (SpecialItemUtil.isVolatile(stack)) {
            stack = SpecialItemUtil.setVolatile(stack, false);
            player.sendMessage(ChatColor.GRAY + "Item is no longer volatile!");
        } else {
            stack = SpecialItemUtil.setVolatile(stack, true);
            player.sendMessage(ChatColor.GRAY + "Item is now volatile!");
        }
        player.getInventory().setItemInMainHand(stack);
        player.updateInventory();
        return true;
    }

    @Override
    public String getSyntax() {
        return "/volatile";
    }

    @Override
    public String getDescription() {
        return "Makes an item volatile (impossible to pick up for Players without a special permission)";
    }

}
