/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author Dennis
 */
public class WatcherWand implements Listener {
    
    private static WatcherWand INSTANCE = new WatcherWand();

    private WatcherWand() {
    }

    public static WatcherWand instance() {
        return INSTANCE;
    }

    @EventHandler
    public void onClickBlock(BlockBreakEvent evt) {
        if (evt.isCancelled()) {
            return;
        }
        Wand.apply(evt.getPlayer(), (wand) -> {
            wand.click(evt);
        });
    }

    @EventHandler
    public void onClickBlock(PlayerInteractEvent evt) {
        if (evt.isCancelled() || evt.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Wand.apply(evt.getPlayer(), (wand) -> {
            wand.click(evt);
        });
    }

    @EventHandler
    public void onClickBlock(PlayerInteractEntityEvent evt) {
        if (evt.isCancelled() || evt.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Wand.apply(evt.getPlayer(), (wand) -> {
            wand.click(evt);
        });
    }
}
