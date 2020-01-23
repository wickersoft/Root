/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;
import wickersoft.root.StringUtil;

/**
 *
 * @author wicden
 */
public class Delmark extends Command {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendExtendedUsage(sender);
            return true;
        }
        UserData data = UserDataProvider.getUser(args[0]);
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Player name not recognized");
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(StringUtil.generateHLineTitle(data.getName() + " - Marks"));
            if (data.getMarks().isEmpty()) {
                sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "(none)");
                return true;
            }
            for (int markId = 0; markId < data.getMarks().size(); markId++) {
                syn.root.user.Mark mark = data.getMarks().get(markId);
                if (mark.getPriority() == 0) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " [" + ChatColor.DARK_AQUA + (markId + 1) + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + mark.getAuthor()
                            + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + mark.getMessage());
                } else if (mark.getPriority() > 0) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " [" + ChatColor.RED + (markId + 1) + ChatColor.DARK_GRAY + "] " + ChatColor.RED + mark.getAuthor()
                            + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + mark.getMessage());
                } else if (mark.getPriority() < 0) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + (markId + 1) + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + mark.getAuthor()
                            + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + mark.getMessage());
                }
            }
            sender.sendMessage("");
            return true;
        }
        if (!args[1].matches("\\d{1,3}")) {
            sendExtendedUsage(sender);
            return true;
        }
        int markId = Integer.parseInt(args[1]) - 1;
        if (markId >= data.getMarks().size() || markId < 0) {
            sender.sendMessage(ChatColor.GRAY + "Invalid mark ID");
            return true;
        }
        data.removeMark(markId);
        sender.sendMessage(ChatColor.GRAY + "Mark " + (markId + 1) + " removed");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/delmark [player] [id]";
    }

    @Override
    public String getDescription() {
        return "Remove a mark from a Player";
    }

    public void sendExtendedUsage(CommandSender sender) {
        sendUsage(sender);
        sender.sendMessage(ChatColor.BLUE + "/delmark [player] [id]" + ChatColor.GRAY + " - remvove a mark");
    }
}
