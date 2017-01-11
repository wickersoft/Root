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
            for (wickersoft.root.Mark mark : data.getMarks()) {
                if (mark.getPriority() == 0) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.DARK_AQUA + mark.getAuthor()
                            + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + mark.getMessage());
                } else if (mark.getPriority() > 0) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + mark.getAuthor()
                            + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + mark.getMessage());
                } else if (mark.getPriority() < 0) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + mark.getAuthor()
                            + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + mark.getMessage());
                }
            }
            sender.sendMessage("");
            return true;
        }
        if (!args[1].matches("\\d{1,3}")) {
            String markMessage = StringUtils.join(args, ' ', 1, args.length);
            wickersoft.root.Mark mark = new wickersoft.root.Mark(sender, markMessage);
            data.addMark(mark);
            sender.sendMessage(ChatColor.GRAY + "Mark " + data.getMarks().size() + " created");
            return true;
        }
        int markId = Integer.parseInt(args[1]) - 1;
        if (markId >= data.getMarks().size() || markId < 0) {
            sender.sendMessage(ChatColor.GRAY + "Invalid mark ID");
            return true;
        }
        wickersoft.root.Mark mark = data.getMarks().get(markId);
        boolean changed = false;
        for (int i = 2; i < args.length; i++) {
            switch (args[i]) {
                case "done":
                case "close":
                case "delete":
                case "del":
                case "remove":
                    data.removeMark(markId);
                    sender.sendMessage("Mark " + (markId + 1) + " removed");
                    return true;
                default:
                    if (applyChanges(mark, args[i])) {
                        changed = true;
                    } else {
                        sender.sendMessage("Invalid argument \"" + args[i] + "\"");
                    }
            }
        }
        if (changed) {
            data.touch();
            sender.sendMessage("Mark " + (markId + 1) + " edited");
        }
        return true;
    }

    private boolean applyChanges(wickersoft.root.Mark mark, String argument) {
        String[] parameters = argument.split(":");
        if (parameters.length == 2) {
            switch (parameters[0].toLowerCase()) {
                case "e":
                case "ex":
                case "exp":
                case "expires":
                    if (parameters[1].matches("\\d{1,5}d")) {
                        long expiryTime = Long.parseLong(parameters[1].substring(0, parameters[1].length() - 1))
                                + System.currentTimeMillis();
                        mark.setExpiryTime(expiryTime);
                        return true;
                    } else if (parameters[1].equalsIgnoreCase("never")) {
                        mark.setExpiryTime(Long.MAX_VALUE);
                        return true;
                    }
                    return false;
                case "p":
                case "prio":
                case "priority":
                    switch (parameters[1].toLowerCase()) {
                        case "lo":
                        case "low":
                            mark.setPriority(-1);
                            return true;
                        case "normal":
                        case "med":
                        case "medium":
                            mark.setPriority(0);
                            return true;
                        case "hi":
                        case "high":
                            mark.setPriority(1);
                            return true;
                    }
                    return false;
            }
        }
        return false;
    }

    @Override
    public String getSyntax() {
        return "/mark [player] ([index] [actions])";
    }

    @Override
    public String getDescription() {
        return "Keep notes about a person";
    }

    public void sendExtendedUsage(CommandSender sender) {
        sendUsage(sender);
        sender.sendMessage(ChatColor.BLUE + "/mark player {message} " + ChatColor.GRAY + " - create a mark");
        sender.sendMessage(ChatColor.BLUE + "/mark player [index] [actions] " + ChatColor.GRAY + " - edit marks");
        sender.sendMessage(ChatColor.GRAY + "Supported actions: expires:30d/never, priority:low/normal/high, delete");
    }
}
