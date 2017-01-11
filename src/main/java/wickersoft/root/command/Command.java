/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.util.List;
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
    
    protected void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + getSyntax() + " " + ChatColor.GRAY + " - " + getDescription() + ".");
    }
    
    protected String format(String key, String value) {
        return ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + key + ": " + ChatColor.DARK_AQUA + value;
    }

    protected String format(String key, boolean value) {
        return ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + key + ": "
                + (value ? ChatColor.RED + "Yes" : ChatColor.DARK_AQUA + "No");
    }

    protected String format(String key, int value) {
        return ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + key + ": " + ChatColor.DARK_AQUA + value;
    }

    protected String[] format(String key, List<String> values) {
        return format(key, values.toArray());
    }

    protected String[] format(String key, Object[] values) {
        String[] formats = new String[Math.max(values.length + 1, 2)];
        formats[0] = ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + key + ": ";
        if (values.length == 0) {
            formats[1] = ChatColor.DARK_GRAY + "   - " + ChatColor.GRAY + "(none)";
        }
        for (int i = 0; i < values.length; i++) {
            formats[i + 1] = ChatColor.DARK_GRAY + "   - " + ChatColor.DARK_AQUA + values[i];
        }
        return formats;
    }
    
}
