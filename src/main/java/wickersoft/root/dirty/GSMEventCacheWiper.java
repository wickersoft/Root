/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.dirty;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import wickersoft.root.Root;

/**
 *
 * @author wicden
 */
public class GSMEventCacheWiper implements Runnable {

    private static final GSMEventCacheWiper INSTANCE = new GSMEventCacheWiper();
    private static Object GSM;
    private static Collection eventCollection;
    private static boolean reflectionSuccessful;
    private int taskId;

    private GSMEventCacheWiper() {
    }

    public static GSMEventCacheWiper instance() {
        if(reflectionSuccessful && eventCollection != null) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Root.instance(), INSTANCE, 20, 20);
        }
        return INSTANCE;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        if (!reflectionSuccessful || eventCollection == null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        int eventSize = eventCollection.size();
        eventCollection.clear();
        //System.out.println("GSM Wiper: Deleted " + eventSize + " events");
    }

    static {
        try {
            Class GSMClass = Class.forName("com.guillaumevdn.gslotmachine.GSlotMachine");
            GSM = GSMClass.getDeclaredField("instance").get(null);
            Field eventField = Class.forName("com.guillaumevdn.gslotmachine.GSlotMachine").getDeclaredField("events");
            eventField.setAccessible(true);
            eventCollection = (Collection) eventField.get(GSM);
            reflectionSuccessful = true;
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println("GSM Wiper failed to initialize. This is fine. Disable gsm-wiper in Root config");
            reflectionSuccessful = false;
        }
    }

}
