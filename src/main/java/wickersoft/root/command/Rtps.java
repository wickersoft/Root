/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.util.HashMap;
import org.bukkit.Bukkit;
import wickersoft.root.LoadAverage;
import wickersoft.root.Root;

/**
 *
 * @author wicden
 */
public class Rtps extends PlayerCommand {

    private static final HashMap<org.bukkit.entity.Player, Integer> RTPS_TASK_IDS = new HashMap<>();

    @Override
    boolean onCommand(org.bukkit.entity.Player player, String[] args) {
        if (RTPS_TASK_IDS.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(RTPS_TASK_IDS.remove(player));
        } else {
            RtpsTask rtpsTask = new RtpsTask(player);
            rtpsTask.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(Root.instance(), rtpsTask, 0, 10));
        }
        return true;
    }

    @Override
    public String getSyntax() {
        return "/rtps";
    }

    @Override
    public String getDescription() {
        return "Continuously shows instantaneous TPS and tick times";
    }

    private class RtpsTask implements Runnable {

        private final org.bukkit.entity.Player player;
        private final String playerName;
        private int taskId = -1;

        public RtpsTask(org.bukkit.entity.Player player) {
            this.player = player;
            this.playerName = player.getName();
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                Bukkit.getScheduler().cancelTask(taskId);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        String.format("/title %s actionbar "
                                + "[\"\",{\"text\":\"TPS:  \",\"color\":\"gray\"},"
                                + "{\"text\":\"%2.2f\",\"bold\":true,\"color\":\"gray\"},"
                                + "{\"text\":\"  -  \",\"color\":\"dark_gray\"},"
                                + "{\"text\":\"ms (Avg):  \",\"color\":\"blue\"},"
                                + "{\"text\":\"%d\",\"bold\":true,\"color\":\"blue\"},"
                                + "{\"text\":\"  -  \",\"color\":\"dark_gray\"},"
                                + "{\"text\":\"ms (Peak):  \",\"color\":\"gray\"},"
                                + "{\"text\":\"%d\",\"bold\":true,\"color\":\"gray\"}]",
                                playerName, LoadAverage.getTps(), LoadAverage.getMsAvg(), LoadAverage.getMsPeak()));
            }
        }

    }

}
