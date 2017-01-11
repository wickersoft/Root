/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;
import wickersoft.root.StringUtil;

/**
 *
 * @author Dennis
 */
public class Activity extends Command {

    private static final String[] HEATMAP_SCALE = {
        //ChatColor.BOLD + " " + ChatColor.RESET + " ",
        ChatColor.DARK_GRAY + "\u2591",
        ChatColor.BLACK + "\u2591",
        ChatColor.DARK_RED + "\u2591",
        ChatColor.RED + "\u2591",
        ChatColor.GOLD + "\u2591",
        ChatColor.GOLD + "\u2589",
        ChatColor.YELLOW + "\u2589",
        ChatColor.WHITE + "\u2589",
        ChatColor.AQUA + "\u2589"};
    private static final String[] WEEKDAY = {" Mon", " Tue", " Wed", " Thu", " Fri ", " Sat.", " Sun"};
    private static final String[] LEGEND = {
        ChatColor.BLUE + "Legend:",
        HEATMAP_SCALE[1] + HEATMAP_SCALE[2]
        + HEATMAP_SCALE[3] + HEATMAP_SCALE[4] + HEATMAP_SCALE[5]
        + HEATMAP_SCALE[6] + HEATMAP_SCALE[7] + HEATMAP_SCALE[8],
        "",
        "",
        "",
        "",
        "",
        "",
        ChatColor.YELLOW + "Server Time"};
    private static final String PIPE = ChatColor.DARK_GRAY + "|";
    private static final String AXIS_SPACER = ChatColor.BOLD + "  " + ChatColor.RESET + "    ";
    private static final String BOX_SPACER = ChatColor.DARK_GRAY + "\u2591";
    private static final Calendar CALENDAR = Calendar.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        UserData data = UserDataProvider.getUser(args[0]);

        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Player name not recognized");
            return true;
        }

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

        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        int currentMonth = CALENDAR.get(Calendar.MONTH);
        int currentDayOfWeek = (CALENDAR.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        int currentHour = CALENDAR.get(Calendar.HOUR_OF_DAY);

        sender.sendMessage(StringUtil.generateHLineTitle(data.getName() + " - Activity statistics"));

        for (int wDay = 0; wDay < 7; wDay++) {
            StringBuilder sb = new StringBuilder();
            if (wDay == currentDayOfWeek) {
                sb.append(ChatColor.YELLOW).append(WEEKDAY[wDay]).append(">");
            } else {
                sb.append(ChatColor.GRAY).append(WEEKDAY[wDay]).append(ChatColor.BOLD).append(" ");
            }
            sb.append(PIPE);
            for (int i = 0; i < 24; i++) {
                int boxCharIndex = 8 * weeklyMetrics[24 * wDay + i] / weekMax;
                sb.append(HEATMAP_SCALE[boxCharIndex]);
            }
            sb.append("  ");
            sb.append(LEGEND[wDay]);
            sender.sendMessage(sb.toString());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("    ");

        for (int i = 0; i < currentHour; i++) {
            sb.append(" ").append(ChatColor.BOLD).append(" ").append(ChatColor.RESET);
        }
        sb.append(ChatColor.YELLOW).append("^").append(currentHour).append("h");
        sender.sendMessage(sb.toString());
        return true;
    }

    @Override
    public String getSyntax() {
        return "/activity [player]";
    }

    @Override
    public String getDescription() {
        return "Find out when to expect a player online";
    }

}
