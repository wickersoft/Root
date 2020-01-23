/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fakeentitysender;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public abstract class FakeEntitySender {

    private static FakeEntitySender INSTANCE = null;

    public static FakeEntitySender instance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        
       
        try {
            Class.forName("net.minecraft.server.v1_15_R1.EntityPlayer");
            INSTANCE = new NMS_1_15_R1();
            System.out.println("Compatible NMS version detected");
            return INSTANCE;
        } catch (Exception e) {
        }
        INSTANCE = new NoNMSDummy();
        return INSTANCE;
    }

    public abstract boolean showHighlightBlock(Block block, Player player);

    public abstract boolean hideHighlightBlock(Block block, Player player);
    
    public boolean isCompatible() {
        return true;
    }
}
