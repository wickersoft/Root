/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Dennis
 */
public abstract class Command {
    
    public abstract boolean onCommand(CommandSender sender, String[] args);
    
    public abstract String getSyntax();
    
    public abstract String getDescription();
    
    public void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + getSyntax() + " " + ChatColor.GRAY + " - " + getDescription() + ".");
    }
    
}
