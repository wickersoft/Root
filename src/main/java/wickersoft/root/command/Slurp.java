/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import wickersoft.root.Storage;

/**
 *
 * @author Dennis
 */
public class Slurp extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        int range = getRange(player, args);
        int itemCount = 0;
        int xpCount = 0;
        int itemEntityCount = 0;
        int xpEntityCount = 0;
        for (Entity ent : player.getNearbyEntities(range, range, range)) {
            if (ent instanceof Item) {
                itemEntityCount++;
                itemCount += ((Item) ent).getItemStack().getAmount();
            } else if (ent instanceof ExperienceOrb) {
                xpEntityCount++;
                xpCount += ((ExperienceOrb) ent).getExperience();
            } else {
                continue;
            }
            ent.teleport(player);
        }
        player.sendMessage(ChatColor.GRAY + "Slurped "
                + ChatColor.BLUE + itemCount + ChatColor.GRAY + " items / "
                + ChatColor.BLUE + itemEntityCount + ChatColor.GRAY + " stacks, "
                + ChatColor.BLUE + xpCount + ChatColor.GRAY + " xp / "
                + ChatColor.BLUE + xpEntityCount + ChatColor.GRAY + " orbs in a radius of "
                + ChatColor.BLUE + range + ChatColor.GRAY + ".");
        return true;
    }

    private int getRange(Player player, String[] args) {
        int range;
        if (args.length >= 1) {
            try {
                range = Integer.parseInt(args[0]);
                if (range > 0 && range < Storage.MAX_SLURP_RANGE) {
                    return range;
                }
            } catch (NumberFormatException ex) {
            }
        }
        return Storage.DEFAULT_SLURP_RANGE;
    }
    
     public String getSyntax() {
        return "/slurp ([radius])";
    }
    
    public String getDescription() {
        return "Picks up all items in the given radius";
    }

}
