/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.io.IOException;
import java.net.URLEncoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class WatcherChat implements Listener {

    private static final WatcherChat INSTANCE = new WatcherChat();

    public static WatcherChat instance() {
        return INSTANCE;
    }

    private WatcherChat() {
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent evt) {
        Player player = evt.getPlayer();
        UserData data = UserDataProvider.getOrCreateUser(player);

        if (data.isShadowmuted()) {
            evt.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Root.instance(), () -> {
                player.sendMessage(String.format(evt.getFormat(), evt.getPlayer().getDisplayName(), evt.getMessage()));
                for(Player recp : Bukkit.getOnlinePlayers()) {
                    if(recp.hasPermission("root.shadowmute.see")) {
                        player.sendMessage(String.format(Storage.SHADOWMUTE_SEE_CHAT_FORMAT, evt.getPlayer().getDisplayName(), evt.getMessage()));
                    }
                }
            });
            
            return;
        }

        if (data.isUndercover()) {
            evt.setFormat(Storage.UNDERCOVER_CHAT_FORMAT);
            evt.getPlayer().setDisplayName(evt.getPlayer().getName());
        }

        if (evt.isCancelled()) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Root.instance(), () -> {
            for (Player recp : Bukkit.getOnlinePlayers()) {
                if (recp.hasPermission("root.ding")
                        && UserDataProvider.getOrCreateUser(recp).matchDingPattern(evt.getMessage())) {
                    for (int i = 0; i < 16; i++) {
                        recp.playSound(recp.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2f);
                    }
                    for (int i = 0; i < 4; i++) {
                        recp.playSound(recp.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.5f);
                    }
                }
            }
        });

        if (evt.isAsynchronous() && !data.getSubtitleLangPair().equals("")) {
            String translation = translate(evt.getMessage(), data.getSubtitleLangPair());
            evt.setMessage(evt.getMessage().concat("  " + ChatColor.GRAY + ChatColor.ITALIC + "[" + translation + "]"));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent evt) {
        String[] message = {evt.getMessage()};
        Storage.SHORTCUTS.forEach((pattern, replacement) -> {
            message[0] = pattern.matcher(message[0]).replaceFirst(replacement);
        });
        evt.setMessage(message[0]);
        ForeignCommandHook.onCommand(evt);
    }

    /*
    If you really want to use this plugin on your own server, have the courtesy to insert your own e-mail here.
     */
    private String translate(String message, String langpair) {
        try {
            String trans = new String(
                    HTTP.http("http://mymemory.translated.net/api/get?langpair="
                            + langpair + "&de=" + Storage.MYMEMORY_TRANSLATED_NET_API_KEY + "&q="
                            + URLEncoder.encode(message, "UTF-8"), Storage.TRANSLATION_TIMEOUT).content);
            String translated = parse(trans);
            return translated;
        } catch (IOException ex) {
        }
        return "";
    }

    private String parse(String json) {
        String content = StringUtil.extract(json, "translatedText\":\"", "\"");
        content = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(content);
        content = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(content);
        return content;
    }
}
