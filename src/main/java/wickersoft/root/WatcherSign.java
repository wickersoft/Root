/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.block.Sign;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 *
 * @author Dennis
 */
public class WatcherSign implements Listener {

    private static final BlockFace[] CARDINAL_FACES = {BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST};
    private static final HashSet<Block> INSTANT_SIGNS = new HashSet<>();
    private static final HashMap<Block, Integer> DROPPER_POWER_CACHE = new HashMap<>();
    private static final WatcherSign INSTANCE = new WatcherSign();

    public static WatcherSign instance() {
        return INSTANCE;
    }

    private WatcherSign() {
    }

    @EventHandler
    public void dropperRedstone(BlockPhysicsEvent evt) {
        if (evt.getBlock().getType() != Material.DROPPER || evt.isCancelled()) {
            return;
        }
        Block dropperBlock = evt.getBlock();
        int power = dropperBlock.getBlockPower();
        Integer oldPower = DROPPER_POWER_CACHE.put(dropperBlock, power);
        if (oldPower != null && oldPower == 0 && power > 0) { // Positive redstone edge on a dropper
            Dropper dropper = (Dropper) dropperBlock.getState();
            boolean tesseractApplied = false;
            for (BlockFace face : CARDINAL_FACES) {
                if (Tesseract.isTesseract(dropperBlock.getRelative(face))) {
                    Sign sign = (Sign) dropperBlock.getRelative(face).getState();
                    Tesseract tesseract = new Tesseract(sign);
                    tesseractApplied |= tesseract.storeInventory(dropper.getInventory());
                }
            }
            if (tesseractApplied) {
                dropper.update();
            }
        }
    }

    @EventHandler
    public void signDetachCheck(BlockPhysicsEvent evt) {
        if (Tesseract.isTesseract(evt.getBlock())) {
            Block signBlock = evt.getBlock();
            org.bukkit.material.Sign signData = (org.bukkit.material.Sign) signBlock.getState().getData();
            Block attachedBlock = signBlock.getRelative(signData.getAttachedFace());
            if (!attachedBlock.getType().isSolid()) {
                Sign sign = (Sign) signBlock.getState();
                Tesseract tesseract = new Tesseract(sign);
                if (tesseract.isValid() && !tesseract.isEmpty()) {
                    ItemStack stack = tesseract.drop();
                    evt.setCancelled(true);
                    sign.getWorld().dropItem(sign.getLocation().add(0.5, 0, 0.5), stack).setItemStack(stack);
                }
            }
        }
    }

