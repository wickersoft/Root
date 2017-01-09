/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import wickersoft.root.SpecialItemUtil;

/**
 *
 * @author Dennis
 */
public class Kleinbottle extends PlayerCommand {

    @Override
    boolean onCommand(org.bukkit.entity.Player player, String[] args) {
        int amount = 1;
        if (args.length == 1 && args[0].matches("\\d{1,8}")) {
            amount = Integer.parseInt(args[0]);
        }
        ItemStack kleinBottles = SpecialItemUtil.generateKleinBottle(amount);
        player.getWorld()
                .dropItem(player.getLocation(), kleinBottles).setPickupDelay(0);
        player.sendMessage(ChatColor.GRAY
                + "Generated " + ChatColor.BLUE + amount + ChatColor.GRAY + " Klein Bottles");

        return true;
    }

    @Override
    public String getSyntax() {
        return "/kleinbottle (amount)";
    }

    @Override
    public String getDescription() {
        return "Generates an item that can instantly transfer all of a Tesseract's contents";
    }

}
