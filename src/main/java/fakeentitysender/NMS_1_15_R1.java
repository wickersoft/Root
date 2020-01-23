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
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.minecraft.server.v1_15_R1.DataWatcherSerializer;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class NMS_1_15_R1 extends FakeEntitySender {

    public NMS_1_15_R1() {
    }

    @Override
    public boolean showHighlightBlock(Block block, Player player) {
        int entityId = 2000000000 + (block.hashCode()) % 10000000;
        return showHighlightBlock(block.getX(), block.getY(), block.getZ(), entityId, player);
    }

    @Override
    public boolean hideHighlightBlock(Block block, Player player) {
        int entityId = 2000000000 + (block.hashCode()) % 10000000;
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityId);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(ppoed);
        return true;
    }

    private boolean showHighlightBlock(int x, int y, int z, int entityId, Player player) {
        PacketPlayOutSpawnEntityLiving pposel = generateShulkerSpawnPacket(x, y, z, entityId);
        PacketPlayOutEntityMetadata ppoem = generateShulkerGlowPacket(entityId);
        if (pposel == null) {
            return false;
        }
        if (ppoem == null) {
            return false;
        }
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(pposel);
        ep.playerConnection.networkManager.sendPacket(ppoem);
        return true;
    }

    private static PacketPlayOutEntityMetadata generateShulkerGlowPacket(int entityId) {
        PacketPlayOutEntityMetadata ppoem = new PacketPlayOutEntityMetadata();
        Class clazz = ppoem.getClass();
        try {
            Field f = clazz.getDeclaredField("a");
            f.setAccessible(true);
            f.setInt(ppoem, entityId);
            
            // Build data structure for Entity Metadata. Requires an index, a type and a value. 
            // As of 1.15.2, an invisible + glowing LivingEntity is set by Index 0 Type Byte Value 0x60
            DataWatcherSerializer<Byte> dws = DataWatcherRegistry.a; // Type (Byte)
            DataWatcherObject<Byte> dwo = new DataWatcherObject<>(0, dws); // Index (0)
            DataWatcher.Item<Byte> dwi = new DataWatcher.Item<>(dwo, (byte) 0x60); // Value (0x60)
            List<DataWatcher.Item<Byte>> list = new ArrayList<>();
            list.add(dwi); // Pack it in a list

            f = clazz.getDeclaredField("b");
            f.setAccessible(true);
            f.set(ppoem, list);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
        return ppoem;
    }

    private static PacketPlayOutSpawnEntityLiving generateShulkerSpawnPacket(int x, int y, int z, int entityId) {
        PacketPlayOutSpawnEntityLiving pposel = new PacketPlayOutSpawnEntityLiving();
        
        int mobTypeId = net.minecraft.server.v1_15_R1.IRegistry.ENTITY_TYPE.a(EntityTypes.SHULKER);
        
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
            f.setInt(pposel, mobTypeId);
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
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
        return pposel;
    }
}
