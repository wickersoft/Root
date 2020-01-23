package wickersoft.root;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fakeentitysender.FakeEntitySender;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Util {

    private static boolean nmsDetected = false;
    private static FakeEntitySender fakeEntitySender;

    static {
        fakeEntitySender = FakeEntitySender.instance();
        nmsDetected = fakeEntitySender.isCompatible();
    }

    public static boolean canPlayerHoldVolatileItem(Player player, ItemStack is) {
        if (player.hasPermission("root.item.volatile")) {
            return true;
        } else if (!SpecialItemUtil.isVolatile(is)) {
            return true;
        } else if (Storage.getWorldguard() == null) {
            return false;
        } else {
            String volatileRegion = SpecialItemUtil.getVolatileRegion(is);
            if (volatileRegion == null) {
                return false;
            } else {
                BlockVector3 bv3 = BukkitAdapter.asBlockVector(player.getLocation());
                World wld = BukkitAdapter.adapt(player.getWorld());
                ApplicableRegionSet ars = Storage.getWorldguard().get(wld).getApplicableRegions(bv3);
                Iterator<ProtectedRegion> it = ars.iterator();
                while (it.hasNext()) {
                    ProtectedRegion pr = it.next();
                    if (pr.getId().equals(volatileRegion)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    // Returns the exact center of a block of a given location
    public static Location getCenter(Location loc) {
        return getCenter(loc, false);
    }

    // Returns the exact center of a block of a given location
    public static Location getCenter(Location loc, boolean centerVertical) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        if (x >= 0) {
            x += .5;
        } else {
            x += .5;
        }
        if (centerVertical) {
            y = (int) y + .5;
        }
        if (z >= 0) {
            z += .5;
        } else {
            z += .5;
        }
        Location lo = loc.clone();
        lo.setX(x);
        lo.setY(y);
        lo.setZ(z);
        return lo;
    }

    // Returns the exact center of a block of a given block
    public static Location getCenter(Block blk) {
        return getCenter(blk.getLocation());
    }

    // Returns the exact center of a block of a given block
    public static Location getCenter(Block blk, boolean centerVertical) {
        return getCenter(blk.getLocation(), centerVertical);
    }

    public static List<String> getRegionsCollidingWithPlayerSelection(Player p) {
        List<String> regionNames = new ArrayList<>();
        BukkitPlayer player = BukkitAdapter.adapt(p);
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(p.getWorld());

        LocalSession weSession = WorldEdit.getInstance().getSessionManager().get(player);
        if (weSession == null) {
            return regionNames;
        }

        try {
            Region sel = weSession.getSelection(world);
            AABB selectionAABB = new AABB(sel);

            RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
            Map<String, ProtectedRegion> allRegions = rm.getRegions();
           
            allRegions.forEach((name, prot) -> {
                if (new AABB(prot).collides(selectionAABB)) {
                    regionNames.add(name);
                }
            });
        } catch (Exception ex) {
        }
        return regionNames;
    }

    public static Location getLiftDestination(Location originalPlayerLoc, Location startLiftLoc, Location destinationLiftLoc) {
        Block block = originalPlayerLoc.getWorld().getBlockAt(originalPlayerLoc.getBlockX(), destinationLiftLoc.getBlockY(), originalPlayerLoc.getBlockZ());
        for (int i = 0; i++ < 5 && block.getY() > 0; block = block.getRelative(BlockFace.DOWN)) {
            if (isBlockSafeToStand(block)) {
                return block.getLocation().add(.5, 1, .5).setDirection(originalPlayerLoc.getDirection());
            }
        }
        if (destinationLiftLoc.getBlockY() == 0) {
            return null;
        }
        block = originalPlayerLoc.getWorld().getBlockAt(destinationLiftLoc.getBlockX(), destinationLiftLoc.getBlockY() + 1, destinationLiftLoc.getBlockZ());
        for (int i = 0; i++ < 5 && block.getY() > 0; block = block.getRelative(BlockFace.DOWN)) {
            if (isBlockSafeToStand(block)) {
                return block.getLocation().add(.5, 1, .5).setDirection(originalPlayerLoc.getDirection());
            }
        }
        return null;
    }

    public static boolean isBlockSafeToStand(Block block) {
        if (block.getType().isSolid()) {
            for (int i = 0; i++ < 2 && block.getY() < 255;) {
                if ((block = block.getRelative(BlockFace.UP)).getType().isSolid() && !WatcherSign.isSign(block)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // Displays a particle with the given data
    public static void display(Location loc, Particle particle, int amount, float speed, float xO, float yO, float zO) {
        loc.getWorld().spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), amount, xO, yO, zO, speed);
    }

    public static boolean showHighlightBlocks(List<Block> blocks, Player player) {
        for (Block block : blocks) {
            if (!showHighlightBlock(block, player)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hideHighlightBlocks(List<Block> blocks, Player player) {
        for (Block block : blocks) {
            if (!hideHighlightBlock(block, player)) {
                return false;
            }
        }
        return true;
    }

    public static boolean sendRawMessage(String message, Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + message);
        return true;
    }

    public static boolean showHighlightBlock(Block block, Player player) {
        if (!nmsDetected) {
            return false;
        }
        return fakeEntitySender.showHighlightBlock(block, player);
    }

    public static boolean hideHighlightBlock(Block block, Player player) {
        if (!nmsDetected) {
            return false;
        }
        return fakeEntitySender.hideHighlightBlock(block, player);
    }
    
    private static class AABB {

        private final double xMin, xMax, yMin, yMax, zMin, zMax;

        public AABB(BlockVector3 min, BlockVector3 max) {
            this.xMin = min.getBlockX();
            this.yMin = min.getBlockY();
            this.zMin = min.getBlockZ();
            this.xMax = max.getBlockX();
            this.yMax = max.getBlockY();
            this.zMax = max.getBlockZ();
        }

        public AABB(Region region) {
            this(region.getMinimumPoint(), region.getMaximumPoint());
        }
        
        public AABB(ProtectedRegion region) {
            this(region.getMinimumPoint(), region.getMaximumPoint());
        }

        public boolean collides(AABB other) {
            return !(this.xMax < other.xMin || this.xMin > other.xMax
                    || this.yMax < other.yMin || this.yMin > other.yMax
                    || this.zMax < other.zMin || this.zMin > other.zMax);
        }
    }
}
