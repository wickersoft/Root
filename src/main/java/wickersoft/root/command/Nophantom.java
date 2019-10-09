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
public class Nophantom extends PlayerCommand {

    @Override
    public boolean onCommand(org.bukkit.entity.Player player, String[] args) {
        UserData data = UserDataProvider.getOrCreateUser(player);
        if (data == null) {
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " What are you??");
            return true;
        }
        data.setNoPhantom(!data.hasNoPhantom());
        player.sendMessage(ChatColor.GRAY + "Anti-Phantom " + ChatColor.BLUE + (data.hasNoPhantom() ? "enabled" : "disabled"));
        return true;
    }

    public String getSyntax() {
        return "/nophantom";
    }

    public String getDescription() {
        return "Prevents Phantoms from spawning within range of the user";
    }
}
