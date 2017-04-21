/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class TaskCrash implements Runnable {
    
    private final Player target;
    private final int entitiesPerTick;
    private final Random rnd = new Random();
    private int id = -1;
    private int entityId = 1000000000;
    
    public TaskCrash(Player target, int entitiesPerTick) {
        this.target = target;
        this.entitiesPerTick = entitiesPerTick;
    }
    
    public void run() {
        if(!target.isOnline()) {
            Bukkit.getScheduler().cancelTask(id);
        }
        Location loc = target.getLocation();
        int x = loc.getBlockX() + rnd.nextInt(100) - 50;
        int y = loc.getBlockY() + rnd.nextInt(100) - 50;
        int z = loc.getBlockZ() + rnd.nextInt(100) - 50;
        for(int i = 0; i < entitiesPerTick; i++) {
            Util.showBlock(x, y, z, entityId++, target);
        }
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
}
