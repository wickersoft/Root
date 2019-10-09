/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fakeentitysender;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class NMS_1_14_R1 extends FakeEntitySender {

    public NMS_1_14_R1() {
    }

    public boolean showHighlightBlock(Block block, Player player) {
        int entityId = 2000000000 + (block.hashCode()) % 10000000;
        return showHighlightBlock(block.getX(), block.getY(), block.getZ(), entityId, player, true);
    }

    public boolean showHighlightBlock(int x, int y, int z, int entityId, Player player, boolean glow) {
        PacketPlayOutSpawnEntityLiving pposel = generateShulkerSpawnPacket(x, y, z, entityId);
        if (pposel == null) {
            return false;
        }
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(pposel);
        return true;
    }

    public boolean hideHighlightBlock(Block block, Player player) {
        int entityId = 2000000000 + (block.hashCode()) % 10000000;
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityId);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(ppoed);
        return true;
    }
    
    private static PacketPlayOutSpawnEntityLiving generateShulkerSpawnPacket(int x, int y, int z, int entityId) {
        PacketPlayOutSpawnEntityLiving pposel = new PacketPlayOutSpawnEntityLiving();
        Class clazz = pposel.getClass();
        try {
            Field f = clazz.getDeclaredField("a");
            f.setAccessible(true);
            f.setInt(pposel, entityId);
            f = clazz.getDeclaredField("b");
            f.setAccessible(true);
            f.set(pposel, new UUID(0xFF00FF00FF00FF00L, 0xFF00FF00FF00FF00L));
            f = clazz.getDeclaredField("c");
            f.setAccessible(true);
            f.setInt(pposel, 62); // Mod data (changes every NMS update)
            f = clazz.getDeclaredField("d");
            f.setAccessible(true);
            f.setDouble(pposel, x + 0.5);
            f = clazz.getDeclaredField("e");
            f.setAccessible(true);
            f.setDouble(pposel, y);
            f = clazz.getDeclaredField("f");
            f.setAccessible(true);
            f.setDouble(pposel, z + 0.5);
            f = clazz.getDeclaredField("g");
            f.setAccessible(true);
            f.setInt(pposel, 0);
            f = clazz.getDeclaredField("h");
            f.setAccessible(true);
            f.setInt(pposel, 0);
            f = clazz.getDeclaredField("i");
            f.setAccessible(true);
            f.setInt(pposel, 0);
            f = clazz.getDeclaredField("j");
            f.setAccessible(true);
            f.setByte(pposel, (byte) 0);
            f = clazz.getDeclaredField("k");
            f.setAccessible(true);
            f.setByte(pposel, (byte) 0);
            f = clazz.getDeclaredField("l");
            f.setAccessible(true);
            f.setByte(pposel, (byte) 0);

            DataWatcher m = new FakeDataWatcher();
            f = clazz.getDeclaredField("m");
            f.setAccessible(true);
            f.set(pposel, m);

        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }

        return pposel;
    }

    private static class FakeDataWatcher extends DataWatcher {

        public FakeDataWatcher() {
            super(null); // We don't actually need DataWatcher methods, just the inheritance
        }

        // Inject metadata into network stream
        @Override
        public void a(PacketDataSerializer pds) {
            pds.writeByte(0); // Set Metadata at index 0
            pds.writeByte(0); // Value is type Byte
            pds.writeByte(0x60); // Set Glowing and Invisible bits
            pds.writeByte(0xFF); // Index -1 indicates end of Metadata
        }
    }
}
