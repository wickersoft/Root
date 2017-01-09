/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root.command;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;
import wickersoft.root.Storage;

/**
 *
 * @author Dennis
 */
public class Sub extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (args.length < 2) {
            sendUsage(player);
            return true;
        }
        UserData data = UserDataProvider.getUser(args[0]);
        if (data == null) {
            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + " Player name not recognized");
            return true;
        }

        if (args[1].equalsIgnoreCase("off")) {
            data.setSubtitleLangPair("");
            player.sendMessage(ChatColor.GRAY + "Subtitles for Player " + data.getName() + ChatColor.RED + " disabled");
            return true;
        }

        String sourceLanguage = Storage.LANGUAGE_ALIASES.getOrDefault(args[1].toLowerCase(), args[1].toLowerCase());
        String destLanguage;
        if (args.length >= 3) {
            destLanguage = Storage.LANGUAGE_ALIASES.getOrDefault(args[2].toLowerCase(), args[2].toLowerCase());
        } else {
            destLanguage = "en";
        }

        if (!ArrayUtils.contains(Storage.KNOWN_LANGCODES, sourceLanguage)) {
            player.sendMessage(ChatColor.GRAY + "Unknown language code " + ChatColor.RED + sourceLanguage);
            return true;
        }
        if (!ArrayUtils.contains(Storage.KNOWN_LANGCODES, destLanguage)) {
            player.sendMessage(ChatColor.GRAY + "Unknown language code " + ChatColor.RED + destLanguage);
            return true;
        }

        data.setSubtitleLangPair(sourceLanguage + "|" + destLanguage);
        player.sendMessage(ChatColor.GRAY + "Subtitles for Player " + data.getName() + ChatColor.GREEN + " enabled");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/sub [player] [source language/off] ([sub language])";
    }

    @Override
    public String getDescription() {
        return "Display subtitles after a player's essages";
    }

}
