/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class WatcherEnvironment implements Listener {

    private static final WatcherEnvironment INSTANCE = new WatcherEnvironment();

    public static WatcherEnvironment instance() {
        return INSTANCE;
    }

    private WatcherEnvironment() {
    }

    @EventHandler
    public void onPhantomSpawn(CreatureSpawnEvent evt) {
        EntityType type = evt.getEntityType();
        if (evt.getEntityType().toString().equals("PHANTOM")) {
            for(Player player : evt.getLocation().getWorld().getPlayers()) {
                if(UserDataProvider.getOrCreateUser(player).hasNoPhantom() && player.getLocation().distance(evt.getLocation()) < 40) {
                    evt.setCancelled(true);
                }
            }
        }
    }

}
