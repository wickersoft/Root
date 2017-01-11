/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syn.root.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wickersoft.root.Root;

/**
 *
 * @author Dennis
 */
public class InventoryProvider {

    public static boolean loadInventory(Player player, File inventoryFile) {
        YamlConfiguration invYaml = new YamlConfiguration();
        try {
            invYaml.load(inventoryFile);
            ArrayList<ItemStack> inventoryContents = (ArrayList<ItemStack>) invYaml.getList("inventory");
            ItemStack[] toSet = new ItemStack[inventoryContents.size()];
            for (int i = 0; i < inventoryContents.size(); i++) {
                toSet[i] = inventoryContents.get(i);
            }
            File inventoryBackFile = getInventoryFile(player, ".last");
            saveInventory(player, inventoryBackFile);
            player.getInventory().setContents(toSet);
            player.setLevel(invYaml.getInt("level", 0));
            player.setExp((float) invYaml.getDouble("xp", 0));
        } catch (IOException | InvalidConfigurationException ex) {
            return false;
        }
        return true;
    }

    public static boolean saveInventory(Player player, File inventoryFile) {
        inventoryFile.getParentFile().mkdir();
        YamlConfiguration invYaml = new YamlConfiguration();
        invYaml.set("inventory", player.getInventory().getContents());
        invYaml.set("level", player.getLevel());
        invYaml.set("xp", player.getExp());
        try {
            invYaml.save(inventoryFile);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static String[] listInventories(UUID uuid) {
        File[] inventories = getInventoryFiles(uuid);
        String[] inventoryNames = new String[inventories.length];
        for (int i = 0; i < inventories.length; i++) {
            String name = inventories[i].getName();
            inventoryNames[i] = name.substring(0, name.lastIndexOf("."));
        }
        return inventoryNames;
    }

    public static File[] getInventoryFiles(UUID uuid) {
        File inventoryFolderFile = new File(Root.instance().getDataFolder(), "users/" + uuid + "/inventories");
        if (inventoryFolderFile.exists() || inventoryFolderFile.isDirectory()) {
            return inventoryFolderFile.listFiles();
        } else {
            return new File[0];
        }
    }

    public static File getInventoryFile(Player player, String name) {
        return getInventoryFile(player.getUniqueId(), name);
    }

    public static File getInventoryFile(UUID uuid, String name) {
        return new File(Root.instance().getDataFolder(), "users/" + uuid + "/inventories/" + name + ".yml");
    }

    public static String[] listInventories(Player player) {
        return listInventories(player.getUniqueId());
    }

}
