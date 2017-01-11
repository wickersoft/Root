/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import com.griefcraft.model.Protection;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import wickersoft.root.Root;
import wickersoft.root.Storage;
import wickersoft.root.Util;

/**
 *
 * @author Dennis
 */
public class Seelwc extends PlayerCommand {

    @Override
    public boolean onCommand(Player sender, String[] args) {
        if (args.length != 0 && !args[0].matches("^\\d{1,3}$")) {
            sendUsage(sender);
            return true;
        }

        int numHiddenBlocks = 0;
        if (sender.hasMetadata("root.seelwc")) {
            List<Block> seeLwcBlocks = (List<Block>) sender.getMetadata("root.seelwc").get(0).value();
            numHiddenBlocks = seeLwcBlocks.size();
            Util.hideHighlightBlocks(seeLwcBlocks, sender);
            sender.removeMetadata("root.seelwc", Root.instance());
            sender.removeMetadata("root.seelwc-loc", Root.instance());
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "" + numHiddenBlocks + ChatColor.GRAY + " blocks hidden");
            return true;
        } else {
            int radius = Integer.parseInt(args[0]);
            if (Storage.lwc == null) {
                sender.sendMessage(ChatColor.GRAY + "Unable to connect to LWC!");
                return true;
            }
            Location playerLoc = sender.getLocation();
            String worldName = playerLoc.getWorld().getName();
            int x = playerLoc.getBlockX();
            int y = playerLoc.getBlockY();
            int z = playerLoc.getBlockZ();
            List<Protection> nearbyLwcProtections = Storage.lwc.getLWC().getPhysicalDatabase().loadProtections(worldName, x, y, z, radius);
            List<Block> nearbyLwcBlocks = new ArrayList<>();
            for (Protection prot : nearbyLwcProtections) {
                Block block = prot.getBlock();
                if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                    for(BlockFace face : Storage.CARDINAL_FACES) {
                        if(block.getRelative(face).getType() == block.getType()) {
                            nearbyLwcBlocks.add(block.getRelative(face));
                        }
                    }
                }
                nearbyLwcBlocks.add(block);
            }
            if (!Util.showHighlightBlocks(nearbyLwcBlocks, sender)) {
                sender.sendMessage(ChatColor.GRAY + "Unable to connect to NMS!");
                return true;
            }
            int numShownBlocks = nearbyLwcBlocks.size();
            sender.setMetadata("root.seelwc-loc", new FixedMetadataValue(Root.instance(), playerLoc));
            sender.setMetadata("root.seelwc", new FixedMetadataValue(Root.instance(), nearbyLwcBlocks));
            sender.sendMessage(ChatColor.BLUE + "" + numShownBlocks + ChatColor.GRAY + " blocks shown");
        }
        return true;
    }

    public String getSyntax() {
        return "/seelwc [radius]";
    }

    public String getDescription() {
        return "Lights up blocks protected by LWC in your vicinity";
    }

}
