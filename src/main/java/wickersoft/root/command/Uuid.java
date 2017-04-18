/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;
import wickersoft.root.HTTP;
import wickersoft.root.HTTP.HTTPResponse;
import wickersoft.root.StringUtil;

/**
 *
 * @author Dennis
 */
public class Uuid extends Command {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d YYYY");

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String uuid = "";

        UserData data = UserDataProvider.getUser(args[0]);

        if (data != null) {
            uuid = data.getUUID().toString().replace("-", "");
        } else if (args[0].length() == 36) {
            try {
                UUID u = UUID.fromString(args[0]);
                uuid = args[0];
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Player name or UUID not recognized");
                return true;
            }
            uuid = args[0].replace("-", "");
        } else if (args[0].matches("[0-9a-zA-Z_]{2,16}")) {
            try {
                HTTPResponse http = HTTP.http("https://api.mojang.com/profiles/minecraft",
                        "[\"" + args[0] + "\"]", "application/json", 30000);

                String responseString = new String(http.content);
                if (responseString.contains("\"id\":")) {

                    uuid = StringUtil.extract(responseString, "\"id\":\"", "\"");
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Player name or UUID not recognized");
                    return true;
                }
            } catch (IOException ex) {
                sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Mojang API could not be reached");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Player name or UUID not recognized");
            return true;
        }

        String[] entries = null;

        try {
            HTTPResponse http = HTTP.http("https://api.mojang.com/user/profiles/" + uuid + "/names");
            String responseString = new String(http.content);
            entries = StringUtil.extractAll(responseString, "{", "}");
        } catch (IOException ex) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Mojang API could not be reached");
            return true;
        }

        if (entries == null || entries.length == 0) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Player name or UUID not recognized");
            return true;
        }

        uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20)
                + "-" + uuid.substring(20);
        String recentName = StringUtil.extract(entries[entries.length - 1], "\"name\":\"", "\"");
        sender.sendMessage(StringUtil.generateHLineTitle(recentName + " - UUID and name history "));
        sender.sendMessage("");
        
        if (sender instanceof org.bukkit.entity.Player) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "tellraw " + ((org.bukkit.entity.Player) sender).getName()
                    + " [\"\",{\"text\":\" - \",\"color\":\"dark_gray\"},"
                    + "{\"text\":\"UUID: \",\"color\":\"gray\"},"
                    + "{\"text\":\""
                    + uuid
                    + "\",\"color\":\"dark_aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\""
                    + uuid
                    + "\"}, \"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"<Copy to chat>\"}]}}}]");
        } else {
            sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "UUID: " + ChatColor.AQUA + uuid);
        }

        sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Names: ");

        for (String entry : entries) {

            String name = StringUtil.extract(entry, "\"name\":\"", "\"");
            
            
            if (!entry.contains("changedToAt")) {
                sender.sendMessage(ChatColor.DARK_GRAY + "   - " + ChatColor.DARK_AQUA + name);
            } else {
                String changedTo = entry.substring(entry.lastIndexOf(":") + 1);
                long changedToAt = Long.parseLong(changedTo);
                sender.sendMessage(ChatColor.DARK_GRAY + "   - " + ChatColor.DARK_AQUA + name + ChatColor.GRAY + " on "
                        + ChatColor.DARK_AQUA + DATE_FORMAT.format(Date.from(Instant.ofEpochMilli(changedToAt))));
            }

        }
        
        sender.sendMessage("");

        // Resolve UUID to name[]
        return true;
    }

    @Override
    public String getSyntax() {
        return "/uuid [player/uuid]";
    }

    @Override
    public String getDescription() {
        return "Shows a player's UUID, current and past names";
    }

}
