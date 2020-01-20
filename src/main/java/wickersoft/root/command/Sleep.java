/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.ServerStatisticManager;
import net.minecraft.server.v1_14_R1.StatisticList;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class Sleep extends Command {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                resetSleepTimer((Player) sender);
                sender.sendMessage(ChatColor.GRAY + "Reset sleep timer for " + ChatColor.BLUE + ((Player) sender).getName());
                return true;
            } else {
                sendUsage(sender);
                return false;
            }
        } else if (args[0].equals("all") && sender.hasPermission("root.sleep.all")) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                resetSleepTimer(player);
            }
            sender.sendMessage(ChatColor.GRAY + "Reset sleep timer for " + ChatColor.BLUE + "everybody");
            return true;
        } else if (sender.hasPermission("root.sleep.all")) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.GRAY + "Could not find player " + ChatColor.RED + args[0]);
                return false;
            }
            resetSleepTimer(player);
            sender.sendMessage(ChatColor.GRAY + "Reset sleep timer for " + ChatColor.BLUE + player.getName());
            return true;
        }
        return false;
    }

    @Override
    public String getSyntax() {
        return "/sleep (player)";
    }

    @Override
    public String getDescription() {
        return "Resets a player's sleep timer";
    }

    private void resetSleepTimer(Player player) {
        EntityHuman human = ((CraftPlayer) player).getHandle();
        ServerStatisticManager serverstatisticmanager = ((EntityPlayer) human).getStatisticManager();
        serverstatisticmanager.setStatistic(human, StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_REST), 0);
    }

}
