/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.io.File;
import java.util.List;
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

    private static final ChatColor[] HEATMAP_SCALE = {ChatColor.BLACK, ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.WHITE, ChatColor.AQUA};

    private static final String[] WEEKDAY_AXIS
            = {"Mon ", "  " + ChatColor.BOLD + "  ", "  " + ChatColor.BOLD + "  ",
                "Thu ", "  " + ChatColor.BOLD + "  ", "  " + ChatColor.BOLD + "  ", "Sun "};

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
        if (player != null) {
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

        sender.sendMessage(format("GeoLocation", data.getGeoLocation()));
        sender.sendMessage(format("Frozen", data.isFrozen()));
        sender.sendMessage(format("Shadowmuted", data.isShadowmuted()));
        sender.sendMessage(format("Undercover", data.isUndercover()));

        if (Storage.essentials != null) {
            YamlConfiguration yaml = YamlConfiguration.read(new File(Storage.essentials.getDataFolder(), "userdata/" + data.getUUID() + ".yml"));
            if (player == null) {
                YamlConfiguration logoutLoc = yaml.getOrCreateSection("logoutlocation");
                String logoutLocString = StringUtil.padToWidthMod(logoutLoc.getString("world", "unknown"), 16)
                        + ChatColor.DARK_GRAY + " / "
                        + ChatColor.DARK_AQUA
                        + (int) logoutLoc.getDouble("x", 0) + " "
                        + (int) logoutLoc.getDouble("y", 0) + " "
                        + (int) logoutLoc.getDouble("z", 0);
                sender.sendMessage(format("Logout at", logoutLocString));
            }
            if (ArrayUtils.contains(args, "homes")) {
                YamlConfiguration homes = yaml.getOrCreateSection("homes");
                String[] homeStrings;
                if (homes != null) {
                    homeStrings = new String[homes.size()];
                    int homeIndex = 0;
                    for (String key : homes.keySet()) {
                        YamlConfiguration homeLoc = yaml.getOrCreateSection("logoutlocation");
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
                sender.sendMessage(format("Homes", "" + ((homes != null) ? homes.size() : 0) + ChatColor.GRAY + ChatColor.ITALIC + "  [/p " + data.getName() + " homes]"));
            }
            /*
            if (ArrayUtils.contains(args, "metrics")) {
                int[] weeklyMetrics = data.getWeeklyMetrics();
                int[] yearlyMetrics = data.getYearlyMetrics();
                int weekMax = 1;
                int yearMax = 1;
                for (int i = 0; i < 12; i++) {
                    if (yearlyMetrics[i] > yearMax) {
                        yearMax = yearlyMetrics[i];
                    }
                }
                for (int i = 0; i < 24 * 7; i++) {
                    if (weeklyMetrics[i] > weekMax) {
                        weekMax = weeklyMetrics[i];
                    }
                }
                
                StringBuilder sb = new StringBuilder();

                sb.append(ChatColor.RED);

            } else {
                int[] yearlyMetrics = data.getYearlyMetrics();
                int sum = 0;
                for (int i = 0; i < 12; i++) {
                    sum += yearlyMetrics[i];
                }
                sender.sendMessage(format("Hours played", "" + sum + ChatColor.GRAY + ChatColor.ITALIC + "  [/p " + data.getName() + " metrics]"));
            }
             */
            sender.sendMessage("");
        }

        return true;
    }

    private String[] buildStatChats(int[] weeklyMetrics, int[] yearlyMetrics) {
        String[] lines = new String[9];
        return lines;
    }

    public String getSyntax() {
        return "/player [name]";
    }

    public String getDescription() {
        return "View information about a player";
    }
}
