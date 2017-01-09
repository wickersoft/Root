/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wickersoft.root.SpecialItemUtil;

/**
 *
 * @author Dennis
 */
public class InstantSign extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        boolean amountSpecified = false;
        int amount = 1;
        if (args.length == 0 || ((amountSpecified = args[args.length - 1].matches("\\d{1,8}")) && args.length == 1)) {
            sendUsage(player);
            return true;
        }

        int argJoinEnd = args.length - 1;
        if (amountSpecified) {
            argJoinEnd = args.length - 2;
            amount = Integer.parseInt(args[args.length - 1]);
            if (amount > 64) {
                amount = 64;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < argJoinEnd; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        sb.append(args[argJoinEnd]);
        String[] lines = sb.toString().split("\\|");
        ItemStack instantSigns = SpecialItemUtil.generateInstantSign(amount, lines);
        player.getWorld().dropItem(player.getLocation(), instantSigns).setPickupDelay(0);
        player.sendMessage(ChatColor.GRAY + "Generated " + ChatColor.BLUE + amount + ChatColor.GRAY + " instant signs");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/instantsign line1|line2|line3|line4 (amount)";
    }

    @Override
    public String getDescription() {
        return "Generates a sign that can be placed without editing";
    }

}
