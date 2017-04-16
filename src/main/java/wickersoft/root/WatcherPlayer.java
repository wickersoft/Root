/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import syn.root.user.Mark;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import syn.root.user.InventoryProvider;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class WatcherPlayer implements Listener {

    private static final WatcherPlayer INSTANCE = new WatcherPlayer();
    private static final SimpleDateFormat DEATH_INVENTORY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static WatcherPlayer instance() {
        return INSTANCE;
    }

    private WatcherPlayer() {
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        UserData data = UserDataProvider.getOrCreateUser(evt.getPlayer());
        if (data.isFrozen() && !evt.getPlayer().hasPermission("root.freeze.bypass")) {
            evt.setCancelled(true);
            return;
        }

        if (!evt.isCancelled() && evt.getBlock().getY() <= 16 && evt.getBlock().getType() == Material.DIAMOND_ORE
                && !evt.getPlayer().hasPermission("root.notify.xray.bypass")) {
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
        if (data.isFrozen() && !evt.getPlayer().hasPermission("root.freeze.bypass")) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent evt) {
        if (evt.getDamager().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) evt.getDamager();
        if (UserDataProvider.getOrCreateUser(player).isFrozen() && !player.hasPermission("root.freeze.bypass")) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        long currentMillis = System.currentTimeMillis();
        File[] allInventoryFiles = InventoryProvider.getInventoryFiles(evt.getEntity().getUniqueId());
        for (File file : allInventoryFiles) {
            if (file.getName().startsWith("_death_")
                    && currentMillis - file.lastModified() > Storage.MAX_DEATH_INV_AGE_MILLIS) {
                file.delete(); // Clean up old death inventories
            }
        }

        String deathInventoryName = "_death_" + DEATH_INVENTORY_DATE_FORMAT.format(Date.from(Instant.ofEpochMilli(currentMillis)));
        File deathInventoryFile = InventoryProvider.getInventoryFile(evt.getEntity(), deathInventoryName);
        InventoryProvider.saveInventory(evt.getEntity(), deathInventoryFile);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen() && !evt.getPlayer().hasPermission("root.freeze.bypass")) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen() && !evt.getPlayer().hasPermission("root.freeze.bypass")) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(PlayerLoginEvent evt) {
        System.out.print(evt);
        if (evt.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            String kickMessage = evt.getKickMessage();
            kickMessage += "\n\n";
            kickMessage += Storage.BAN_APPEAL_MESSAGE;
            evt.setKickMessage(kickMessage);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        
        String ip = evt.getPlayer().getAddress().getAddress().getHostAddress();
        UserData data = UserDataProvider.getOrCreateUser(evt.getPlayer());
        data.setName(evt.getPlayer().getName());
        data.setIp(ip);
        if (Storage.WARN_IPS.containsKey(ip)
                && !evt.getPlayer().getName().equals(Storage.WARN_IPS.get(ip))) {
            Bukkit.broadcast(ChatColor.RED + "Player " + evt.getPlayer().getName() + "\'s IP matches with " + Storage.WARN_IPS.get(ip) + "!", "root.notify.iprec");
        }
        for (Mark mark : data.getMarks()) {
            if (mark.getPriority() > 0) {
                Bukkit.broadcast(ChatColor.RED + "Player " + evt.getPlayer().getName() + " has Marks! " + ChatColor.GRAY + ChatColor.ITALIC + "[/mark " + data.getName() + "]", "root.notify.mark");
            }
        }

        new TaskGeoQuery(data, false, (geoData) -> {
            String geoLocation = (String) geoData.getOrDefault("geoplugin_countryName", "GeoQuery Error");
            String preciseGeoLocation = (String) geoData.getOrDefault("geoplugin_city", "Unknown") + ", "
                    + (String) geoData.getOrDefault("geoplugin_regionName", "Unknown") + ", "
                    + geoLocation;
            geoLocation = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(geoLocation);
            preciseGeoLocation = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(preciseGeoLocation);
            String timezone = (String) geoData.getOrDefault("maps_timezone", "GeoQuery Error");
            data.setGeoLocation(geoLocation);
            data.setPreciseGeoLocation(preciseGeoLocation);
            data.setTimezone(timezone);
            if (!evt.getPlayer().hasPlayedBefore()) {
                Bukkit.broadcast(ChatColor.BLUE + data.getName() + ChatColor.GRAY + ": "
                        + ChatColor.BLUE + ip + ChatColor.GRAY + " / "
                        + ChatColor.BLUE + geoLocation, "root.notify.firstjoin");
            }
        }).runTaskAsynchronously(Root.instance());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen() && !evt.getPlayer().hasPermission("root.freeze.bypass")) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent evt) {
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen() && !evt.getPlayer().hasPermission("root.freeze.bypass")) {
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
            Block block = evt.getPlayer().getLocation().getBlock();
            int altitude = 0;
            while (block.getY() > 0 && block.getType() == Material.AIR) {
                block = block.getRelative(BlockFace.DOWN);
                altitude++;
            }
            Bukkit.broadcast(ChatColor.RED + evt.getPlayer().getName() + " was kicked for flying! Altitiude: "
                    + altitude + "  (" + block.getX() + " " + block.getY() + " " + block.getZ() + ")", "root.notify.flykick");
        }

        if (evt.getReason().equals("disconnect.spam") && evt.getPlayer().hasPermission("root.chat.nodisconnectspam")) {
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
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        if (evt.getPlayer().hasMetadata("root.task.launch")) {
            TaskLaunchPlayer launcher = (TaskLaunchPlayer) evt.getPlayer().getMetadata("root.task.launch").get(0).value();
            launcher.cancel();
        }
        if (UserDataProvider.getOrCreateUser(evt.getPlayer()).isFrozen() && !evt.getPlayer().hasPermission("root.freeze.bypass")) {
            evt.setCancelled(true);
        }
    }

}
