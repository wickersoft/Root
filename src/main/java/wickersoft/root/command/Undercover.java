/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class Undercover extends PlayerCommand {

    @Override
    public boolean onCommand(Player sender, String[] args) {
        UserData data = UserDataProvider.getOrCreateUser(sender);
        data.setUndercover(!data.isUndercover());
        if (data.isUndercover()) {
            sender.sendMessage(ChatColor.GRAY + "Your rank is now " + ChatColor.GREEN + "hidden " + ChatColor.GRAY + "in chat");
        } else {
            sender.sendMessage(ChatColor.GRAY + "Your rank is now " + ChatColor.RED + "visible " + ChatColor.GRAY + "in chat");
        }
        return true;
    }

    public String getSyntax() {
        return "/undercover";
    }

    public String getDescription() {
        return "Hides your rank in chat messages";
    }
}
