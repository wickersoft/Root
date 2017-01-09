/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Dennis
 */
public class SpecialItemUtil {

    private static final String VOLATILE_LORE = ChatColor.GRAY + "Volatile";

    public static ItemStack generateInstantSign(int amount, String... lines) {
        ItemStack signStack = new ItemStack(Material.SIGN, amount, (short) 0);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Instant Sign");
        int maxLine = Math.min(4, lines.length);
        for (int i = 0; i < maxLine; i++) {
            lore.add(lines[i].replace(ChatColor.COLOR_CHAR, '&'));
        }
        ItemMeta meta = signStack.getItemMeta();
        meta.setLore(lore);
        signStack.setItemMeta(meta);
        return signStack;
    }
    
    public static ItemStack generateKleinBottle(int amount) {
        ItemStack signStack = new ItemStack(Material.DRAGONS_BREATH, amount, (short) 0);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Klein Bottle");
        lore.add(ChatColor.GRAY + "-");
        lore.add(ChatColor.GRAY + "");
        lore.add(ChatColor.GRAY + "000000000000000");
        ItemMeta meta = signStack.getItemMeta();
        meta.setLore(lore);
        signStack.setItemMeta(meta);
        return signStack;
    }

    public static ItemStack setLoreFlag(ItemStack original, String loreLine, boolean setOrUnset) {
        ItemStack clone = original.clone();
        if (setOrUnset && !hasLoreFlag(clone, loreLine)) {
            ItemMeta meta = clone.getItemMeta();
            if (meta == null) {
                System.err.println("Unable to set a Lore lag: ItemMeta is null!");
                System.err.println("Item: ");
                System.err.println(original);
                return original;
            }
            List<String> lore;
            if (meta.hasLore()) {
                lore = meta.getLore();
            } else {
                lore = new ArrayList<>();
            }
            lore.add(loreLine);
            meta.setLore(lore);
            clone.setItemMeta(meta);
        } else if (!setOrUnset && hasLoreFlag(clone, loreLine)) {
            ItemMeta meta = clone.getItemMeta();
            List<String> lore = meta.getLore();
            Iterator<String> loreIterator = lore.iterator();
            while (loreIterator.hasNext()) {
                if (loreIterator.next().equals(loreLine)) {
                    loreIterator.remove();
                }
            }
            meta.setLore(lore);
            clone.setItemMeta(meta);
        }
        return clone;
    }

    public static boolean hasLoreFlag(ItemStack is, String loreFlag) {
        if (is == null) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().hasLore()) {
            return false;
        }
        for (String line : is.getItemMeta().getLore()) {
            if (line.equals(loreFlag)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack setVolatile(ItemStack original, boolean setVolatile) {
        return setLoreFlag(original, VOLATILE_LORE, setVolatile);
    }

    public static boolean isVolatile(ItemStack is) {
        return hasLoreFlag(is, VOLATILE_LORE);
    }

    public static boolean isCursedSword(ItemStack is) {
        if (is == null) {
            return false;
        }
        if (is.getType() != Material.DIAMOND_SWORD) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().hasLore()) {
            return false;
        }
        if (is.getItemMeta().hasEnchants()) {
            return false;
        }
        return is.getItemMeta().getLore().get(0).equals("Forged by the undead");
    }

}
