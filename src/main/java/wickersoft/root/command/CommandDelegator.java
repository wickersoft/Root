/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import wickersoft.root.Storage;

/**
 *
 * @author Dennis
 */
public class CommandDelegator {

    private static final HashMap<String, Command> DELEGATIONS = new HashMap<>();

    public static boolean onCommand(CommandSender sender, String label, String[] args) {
        long nanos = System.nanoTime();
        Command cmd = DELEGATIONS.get(label);
        if (cmd == null) {
            sender.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Hey! " + ChatColor.GRAY + "Brainiac is a dumbass and forgot to register this command :(");
            return true;
        }
        boolean ret = cmd.onCommand(sender, args);
        nanos = System.nanoTime() - nanos;
        if (Storage.DEBUG) {
            System.out.println("Command " + label + " runtime: " + nanos + "ns");
        }
        return ret;
    }

    static {
        DELEGATIONS.put("ding", new Ding());
        DELEGATIONS.put("freeze", new Freeze());
        DELEGATIONS.put("instantsign", new InstantSign());
        DELEGATIONS.put("inventory", new Inv());
        DELEGATIONS.put("kleinbottle", new Kleinbottle());
        DELEGATIONS.put("lore", new Lore());
        DELEGATIONS.put("name", new Name());
        DELEGATIONS.put("nv", new Nv());
        DELEGATIONS.put("player", new Player());
        DELEGATIONS.put("shadowmute", new Shadowmute());
        DELEGATIONS.put("slurp", new Slurp());
        DELEGATIONS.put("seelwc", new Seelwc());
        DELEGATIONS.put("sub", new Sub());
        DELEGATIONS.put("undercover", new Undercover());
        DELEGATIONS.put("volatile", new Volatile());
        DELEGATIONS.put("wand", new Wand());
    }
}
