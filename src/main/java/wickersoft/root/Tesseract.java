/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Dennis
 */
public class Tesseract {

    private static final long DOUBLE_CLICK_MAX_MILLIS = 600;
    private static final long TESSERACT_CAPACITY = Long.MAX_VALUE;
    private static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);

    private static final char[] BASE64_CHARS
            = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', '+', '/'};
    private static final HashMap<Player, Long> DOUBLE_CLICK_TIMER = new HashMap<>();
    private static final HashMap<Block, Tesseract> TESSERACT_CACHE = new HashMap<>();
    private final Sign sign;
    private boolean valid;
    private Material material;
    private long damage;
    private long amount;

    public static void garbageCollect() {
        long currentMillis = System.currentTimeMillis();
        Iterator<Entry<Player, Long>> mapIterator = DOUBLE_CLICK_TIMER.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Entry<Player, Long> entry = mapIterator.next();
            if (currentMillis - entry.getValue() > DOUBLE_CLICK_MAX_MILLIS || !entry.getKey().isOnline()) {
                mapIterator.remove();
            }
        }
    }

    public static boolean isDoubleClick(Player player) {
        return DOUBLE_CLICK_TIMER.containsKey(player) && (System.currentTimeMillis() - DOUBLE_CLICK_TIMER.get(player)) < DOUBLE_CLICK_MAX_MILLIS;
    }

    public static void rememberClick(Player player) {
        DOUBLE_CLICK_TIMER.put(player, System.currentTimeMillis());
    }

    public static boolean isKleinBottle(ItemStack stack) {
        if (stack == null || stack.getType() != Material.DRAGONS_BREATH || stack.getAmount() != 1 || !stack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        if (!meta.hasLore() || meta.getLore().size() < 4) {
            return false;
        }
        List<String> lore = meta.getLore();
        int loreStringBase = -1;
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).equals(ChatColor.GRAY + "Klein Bottle")) {
                loreStringBase = i;
            }
        }
        if (loreStringBase == -1) {
            return false;
        }
        if (lore.size() - loreStringBase < 4) {
            return false;
        }
        return lore.get(loreStringBase + 3).matches("^\\u00a77[0-9a-zA-Z+/]{15}$");
    }

    public static boolean isTesseract(Block block) {
        if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) block.getState();
            if ((sign.getLine(0).equals("\u00A71[Tesseract]")
                    || sign.getLine(0).equals("\u00A71[Tess\u00A71eract]"))) {
                return true;
            }
        }
        return false;
    }

    private static long getBase64Bits(String source, int start, int end) {
        long result = 0;
        char[] chars = source.toCharArray();
        for (int i = start; i < end; i++) {
            result |= ((long) (ArrayUtils.indexOf(BASE64_CHARS, chars[i])) << (6 * (end - i - 1)));
        }
        return result;
    }

    private static String toBase64(long bits, int fixedLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = fixedLength - 1; i >= 0; i--) {
            sb.append(BASE64_CHARS[(int) ((bits >> (6 * i)) & 0x3FL)]);
        }
        return sb.toString();
    }

    public Tesseract(Sign sign) {
        this.sign = sign;
        long upperBits, lowerBits;
        String dataField = sign.getLine(3);
        if (sign.getLine(0).equals("\u00A71[Tesseract]")) {
            if (!dataField.matches("^[0-9A-Za-z]{15}")) {
                valid = false;
                return;
            }
            long dataBits = Long.parseLong(sign.getLine(3), 16);
            valid = true;
            amount = dataBits >> 32;
            material = Material.getMaterial((int) ((dataBits >> 16) & 0xFFFF));
            damage = (dataBits) & 0xFFFF;
        } else {
            if (!dataField.matches("^[0-9A-Za-z+/]{15}")) {
                valid = false;
                return;
            }
            upperBits = getBase64Bits(dataField, 0, 7);
            lowerBits = getBase64Bits(dataField, 7, 15);
            valid = true;
            amount = ((upperBits << 21) & 0x7FFFFFFFFFE00000L) | ((lowerBits >> 27) & 0x1FFFFF);
            material = Material.getMaterial((int) ((lowerBits >> 15) & 0xFFF));
            damage = (lowerBits) & 0x7FFFL;
        }
    }

    public boolean isValid() {
        return valid;
    }

    public ItemStack withdrawItem() {
        return withdrawStack(1);
    }

    public ItemStack withdrawStack() {
        return withdrawStack(64);
    }

    public ItemStack withdrawStack(long stackSize) {
        if (stackSize > amount) {
            stackSize = amount;
        }
        amount -= stackSize;
        updateSign(false);
        return new ItemStack(material, (int) stackSize, (short) damage);
    }

    public ItemStack drop() {
        updateSign(false);
        sign.getBlock().setType(Material.AIR, false);
        return SpecialItemUtil.generateInstantSign(1, sign.getLine(0), sign.getLine(1), sign.getLine(2), sign.getLine(3));
    }

    public boolean storeStack(PlayerInventory inv) {
        ItemStack stackInHand = inv.getItemInMainHand();
        if (!canHoldStack(stackInHand)) {
            return false;
        }
        long storeAmount = TESSERACT_CAPACITY - amount;
        if (amount == 0) {
            material = stackInHand.getType();
            damage = stackInHand.getDurability();
        }
        if (stackInHand.getAmount() > storeAmount) {
            amount = TESSERACT_CAPACITY;
            stackInHand.setAmount(stackInHand.getAmount() - (int) storeAmount);
            inv.setItemInMainHand(stackInHand);
        } else {
            amount += stackInHand.getAmount();
            inv.setItemInMainHand(AIR_ITEM.clone());
        }
        updateSign(false);
        return true;
    }

    public boolean storeInventory(Inventory inv) {
        if (amount == 0) {
            return false;
        }
        int size = inv.getSize();
        for (int i = 0; i < size; i++) {
            ItemStack stack = inv.getItem(i);
            if (!canHoldStack(stack)) {
                continue;
            }
            long storeAmount = TESSERACT_CAPACITY - amount;
            if (amount == 0) {
                material = stack.getType();
                damage = stack.getDurability();
            }
            if (stack.getAmount() > storeAmount) {
                amount = TESSERACT_CAPACITY;
                stack.setAmount(stack.getAmount() - (int) storeAmount);
                inv.setItem(i, stack);
                break;
            } else {
                amount += stack.getAmount();
                inv.setItem(i, AIR_ITEM.clone());
            }
        }
        updateSign(false);
        return true;
    }

    public ItemStack storeFromKleinBottle(ItemStack bottle) {
        ItemStack newBottle = bottle.clone();
        ItemMeta meta = newBottle.getItemMeta();
        List<String> lore = meta.getLore();
        int loreStringBase = -1;
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).equals(ChatColor.GRAY + "Klein Bottle")) {
                loreStringBase = i;
                break;
            }
        }

        if (lore.get(loreStringBase + 3).equals(ChatColor.GRAY + "000000000000000")) {
            return bottle;
        }

        sign.setLine(1, lore.get(loreStringBase + 1).substring(2));
        sign.setLine(2, lore.get(loreStringBase + 2).substring(2));
        sign.setLine(3, lore.get(loreStringBase + 3).substring(2));
        lore.set(loreStringBase + 1, ChatColor.GRAY + "-");
        lore.set(loreStringBase + 2, ChatColor.GRAY + "");
        lore.set(loreStringBase + 3, ChatColor.GRAY + "000000000000000");
        sign.update();
        meta.setLore(lore);
        newBottle.setItemMeta(meta);
        return newBottle;
    }

    public ItemStack withdrawIntoKleinBottle(ItemStack bottle) {
        ItemStack newBottle = bottle.clone();
        ItemMeta meta = newBottle.getItemMeta();
        List<String> lore = meta.getLore();
        int loreStringBase = -1;
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).equals(ChatColor.GRAY + "Klein Bottle")) {
                loreStringBase = i;
                break;
            }
        }

        if (!lore.get(loreStringBase + 1).equals(ChatColor.GRAY + "-")
                || !lore.get(loreStringBase + 2).equals(ChatColor.GRAY + "")
                || !lore.get(loreStringBase + 3).equals(ChatColor.GRAY + "000000000000000")) {
            return bottle;
        }

        lore.set(loreStringBase + 1, ChatColor.GRAY + sign.getLine(1));
        lore.set(loreStringBase + 2, ChatColor.GRAY + sign.getLine(2));
        lore.set(loreStringBase + 3, ChatColor.GRAY + sign.getLine(3));
        sign.setLine(1, "-");
        sign.setLine(2, "");
        sign.setLine(3, "000000000000000");
        sign.update();
        meta.setLore(lore);
        newBottle.setItemMeta(meta);
        return newBottle;
    }

    public boolean canHoldStack(ItemStack stack) {
        if (amount == TESSERACT_CAPACITY) {
            return false;
        }
        if (stack == null || stack.getType() == Material.AIR) {
            return false;
        }
        if ((amount != 0
                && (stack.getType() != material
                || damage != stack.getDurability()))) {
            return false;
        }
        if (stack.getAmount() <= 0 || stack.getAmount() > 64) {
            return false;
        }
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (!meta.getClass().getName().equals("org.bukkit.craftbukkit.inventory.CraftMetaItem")) {
                return false;
            }
            if (meta.hasDisplayName()
                    || meta.hasLore()
                    || meta.hasEnchants()
                    || !meta.getItemFlags().isEmpty()
                    || meta.isUnbreakable()) {
                return false;
            }
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return amount == 0;
    }

    public boolean isFull() {
        return amount == TESSERACT_CAPACITY;
    }

    public final void updateSign(boolean blockUpdate) {
        if (!valid) {
            sign.setLine(0, ChatColor.RED + "[Tesseract]");
        } else {
            sign.setLine(0, "\u00A71[Tess\u00A71eract]");
            if (amount <= 0) {
                sign.setLine(1, "-");
                sign.setLine(2, "");
                sign.setLine(3, "000000000000000");
            } else {
                sign.setLine(1, material.name());
                sign.setLine(2, "" + (amount / 64) + "x64+" + (amount % 64));
                long upperBits = (amount >> 21) & 0x3FFFFFFFFFFL; // Upper 42 bits of amount (not the sign)
                long lowerBits = ((amount << 27) & 0xFFFFF8000000L) | (((long) material.getId() << 15) & 0x7FF8000L) | ((damage) & 0x7FFFL);
                sign.setLine(3, toBase64(upperBits, 7) + toBase64(lowerBits, 8));
            }
        }
        sign.update(true, blockUpdate);
    }

    @Override
    public String toString() {
        return "{Tesseract: " + (amount / 64) + "x64+" + (amount % 64) + " " + material + ":" + damage + "}";
    }

}
