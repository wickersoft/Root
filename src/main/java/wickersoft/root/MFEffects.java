package wickersoft.root;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

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
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                        player.updateInventory();
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
            //UserData data = UserDataProvider.getOrCreateUser(player);
        }
        Tesseract.garbageCollect();
    }
}
