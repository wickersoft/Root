/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Dennis
 */
public class Wand {

    private static final HashMap<String, Wand> LORE_TO_WAND = new HashMap<>();
    private static final HashMap<Wand, String> WAND_TO_LOWERCASE = new HashMap<>();
    private static final HashMap<String, String> LOWERCASE_TO_LORE = new HashMap<>();

    public static boolean isWandKnown(String wandName) {
        return LOWERCASE_TO_LORE.containsKey(wandName);
    }

    public static String getLoreString(String wandName) {
        return LOWERCASE_TO_LORE.get(wandName);
    }

    /**
     * Called by Root after static initialization is finished so getClasses()
     * works.
     */
    public static void load() {
        LORE_TO_WAND.clear();
        WAND_TO_LOWERCASE.clear();
        LOWERCASE_TO_LORE.clear();
        try {
            for (Class clazz : Wand.class.getClasses()) {
                Wand wand = (Wand) clazz.newInstance();
                LORE_TO_WAND.put(ChatColor.GRAY + clazz.getSimpleName(), wand);
                WAND_TO_LOWERCASE.put(wand, clazz.getSimpleName().toLowerCase());
                LOWERCASE_TO_LORE.put(clazz.getSimpleName().toLowerCase(), ChatColor.GRAY + clazz.getSimpleName());
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            System.err.println("Wands are broken!");
            ex.printStackTrace();
        }
    }

    public static void apply(Player player, Consumer<Wand> action) {
        ItemStack is = player.getInventory().getItemInMainHand();
        if (is == null
                || is.getType() == Material.AIR
                || !is.hasItemMeta()
                || !is.getItemMeta().hasLore()) {
            return;
        }
        List<String> lore = is.getItemMeta().getLore();
        for (String line : lore) {
            if (LORE_TO_WAND.containsKey(line)) {
                Wand wand = LORE_TO_WAND.get(line);
                if (player.hasPermission("root.wand." + WAND_TO_LOWERCASE.get(wand))) {
                    action.accept(wand);
                }
            }
        }
    }

    protected void click(BlockBreakEvent evt) {
    }

    protected void click(PlayerInteractEvent evt) {
    }

    protected void click(PlayerInteractEntityEvent evt) {
    }

    public static class PetOwner extends Wand {

        @Override
        protected void click(PlayerInteractEntityEvent evt) {
            if (!(evt.getRightClicked() instanceof Tameable)) {
                return;
            }
            Tameable t = (Tameable) evt.getRightClicked();
            evt.setCancelled(true);
            if (!t.isTamed()) {
                evt.getPlayer().sendMessage(ChatColor.GRAY + "This " + evt.getRightClicked().getType() + " is not tamed");
                return;
            }
            evt.getPlayer().sendMessage(ChatColor.GRAY + "This " + evt.getRightClicked().getType() + " belongs to " + t.getOwner().getName());
        }
    }

    public static class SilkDick extends Wand {

        @Override
        protected void click(BlockBreakEvent evt) {
            if (evt.isCancelled()) {
                return;
            }
            Block block = evt.getBlock();
            if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) block.getState();
                ItemStack dropSign = SpecialItemUtil.generateInstantSign(1, sign.getLines());
                evt.setCancelled(true);
                block.setType(Material.AIR);
                block.getWorld().dropItem(Util.getCenter(block), dropSign);
            }
        }
    }

    public static class Stack extends Wand {

        @Override
        protected void click(PlayerInteractEntityEvent evt) {
            if (evt.getPlayer().isSneaking()) {
                evt.getRightClicked().leaveVehicle();
                evt.getPlayer().sendMessage(ChatColor.GRAY + "Passenger dismounted");
                return;
            }

            if (evt.getPlayer().hasMetadata("root.clipboard.stack")) {
                Entity e = (Entity) evt.getPlayer().getMetadata("root.clipboard.stack").get(0).value();
                if (e != evt.getRightClicked()) {
                    evt.getRightClicked().addPassenger(e);
                    evt.getPlayer().sendMessage(ChatColor.GRAY + "Passenger selected");
                    evt.getPlayer().removeMetadata("root.clipboard.stack", Root.instance());
                }
            } else {
                evt.getPlayer().setMetadata("root.clipboard.stack", new FixedMetadataValue(Root.instance(), evt.getRightClicked()));
                evt.getPlayer().sendMessage(ChatColor.GRAY + "Passenger selected");
            }
        }
    }

    public static class Strip extends Wand {

        @Override
        protected void click(PlayerInteractEntityEvent evt) {
            if ((evt.getRightClicked() instanceof Player
                    && evt.getPlayer().hasPermission("root.wand.strip.player"))
                    || evt.getRightClicked() instanceof LivingEntity) {
                final LivingEntity le = (LivingEntity) evt.getRightClicked();
                EntityEquipment ee = le.getEquipment();
                ItemStack is = ee.getItemInMainHand();
                if (is != null && is.getType() != Material.AIR) {
                    le.getWorld().dropItem(le.getLocation(), is).setPickupDelay(40);
                    ee.setItemInMainHand(null);
                }
                is = ee.getItemInOffHand();
                if (is != null && is.getType() != Material.AIR) {
                    le.getWorld().dropItem(le.getLocation(), is).setPickupDelay(40);
                    ee.setItemInOffHand(null);
                }
                is = ee.getHelmet();
                if (is != null && is.getType() != Material.AIR) {
                    le.getWorld().dropItem(le.getLocation(), is).setPickupDelay(40);
                    ee.setHelmet(null);
                }
                is = ee.getChestplate();
                if (is != null && is.getType() != Material.AIR) {
                    le.getWorld().dropItem(le.getLocation(), is).setPickupDelay(40);
                    ee.setChestplate(null);
                }
                is = ee.getLeggings();
                if (is != null && is.getType() != Material.AIR) {
                    le.getWorld().dropItem(le.getLocation(), is).setPickupDelay(40);
                    ee.setLeggings(null);
                }
                is = ee.getBoots();
                if (is != null && is.getType() != Material.AIR) {
                    le.getWorld().dropItem(le.getLocation(), is).setPickupDelay(40);
                    ee.setBoots(null);
                }
                boolean can = le.getCanPickupItems();
                if (can) {
                    le.setCanPickupItems(false);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Root.instance(), () -> {
                        le.setCanPickupItems(true);
                    }, 100);
                }
                evt.getPlayer().sendMessage(ChatColor.GRAY + "Entity stripped!");
            }
        }
    }
}
