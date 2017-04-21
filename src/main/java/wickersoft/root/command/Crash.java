/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;
import wickersoft.root.Root;
import wickersoft.root.TaskCrash;

/**
 *
 * @author Dennis
 */
public class Crash extends Command {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        org.bukkit.entity.Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Player name not recognized");
            return true;
        }

        int intensity = 1;

        if (args.length >= 2 && args[1].matches("[0-9]{1,2}")) {
            intensity = Integer.parseInt(args[1]);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Root.instance(), new TaskCrash(player, intensity), 1, 1);
        sender.sendMessage(ChatColor.GRAY + "Crashing player " + ChatColor.BLUE + player.getName() 
                + ChatColor.GRAY + " with intensity " + ChatColor.BLUE + intensity + ChatColor.GRAY + "..");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/crash [player] (intensity)";
    }

    @Override
    public String getDescription() {
        return "Makes a player's game grind to a halt by spawning countless fake entities";
    }

}
