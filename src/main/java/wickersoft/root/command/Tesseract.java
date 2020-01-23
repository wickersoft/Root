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
 * @author wicden
 */
public class Tesseract extends PlayerCommand {

    @Override
    boolean onCommand(org.bukkit.entity.Player player, String[] args) {
        int numSigns = 1;
        Material mat = Material.AIR;
        int numItems = 0;

        if (args.length >= 1) {
            if (!args[0].matches("\\d+")) {
                sendUsage(player);
                return true;
            }
            numSigns = Integer.parseInt(args[0]);
        }

        if (args.length >= 2) {
            try {
                mat = Material.valueOf(args[1].toUpperCase());
                if(!mat.isItem()) {
                    player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Material " + args[1].toUpperCase() + " is not an item");
                    return true;
                }
                numItems = 64;
            } catch (IllegalArgumentException ex) {
                player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Unknown Material " + args[1].toUpperCase());
                return true;
            }
        }

        if (args.length == 3) {
            if (!args[2].matches("\\d+")) {
                sendUsage(player);
                return true;
            }
            numItems = Integer.parseInt(args[2]);
        }

        String[] signText;

        if (numItems > 0) {
            int stackSize = mat.getMaxStackSize();
            String amountString = stackSize > 1 ? ("" + (numItems / stackSize) + "x" + stackSize + "+" + (numItems % stackSize)) : "" + numItems;
            signText = new String[]{ChatColor.DARK_BLUE + "[Tesseract]", mat.toString(), amountString, ""};
        } else {
            signText = new String[]{ChatColor.DARK_BLUE + "[Tesseract]", "EMPTY", "0", ""};
        }

        ItemStack instantSigns = SpecialItemUtil.generateInstantSign(numSigns, signText);
        player.getWorld().dropItem(player.getLocation(), instantSigns).setPickupDelay(0);
        player.sendMessage(ChatColor.GRAY + "Generated " + ChatColor.BLUE + numSigns + ChatColor.GRAY + " Tesseracts");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/tesseract (#signs) (item) (#items)";
    }

    @Override
    public String getDescription() {
        return "Generates Tesseract Instant Signs (with contents)";
    }
}
