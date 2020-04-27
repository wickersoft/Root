package wickersoft.root;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MFEffects implements Runnable {

    private static final MFEffects INSTANCE = new MFEffects();
    private static final HighFrequencyRunnableCache CACHE = new HighFrequencyRunnableCache(MFEffects::sweepPlayers, 40);

    public static MFEffects instance() {
        return INSTANCE;
    }

    private MFEffects() {
    }

    private static void sweepPlayers(Player player, Consumer<Supplier<Boolean>> consoomer) {
        if (!player.hasPermission("root.item.volatile")) {
            ItemStack[] inv = player.getInventory().getContents();
            for (int i = 0; i < inv.length; i++) {
                if (SpecialItemUtil.isVolatile(inv[i])) {
                    if (!Util.canPlayerHoldVolatileItem(player, inv[i])) {
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                        player.updateInventory();
                    }
                }
                if (inv[i] != null) {
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
    }

    public void run() {
        CACHE.run();
    }
}
