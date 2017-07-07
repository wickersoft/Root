package wickersoft.root;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Util {

    private static boolean nmsDetected = false;
    private static FakeEntitySender fakeEntitySender;

    static {
        try {
            Class.forName("net.minecraft.server.v1_11_R1.EntityPlayer");
            nmsDetected = true;
            fakeEntitySender = new FakeEntitySender();
            System.out.println("Compatible NMS version detected");
        } catch (Exception e) {
            System.out.println("NMS version not compatible!");
            fakeEntitySender = null;
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

    public static boolean canBuild(Player player, Block block) {
        return Storage.worldguard == null || Storage.worldguard.canBuild(player, block);
    }

    public static boolean isProtected(Block block) {
        return Storage.worldguard != null
                && Storage.worldguard.getRegionManager(block.getWorld())
                        .getApplicableRegions(block.getLocation()).size() != 0;
    }

    public static boolean showBlock(int x, int y, int z, int entityId, Player player) {
        if (!nmsDetected) {
            return false;
        }
        return fakeEntitySender.showHighlightBlock(x, y, z, entityId, player, false);
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

    private static class FakeEntitySender {

        private FakeEntitySender() {
        }

        public boolean showHighlightBlock(Block block, Player player) {
            int entityId = 2000000000 + (block.hashCode()) % 10000000;
            return showHighlightBlock(block.getX(), block.getY(), block.getZ(), entityId, player, true);
        }

        public boolean showHighlightBlock(int x, int y, int z, int entityId, Player player, boolean glow) {
            if (!nmsDetected) {
                return false;
            }

            PacketPlayOutSpawnEntity ppose = new PacketPlayOutSpawnEntity();
            Class clazz = ppose.getClass();

            try {
                // Entity ID and UUID
                Field f = clazz.getDeclaredField("a");
                f.setAccessible(true);
                f.setInt(ppose, entityId);
                f = clazz.getDeclaredField("b");
                f.setAccessible(true);
                f.set(ppose, new UUID(0xFF00FF00FF00FF00L, 0xFF00FF00FF00FF00L));
                // Position
                f = clazz.getDeclaredField("c");
                f.setAccessible(true);
                f.setDouble(ppose, x + 0.5);
                f = clazz.getDeclaredField("d");
                f.setAccessible(true);
                f.setDouble(ppose, y);
                f = clazz.getDeclaredField("e");
                f.setAccessible(true);
                f.setDouble(ppose, z + 0.5);
                // Velocity
                f = clazz.getDeclaredField("f");
                f.setAccessible(true);
                f.setInt(ppose, 0);
                f = clazz.getDeclaredField("g");
                f.setAccessible(true);
                f.setInt(ppose, 0);
                f = clazz.getDeclaredField("h");
                f.setAccessible(true);
                f.setInt(ppose, 0);
                // Yaw and Pitch
                f = clazz.getDeclaredField("i");
                f.setAccessible(true);
                f.setInt(ppose, 0);
                f = clazz.getDeclaredField("j");
                f.setAccessible(true);
                f.setInt(ppose, 0);
                // Entity Type and Object data (block type)
                f = clazz.getDeclaredField("k");
                f.setAccessible(true);
                f.setInt(ppose, 70); // minecraft:falling_block
                f = clazz.getDeclaredField("l");
                f.setAccessible(true);
                f.setInt(ppose, glow ? 1 : 36); // Stone:0

            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
                return false;
            }

            DataWatcherObject<Byte> dwo0 = new DataWatcherObject<>(0, DataWatcherRegistry.a); // Indicating a metadata value of type Byte at index 0
            DataWatcherObject<Boolean> dwo5 = new DataWatcherObject<>(5, DataWatcherRegistry.h); // Indicating a metadata value of type Boolean at index 5
            DataWatcher.Item<Byte> dwi0 = new DataWatcher.Item<>(dwo0, glow ? (byte) 0x60 : (byte) 0x00); // A Metadata item of type Byte with value 0x60
            DataWatcher.Item<Boolean> dwi5 = new DataWatcher.Item<>(dwo5, true); // A Metadata item of type Boolean with value true

            List<DataWatcher.Item> dwiList = new ArrayList<>();
            dwiList.add(dwi0);
            dwiList.add(dwi5);

            PacketPlayOutEntityMetadata ppoem = new PacketPlayOutEntityMetadata();
            clazz = ppoem.getClass();

            try {
                // Entity ID and DataWatcher items
                Field f = clazz.getDeclaredField("a");
                f.setAccessible(true);
                f.setInt(ppoem, entityId);
                f = clazz.getDeclaredField("b");
                f.setAccessible(true);
                f.set(ppoem, dwiList);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
                return false;
            }

            EntityPlayer ep = ((CraftPlayer) player).getHandle();
            ep.playerConnection.networkManager.sendPacket(ppose);
            ep.playerConnection.networkManager.sendPacket(ppoem);
            return true;
        }

        public boolean hideHighlightBlock(Block block, Player player) {
            if (!nmsDetected) {
                return false;
            }
            int entityId = 2000000000 + (block.hashCode()) % 10000000;
            PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityId);
            EntityPlayer ep = ((CraftPlayer) player).getHandle();
            ep.playerConnection.networkManager.sendPacket(ppoed);
            return true;
        }

    }
}