    @EventHandler
    public void signPlace(BlockPlaceEvent evt) {
        if (!evt.isCancelled() && evt.getItemInHand().getType() == Material.SIGN) {
            ItemStack stack = evt.getItemInHand();
            if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
                List<String> lore = stack.getItemMeta().getLore();
                for (int i = 0; i < lore.size(); i++) {
                    if (lore.get(i).equals(ChatColor.GRAY + "Instant Sign")) {
                        Sign sign = (Sign) evt.getBlockPlaced().getState();
                        for (int j = 0; j < 4 && i + j + 1 < lore.size(); j++) {
                            sign.setLine(j, ChatColor.translateAlternateColorCodes('&', lore.get(i + j + 1)));
                        }
                        sign.update();
                        INSTANT_SIGNS.add(sign.getBlock());
                        evt.getPlayer().closeInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public boolean signEdit(SignChangeEvent evt) {
        if (INSTANT_SIGNS.contains(evt.getBlock())) {
            evt.setCancelled(true);
            INSTANT_SIGNS.remove(evt.getBlock());
            return true;
        }
        switch (evt.getLine(1).toLowerCase()) {
            case "[lift up]":
                if (evt.getPlayer().hasPermission("root.sign.lift.create")) {
                    evt.setLine(1, "[Lift Up]");
                } else {
                    evt.setLine(1, ChatColor.DARK_RED + "[Lift Up]");
                    break;
                }
                Block thisblock = evt.getBlock();
                for (int i = 0; i <= 255 - thisblock.getY(); i++) {
                    Block block = thisblock.getRelative(0, i, 0);
                    if (isSign(block)) {
                        Sign sgn = (Sign) block.getState();
                        if (sgn.getLine(1).equals("[Lift Down]")) {
                            Util.display(Util.getCenter(block, true), Particle.VILLAGER_HAPPY, 3, 0f, .1f, .1f, .1f);
                            Util.display(Util.getCenter(thisblock, true), Particle.VILLAGER_HAPPY, 3, 0f, .1f, .1f, .1f);
                            break;
                        }
                    }
                }
                break;
            case "[lift down]":
                if (evt.getPlayer().hasPermission("root.sign.lift.create")) {
                    evt.setLine(1, "[Lift Down]");
                } else {
                    evt.setLine(1, ChatColor.DARK_RED + "[Lift Down]");
                    break;
                }
                thisblock = evt.getBlock();
                for (int i = 0; i <= thisblock.getY(); i++) {
                    Block block = thisblock.getRelative(0, -i, 0);
                    if (isSign(block)) {
                        Sign sgn = (Sign) block.getState();
                        if (sgn.getLine(1).equals("[Lift Up]")) {
                            Util.display(Util.getCenter(block, true), Particle.VILLAGER_HAPPY, 3, 0f, .1f, .1f, .1f);
                            Util.display(Util.getCenter(thisblock, true), Particle.VILLAGER_HAPPY, 3, 0f, .1f, .1f, .1f);
                            break;
                        }
                    }
                }
                break;
            case "[cart]":
                if (evt.getPlayer().hasPermission("root.sign.cart.create")) {
                    String spd = evt.getLine(2);
                    evt.setLine(1, ChatColor.DARK_BLUE + "[Cart]");
                    evt.setLine(2, "8");
                    if (spd.matches("^[\\d]{0,2}$")) {
                        evt.setLine(2, spd);
                    }
                }
                break;
            case "[boat]":
                if (evt.getPlayer().hasPermission("root.sign.boat.create")) {
                    String spd = evt.getLine(2);
                    evt.setLine(1, ChatColor.DARK_BLUE + "[Boat]");
                    evt.setLine(2, "8");
                    if (spd.matches("^[\\d]{0,2}$")) {
                        evt.setLine(2, spd);
                    }
                }
                break;
        }
        switch (evt.getLine(0).toLowerCase()) {
            case "[info]":
                if (evt.getPlayer().hasPermission("root.sign.info.create")) {
                    evt.setLine(0, ChatColor.DARK_BLUE + "[Info]");
                }
                break;
            case "[tesseract]":
                if (!evt.getPlayer().hasPermission("root.sign.tesseract.create")) {
                    evt.setLine(0, "");
                    evt.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to make a Tesseract!");
                    return true;
                }

                if (!Util.isProtected(evt.getBlock())) {
                    evt.getPlayer().sendMessage(ChatColor.RED + "Don't create a Tesseract in an unprotected area!");
                    return true;
                }

                evt.setLine(0, ChatColor.DARK_BLUE + "[Tess" + ChatColor.DARK_BLUE + "eract]");
                evt.setLine(1, "-");
                evt.setLine(2, "");
                evt.setLine(3, "000000000000000");
                break;
            case "[launch]":
                if (!evt.getPlayer().hasPermission("root.sign.launch.create")) {
                    break;
                }
                if (evt.getLine(1).matches("^\\d+$")
                        && evt.getLine(2).matches("^\\d+$")
                        && evt.getLine(3).matches("^\\d+$")) {
                    evt.setLine(0, ChatColor.DARK_BLUE + "[Launch]");
                } else {
                    evt.setLine(0, ChatColor.DARK_RED + "[Launch]");
                }
                break;
            case "[petition]":
                if (!evt.getPlayer().hasPermission("root.sign.petition.create")) {
                    break;
                }
                if (Storage.PETITIONS.containsKey(evt.getLine(1))) {
                    evt.getPlayer().sendMessage(Root.logo + " There already exists a petition with this name!");
                    evt.setLine(2, "" + ChatColor.GREEN + Storage.PETITIONS.get(evt.getLine(1)).getNumberOfSignatures() + " Signatures");
                } else {
                    Storage.PETITIONS.put(evt.getLine(1), new Petition(evt.getLine(1)));
                    evt.setLine(2, ChatColor.GREEN + "0 Signatures");
                }
                evt.setLine(0, "\u00A71[Petition]");
                break;
        }
        return true;
    }

    @EventHandler
    public void click(PlayerInteractEvent evt) {
        if (evt.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if ((evt.getClickedBlock() != null && (evt.getClickedBlock().getTypeId() == 68 || evt.getClickedBlock().getTypeId() == 63))) {
            evt.setCancelled(!processSignInteraction(evt.getPlayer(), evt.getClickedBlock(), evt.getAction()));
        } else if ((evt.getAction() == Action.RIGHT_CLICK_AIR && isSign(evt.getPlayer().getTargetBlock((HashSet<Material>) null, 5)))) {
            evt.setCancelled(!processSignInteraction(evt.getPlayer(), evt.getPlayer().getTargetBlock((HashSet<Material>) null, 5), Action.RIGHT_CLICK_BLOCK));
        }
    }

    public boolean processSignInteraction(Player player, Block clickedBlock, Action action) {
        Sign sign = (Sign) clickedBlock.getState();
        switch (sign.getLine(1)) {
            case "[Lift Up]":
                if (action != Action.RIGHT_CLICK_BLOCK || !player.hasPermission("root.sign.lift")) {
                    return true;
                }
                for (int i = 1; i < 255; i++) {
                    Block block = clickedBlock.getRelative(0, i, 0);
                    if (isSign(block)) {
                        Sign sgn = (Sign) block.getState();
                        if (sgn.getLine(1).equals("[Lift Down]")) {
                            Location loc = Util.getLiftDestination(player.getLocation(), sgn.getLocation());
                            if (loc == null) {
                                player.sendMessage(ChatColor.GRAY + "This Lift is not safe to use!");
                            } else {
                                player.teleport(loc);
                            }
                            return false;
                        }
                    }
                }
                player.sendMessage(ChatColor.GRAY + "This Lift sign is not linked!");
                return false;
            case "[Lift Down]":
                if (action != Action.RIGHT_CLICK_BLOCK || !player.hasPermission("root.sign.lift")) {
                    return true;
                }
                for (int i = 1; i < 255; i++) {
                    Block block = clickedBlock.getRelative(0, -i, 0);
                    if (isSign(block)) {
                        Sign sgn = (Sign) block.getState();
                        if (sgn.getLine(1).equals("[Lift Up]")) {
                            Location loc = Util.getLiftDestination(player.getLocation(), sgn.getLocation());
                            if (loc == null) {
                                player.sendMessage(ChatColor.GRAY + "This Lift is not safe to use!");
                            } else {
                                player.teleport(loc);
                            }
                            return false;
                        }
                    }
                }
                player.sendMessage(ChatColor.GRAY + "This Lift sign is not linked!");
                return false;
            case "\u00A71[Cart]":
                if (!player.hasPermission("root.sign.cart")) {
                    player.sendMessage(ChatColor.GRAY + "You do not have permission to use these Signs!");
                    return true;
                }
                if (action != Action.RIGHT_CLICK_BLOCK) {
                    return true;
                }
                Location lctn;
                switch (sign.getBlock().getData()) {
                    case 3:
                        lctn = sign.getLocation().add(new Vector(0.5, -0.175, -1.5));
                        break;
                    case 2:
                        lctn = sign.getLocation().add(new Vector(0.5, -0.175, 2.5));
                        break;
                    case 4:
                        lctn = sign.getLocation().add(new Vector(2.5, -0.175, 0.5));
                        break;
                    case 5:
                        lctn = sign.getLocation().add(new Vector(-1.5, -0.175, 0.5));
                        break;
                    default:
                        lctn = sign.getLocation();
                }
                Minecart cart = (Minecart) clickedBlock.getWorld().spawnEntity(lctn, EntityType.MINECART);
                cart.setPassenger(player);
                cart.setMaxSpeed(Double.parseDouble(sign.getLine(2)) / 20);
                Storage.VEHICLES.add(cart);
                return false;
            case "\u00A71[Boat]":
                if (!player.hasPermission("root.sign.boat")) {
                    player.sendMessage(ChatColor.GRAY + "You do not have permission to use these Signs!");
                    return true;
                }
                if (action != Action.RIGHT_CLICK_BLOCK) {
                    return true;
                }
                switch (sign.getBlock().getData()) {
                    case 3:
                        lctn = sign.getLocation().add(new Vector(0.5, -0.175, -1.5));
                        break;
                    case 2:
                        lctn = sign.getLocation().add(new Vector(0.5, -0.175, 2.5));
                        break;
                    case 4:
                        lctn = sign.getLocation().add(new Vector(2.5, -0.175, 0.5));
                        break;
                    case 5:
                        lctn = sign.getLocation().add(new Vector(-1.5, -0.175, 0.5));
                        break;
                    default:
                        lctn = sign.getLocation();
                }
                Boat boat = (Boat) clickedBlock.getWorld().spawnEntity(lctn, EntityType.BOAT);
                boat.setPassenger(player);
                Storage.VEHICLES.add(boat);
                return false;
        }
        switch (sign.getLine(0)) {
            case "\u00A71[Tesseract]":
            case "\u00A71[Tess\u00A71eract]":
                if (!Util.canBuild(player, clickedBlock)) {
                    return true;
                }
                long timer = System.nanoTime();
                Tesseract tesseract = new Tesseract(sign);
                if (!tesseract.isValid()) {
                    tesseract.updateSign(false);
                    return true;
                }

                if (player.hasPermission("root.item.kleinbottle") && Tesseract.isKleinBottle(player.getInventory().getItemInMainHand())) {
                    ItemStack bottle = player.getInventory().getItemInMainHand();
                    if (action == Action.LEFT_CLICK_BLOCK && !tesseract.isEmpty()) {
                        bottle = tesseract.withdrawIntoKleinBottle(bottle);
                    } else if (action == Action.RIGHT_CLICK_BLOCK && tesseract.isEmpty()) {
                        bottle = tesseract.storeFromKleinBottle(bottle);
                    }
                    player.getInventory().setItemInMainHand(bottle);
                    player.updateInventory();
                    return true;
                }

                if (action == Action.LEFT_CLICK_BLOCK && !tesseract.isEmpty()) {
                    ItemStack stack;
                    if (player.isSneaking()) {
                        stack = tesseract.withdrawItem();
                    } else {
                        stack = tesseract.withdrawStack();
                    }
                    player.getWorld().dropItem(sign.getLocation().add(0.5, 0, 0.5), stack).setPickupDelay(0);

                } else if (action == Action.RIGHT_CLICK_BLOCK) {
                    if ((Tesseract.isDoubleClick(player)
                            && tesseract.storeInventory(player.getInventory()))
                            || tesseract.storeStack(player.getInventory())) {
                        player.updateInventory();
                    }
                    Tesseract.rememberClick(player);
                }
                return false;
            case "\u00A71[Info]":
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    if ((player.getInventory().getItemInMainHand().getType() == Material.BOOK_AND_QUILL
                            || player.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK)
                            && player.isSneaking()
                            && player.hasPermission("root.sign.info.edit")) {
                        BookMeta bookmeta = (BookMeta) player.getInventory().getItemInMainHand().getItemMeta();
                        if (bookmeta.hasPages()) {
                            StringBuilder sb = new StringBuilder();
                            for (String str : bookmeta.getPages()) {
                                sb.append(ChatColor.translateAlternateColorCodes('&', ChatColor.stripColor(str)))
                                        .append("\n");
                            }
                            Storage.INFO_SIGNS.put(sign.getLine(1), sb.toString());
                            player.sendMessage(ChatColor.GRAY + "Info Sign updated!");
                            player.closeInventory();
                        }
                    } else {
                        if (Storage.INFO_SIGNS.containsKey(sign.getLine(1))) {
                            player.sendMessage(Storage.INFO_SIGNS.get(sign.getLine(1)));
                        } else {
                            player.sendMessage(ChatColor.GRAY + "This Info-Sign has no content!");
                        }
                        return false;
                    }
                }
                break;
            case "\u00A71[Launch]":
                if (!player.hasPermission("root.sign.launch")) {
                    player.sendMessage(ChatColor.GRAY + "You do not have permission to use these Signs!");
                    return true;
                }
                if (action != Action.RIGHT_CLICK_BLOCK
                        && action != Action.RIGHT_CLICK_AIR) {
                    return true;
                }

                if (player.hasMetadata("root.task.launch")) {
                    return true;
                }
                int x = Integer.parseInt(sign.getLine(1));
                int y = Integer.parseInt(sign.getLine(2));
                int z = Integer.parseInt(sign.getLine(3));
                TaskLaunchPlayer launcher = new TaskLaunchPlayer(player, new Location(player.getWorld(), x, y, z));
                launcher.runTaskTimer(Root.instance(), 2, 2);
                player.setMetadata("root.task.launch", new FixedMetadataValue(Root.instance(), launcher));
                return false;
            case "\u00A71[Petition]":
                if (!player.hasPermission("root.sign.petition")) {
                    player.sendMessage(ChatColor.GRAY + "You do not have permission to use these Signs!");
                    return true;
                }
                if (action != Action.RIGHT_CLICK_BLOCK) {
                    return true;
                }

                Petition p = Storage.PETITIONS.get(sign.getLine(1));

                if (p == null) {
                    player.sendMessage(ChatColor.GRAY + "This Petition is broken!");
                    sign.setLine(0, "\u00A74[Petition]");
                    sign.update();
                    return true;
                }

                if (p.signOrRevoke(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GRAY + " You have " + ChatColor.BLUE + "signed " + ChatColor.GRAY + "this Petition!");
                } else {
                    player.sendMessage(ChatColor.GRAY + " You have " + ChatColor.RED + "revoked " + ChatColor.GRAY + "your signature on this Petition!");
                }
                sign.setLine(2, "" + ChatColor.GREEN + p.getNumberOfSignatures() + " Signatures");
                sign.update();
                return false;
        }
        return true;
    }

    private boolean isSign(Block block) {
        return block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN;
    }

    private boolean isSign(Location loc) {
        return isSign(loc.getWorld().getBlockAt(loc));
    }

    public String padding(String number, int length) {
        while (number.length() < length) {
            number = "0" + number;
        }
        return number;
    }
}
