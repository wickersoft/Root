/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
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
    private static final HashMap<Player, Long> DOUBLE_CLICK_TIMER = new HashMap<>();

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

    public static Tesseract fromSign(Sign sign) throws IllegalArgumentException {
        if (!isTesseract(sign.getBlock())) {
            throw new IllegalArgumentException("Item Stack does not represent a Tesseract");
        }
        String code = sign.getLine(3);
        return new Tesseract(code, sign.getLine(0).equals("\u00A71[Tesseract]"));
    }

    public static Tesseract fromKleinBottle(ItemStack stack) throws IllegalArgumentException {
        if (!isKleinBottle(stack)) {
            throw new IllegalArgumentException("Item Stack does not represent a Tesseract");
        }
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();
        int loreStringBase = -1;
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).equals(ChatColor.GRAY + "Klein Bottle")) {
                loreStringBase = i;
            }
        }
        String code = ChatColor.stripColor(lore.get(loreStringBase + 3));
        return new Tesseract(code, false);
    }

    public static boolean isDoubleClick(Player player) {
        return DOUBLE_CLICK_TIMER.containsKey(player) && (System.currentTimeMillis() - DOUBLE_CLICK_TIMER.get(player)) < DOUBLE_CLICK_MAX_MILLIS;
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
        if (!lore.get(0).equals(ChatColor.GRAY + "Klein Bottle")) {
            return false;
        }
        if (lore.size() != 4) {
            return false;
        }
        return lore.get(3).matches("^\\u00a77[0-9a-zA-Z+/]{15}$");
    }

    public static boolean isTesseract(Block block) {
        if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) block.getState();
            if ((sign.getLine(0).equals("\u00A71[Tesseract]")
                    && sign.getLine(3).matches("^[0-9A-Za-z]{15}"))
                    || (sign.getLine(0).equals("\u00A71[Tess\u00A71eract]")
                    && sign.getLine(3).matches("^[0-9A-Za-z+/]{15}"))) {
                return true;
            }
        }
        return false;
    }

    public static void rememberClick(Player player) {
        DOUBLE_CLICK_TIMER.put(player, System.currentTimeMillis());
    }

    public static boolean storeStack(PlayerInventory inv, Tesseract tesseract) {
        ItemStack stackInHand = inv.getItemInMainHand();
        if (!tesseract.canHoldStack(stackInHand)) {
            return false;
        }
        long storeAmount = TESSERACT_CAPACITY - tesseract.amount;
        if (tesseract.amount == 0) {
            tesseract.material = stackInHand.getType();
            tesseract.damage = stackInHand.getDurability();
        }
        if (stackInHand.getAmount() > storeAmount) {
            tesseract.amount = TESSERACT_CAPACITY;
            stackInHand.setAmount(stackInHand.getAmount() - (int) storeAmount);
            inv.setItemInMainHand(stackInHand);
        } else {
            tesseract.amount += stackInHand.getAmount();
            inv.setItemInMainHand(AIR_ITEM.clone());
        }
        return true;
    }

    public static boolean storeInventory(Inventory inv, Tesseract tesseract) {
        if (tesseract.amount == 0) {
            return false;
        }
        int size = inv.getSize();
        for (int i = 0; i < size; i++) {
            ItemStack stack = inv.getItem(i);
            if (!tesseract.canHoldStack(stack)) {
                continue;
            }
            long storeAmount = TESSERACT_CAPACITY - tesseract.amount;
            if (tesseract.amount == 0) {
                tesseract.material = stack.getType();
                tesseract.damage = stack.getDurability();
            }
            if (stack.getAmount() > storeAmount) {
                tesseract.amount = TESSERACT_CAPACITY;
                stack.setAmount(stack.getAmount() - (int) storeAmount);
                inv.setItem(i, stack);
                break;
            } else {
                tesseract.amount += stack.getAmount();
                inv.setItem(i, AIR_ITEM.clone());
            }
        }
        return true;
    }

    private Tesseract(String code, boolean legacyEncoding) {
        if (legacyEncoding) {
            long dataBits = Long.parseLong(code, 16);
            amount = dataBits >> 32;
            material = Material.getMaterial((int) ((dataBits >> 16) & 0xFFFF));
            damage = (dataBits) & 0xFFFF;
        } else {
            long upperBits = StringUtil.getBase64Bits(code, 0, 7);
            long lowerBits = StringUtil.getBase64Bits(code, 7, 15);
            amount = ((upperBits << 21) & 0x7FFFFFFFFFE00000L) | ((lowerBits >> 27) & 0x1FFFFF);
            material = Material.getMaterial((int) ((lowerBits >> 15) & 0xFFF));
            damage = (lowerBits) & 0x7FFFL;
        }
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

    public ItemStack drop() {
        String[] text = toText();
        return SpecialItemUtil.generateInstantSign(1, "\u00A71[Tess\u00A71eract]", text[0], text[1], text[2]);
    }

    public boolean fuseInto(Tesseract other) {
        if(other.material == Material.AIR || other.amount == 0) {
            other.material = Material.AIR;
            other.amount = 0;
        }
        if (other.amount == 0
                || (other.material == this.material
                && other.damage == this.damage)) {
            other.material = this.material;
            other.damage = this.damage;
            other.amount += this.amount;
            this.amount = 0;
            this.material = Material.AIR;
            this.damage = 0;
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return amount == 0;
    }

    public boolean isFull() {
        return amount == TESSERACT_CAPACITY;
    }

    public String[] toText() {
        String[] text = new String[3];
        if (amount <= 0) {
            text[0] = "-";
            text[1] = "";
            text[2] = "000000000000000";
        } else {
            text[0] = material.name();
            text[1] = "" + (amount / 64) + "x64+" + (amount % 64);
            long upperBits = (amount >> 21) & 0x3FFFFFFFFFFL; // Upper 42 bits of amount (not the sign)
            long lowerBits = ((amount << 27) & 0xFFFFF8000000L) | (((long) material.getId() << 15) & 0x7FF8000L) | ((damage) & 0x7FFFL);
            text[2] = StringUtil.toBase64(upperBits, 7) + StringUtil.toBase64(lowerBits, 8);
        }
        return text;
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
        if (amount == 0) {
            material = Material.AIR;
            damage = 0;
        }
        return new ItemStack(material, (int) stackSize, (short) damage);
    }

    public final boolean writeToKleinBottle(ItemStack bottle) {
        if (!isKleinBottle(bottle)) {
            return false;
        }
        String[] text = toText();
        ItemMeta meta = bottle.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(1, ChatColor.GRAY + text[0]);
        lore.set(2, ChatColor.GRAY + text[1]);
        lore.set(3, ChatColor.GRAY + text[2]);
        meta.setLore(lore);
        bottle.setItemMeta(meta);
        return true;
    }

    public final void writeToSign(Sign sign, boolean blockUpdate) {
        sign.setLine(0, "\u00A71[Tess\u00A71eract]");
        if (amount <= 0 || material == Material.AIR) {
            sign.setLine(1, "-");
            sign.setLine(2, "");
            sign.setLine(3, "000000000000000");
        } else {
            sign.setLine(1, material.name());
            sign.setLine(2, "" + (amount / 64) + "x64+" + (amount % 64));
            long upperBits = (amount >> 21) & 0x3FFFFFFFFFFL; // Upper 42 bits of amount (not the sign)
            long lowerBits = ((amount << 27) & 0xFFFFF8000000L) | (((long) material.getId() << 15) & 0x7FF8000L) | ((damage) & 0x7FFFL);
            sign.setLine(3, StringUtil.toBase64(upperBits, 7) + StringUtil.toBase64(lowerBits, 8));
        }
        sign.update(true, blockUpdate);
    }

    @Override
    public String toString() {
        return "{Tesseract: " + (amount / 64) + "x64+" + (amount % 64) + " " + material + ":" + damage + "}";
    }
}
