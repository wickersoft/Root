/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Dennis
 */
public class Nv extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(ChatColor.GRAY + "Night vision " + ChatColor.RED + "disabled");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true));
            player.sendMessage(ChatColor.GRAY + "Night vision " + ChatColor.BLUE + "enabled");
        }
        return true;
    }

     public String getSyntax() {
        return "/nv";
    }
    
    public String getDescription() {
        return "Toggles night vision";
    }
    
}
