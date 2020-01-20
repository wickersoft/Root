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
public class NoNMSDummy extends FakeEntitySender {

    @Override
    public boolean showHighlightBlock(Block block, Player player) {
        System.err.println("Root: Unable to connect to NMS");
        return false;
    }

    @Override
    public boolean showHighlightBlock(int x, int y, int z, int entityId, Player player, boolean glow) {
        System.err.println("Root: Unable to connect to NMS");
        return false;
    }

    @Override
    public boolean hideHighlightBlock(Block block, Player player) {
        System.err.println("Root: Unable to connect to NMS");
        return false;
    }
    
    @Override
    public boolean isCompatible() {
        return false;
    }
}
