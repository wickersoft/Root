package wickersoft.root;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MFEffects implements Runnable {

    private static final MFEffects INSTANCE = new MFEffects();

    public static MFEffects instance() {
        return INSTANCE;
    }

    private MFEffects() {
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("root.item.volatile")) {
                ItemStack[] inv = player.getInventory().getContents();
                for (int i = 0; i < inv.length; i++) {
                    if (SpecialItemUtil.isVolatile(inv[i])
                            || SpecialItemUtil.isCursedSword(inv[i])) {
                        if (!Util.canPlayerHoldVolatileItem(player, inv[i])) {
                            player.getInventory().setItem(i, new ItemStack(Material.AIR));
                            player.updateInventory();
                        }
                    }
                    if (inv[i] != null && inv[i].getEnchantments() != null) {
                        for (Enchantment e : inv[i].getEnchantments().keySet()) {
                            if (inv[i].getEnchantments().get(e) > 16) {
                                player.getInventory().setItem(i, new ItemStack(Material.AIR));
                                player.updateInventory();
                                break;
                            }
                        }
                    }
                }
            }
            if (player.hasPermission("root.seelwc") && player.hasMetadata("root.seelwc")
                    && player.hasMetadata("root.seelwc-loc")) {
                Location seeLwcLoc = (Location) player.getMetadata("root.seelwc-loc").get(0).value();
                List<Block> seeLwcBlocks = (List<Block>) player.getMetadata("root.seelwc").get(0).value();
                if (seeLwcLoc.getWorld() != player.getWorld() || player.getLocation().distanceSquared(seeLwcLoc) > 1024) {
                    Util.hideHighlightBlocks(seeLwcBlocks, player);
                } else {
                    for (Block b : seeLwcBlocks) {
                        if (b.getType() == Material.AIR) {
                            Util.hideHighlightBlock(b, player);
                        }
                    }
                }
            }
            //UserData data = UserDataProvider.getOrCreateUser(player);
        }
    }
}
