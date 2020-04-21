/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class Portal extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {

        String worldName = player.getWorld().getName();
        String remoteWorldName = null;
        boolean inNether = false;

        if (worldName.endsWith("_the_end")) {

        }

        if (worldName.endsWith("_nether")) {
            inNether = true;
            remoteWorldName = worldName.substring(0, worldName.length() - 7);
        } else {
            remoteWorldName = worldName + "_nether";
        }

        if (Bukkit.getWorld(remoteWorldName) == null) {

        }

        if (args.length == 0) {

        } else {
            switch (args[0]) {
                case "tp":
                    Location loc = player.getLocation();
                    player.sendMessage(ChatColor.GRAY + "Teleporting to proper portal destination..");
                    if (inNether) {
                        Location destination = new Location(
                                Bukkit.getWorld(remoteWorldName),
                                loc.getX() * 8,
                                loc.getY(),
                                loc.getZ() * 8,
                                loc.getYaw(),
                                loc.getPitch());
                        destination = destination.getWorld().getHighestBlockAt(destination).getLocation();
                        //player.teleport();
                    } else {
                        player.teleport(new Location(
                                Bukkit.getWorld(remoteWorldName),
                                loc.getX() / 8,
                                loc.getY(),
                                loc.getZ() / 8,
                                loc.getYaw(),
                                loc.getPitch()));
                    }
                    break;
                case "create":

                    break;
            }
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

}
