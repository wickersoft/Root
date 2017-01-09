/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class Freeze extends Command {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        UserData data = UserDataProvider.getUser(args[0]);
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Player name not recognized");
            return true;
        }
        data.setFrozen(!data.isFrozen());
        if (data.getPlayerInstance() == null) {
            data.saveData();
        }
        sender.sendMessage(ChatColor.GRAY + "Player " + data.getName() + " is now " + ChatColor.BLUE + (data.isFrozen() ? "" : "un") + "frozen");
        return true;
    }

    public String getSyntax() {
        return "/freeze [player]";
    }
    
    public String getDescription() {
        return "Makes a player unable to move or break blocks";
    }
    
}
