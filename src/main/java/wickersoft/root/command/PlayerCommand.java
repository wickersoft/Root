/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public abstract class PlayerCommand extends Command {

    @Override
    public final boolean onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Hey! " + ChatColor.GRAY + "You must be ingame to use this!");
            return true;
        } else {
            return onCommand((Player) sender, args);
        }
    }

    abstract boolean onCommand(Player player, String[] args);

}
