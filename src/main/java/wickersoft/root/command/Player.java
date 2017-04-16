/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;
import wickersoft.root.Storage;
import wickersoft.root.StringUtil;
import wickersoft.root.YamlConfiguration;

/**
 *
 * @author Dennis
 */
public class Player extends Command {

    private static final SimpleDateFormat LOCAL_TIME_FORMAT = new SimpleDateFormat("MMM dd, HH:mm");

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + getSyntax() + ChatColor.GRAY + " - " + getDescription());
            return true;
        }
        UserData data = UserDataProvider.getUser(args[0]);
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Player name not recognized");
            return true;
        }
        org.bukkit.entity.Player player = data.getPlayerInstance();
        
        if (player != null && player.isOnline()) {
            sender.sendMessage(StringUtil.generateHLineTitle(player.getName()
                    + (player.isOp() ? ChatColor.RED + " (Op)" : ""))
            );
            String ip = player.getAddress().getAddress().getHostAddress();
            sender.sendMessage(format("IP Address", ip));
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(data.getUUID());
            sender.sendMessage(StringUtil.generateHLineTitle(data.getName() + ChatColor.GRAY + " (Offline)"
                    + (offlinePlayer.isOp() ? ChatColor.RED + " (Op)" : "")));
        }

        if (sender.hasPermission("root.player.preciselocation")) {
            sender.sendMessage(format("GeoLocation", data.getPreciseGeoLocation()));
        } else {
            sender.sendMessage(format("GeoLocation", data.getGeoLocation()));
        }

        if (!data.getTimezone().equals("Unknown")) {
            TimeZone tz = TimeZone.getTimeZone(data.getTimezone());
            LOCAL_TIME_FORMAT.setTimeZone(tz);
            String localTime = LOCAL_TIME_FORMAT.format(Date.from(Instant.ofEpochMilli(System.currentTimeMillis())));
            sender.sendMessage(format("Local Time", localTime));
        }
        
        sender.sendMessage(format("Frozen", data.isFrozen()));
        sender.sendMessage(format("Shadowmuted", data.isShadowmuted()));
        sender.sendMessage(format("Undercover", data.isUndercover()));
        sender.sendMessage(format("Marks", "" + data.getMarks().size()));

        if (Storage.essentials != null) {
            YamlConfiguration yaml = YamlConfiguration.read(new File(Storage.essentials.getDataFolder(), "userdata/" + data.getUUID() + ".yml"));
            if (player == null || !player.isOnline()) {
                YamlConfiguration logoutLoc = yaml.getOrCreateSection("logoutlocation");
                String logoutLocString = StringUtil.padToWidthMod(logoutLoc.getString("world", "unknown"), 16)
                        + ChatColor.DARK_GRAY + " / "
                        + ChatColor.DARK_AQUA
                        + (int) logoutLoc.getDouble("x", 0) + " "
                        + (int) logoutLoc.getDouble("y", 0) + " "
                        + (int) logoutLoc.getDouble("z", 0);
                sender.sendMessage(format("Logout at", logoutLocString));
            }

            if (sender.hasPermission("root.player.homes")) {
                if (ArrayUtils.contains(args, "homes")) {
                    YamlConfiguration homes = yaml.getOrCreateSection("homes");
                    String[] homeStrings;
                    if (homes != null) {
                        homeStrings = new String[homes.size()];
                        int homeIndex = 0;
                        for (String key : homes.keySet()) {
                            YamlConfiguration homeLoc = homes.getOrCreateSection(key);
                            homeStrings[homeIndex++] = StringUtil.padToWidthMod(key, 16) + ChatColor.DARK_GRAY + " at "
                                    + ChatColor.DARK_AQUA + StringUtil.padToWidthMod(homeLoc.getString("world", "unknown"), 16)
                                    + ChatColor.DARK_GRAY + " / "
                                    + ChatColor.DARK_AQUA
                                    + (int) homeLoc.getDouble("x", 0) + " "
                                    + (int) homeLoc.getDouble("y", 0) + " "
                                    + (int) homeLoc.getDouble("z", 0);
                        }
                    } else {
                        homeStrings = new String[0];
                    }
                    sender.sendMessage(format("Homes", homeStrings));
                } else {
                    YamlConfiguration homes = yaml.getOrCreateSection("homes");
                    sender.sendMessage(format("Homes", "" + ((homes != null) ? homes.size() : 0)));
                }
            }

            int[] yearlyMetrics = data.getYearlyMetrics();
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += yearlyMetrics[i];
            }
            sum /= 12;
            sender.sendMessage(format("Hours played", "" + sum));
            sender.sendMessage("");
        }

        return true;
    }

    public String getSyntax() {
        return "/player [name]";
    }

    public String getDescription() {
        return "View information about a player";
    }
}
