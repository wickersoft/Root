/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class Portal extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        Location srcLoc = player.getLocation();
        Location destLoc = getRemoteLocation(player);
        if (destLoc == null) {
            player.sendMessage(ChatColor.GRAY + "Could not find destination world!");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.GRAY + "Your destination would be on "
                    + ChatColor.BLUE + destLoc.getWorld().getName() + ChatColor.GRAY + " at "
                    + ChatColor.BLUE + destLoc.getBlockX() + " " + destLoc.getBlockY() + " " + destLoc.getBlockZ());
            return true;
        }
        switch (args[0]) {
            case "tp":
                player.sendMessage(ChatColor.GRAY + "Teleporting to proper portal destination..");
                player.teleport(destLoc);
                break;
            case "create":
                buildPortal(srcLoc);
                buildPortal(destLoc);
                player.sendMessage(ChatColor.GRAY + "Source and destination portals spawned");
                break;
        }

        return true;
    }

    @Override
    public String getSyntax() {
        return "/portal (tp/create)";
    }

    @Override
    public String getDescription() {
        return "Creates, debugs or simulates Portals";
    }

    private static Location getRemoteLocation(Player player) {
        String worldName = player.getWorld().getName();
        String remoteWorldName = null;
        boolean inNether = false;
        Location loc = player.getLocation();

        if (worldName.endsWith("_the_end")) {
            return null;
        }

        if (worldName.endsWith("_nether")) {
            inNether = true;
            remoteWorldName = worldName.substring(0, worldName.length() - 7);
        } else {
            remoteWorldName = worldName + "_nether";
        }

        if (Bukkit.getWorld(remoteWorldName) == null) {
            return null;
        }
        if (inNether) {
            Location destination = new Location(
                    Bukkit.getWorld(remoteWorldName),
                    loc.getX() * 8,
                    loc.getY(),
                    loc.getZ() * 8,
                    loc.getYaw(),
                    loc.getPitch());
            destination = destination.getWorld().getHighestBlockAt(destination).getLocation();
            return destination;
        } else {
            return new Location(
                    Bukkit.getWorld(remoteWorldName),
                    loc.getX() / 8,
                    loc.getY(),
                    loc.getZ() / 8,
                    loc.getYaw(),
                    loc.getPitch());
        }
    }

    private static void buildPortal(Location loc) {
        if (loc.getBlockY() > 252) {
            loc.setY(252);
        }
        World world = loc.getWorld();
        for (int x = loc.getBlockX() - 1; x < loc.getBlockX() + 2; x++) {
            for (int y = loc.getBlockY() - 1; y < loc.getBlockY() + 3; y++) {
                Block b = world.getBlockAt(x, y, loc.getBlockZ());
                if (x == loc.getBlockX() - 1 || x == loc.getBlockX() + 2
                        || y == loc.getBlockY() - 1 || y == loc.getBlockY() + 3) {
                    b.setType(Material.OBSIDIAN);
                } else {
                    b.setType(Material.NETHER_PORTAL);
                }
            }
        }
    }
}
