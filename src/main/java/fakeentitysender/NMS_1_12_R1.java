/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fakeentitysender;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class NMS_1_12_R1 extends FakeEntitySender {

    public NMS_1_12_R1() {
    }

    public boolean showHighlightBlock(Block block, Player player) {
        int entityId = 2000000000 + (block.hashCode()) % 10000000;
        return showHighlightBlock(block.getX(), block.getY(), block.getZ(), entityId, player, true);
    }

    public boolean showHighlightBlock(int x, int y, int z, int entityId, Player player, boolean glow) {

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
        int entityId = 2000000000 + (block.hashCode()) % 10000000;
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityId);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(ppoed);
        return true;
    }
}
