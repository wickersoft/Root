/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Dennis
 */
public class CommandDelegator {

    private static final HashMap<String, Command> DELEGATIONS = new HashMap<>();

    public static boolean onCommand(CommandSender sender, String label, String[] args) {
        Command cmd = DELEGATIONS.get(label);
        if (cmd == null) {
            sender.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Hey! " + ChatColor.GRAY + "Brainiac is a dumbass and forgot to register this command :(");
            return true;
        }
        return cmd.onCommand(sender, args);
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
        DELEGATIONS.put("slurp", new Slurp());
        DELEGATIONS.put("sub", new Sub());
        DELEGATIONS.put("undercover", new Undercover());
        DELEGATIONS.put("volatile", new Volatile());
        DELEGATIONS.put("wand", new Wand());
    }
}
