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
public class Ding extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        UserData data = UserDataProvider.getOrCreateUser(player);

        if (args.length == 1 && args[0].equals("off")) {
            data.setDingPattern("");
            player.sendMessage(ChatColor.GRAY + "Ding alerts " + ChatColor.RED + "disabled");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length - 1; i++) {
            sb.append(args[i]).append(" ");
        }
        sb.append(args[args.length - 1]);
        String dingPattern = sb.toString();
        if (!data.setDingPattern(dingPattern)) {
            player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Error: " + ChatColor.GRAY + "Invalid regular expression");
        } else {
            player.sendMessage(ChatColor.GRAY + "Ding alerts " + ChatColor.GREEN + "enabled");
        }
        return true;
    }

    @Override
    public String getSyntax() {
        return "/ding [pattern / off]";
    }

    @Override
    public String getDescription() {
        return "Plays a \"Ding\" sound when a chat message matches the specified pattern";
    }

}
