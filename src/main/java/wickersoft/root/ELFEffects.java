/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.Calendar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class ELFEffects implements Runnable {

    private static final ELFEffects INSTANCE = new ELFEffects();
    private final Calendar calendar = Calendar.getInstance();

    public static ELFEffects instance() {
        return INSTANCE;
    }

    private ELFEffects() {
    }

    public void run() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int fixedDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        int month = calendar.get(Calendar.MONTH);
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            UserDataProvider.getOrCreateUser(player).incrementActivityStatistic(hour, fixedDay, month);
        }
        Tesseract.garbageCollect();
    }
}
