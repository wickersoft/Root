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
 * @author Dennis
 */
public class Mark extends Command {

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
        boolean highPrio = args[1].startsWith("@");
        if(highPrio) {
            args[1] = args[1].substring(1);   
        }
        String markMessage = StringUtils.join(args, ' ', 1, args.length);
        syn.root.user.Mark mark = new syn.root.user.Mark(sender, markMessage);
        data.addMark(mark);
        sender.sendMessage(ChatColor.GRAY + "Mark " + data.getMarks().size() + " created");
        if(highPrio) {
            mark.setPriority(1);
        }
        return true;
    }

    @Override
    public String getSyntax() {
        return "/mark [player] {message}/{@message}";
    }

    @Override
    public String getDescription() {
        return "Keep notes about a person";
    }

    public void sendExtendedUsage(CommandSender sender) {
        sendUsage(sender);
        sender.sendMessage(ChatColor.BLUE + "/mark player {message}/{@message} " + ChatColor.GRAY + " - create a mark");
    }
}
