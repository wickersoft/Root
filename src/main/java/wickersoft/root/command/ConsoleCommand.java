/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author Dennis
 */
public abstract class ConsoleCommand extends Command {
    
    @Override
    public final boolean onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Hey! " + ChatColor.GRAY + "You must be in the console to use this!");
            return true;
        } else {
            return onCommand((ConsoleCommandSender) sender, args);
        }
    }
    
    abstract boolean onCommand(ConsoleCommandSender sender, String[] args);
    
    
}
