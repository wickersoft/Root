/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class WatcherPlayer implements Listener {

    private static final WatcherPlayer INSTANCE = new WatcherPlayer();

    public static WatcherPlayer instance() {
        return INSTANCE;
    }

    private WatcherPlayer() {
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        UserData data = UserDataProvider.getOrCreateUser(evt.getPlayer());
        if (data.isFrozen()) {
            evt.setCancelled(true);
        }

        if (evt.getBlock().getY() <= 16 && evt.getBlock().getType() == Material.DIAMOND_ORE) {
            long xrayTest = data.testXrayWarnTime();
            if (xrayTest < Storage.XRAY_WARN_TIME) {
                long minutes = xrayTest / 60000;
                long seconds = (xrayTest / 1000) % 60;
                Bukkit.broadcast(ChatColor.RED + "Player " + evt.getPlayer().getName() + " has mined 20 Diamond Ore in "
                        + minutes + " minutes and " + seconds + " seconds!", "root.notify.xray");
                data.resetXrayWarnTime();
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent evt) {
        UserData data = UserDataProvider.getOrCreateUser(evt.getPlayer());
        if (data.isFrozen()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent evt) {
        if (evt.getDamager().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) evt.getDamager();
        if (UserDataProvider.getOrCreateUser(player).isFrozen()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        String ip = evt.getPlayer().getAddress().getAddress().getHostAddress();
        UserData data = UserDataProvider.getOrCreateUser(evt.getPlayer());
        data.setName(evt.getPlayer().getName());
        data.setIp(ip);
        if(Storage.WARN_IPS.containsKey(ip)
                && !evt.getPlayer().getName().equals(Storage.WARN_IPS.get(ip))) {
            Bukkit.broadcast(ChatColor.RED + "Player " + evt.getPlayer().getName() + "\'s IP matches with " + Storage.WARN_IPS.get(ip) +"!", "root.notify.iprec");
        }
        new TaskGeoQuery(data, !evt.getPlayer().hasPlayedBefore()).runTaskAsynchronously(Root.instance());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen()) {
            evt.setCancelled(true);
        } else if ((!evt.getPlayer().hasPermission("root.item.volatile")
                && SpecialItemUtil.isVolatile(evt.getItem().getItemStack()))
                || SpecialItemUtil.isCursedSword(evt.getItem().getItemStack())) {
            evt.setCancelled(true);
            evt.getItem().remove();
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent evt) {
        if (evt.getReason().equals("Flying is not enabled on this server")) {
            Bukkit.broadcast(ChatColor.RED + "Player " + evt.getPlayer().getName() + " was kicked for flying!", "root.notify.flykick");
        }
        if (evt.getReason().equals("disconnect.spam") && evt.getPlayer().hasPermission("root.chat.bypassdisconnectspam")) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        if (evt.getPlayer().hasMetadata("root.task.launch")) {
            TaskLaunchPlayer launcher = (TaskLaunchPlayer) evt.getPlayer().getMetadata("root.task.launch").get(0).value();
            launcher.cancel();
        }
        UserData data = UserDataProvider.getOrCreateUser(evt.getPlayer());
        data.saveData();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        if (evt.getPlayer().hasMetadata("root.task.launch")) {
            TaskLaunchPlayer launcher = (TaskLaunchPlayer) evt.getPlayer().getMetadata("root.task.launch").get(0).value();
            launcher.cancel();
        }
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen()) {
            evt.setCancelled(true);
        }
    }

}
